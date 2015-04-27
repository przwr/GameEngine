/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Point;

/**
 *
 * @author WROBELP1
 */
public class Triangle {

    private final static int MIN = 0, MAX = 1;
    private final static float EPSILON = 0.001f;
    private final Node[] nodes = new Node[3];
    private final Point[] bounds;
    private final Connection[] connections = new Connection[3];
    private int connectionsNumber = 0;

    public static Triangle create(Point firstPoint, Point secondPoint, Point thirdPoint) {
        return new Triangle(firstPoint, secondPoint, thirdPoint);
    }

    public static Triangle createAndConnectNeightbours(Point firstPoint, Point secondPoint, Point thirdPoint) {
        Triangle triangle = new Triangle(firstPoint, secondPoint, thirdPoint);
        triangle.connectNeightbours();
        return triangle;
    }

    private Triangle(Point firstPoint, Point secondPoint, Point thirdPoint) {
        nodes[0] = new Node(firstPoint);
        nodes[1] = new Node(secondPoint);
        nodes[2] = new Node(thirdPoint);
        bounds = calculateBounds();
    }

    private Point[] calculateBounds() {
        Point[] tempBounds = new Point[2];
        tempBounds[MIN] = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        tempBounds[MAX] = new Point(0, 0);
        for (Node node : nodes) {
            tempBounds = findMinAndMaxValues(node.getPoint(), tempBounds);
        }
        return tempBounds;
    }

    private static Point[] findMinAndMaxValues(Point point, Point[] bounds) {
        int minX = Math.min(point.getX(), bounds[MIN].getX());
        int minY = Math.min(point.getY(), bounds[MIN].getY());
        int maxX = Math.max(point.getX(), bounds[MAX].getX());
        int maxY = Math.max(point.getY(), bounds[MAX].getY());
        bounds[MIN].set(minX, minY);
        bounds[MAX].set(maxX, maxY);
        return bounds;
    }

    private void connectNeightbours() {
        nodes[0].addNeightbour(nodes[1]);
        nodes[0].addNeightbour(nodes[2]);
        nodes[1].addNeightbour(nodes[0]);
        nodes[1].addNeightbour(nodes[2]);
        nodes[2].addNeightbour(nodes[0]);
        nodes[2].addNeightbour(nodes[1]);
    }

    public void addConnection(Connection connection) {
            connections[connectionsNumber++] = connection;
        }

    public boolean isPointInTriangle(Point point) {
        if (isOutOfBoundsToEpsilon(point)) {
            return false;
        }
        return baricentricPointInTriangle(point);
    }

    private boolean isOutOfBoundsToEpsilon(Point point) {
        return (point.getX() < bounds[MIN].getX() - EPSILON)
                || (point.getX() - EPSILON > bounds[MAX].getX())
                || (point.getY() < bounds[MIN].getY() - EPSILON)
                || (point.getY() - EPSILON > bounds[MAX].getY());
    }

    private boolean baricentricPointInTriangle(Point point) {
        float denominator = getDenominator();
        float a = ((nodes[1].getY() - nodes[2].getY()) * (point.getX() - nodes[2].getX())
                + ((nodes[2].getX() - nodes[1].getX()) * (point.getY() - nodes[2].getY()))) / denominator;
        float b = ((nodes[2].getY() - nodes[0].getY()) * (point.getX() - nodes[2].getX())
                + ((nodes[0].getX() - nodes[2].getX()) * (point.getY() - nodes[2].getY()))) / denominator;
        float c = 1 - a - b;
        return isInZeroOneRangeToEpsilon(a) && isInZeroOneRangeToEpsilon(b) && isInZeroOneRangeToEpsilon(c);
    }

    private float getDenominator() {
        return (float) ((nodes[1].getY() - nodes[2].getY()) * (nodes[0].getX() - nodes[2].getX()))
                + ((nodes[2].getX() - nodes[1].getX()) * (nodes[0].getY() - nodes[2].getY()));
    }

    private static boolean isInZeroOneRangeToEpsilon(float number) {
        return -EPSILON <= number && number <= 1 + EPSILON;
    }

    public boolean containsConnection(Node firstNode, Node secondNode) {
        for (int i = 0; i < getConnectionsNumber(); i++) {
            if ((connections[i].getNode(0) == firstNode && connections[i].getNode(1) == secondNode)
                    || (connections[i].getNode(0) == secondNode && connections[i].getNode(1) == firstNode)) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        for (Node node : nodes) {
            node.reset();
        }
    }

    @Override
    public String toString() {
        return "(" + nodes[0].getX() + "," + nodes[0].getY() + ")(" + nodes[1].getX() + "," + nodes[1].getY() + ")(" + nodes[2].getX() + "," + nodes[2].getY() + ")";
    }

    public Point getPointFromNode(int index) {
        return nodes[index].getPoint();
    }

    public Node getNode(int index) {
        return nodes[index];
    }

    public void setNode(int index, Node node) {
        nodes[index] = node;
    }

    public Connection getConnection(int i) {
        return connections[i];
    }

    public int getConnectionsNumber() {
        return connectionsNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Triangle)) {
            return false;
        }
        int sharedPoints = 0, checkedPoints = 0;
        Point point1, point2;
        for (Node node : nodes) {
            point1 = node.getPoint();
            for (Node node2 : ((Triangle) o).nodes) {
                point2 = node2.getPoint();
                if (point1.equals(point2)) {
                    sharedPoints++;
                    break;
                }
            }
            checkedPoints++;
            if (sharedPoints != checkedPoints) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int point1Hashcode = nodes[0].getPoint().hashCode();
        int point2Hashcode = nodes[1].getPoint().hashCode();
        int point3Hashcode = nodes[2].getPoint().hashCode();
        return 83 * (point1Hashcode * point1Hashcode + point2Hashcode * point2Hashcode + point3Hashcode * point3Hashcode);
    }
}
