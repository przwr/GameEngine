/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utilities;

/**
 * @author przemek
 */
public class PointedValue {

    private int x, y, value;

    public PointedValue() {
    }

    public PointedValue(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public boolean equals(Object test) {
        if (test instanceof PointedValue) {
            PointedValue point = (PointedValue) test;
            if (x == point.x && y == point.y && value == point.value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        hash = 79 * hash + this.value;
        return hash;
    }

    @Override
    public String toString() {
        return "[" + x + " : " + y + " - " + value + "]";
    }
}
