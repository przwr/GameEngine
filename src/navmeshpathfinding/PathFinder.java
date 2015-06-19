/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import collision.Figure;
import engine.BlueArray;
import engine.Point;
import game.place.Place;
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
    private static Point destinationPoint = new Point(), startPoint = new Point(), temp = new Point();
    private static int width, height;
    private static Triangle startTriangle, endTriangle;
    private static Node destination, beginning;
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
        createBeginningAndAdjacent(mesh);
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

    private static void createBeginningAndAdjacent(NavigationMesh mesh) {
        beginning = new Node(destinationPoint);
        beginning.setGHCosts(0, countH(destinationPoint, startPoint));
        closedList.add(beginning);
        for (int i = 0; i < 3; i++) {
            Node node = startTriangle.getNode(i);
            calculateAndAddToOpenList(node, beginning, mesh);
        }
    }

    private static void calculateAndAddToOpenListIfFits(Node node, Node parent, NavigationMesh mesh) {
        if (isFittingPoint(node.getPoint(),parent.getPoint(), mesh)) {
            calculateAndAddToOpenList(node, parent, mesh);
        }
    }

    private static void calculateAndAddToOpenList(Node node, Node parent, NavigationMesh mesh) {
        node.setParentMakeChild(parent);
        node.setGHCosts(countG(node.getPoint(), parent.getPoint()), countH(node.getPoint(), startPoint));
        openList.add(node);
    }

    private static boolean isFittingPoint(Point point, Point parent, NavigationMesh mesh) {
    	// TO DO - fix this Method! - do using Polygon!
    	    	
        return !(mesh.linesIntersectsMeshBounds(getShiftValues(parent, mesh, width * 2, height * 2), getShiftValues(point, mesh, width * 2, height * 2)));
    }

    private static Point getShiftValues(Point point, NavigationMesh mesh, int width, int height) {
        switch (mesh.getShiftDirections()[getIndexForShifts(point.getX() / Place.tileSize, point.getY() / Place.tileSize)]) {
            case TO_LEFT_TOP:
                temp.set(-width, -height);
                break;
            case TO_LEFT_BOTTOM:
                temp.set(-width, height);
                break;
            case TO_RIGHT_BOTTOM:
                temp.add(width, height);
                break;
            case TO_RIGHT_TOP:
                temp.set(width, -height);
                break;
            case TO_TOP:
                temp.set(0, -height);
                break;
            case TO_LEFT:
                temp.set(-width, 0);
                break;
            case TO_BOTTOM:
                temp.set(0, height);
                break;
            case TO_RIGHT:
                temp.set(width, 0);
                break;
            default:
                temp.set(0, 0);
                break;
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
                if (canBeSkipped(previous, next, mesh) && canBeSkipped(result.get(i - 1), result.get(j), mesh)) {
                    shifted.remove(i);
                    result.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    private static boolean canBeSkipped(Point previous, Point next, NavigationMesh mesh) {
        return !mesh.lineIntersectsMeshBounds(previous, next);
    }
}
