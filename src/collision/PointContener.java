/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;

/**
 *
 * @author przemek
 */
public class PointContener {

    private static byte INITIAL_POINT_COUNT = 8;
    private Point[] points;
    private int pointCount;

    public PointContener() {
        points = new Point[INITIAL_POINT_COUNT];
        for (int i = 0; i < INITIAL_POINT_COUNT; i++) {
            points[i] = new Point();
        }
    }

    public void add(int x, int y) {
        ensureCapacity(1);
        points[pointCount].setX(x);
        points[pointCount].setY(y);
        pointCount++;
    }

    private static int caps, maxSize;

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
            System.out.println("Capacity of PointContener enlarged " + caps + " times to maxSize: " + maxSize);
        }
    }

    public Point get(int i) {
        return points[i];
    }

    public void remove(int x, int y) {
        for (int i = 0; i < pointCount; i++) {
            if (points[i].getX() == x && points[i].getY() == y) {
                pointCount--;
                points[i].setX(points[pointCount].getX());
                points[i].setY(points[pointCount].getY());
                break;
            }
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
