/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.navmeshpathfinding.navigationmesh;

import engine.utilities.BlueArray;
import engine.utilities.Point;
import net.jodk.lang.FastMath;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author WROBELP1
 */
public class NavigationMesh {

    final ArrayList<Bound> bounds = new ArrayList<>();
    private final Set<Triangle> mesh = new HashSet<>();
    private final int NOT_SHARED = 2;
    private final Node[] sharedNodes = new Node[2];
    private final int tempNodeIndexes[] = new int[5], sharedNodesIndexes[] = new int[12];
    private final Triangle[] connectedTriangles = new Triangle[3];
    private final List<Point> collisionPoints;
    private final byte[] shiftDirections;

    private final BlueArray<Node> toRemove = new BlueArray<>();
    private int sharedNodeNumber, sharedNodesNumber, connectionsNumber;

    public NavigationMesh(Point firstPoint, Point secondPoint, Point thirdPoint, List<Point> collisionPoints, byte[] shiftDirections) {
        mesh.add(Triangle.createAndConnectNeighbours(firstPoint, secondPoint, thirdPoint));
        this.collisionPoints = collisionPoints;
        this.shiftDirections = shiftDirections;
    }

    public void addTriangle(Triangle triangleToAdd) {
        connectionsNumber = 0;
        connectedTriangles[0] = connectedTriangles[1] = connectedTriangles[2] = null;
        if (mesh.contains(triangleToAdd)) {
            return;
        }
        mesh.stream().forEach((triangle) -> {
            findAndMergeSharedPoints(triangle, triangleToAdd);
            connectIfPossible(triangle, triangleToAdd);
        });
        addAndSolveDependenciesIfConnected(triangleToAdd);
    }

