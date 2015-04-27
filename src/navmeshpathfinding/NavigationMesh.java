/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.BlueArray;
import engine.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author WROBELP1
 */
public class NavigationMesh {

    private final Set<Triangle> mesh = new HashSet<>();
    private final Node[] sharedNodes = new Node[2];
    private final int notShared = 2;
    private final int tempNodeIndexes[] = new int[5], sharedNodesIndexes[] = new int[12];
    private int sharedNodeNumber, nrSharedNodes, nrConnections;
    private final Triangle[] connectedTriangles = new Triangle[3];
    BlueArray<Node> toRemove = new BlueArray<>();
    ArrayList<Bound> bounds = new ArrayList<>();

    public NavigationMesh(Point firstPoint, Point secondPoint, Point thirdPoint) {
        mesh.add(Triangle.createAndConnectNeightbours(firstPoint, secondPoint, thirdPoint));
    }

    public void addTriangle(Triangle triangleToAdd) {
        nrConnections = 0;
        connectedTriangles[0] = connectedTriangles[1] = connectedTriangles[2] = null;
        if (mesh.contains(triangleToAdd)) {
            return;
        }
        mesh.stream().forEach((triangle) -> {
            findAndMergeSharedPoints(triangle, triangleToAdd);
            connectIfPossible(triangle, triangleToAdd);
        });
        addAndSolveDependancesIfConnected(triangleToAdd);
    }

    private void findAndMergeSharedPoints(Triangle triangle, Triangle triangleToAdd) {
        nrSharedNodes = sharedNodeNumber = 0;
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
        nrSharedNodes++;
    }

    private void connectIfPossible(Triangle triangle, Triangle triangleToAdd) {
        if (nrSharedNodes == 2) {
            connectTriangles(triangle, triangleToAdd);
            nrConnections++;
        }
    }

    private void connectTriangles(Triangle triangle, Triangle triangleToAdd) {
        findNotSharedNode();
        ifNextConnectionSolveNodeDuplicates();
        addNeightboursFromNeightbours(triangleToAdd);
        saveConnectionNodes(triangle);
    }

    private void findNotSharedNode() {
        for (int i = 0; i < 3; i++) {
            if (tempNodeIndexes[0] != i && tempNodeIndexes[1] != i) {
                tempNodeIndexes[notShared] = i;
            }
        }
    }

    private void ifNextConnectionSolveNodeDuplicates() {
        if (nrConnections > 0) {
            int index = nrConnections - 1;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 2; j++) {
                    if (connectedTriangles[index].getPointFromNode(i).equals(sharedNodes[j].getPoint())) {
                        Node node = connectedTriangles[index].getNode(i);
                        for (Node neightbour : node.getNeightbours()) {
                            sharedNodes[j].addIfNotYetNeightbour(neightbour);
                        }
                        connectedTriangles[index].setNode(i, sharedNodes[j]);
                    }
                }
            }
        }
    }

    private void addNeightboursFromNeightbours(Triangle triangleToAdd) {
        for (int i = 0; i < 2; i++) {
            for (Node node : triangleToAdd.getNode(tempNodeIndexes[i]).getNeightbours()) {
                sharedNodes[i].addIfNotYetNeightbour(node);
            }
            triangleToAdd.setNode(tempNodeIndexes[i], sharedNodes[i]);
            Node node = triangleToAdd.getNode(tempNodeIndexes[notShared]);
            node.addIfNotYetNeightbour(sharedNodes[i]);

            sharedNodes[i].addIfNotYetNeightbour(node);
        }
    }

    private void saveConnectionNodes(Triangle triangle) {
        int indexModifier = 4 * nrConnections;
        connectedTriangles[nrConnections] = triangle;
        sharedNodesIndexes[indexModifier] = tempNodeIndexes[0];
        sharedNodesIndexes[indexModifier + 1] = tempNodeIndexes[1];
        sharedNodesIndexes[indexModifier + 2] = tempNodeIndexes[3];
        sharedNodesIndexes[indexModifier + 3] = tempNodeIndexes[4];
    }
