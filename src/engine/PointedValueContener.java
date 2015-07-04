/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

/**
 *
 * @author przemek
 */
public class PointedValueContener {

    private static final byte INITIAL_POINT_COUNT = 8;
    private PointedValue[] points;
    private int pointCount;

    public PointedValueContener() {
        points = new PointedValue[INITIAL_POINT_COUNT];
        for (int i = 0; i < INITIAL_POINT_COUNT; i++) {
            points[i] = new PointedValue();
        }
    }

    public PointedValueContener(int pointCount) {
        points = new PointedValue[pointCount];
        for (int i = 0; i < pointCount; i++) {
            points[i] = new PointedValue();
        }
    }

    public void add(int x, int y, int value) {
        ensureCapacity(1);
        points[pointCount].setX(x);
        points[pointCount].setY(y);
        points[pointCount].setValue(value);
        pointCount++;
    }

    private static int caps, maxSize;

    private void ensureCapacity(int capacity) {
        if (pointCount + capacity > points.length) {
            PointedValue[] tempPoints = new PointedValue[(int) (1.5 * points.length)];
            System.arraycopy(points, 0, tempPoints, 0, points.length);
            points = tempPoints;
            for (int i = pointCount; i < points.length; i++) {
                points[i] = new PointedValue();
            }
            caps++;
            if (points.length > maxSize) {
                maxSize = points.length;
            }
            System.out.println("Capacity of PointedValueContener enlarged " + caps + " times to maxSize: " + maxSize);
        }
    }

    public PointedValue get(int i) {
        return points[i];
    }

    public void remove(int index) {
        if (index < pointCount && index >= 0) {
            pointCount--;
            PointedValue temp = points[index];
            for (int i = index; i < pointCount; i++) {
                points[i] = points[i + 1];
            }
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
