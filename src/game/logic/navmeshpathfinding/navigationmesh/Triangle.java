/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.navmeshpathfinding.navigationmesh;

import engine.utilities.Methods;
import engine.utilities.Point;
import net.jodk.lang.FastMath;

/**
 * @author WROBELP1
 */
public class Triangle {

    private final static int MIN = 0;
    private final static int MAX = 1;
    private final static float EPSILON = 0.001f;
    private final Node[] nodes = new Node[3];
    private final Point[] bounds;
    private final Connection[] connections = new Connection[3];
    private int connectionsNumber = 0;

    private Triangle(Point firstPoint, Point secondPoint, Point thirdPoint) {
        nodes[0] = new Node(firstPoint);
        nodes[1] = new Node(secondPoint);
        nodes[2] = new Node(thirdPoint);
        bounds = calculateBounds();
    }

    public static Triangle create(Point firstPoint, Point secondPoint, Point thirdPoint) {
        return new Triangle(firstPoint, secondPoint, thirdPoint);
    }

    public static Triangle createAndConnectNeighbours(Point firstPoint, Point secondPoint, Point thirdPoint) {
        Triangle triangle = new Triangle(firstPoint, secondPoint, thirdPoint);
        triangle.connectNeighbours();
        return triangle;
    }

    private static Point[] findMinAndMaxValues(Point point, Point[] bounds) {
        int minX = FastMath.min(point.getX(), bounds[MIN].getX());
        int minY = FastMath.min(point.getY(), bounds[MIN].getY());
        int maxX = FastMath.max(point.getX(), bounds[MAX].getX());
        int maxY = FastMath.max(point.getY(), bounds[MAX].getY());
        bounds[MIN].set(minX, minY);
        bounds[MAX].set(maxX, maxY);
        return bounds;
    }

    private static boolean isInZeroOneRangeToEpsilon(float number) {
        return -EPSILON <= number && number <= 1 + EPSILON;
    }

    private Point[] calculateBounds() {
        Point[] tempBounds = new Point[2];
        tempBounds[MIN] = new Point(nodes[0].getX(), nodes[0].getY());
        tempBounds[MAX] = new Point(-1, -1);
        for (Node node : nodes) {
            tempBounds = findMinAndMaxValues(node.getPoint(), tempBounds);
        }
        return tempBounds;
    }

    private void connectNeighbours() {
        nodes[0].addNeighbour(nodes[1]);
        nodes[0].addNeighbour(nodes[2]);
        nodes[1].addNeighbour(nodes[0]);
        nodes[1].addNeighbour(nodes[2]);
        nodes[2].addNeighbour(nodes[0]);
        nodes[2].addNeighbour(nodes[1]);
    }

    public void addConnection(Connection connection) {
        connections[connectionsNumber++] = connection;
    }

    public boolean isPointInTriangle(int x, int y) {
        return !isOutOfBoundsToEpsilon(x, y) && barycentricPointInTriangle(x, y);
    }

    private boolean isOutOfBoundsToEpsilon(int x, int y) {
        return (x < bounds[MIN].getX() - EPSILON)
                || (x - EPSILON > bounds[MAX].getX())
                || (y < bounds[MIN].getY() - EPSILON)
                || (y - EPSILON > bounds[MAX].getY());
    }

    private boolean barycentricPointInTriangle(int x, int y) {
        float denominator = getDenominator();
        float a = ((nodes[1].getY() - nodes[2].getY()) * (x - nodes[2].getX())
                + ((nodes[2].getX() - nodes[1].getX()) * (y - nodes[2].getY()))) / denominator;
        float b = ((nodes[2].getY() - nodes[0].getY()) * (x - nodes[2].getX())
                + ((nodes[0].getX() - nodes[2].getX()) * (y - nodes[2].getY()))) / denominator;
        float c = 1 - a - b;
        return isInZeroOneRangeToEpsilon(a) && isInZeroOneRangeToEpsilon(b) && isInZeroOneRangeToEpsilon(c);
    }

    private float getDenominator() {
        return (float) ((nodes[1].getY() - nodes[2].getY()) * (nodes[0].getX() - nodes[2].getX()))
                + ((nodes[2].getX() - nodes[1].getX()) * (nodes[0].getY() - nodes[2].getY()));
    }

    public boolean containsConnection(Node firstNode, Node secondNode) {
        for (int i = 0; i < getConnectionsNumber(); i++) {
            if ((connections[i].getNode(0).getPoint() == firstNode.getPoint() && connections[i].getNode(1).getPoint() == secondNode.getPoint())
                    || (connections[i].getNode(0).getPoint() == secondNode.getPoint() && connections[i].getNode(1).getPoint() == firstNode.getPoint())) {
                return true;
            }
        }
        return false;
    }

    public int getPointDistance(Point point) {
        int xCenter = 0;
        int yCenter = 0;
        for (Node node : nodes) {
            xCenter += node.getX();
            yCenter += node.getY();
        }
        return Methods.pointDistanceSimple2(xCenter / 3, yCenter / 3, point.getX(), point.getY());
    }

    public void reset() {
        for (Node node : nodes) {
            node.reset();
        }
    }

    @Override
    public String toString() {
        return "(" + nodes[0].getX() + "," + nodes[0].getY() + ")(" + nodes[1].getX() + "," + nodes[1].getY() + ")(" + nodes[2].getX() + "," + nodes[2].getY
                () + ")";
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

    public Point[] getBounds() {
        return bounds;
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
