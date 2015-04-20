/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Point;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 *
 * @author WROBELP1
 */
public class PathFinder {

    private static final ArrayList<Node> closedList = new ArrayList<>();
    private static final PriorityQueue<Node> openList = new PriorityQueue<>(24, (Node n1, Node n2) -> n1.getFCost() - n2.getFCost());
    private static Point startPoint, endPoint;
    private static Triangle startTriangle, endTriangle;
    private static Node destination;

    public static Node findPath(NavigationMesh mesh, Point start, Point end) {
        if (mesh != null) {
            startPoint = start;
            endPoint = end;
            startTriangle = mesh.isPointInMesh(startPoint);
            endTriangle = mesh.isPointInMesh(endPoint);
        }
        return findSolution(mesh);
    }

    private static Node findSolution(NavigationMesh mesh) {
        destination = null;
        if (startTriangle != null && endTriangle != null) {
            if (startTriangle == endTriangle) {
                destination = inOneTriangle();
            } else {
                destination = aStar(mesh);
            }
        }
        printResult(destination);
        return destination;
    }

    private static Node inOneTriangle() {
        Node beginning = new Node(startPoint);
        destination = new Node(endPoint);
        destination.setParentMakeChild(beginning);
        return destination;
    }

    private static Node aStar(NavigationMesh mesh) {
        readyVariables(mesh);
        createBeginningAndAdjacent();
        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            closedList.add(currentNode);
            if (isInEndTriangle(currentNode)) {
                optimize(mesh);
                break;
            }
            keepLooking(currentNode);
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
        Node beginning = new Node(startPoint);
        beginning.setGHCosts(0, countH(startPoint, endPoint));
        closedList.add(beginning);
        for (int i = 0; i < 3; i++) {
            Node node = startTriangle.getNode(i);
            calculateAndAddToOpenList(node, beginning);
        }
    }

    private static void calculateAndAddToOpenList(Node node, Node parent) {
        node.setParentMakeChild(parent);
        node.setGHCosts(countG(node.getPoint(), parent.getPoint()), countH(node.getPoint(), endPoint));
        openList.add(node);
    }

    private static boolean isInEndTriangle(Node currentNode) {
        boolean isFound = false;
        for (int i = 0; i < 3; i++) {
            Node node = endTriangle.getNode(i);
            if (currentNode == node) {
                destination = new Node(endPoint);
                calculateAndAddToOpenList(destination, currentNode);
                isFound = true;
            }
        }
        return isFound;
    }

    private static void keepLooking(Node currentNode) {
        currentNode.getNeightbours().stream().filter((node) -> (!closedList.contains(node))).forEach((node) -> {
            if (openList.contains(node)) {
                changeIfBetterPath(node, currentNode);
            } else {
                calculateAndAddToOpenList(node, currentNode);
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

    private static void optimize(NavigationMesh mesh) {
        Node previous, current = destination;
        while (current.getParent() != null && current.getParent().getParent() != null) {
            previous = current.getParent().getParent();
            while (previous != null) {
                if (canBeSkipped(current, previous, mesh)) {
                    current.setParentMakeChild(previous);
                }
                previous = previous.getParent();
            }
            current = current.getParent();
        }
    }

    private static boolean canBeSkipped(Node startNode, Node endNode, NavigationMesh mesh) {
        return !mesh.lineIntersectsMeshBounds(startNode.getPoint(), endNode.getPoint());
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

    private static void printResult(Node destiation) {
        if (destiation != null) {
            printSolution(destiation);
        } else {
            System.out.println("Nie znaleziono rozwiązania!");
        }
    }

    private static void printSolution(Node destination) {
        System.out.println("Rozwiązanie(" + destination.getFCost() + "): ");
        Node currentNode = destination;
        while (currentNode.getParent() != null) {
            currentNode = currentNode.getParent();
        }
        while (currentNode != null) {
            System.out.println(currentNode);
            currentNode = currentNode.getChild();
        }
    }
}
