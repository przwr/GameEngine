/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.navmeshpathfinding.navigationmesh;

import engine.utilities.Methods;
import engine.utilities.Point;

import java.util.HashSet;
import java.util.Set;

/**
 * @author przemek
 */
class Line implements Comparable<Object> {

    private final Set<Line> joined = new HashSet<>();
    private Point start, end;
    private int length;

    public Line() {
    }

    public Line(Point point1, Point point2) {
        start = point1;
        end = point2;
    }

    public int getLength() {
        return length;
    }

    public void setPoints(Point point1, Point point2) {
        start = point1;
        end = point2;
    }

    public void calculateLength() {
        length = Methods.pointDistanceSimple(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public void addLine(Line line) {
        joined.add(line);
    }

    public void removeLine(Line line) {
        joined.remove(line);
    }

    public Set<Line> getJoined() {
        return joined;
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point point1) {
        start = point1;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point point2) {
        end = point2;
    }

    public boolean isHorizontal() {
        return start.getY() == end.getY();
    }

    public boolean isVertical() {
        return start.getX() == end.getX();
    }

    public double getDirectional() {
        return ((double) (end.getY() - start.getY())) / (double) (end.getX() - start.getX());
    }

    @Override
    public String toString() {
        return start.toString() + end.toString() + " - " + length;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return (start.equals(line.start) && end.equals(line.end)) || (end.equals(line.start) && start.equals(line.end));
    }

    @Override
    public int hashCode() {
        int point1Hashcode = start.hashCode();
        int point2Hashcode = end.hashCode();
        return 83 * (point1Hashcode * point1Hashcode + point2Hashcode * point2Hashcode);
    }

    @Override
    public int compareTo(Object o) {
        return length - ((Line) o).length;
    }

    public void setXStart(int x) {
        start.setX(x);
    }

    public void setXEnd(int x) {
        end.setX(x);
    }

    public void setYStart(int y) {
        start.setY(y);
    }

    public void setYEnd(int y) {
        end.setY(y);
    }

}
