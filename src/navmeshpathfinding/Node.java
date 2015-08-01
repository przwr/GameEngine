/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author WROBELP1
 */
public class Node {

    private static final int halfMaxValue = Integer.MAX_VALUE >> 1;
    private final ArrayList<Node> neighbours = new ArrayList<>();
    private Point point;
    private Node parent, child;
    private int hCost = Integer.MAX_VALUE >> 1, gCost = Integer.MAX_VALUE >> 1, fCost = Integer.MAX_VALUE;

    public Node(Point pt) {
        this.point = pt;
    }

    public void setParentMakeChild(Node parent) {
        this.parent = parent;
        parent.setChild(this);
    }

    public void addIfNotYetNeighbour(Node neighbour) {
        if (!neighbours.contains(neighbour)) {
            this.neighbours.add(neighbour);
        }
    }

    public void addNeighbour(Node neighbour) {
        neighbours.add(neighbour);
    }

    public void removeNeighbour(Node neighbour) {
        neighbours.remove(neighbour);
    }

    public void removeNeighbours(Collection<Node> neighbours) {
        this.neighbours.removeAll(neighbours);
    }

    public void reset() {
        parent = null;
        child = null;
        hCost = halfMaxValue;
        gCost = halfMaxValue;
        fCost = Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return point.toString();
    }

    public int getX() {
        return point.getX();
    }

    public int getY() {
        return point.getY();
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Node getParent() {
        return parent;
    }

    public Node getChild() {
        return child;
    }

    private void setChild(Node child) {
        this.child = child;
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

    public int getNeighboursSize() {
        return neighbours.size();
    }

    public List<Node> getNeighbours() {
        return neighbours;
    }
}
