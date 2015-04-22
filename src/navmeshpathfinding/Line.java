/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Methods;
import engine.Point;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author przemek
 */
public class Line implements Comparable<Object> {

    Point[] points = new Point[2];
    int length;

    public Line(Point point1, Point point2) {
        points[0] = point1;
        points[1] = point2;
        calculateLength();
    }

    public int getLength() {
        return length;
    }

    public List<Point> getPoints() {
        return Arrays.asList(points);
    }

    public void setPoints(Point point1, Point point2) {
        points[0] = point1;
        points[1] = point2;
        calculateLength();
    }

    private void calculateLength() {
        length = Methods.pointDistanceSimple2(points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY());
    }

    public Point getStart() {
        return points[0];
    }

    public Point getEnd() {
        return points[1];
    }

    @Override
    public String toString() {
        return points[0].toString() + points[1].toString() + " - " + length;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return (points[0].equals(line.points[0]) && points[1].equals(line.points[1])) || (points[1].equals(line.points[0]) && points[0].equals(line.points[1]));
    }

    @Override
    public int hashCode() {
        int point1Hashcode = points[0].hashCode();
        int point2Hashcode = points[1].hashCode();
        return 83 * (point1Hashcode * point1Hashcode + point2Hashcode * point2Hashcode);
    }

    @Override
    public int compareTo(Object o) {
        return length - ((Line) o).length;
    }

}