    private void findAndMergeSharedPoints(Triangle triangle, Triangle triangleToAdd) {
        sharedNodesNumber = sharedNodeNumber = 0;
        sharedNodes[0] = sharedNodes[1] = null;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (triangle.getPointFromNode(i).equals(triangleToAdd.getPointFromNode(j))) {
                    saveSharedNodes(triangle, i, j);
                    triangleToAdd.getNode(j).setPoint(triangle.getPointFromNode(i));
                }
            }
        }
    }

    private void saveSharedNodes(Triangle triangle, int i, int j) {
        sharedNodes[sharedNodeNumber] = triangle.getNode(i);
        tempNodeIndexes[sharedNodeNumber + 3] = i;
        tempNodeIndexes[sharedNodeNumber] = j;
        sharedNodeNumber = 1;
        sharedNodesNumber++;
    }

    private void connectIfPossible(Triangle triangle, Triangle triangleToAdd) {
        if (sharedNodesNumber == 2) {
            connectTriangles(triangle, triangleToAdd);
            connectionsNumber++;
        }
    }

    private void connectTriangles(Triangle triangle, Triangle triangleToAdd) {
        findNotSharedNode();
        ifNextConnectionSolveNodeDuplicates();
        addNeighboursFromNeighbours(triangleToAdd);
        saveConnectionNodes(triangle);
    }

    private void findNotSharedNode() {
        for (int i = 0; i < 3; i++) {
            if (tempNodeIndexes[0] != i && tempNodeIndexes[1] != i) {
                tempNodeIndexes[NOT_SHARED] = i;
            }
        }
    }

    private void ifNextConnectionSolveNodeDuplicates() {
        if (connectionsNumber > 0) {
            int index = connectionsNumber - 1;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 2; j++) {
                    if (connectedTriangles[index].getPointFromNode(i).equals(sharedNodes[j].getPoint())) {
                        Node node = connectedTriangles[index].getNode(i);
                        for (Node neighbour : node.getNeighbours()) {
                            sharedNodes[j].addIfNotYetNeighbour(neighbour);
                        }
                        connectedTriangles[index].setNode(i, sharedNodes[j]);
                    }
                }
            }
        }
    }

    private void addNeighboursFromNeighbours(Triangle triangleToAdd) {
        for (int i = 0; i < 2; i++) {
            for (Node node : triangleToAdd.getNode(tempNodeIndexes[i]).getNeighbours()) {
                sharedNodes[i].addIfNotYetNeighbour(node);
            }
            triangleToAdd.setNode(tempNodeIndexes[i], sharedNodes[i]);
            Node node = triangleToAdd.getNode(tempNodeIndexes[NOT_SHARED]);
            node.addIfNotYetNeighbour(sharedNodes[i]);

            sharedNodes[i].addIfNotYetNeighbour(node);
        }
    }

    private void saveConnectionNodes(Triangle triangle) {
        int indexModifier = 4 * connectionsNumber;
        connectedTriangles[connectionsNumber] = triangle;
        sharedNodesIndexes[indexModifier] = tempNodeIndexes[0];
        sharedNodesIndexes[indexModifier + 1] = tempNodeIndexes[1];
        sharedNodesIndexes[indexModifier + 2] = tempNodeIndexes[3];
        sharedNodesIndexes[indexModifier + 3] = tempNodeIndexes[4];
    }

    private void addAndSolveDependenciesIfConnected(Triangle triangleToAdd) {
        if (connectionsNumber > 0) {
            solveDuplicatedNodesAndAddConnections(triangleToAdd);
            mesh.add(triangleToAdd);
            recalculateBounds();
        } else {
            System.out.println("Brak połączeń z siatką!");
        }
    }

    private void solveDuplicatedNodesAndAddConnections(Triangle triangleToAdd) {
        for (int i = 0; i < connectionsNumber; i++) {
            solveDuplicatedNodes(connectedTriangles[i]);
            int indexModifier = 4 * i;
            connectedTriangles[i].addConnection(new Connection(triangleToAdd, sharedNodesIndexes[indexModifier], sharedNodesIndexes[indexModifier + 1]));
            triangleToAdd.addConnection(new Connection(connectedTriangles[i], sharedNodesIndexes[indexModifier + 2], sharedNodesIndexes[indexModifier + 3]));
        }
    }

    private void solveDuplicatedNodes(Triangle triangle) {
        for (int i = 0; i < 3; i++) {
            toRemove.clear();
            Node node = triangle.getNode(i);
            node.getNeighbours().stream().forEach((firstNeighbour) -> node.getNeighbours().stream().forEach((secondNeighbour) -> chooseWhichOneToRemove(firstNeighbour, secondNeighbour)));
            node.removeNeighbours(toRemove);
        }
    }

    private void chooseWhichOneToRemove(Node firstNeighbour, Node secondNeighbour) {
        if (firstNeighbour != secondNeighbour && firstNeighbour.getPoint().equals(secondNeighbour.getPoint())) {
            if (firstNeighbour.getNeighboursSize() > secondNeighbour.getNeighboursSize()) {
                toRemove.add(secondNeighbour);
            } else {
                toRemove.add(firstNeighbour);
            }
        }
    }

    private void recalculateBounds() {
        bounds.clear();
        mesh.stream().forEach(this::checkForBoundsIfOnEdge);
    }

    private void checkForBoundsIfOnEdge(Triangle triangle) {
        if (triangle.getConnectionsNumber() != 3) {
            for (int i = 0; i < 3; i++) {
                addIfABound(triangle, triangle.getNode(i));
            }
        }
    }

    private void addIfABound(Triangle triangle, Node firstNode) {
        for (int j = 0; j < 3; j++) {
            Node secondNode = triangle.getNode(j);
            if (firstNode.getPoint() != secondNode.getPoint()) {
                boolean notContains = true;
                if (triangle.containsConnection(firstNode, secondNode)) {
                    notContains = false;
                }
                if (notContains) {
                    addIfNotYetBound(firstNode, secondNode);
                }
            }
        }
    }

    private void addIfNotYetBound(Node firstNode, Node secondNode) {
        boolean notContains = true;
        for (Bound bound : bounds) {
            if ((bound.getStart() == firstNode.getPoint() && bound.getEnd() == secondNode.getPoint())
                    || (bound.getStart() == secondNode.getPoint() && bound.getEnd() == firstNode.getPoint())) {
                notContains = false;
            }
        }
        if (notContains) {
            bounds.add(new Bound(firstNode.getPoint(), secondNode.getPoint()));
        }
    }

    public Triangle[] getPathBase(Point startPoint, Point destinationPoint, Triangle[] pathBase) {
        boolean start = true, end = true;
        for (Triangle triangle : mesh) {
            if (start && triangle.isPointInTriangle(startPoint)) {
                pathBase[0] = triangle;
                start = false;
            }
            if (end && triangle.isPointInTriangle(destinationPoint)) {
                pathBase[1] = triangle;
                end = false;
            }
        }
        return pathBase;
    }

    private Triangle getTriangleForPoint(Point point) {
        for (Triangle triangle : mesh) {
            if (triangle.isPointInTriangle(point)) {
                return triangle;
            }
        }
        return null;
    }

    public boolean lineIntersectsMeshBounds(int xStart, int yStart, int xEnd, int yEnd) {
        return bounds.stream().anyMatch((bound) -> (lineIntersectsPointsNotLies(bound, xStart, yStart, xEnd, yEnd)));
    }

    public boolean lineIntersectsMeshBounds(Point start, Point end) {
        return bounds.stream().anyMatch((bound) -> (lineIntersectsPointsNotLies(bound, start.getX(), start.getY(), end.getX(), end.getY())));
    }

    public boolean linesIntersectsMeshBounds(Point start, Point end) {
        return bounds.stream().anyMatch((bound) -> (anyLineIntersects(bound, start, end)));
    }

    private boolean anyLineIntersects(Bound bound, Point start, Point end) {
        return end.getX() != 0 && lineIntersectsPointsNotLies(bound, start.getX() + (int) FastMath.signum(end.getX()), start.getY(), start.getX() + end.getX(), start.getY()) || end.getX() != 0 && lineIntersects(bound, start.getX(), start.getY() + (int) FastMath.signum(end.getY()), start.getX(), start.getY() + end.getY());
    }

    private boolean lineIntersectsPointsNotLies(Bound bound, int xStart, int yStart, int xEnd, int yEnd) {
        if (lineIntersects(bound, xStart, yStart, xEnd, yEnd)) {
            if (pointOnLine(bound, xStart, yStart) || pointOnLine(bound, xEnd, yEnd)) {
                if (getTriangleForPoint(new Point((xStart + xEnd) >> 1, (yStart + yEnd) >> 1)) == null) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean lineIntersects(Bound line, int xStart, int yStart, int xEnd, int yEnd) {
        return Line2D.linesIntersect(line.getStart().getX(), line.getStart().getY(), line.getEnd().getX(), line.getEnd().getY(),
                xStart, yStart, xEnd, yEnd);
    }

    private boolean pointOnLine(Bound line, int x, int y) {
        return Line2D.ptSegDistSq(line.getStart().getX(), line.getStart().getY(), line.getEnd().getX(), line.getEnd().getY(), x, y) == 0.0;
    }

    public int size() {
        return mesh.size();
    }

    public List<Point> getCollisionPoints() {
        return collisionPoints;
    }

    public byte[] getShiftDirections() {
        return shiftDirections;
    }

    public Triangle[] getTriangles() {
        Triangle[] triangles = new Triangle[mesh.size()];
        int i = 0;
        for (Triangle triangle : mesh) {
            triangles[i++] = triangle;
        }
        return triangles;
    }

    public void reset() {
        mesh.stream().forEach(Triangle::reset);
    }

    public ArrayList<Bound> getBounds() {
        return bounds;
    }
}