//
//    private void deleteCopiedNeightbours(Triangle triangleToAdd) {
//        for (int i = 0; i < 3; i++) {
//            Node node = triangleToAdd.getNode(i);
//            for (Triangle triangle : connectedTriangles) {
//                if (triangle != null) {
//                    for (int j = 0; j < 3; j++) {
//                        triangle.getNode(j).removeNeightbour(node);
//                    }
//                }
//            }
//        }
//    }

    private void addAndSolveDependancesIfConnected(Triangle triangleToAdd) {
        if (nrConnections > 0) {
            solveDuplicatedNodesAndAddConnections(triangleToAdd);
            mesh.add(triangleToAdd);
            recalculateBounds();
        } else {
            System.out.println("Brak połączeń z siatką!");
        }
    }

    private void solveDuplicatedNodesAndAddConnections(Triangle triangleToAdd) {
        for (int i = 0; i < nrConnections; i++) {
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
            node.getNeightbours().stream().forEach((firstNeightbour) -> {
                node.getNeightbours().stream().forEach((secondNeightbour) -> {
                    chooseWhichOneToRemove(firstNeightbour, secondNeightbour);
                });
            });
            node.removeNeightbours(toRemove);
        }
    }

    private void chooseWhichOneToRemove(Node firstNeightbour, Node secondNeightbour) {
        if (firstNeightbour != secondNeightbour && firstNeightbour.getPoint().equals(secondNeightbour.getPoint())) {
            if (firstNeightbour.getNeightboursSize() > secondNeightbour.getNeightboursSize()) {
                toRemove.add(secondNeightbour);
            } else {
                toRemove.add(firstNeightbour);
            }
        }
    }

    private void recalculateBounds() {
        bounds.clear();
        mesh.stream().forEach((triangle) -> {
            checkforBoundsIfOnEdge(triangle);
        });
    }

    private void checkforBoundsIfOnEdge(Triangle triangle) {
        if (triangle.getConnectionsNumber() != 3) {
            for (int i = 0; i < 3; i++) {
                addIfABound(triangle, triangle.getNode(i));
            }
        }
    }

    private void addIfABound(Triangle triangle, Node firstNode) {
        for (int j = 0; j < 3; j++) {
            Node secondNode = triangle.getNode(j);
            if (firstNode != secondNode) {
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

    public Triangle isPointInMesh(Point point) {
        for (Triangle triangle : mesh) {
            if (triangle.isPointInTriangle(point)) {
                return triangle;
            }
        }
        return null;
    }

    public boolean lineIntersectsMeshBounds(Point start, Point end) {
        for (Bound bound : bounds) {
            if (lineIntersectsPointsNotLies(bound, start, end)) {
                return true;
            }
        }
        return false;
    }

    private boolean lineIntersectsPointsNotLies(Bound bound, Point start, Point end) {
        if (lineIntersects(bound, start, end)) {
            if (pointOnLine(bound, start) || pointOnLine(bound, end)) {
                if (isPointInMesh(new Point((start.getX() + end.getX()) >> 1, (start.getY() + end.getY()) >> 1)) == null) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean lineIntersects(Bound line, Point start, Point end) {
        return Line2D.linesIntersect(line.getStart().getX(), line.getStart().getY(), line.getEnd().getX(), line.getEnd().getY(),
                start.getX(), start.getY(), end.getX(), end.getY());
    }

    private boolean pointOnLine(Bound line, Point point) {
        return Line2D.ptSegDistSq(line.getStart().getX(), line.getStart().getY(), line.getEnd().getX(), line.getEnd().getY(),
                point.getX(), point.getY()) == 0.0;
    }

    public int size() {
        return mesh.size();
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
        mesh.stream().forEach((tr) -> {
            tr.reset();
        });
    }
}
