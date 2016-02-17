package game.logic.betweenareapathfinding;

import engine.utilities.Point;

import java.util.ArrayList;

/**
 * Created by przemek on 15.02.16.
 */
public class AreaNode {


    private int area;
    private AreaNode parent;
    private ArrayList<Point> connection;
    private int hCost = Integer.MAX_VALUE >> 1, gCost = Integer.MAX_VALUE >> 1, fCost = Integer.MAX_VALUE;

    public AreaNode(int area) {
        this.area = area;
    }

    public AreaNode(int area, AreaNode parent) {
        this.area = area;
        this.parent = parent;
    }

    public AreaNode getParent() {
        return parent;
    }

    public void setParent(AreaNode parent) {
        this.parent = parent;
    }

    public int getHCost() {
        return hCost;
    }

    public void setHCost(int hCost) {
        this.hCost = hCost;
        calculateF();
    }

    public int getGCost() {
        return gCost;
    }

    public void setGCost(int gCost) {
        this.gCost = parent != null ? parent.gCost + gCost : gCost;
        calculateF();
    }

    public int getFCost() {
        return fCost;
    }

    public void setGHCosts(int gCost, int hCost) {
        this.hCost = hCost;
        this.gCost = parent != null ? parent.gCost + gCost : gCost;
        calculateF();
    }

    private void calculateF() {
        fCost = gCost + hCost;
    }

    public int getAreaIndex() {
        return area;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AreaNode && area == ((AreaNode) o).area;
    }

    @Override
    public int hashCode() {
        return area;
    }
}
