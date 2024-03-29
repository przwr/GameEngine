/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.navmeshpathfinding;

import collision.Figure;
import engine.utilities.Methods;
import engine.utilities.Point;
import engine.utilities.PointContainer;
import game.logic.navmeshpathfinding.navigationmesh.NavigationMesh;
import game.logic.navmeshpathfinding.navigationmesh.Node;
import game.logic.navmeshpathfinding.navigationmesh.Triangle;
import game.place.Place;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import static game.logic.navmeshpathfinding.navigationmesh.NavigationMeshGenerator.getIndexForShifts;

/**
 * @author WROBELP1
 */
public class PathFinder {

    public static final int TO_LEFT_TOP = 1;
    public static final int TO_LEFT_BOTTOM = 4;
    public static final int TO_RIGHT_BOTTOM = 8;
    public static final int TO_RIGHT_TOP = 2;
    private static final int TO_TOP = 3;
    private static final int TO_LEFT = 5;
    private static final int TO_BOTTOM = 12;
    private static final int TO_RIGHT = 10;
    private static final int START = 0;
    private static final int END = 1;
    private static final Set<Node> closedList = new HashSet<>();
    private static final PriorityQueue<Node> openList = new PriorityQueue<>(24, (Node n1, Node n2) -> n1.getFCost() - n2.getFCost());
    private static final Point[] castingPoints = {new Point(), new Point()}, castingDestination = {new Point(), new Point()};
    private static final Polygon poly = new Polygon();
    private static final Point temp1 = new Point(), temp2 = new Point();
    private static PointContainer shifted;
    private static int widthHalf, heightHalf, widthFraction, heightFraction, xDS, yDS, xDE, yDE;
    private static Point destinationPoint = new Point(), startPoint = new Point();
    private static Node destination;
    private static Triangle startTriangle, endTriangle;
    private static Triangle[] pathBase = new Triangle[2];

    public static PointContainer findPath(NavigationMesh mesh, int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        if (Methods.pointDistance(xStart, yStart, xDestination, yDestination) <= Place.tileSize) {
            PointContainer solution = new PointContainer(2);
            solution.add(xDestination, yDestination);
            return solution;
        }
        if (mesh == null) {
            System.out.println("Brak siatki nawigacji - znalezienie ścieżki niemożliwe");
            return null;
        }
        firstStage(mesh, xStart, yStart, xDestination, yDestination, collision);
        return produceResult(destination, mesh);
    }

    public static boolean pathExists(NavigationMesh mesh, int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        if (Methods.pointDistance(xStart, yStart, xDestination, yDestination) <= Place.tileSize) {
            return true;
        }
        if (mesh == null) {
            System.out.println("Brak siatki nawigacji - znalezienie ścieżki niemożliwe");
            return false;
        }
        firstStage(mesh, xStart, yStart, xDestination, yDestination, collision);
        return destination != null;
    }

    private static void firstStage(NavigationMesh mesh, int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        startPoint = new Point(xStart, yStart);
        destinationPoint = new Point(xDestination, yDestination);
        widthHalf = collision.getWidthHalf();
        heightHalf = collision.getHeightHalf();
        widthFraction = (int) (widthHalf * 0.7);
        heightFraction = (int) (heightHalf * 0.7);
        pathBase = mesh.getPathBase(destinationPoint, startPoint, pathBase);
        startTriangle = pathBase[START];
        endTriangle = pathBase[END];
        findSolution(mesh);
    }


    private static void findSolution(NavigationMesh mesh) {
        destination = null;
        if (startTriangle != null && endTriangle != null) {
            if (startTriangle == endTriangle) {
                destination = inOneTriangle();
            } else {
                destination = aStar(mesh);
            }
        }
    }

    private static Node inOneTriangle() {
        destination = new Node(startPoint);
        destination.setParentMakeChild(new Node(destinationPoint));
        return destination;
    }

    private static Node aStar(NavigationMesh mesh) {
        Node currentNode;
        readyVariables(mesh);
        createBeginningAndAdjacent();
        while (!openList.isEmpty()) {
            currentNode = openList.poll();
            closedList.add(currentNode);
            if (isInEndTriangle(currentNode, mesh)) {
                break;
            }
            keepLooking(currentNode, mesh);
        }
        return destination;
    }

    private static void readyVariables(NavigationMesh mesh) {
        mesh.reset();
        closedList.clear();
        openList.clear();
        destination = null;
    }

    private static void createBeginningAndAdjacent() {
        Node beginning = new Node(destinationPoint);
        beginning.setGHCosts(0, countH(destinationPoint, startPoint));
        closedList.add(beginning);
        for (int i = 0; i < 3; i++) {
            Node node = startTriangle.getNode(i);
            calculateAndAddToOpenList(node, beginning);
        }
    }

    private static void calculateAndAddToOpenListIfFits(Node node, Node parent, NavigationMesh mesh) {
        if (isFittingPoint(node.getPoint(), parent.getPoint(), mesh)) {
            calculateAndAddToOpenList(node, parent);
        }
    }

    private static void calculateAndAddToOpenList(Node node, Node parent) {
        node.setParentMakeChild(parent);
        node.setGHCosts(countG(node.getPoint(), parent.getPoint()), countH(node.getPoint(), startPoint));
        openList.add(node);
    }

    private static boolean isFittingPoint(Point point, Point parent, NavigationMesh mesh) {
        if (point.getX() > 0 && point.getY() > 0) {
            setPolygonForTesting(getShiftPoint(parent, temp1, mesh, widthHalf, heightHalf), getShiftPoint(point, temp2, mesh, widthHalf, heightHalf));
            return isClearWay(poly, mesh.getCollisionPoints());
        }
        return false;
    }

