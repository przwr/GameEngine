/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author WROBELP1
 */
public class Node {

    private final ArrayList<Node> neightbours = new ArrayList<>();
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

    private void setChild(Node child) {
        this.child = child;
    }

    public void addIfNotYetNeightbour(Node neightbour) {
        if (!neightbours.contains(neightbour)) {
            neightbours.add(neightbour);
        }
    }

    public void addNeightbour(Node neightbour) {
        neightbours.add(neightbour);
    }

    public void removeNeightbour(Node neightbour) {
        neightbours.remove(neightbour);
    }

    public void removeNeightbours(Collection<Node> neightbours) {
        neightbours.removeAll(neightbours);
    }

    public void reset() {
        parent = null;
        hCost = Integer.MAX_VALUE >> 1;
        gCost = Integer.MAX_VALUE >> 1;
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

    public int getHCost() {
        return hCost;
    }

    public void setHCost(int hCost) {
        this.hCost = hCost;
        calcF();
    }

    public int getGCost() {
        return gCost;
    }

    public void setGCost(int gCost) {
        this.gCost = parent != null ? parent.gCost + gCost : gCost;
        calcF();
    }

    public int getFCost() {
        return fCost;
    }

    public void setGHCosts(int gCost, int hCost) {
        this.hCost = hCost;
        this.gCost = parent != null ? parent.gCost + gCost : gCost;
        calcF();
    }

    private void calcF() {
        fCost = gCost + hCost;
    }

    public int getNeightboursSize() {
        return neightbours.size();
    }

    public List<Node> getNeightbours() {
        return Collections.unmodifiableList(neightbours);
    }
}
