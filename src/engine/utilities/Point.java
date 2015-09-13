/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utilities;

/**
 * @author przemek
 */
public class Point implements Comparable<Object> {

    private int x;
    private int y;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getFirst() {
        return x;
    }

    public int getSecond() {
        return y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void copy(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public void add(int x, int y) {
        this.x += x;
        this.y += y;
    }

    @Override
    public int compareTo(Object o) {
        return ((y - ((Point) o).y) << 13) + (x - ((Point) o).x);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Point && y == ((Point) o).y && x == ((Point) o).x;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        return hash;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