    private static void setPolygonForTesting(Point current, Point destination) {
        setDestinationCorners(current);
        Methods.getCastingPoints((2 * current.getX() - destination.getX()), (2 * current.getY() - destination.getY()), xDS, xDE, yDS, yDE, castingPoints);
        setDestinationCorners(destination);
        Methods.getCastingPoints((2 * destination.getX() - current.getX()), (2 * destination.getY() - current.getY()), xDS, xDE, yDS, yDE, castingDestination);
        poly.reset();
        poly.addPoint(castingPoints[0].getX(), castingPoints[0].getY());
        poly.addPoint(castingPoints[1].getX(), castingPoints[1].getY());
        if (castingDestination[0].getY() != castingDestination[1].getY()) {
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
        } else if (castingDestination[0].getX() > castingDestination[1].getX()) {
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
        } else {
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
        }
    }

    private static void setDestinationCorners(Point destination) {
        xDS = destination.getX() - widthFraction;
        xDE = destination.getX() + widthFraction;
        yDS = destination.getY() - heightFraction;
        yDE = destination.getY() + heightFraction;
    }

    private static boolean isClearWay(Polygon poly, List<Point> close) {
        return !close.stream().anyMatch((point) -> (poly.contains(point.getX(), point.getY())));
    }

    private static Point getShiftPoint(Point point, Point temp, NavigationMesh mesh, int width, int height) {
        switch (mesh.getShiftDirections()[getIndexForShifts(point.getX() / Place.tileSize, point.getY() / Place.tileSize)]) {
            case TO_LEFT_TOP:
                temp.set(point.getX() - width, point.getY() - height);
                break;
            case TO_LEFT_BOTTOM:
                temp.set(point.getX() - width, point.getY() + height);
                break;
            case TO_RIGHT_BOTTOM:
                temp.set(point.getX() + width, point.getY() + height);
                break;
            case TO_RIGHT_TOP:
                temp.set(point.getX() + width, point.getY() - height);
                break;
            case TO_TOP:
                temp.set(point.getX(), point.getY() - height);
                break;
            case TO_LEFT:
                temp.set(point.getX() - width, point.getY());
                break;
            case TO_BOTTOM:
                temp.set(point.getX(), point.getY() + height);
                break;
            case TO_RIGHT:
                temp.set(point.getX() + width, point.getY());
                break;
            default:
                return point;
        }
        return temp;
    }

    private static boolean isInEndTriangle(Node currentNode, NavigationMesh mesh) {
        boolean isFound = false;
        for (int i = 0; i < 3; i++) {
            Node node = endTriangle.getNode(i);
            if (currentNode.getPoint().equals(node.getPoint())) {
                if (isFittingPoint(startPoint, currentNode.getPoint(), mesh)) {
                    destination = new Node(startPoint);
                    calculateAndAddToOpenList(destination, currentNode);
                    isFound = true;
                }
            }
        }
        return isFound;
    }

    private static void keepLooking(Node currentNode, NavigationMesh mesh) {
        currentNode.getNeighbours().stream().filter((node) -> (!closedList.contains(node))).forEach((node) -> {
            if (openList.contains(node)) {
                changeIfBetterPath(node, currentNode, mesh);
            } else {
                calculateAndAddToOpenListIfFits(node, currentNode, mesh);
            }
        });
    }

    private static void changeIfBetterPath(Node node, Node currentNode, NavigationMesh mesh) {
        int temp = countG(node.getPoint(), currentNode.getPoint());
        if (temp + currentNode.getGCost() < node.getGCost() && isFittingPoint(node.getPoint(), currentNode.getPoint(), mesh)) {
            node.setParentMakeChild(currentNode);
            node.setGCost(temp);
        }
    }

    private static int countG(Point point, Point parentPoint) {
        int x = parentPoint.getX() - point.getX();
        int y = parentPoint.getY() - point.getY();
        return (x * x + y * y);
    }

    private static int countH(Point point, Point endPoint) {
        int x = endPoint.getX() - point.getX();
        int y = endPoint.getY() - point.getY();
        return (x * x + y * y);
    }

    private static PointContainer produceResult(Node destination, NavigationMesh mesh) {
        if (destination != null) {
            return printSolution(destination, mesh);
        }
        return null;
    }

    private static PointContainer printSolution(Node destination, NavigationMesh mesh) {
        shifted = new PointContainer(24);
        Point point;
        Node currentNode = destination;
        while (currentNode != null) {
            point = currentNode.getPoint();
            currentNode = currentNode.getParent();
            if (currentNode != null) {
                shifted.add(getShiftPoint(point, temp1, mesh, widthHalf, heightHalf));
            } else {
                shifted.add(point);
            }
        }
        optimize(mesh);
        return shifted;
    }

    private static void optimize(NavigationMesh mesh) {
        Point next, previous;
        for (int i = 1; i < shifted.size() - 1; i++) {
            previous = shifted.get(i - 1);
            for (int j = shifted.size() - 1; j > i; j--) {
                next = shifted.get(j);
                if (canBeSkippedShifted(previous, next, mesh)) {
                    shifted.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    private static boolean canBeSkippedShifted(Point previous, Point next, NavigationMesh mesh) {
        if (!mesh.lineIntersectsMeshBounds(previous, next)) {
            setPolygonForTesting(previous, next);
            return isClearWay(poly, mesh.getCollisionPoints());
        }
        return false;
    }
}
