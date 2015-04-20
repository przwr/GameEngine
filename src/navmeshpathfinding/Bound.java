/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Point;

/**
 *
 * @author WROBELP1
 */
public class Bound {

    private final Point start;
    private final Point end;

    public Bound(Point startPoint, Point endPoint) {
        this.start = startPoint;
        this.end = endPoint;
    }

    @Override
    public String toString() {
        return "Linia: " + start.toString() + " - " + end.toString();
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }
}
