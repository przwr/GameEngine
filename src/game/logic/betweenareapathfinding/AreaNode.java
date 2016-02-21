package game.logic.betweenareapathfinding;

import engine.utilities.Point;

/**
 * Created by przemek on 15.02.16.
 */
public class AreaNode {

    private AreaNode parent;
    private AreaConnection connection;
    private int hCost = Integer.MAX_VALUE >> 1, gCost = Integer.MAX_VALUE >> 1, fCost = Integer.MAX_VALUE;

    public AreaNode(AreaConnection connection) {
        this.connection = connection;
    }

    public AreaNode(AreaConnection connection, AreaNode parent) {
        this.connection = connection;
        this.parent = parent;
    }

    public int getConnectedAreaIndex(int areaIndex) {
        return connection.getFirstAreaIndex() == areaIndex ? connection.getSecondAreaIndex() : connection.getFirstAreaIndex();
    }

    public Point getCentralPoint() {
        return connection.getCentralPoint();
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

    public boolean connectsWithArea(int area) {
        return connection.getFirstAreaIndex() == area || connection.getSecondAreaIndex() == area ;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AreaNode && connection == ((AreaNode) o).connection;
    }

    @Override
    public int hashCode() {
        return connection.hashCode();
    }

    public AreaConnection getConnection() {
        return connection;
    }
}
