/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utilities;

/**
 * @author przemek
 */
public class PointContainer {

    private static final byte INITIAL_POINT_COUNT = 8;
    private static int caps, maxSize;
    private Point[] points;
    private int pointCount;

    public PointContainer() {
        points = new Point[INITIAL_POINT_COUNT];
        for (int i = 0; i < INITIAL_POINT_COUNT; i++) {
            points[i] = new Point();
        }
    }

    public PointContainer(int pointCount) {
        points = new Point[pointCount];
        for (int i = 0; i < pointCount; i++) {
            points[i] = new Point();
        }
    }

    public void add(int x, int y) {
        ensureCapacity(1);
        points[pointCount].setX(x);
        points[pointCount].setY(y);
        pointCount++;
    }

    public void add(Point point) {
        ensureCapacity(1);
        points[pointCount].setX(point.getX());
        points[pointCount].setY(point.getY());
        pointCount++;
    }

    private void ensureCapacity(int capacity) {
        if (pointCount + capacity > points.length) {
            Point[] tempPoints = new Point[(int) (1.5 * points.length)];
            System.arraycopy(points, 0, tempPoints, 0, points.length);
            points = tempPoints;
            for (int i = pointCount; i < points.length; i++) {
                points[i] = new Point();
            }
            caps++;
            if (points.length > maxSize) {
                maxSize = points.length;
            }
            System.out.println("Capacity of PointContainer enlarged " + caps + " times to maxSize: " + maxSize);
        }
    }

    public Point get(int i) {
        return points[i];
    }

    public void remove(int index) {
        if (index < pointCount && index >= 0) {
            pointCount--;
            Point temp = points[index];
            System.arraycopy(points, index + 1, points, index, pointCount - index);
            points[pointCount] = temp;
        }
    }

    public boolean isEmpty() {
        return pointCount == 0;
    }

    public void clear() {
        pointCount = 0;
    }

    public int size() {
        return pointCount;
    }
}
