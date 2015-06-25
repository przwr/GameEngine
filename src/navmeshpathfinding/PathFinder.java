/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import collision.Figure;
import engine.BlueArray;
import engine.Methods;
import engine.Point;
import game.place.Place;
import java.awt.Polygon;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import static navmeshpathfinding.NavigationMeshGenerator.getIndexForShifts;

/**
 *
 * @author WROBELP1
 */
public class PathFinder {

    public static final int TO_LEFT_TOP = 1, TO_LEFT_BOTTOM = 4, TO_RIGHT_BOTTOM = 8, TO_RIGHT_TOP = 2, TO_TOP = 3, TO_LEFT = 5, TO_BOTTOM = 12, TO_RIGHT = 10;
    private static final Set<Node> closedList = new HashSet<>();
    private static final PriorityQueue<Node> openList = new PriorityQueue<>(24, (Node n1, Node n2) -> n1.getFCost() - n2.getFCost());
    private static Point destinationPoint = new Point(), startPoint = new Point(), temp1 = new Point(), temp2 = new Point();
    private static int width, height, xDS, yDS, xDE, yDE;
    private static Triangle startTriangle, endTriangle;
    private static Node destination, beginning;
    private static Point[] castingPoints = {new Point(), new Point()};
    private static Point[] castingDestination = {new Point(), new Point()};
    private static Polygon poly = new Polygon();
    private static List<Point> shifted = new BlueArray<>(), result = new BlueArray<>();
    private static PathBase pathBase;

    public static Point[] findPath(NavigationMesh mesh, int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        if (mesh != null) {
            startPoint = new Point(xStart, yStart);
            destinationPoint = new Point(xDestination, yDestination);
            width = collision.getWidth() / 2;
            height = collision.getHeight() / 2;
            pathBase = mesh.getPathBase(destinationPoint, startPoint);
            startTriangle = pathBase.startTriangle;
            endTriangle = pathBase.endTriangle;
            return findSolution(mesh);
        }
        return null;
    }

    private static Point[] findSolution(NavigationMesh mesh) {
        destination = null;
        if (startTriangle != null && endTriangle != null) {
            if (startTriangle == endTriangle) {
                destination = inOneTriangle();
            } else {
                destination = aStar(mesh);
            }
        }
        return produceResult(destination, mesh);
    }

    private static Node inOneTriangle() {
        Node beginning = new Node(destinationPoint);
        destination = new Node(startPoint);
        destination.setParentMakeChild(beginning);
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
        beginning = new Node(destinationPoint);
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
        setPolygonForTesting(getShiftPoint(parent, temp1, mesh, width, height), getShiftPoint(point, temp2, mesh, width, height));
        return isClearWay(poly, mesh.getCollisonPoints());
    }

    private static void setPolygonForTesting(Point current, Point destination) {
        setDestinationCorners(current);
        Methods.getCastingPoints(destination.getX(), destination.getY(), xDS, xDE, yDS, yDE, castingPoints);
        setDestinationCorners(destination);
        Methods.getCastingPoints(current.getX(), current.getY(), xDS, xDE, yDS, yDE, castingDestination);
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
        xDS = destination.getX() - width + 10;
        xDE = destination.getX() + width - 10;
        yDS = destination.getY() - height + 10;
        yDE = destination.getY() + height - 10;
    }

    private static boolean isClearWay(Polygon poly, List<Point> close) {
        if (close.stream().anyMatch((point) -> (poly.contains(point.getX(), point.getY())))) {
            return false;
        }
        return true;
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
                destination = new Node(startPoint);
                calculateAndAddToOpenListIfFits(destination, currentNode, mesh);
                isFound = true;
            }
        }
        return isFound;
    }

    private static void keepLooking(Node currentNode, NavigationMesh mesh) {
        currentNode.getNeightbours().stream().filter((node) -> (!closedList.contains(node))).forEach((node) -> {
            if (openList.contains(node)) {
                changeIfBetterPath(node, currentNode);
            } else {
                calculateAndAddToOpenListIfFits(node, currentNode, mesh);
            }
        });
    }

    private static void changeIfBetterPath(Node node, Node currentNode) {
        int temp = countG(node.getPoint(), currentNode.getPoint());
        if (temp + currentNode.getGCost() < node.getGCost()) {
            node.setParentMakeChild(currentNode);
            node.setGCost(temp);
        }
    }

    private static int countG(Point point, Point parentPoint) {
        int x = parentPoint.getX() - point.getX();
        int y = parentPoint.getY() - point.getY();
        return (int) ((x * x + y * y));
    }

    private static int countH(Point point, Point endPoint) {
        int x = endPoint.getX() - point.getX();
        int y = endPoint.getY() - point.getY();
        return (int) ((x * x + y * y));
    }

    private static Point[] produceResult(Node destiation, NavigationMesh mesh) {
        if (destiation != null) {
            return printSolution(destiation, mesh);
        }
        return null;
    }

    private static Point[] printSolution(Node destination, NavigationMesh mesh) {
        shifted.clear();
        result.clear();
        Point point;
        Node currentNode = destination;
        while (currentNode != null) {
            point = currentNode.getPoint();
            currentNode = currentNode.getParent();
            result.add(point);
            if (currentNode != null) {
                shifted.add(getNewShiftedPoint(point, mesh));
            } else {
                shifted.add(point);
            }
        }
        optimizeShifted(mesh);
        return shifted.toArray(new Point[shifted.size()]);
    }

    private static Point getNewShiftedPoint(Point point, NavigationMesh mesh) {     // TODO można zoptymalizować, żeby ustawiał PointContener dla tego, co pyta o ścieżkę.
        switch (mesh.getShiftDirections()[getIndexForShifts(point.getX() / Place.tileSize, point.getY() / Place.tileSize)]) {
            case TO_LEFT_TOP:
                return new Point(point.getX() - width, point.getY() - height);
            case TO_LEFT_BOTTOM:
                return new Point(point.getX() - width, point.getY() + height);
            case TO_RIGHT_BOTTOM:
                return new Point(point.getX() + width, point.getY() + height);
            case TO_RIGHT_TOP:
                return new Point(point.getX() + width, point.getY() - height);
            case TO_TOP:
                return new Point(point.getX(), point.getY() - height);
            case TO_LEFT:
                return new Point(point.getX() - width, point.getY());
            case TO_BOTTOM:
                return new Point(point.getX(), point.getY() + height);
            case TO_RIGHT:
                return new Point(point.getX() + width, point.getY());
        }
        return new Point(point.getX(), point.getY());
    }

    private static void optimizeShifted(NavigationMesh mesh) {
        Point next, previous;
        for (int i = 1; i < shifted.size() - 1; i++) {
            previous = shifted.get(i - 1);
            for (int j = shifted.size() - 1; j > i; j--) {
                next = shifted.get(j);
                if (canBeSkippedShifted(previous, next, mesh) ){
                    shifted.remove(i);
                    result.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    
    private static boolean canBeSkippedShifted(Point previous, Point next, NavigationMesh mesh) {
        if (!mesh.lineIntersectsMeshBounds(previous, next)) {
            setPolygonForTesting(previous, next);
            return isClearWay(poly, mesh.getCollisonPoints());
        }
        return false;
    }    
}
