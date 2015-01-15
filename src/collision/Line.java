/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import game.gameobject.GameObject;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Wojtek
 */
public class Line extends Figure {

    private final int xk; // WHAT ?
    private final int yk;
    private boolean startDoubled;
    private boolean endDoubled;

    public static Line create(int dx, int dy, GameObject owner) {
        return new Line(0, 0, dx, dy, owner);
    }

    public static Line create(int xStart, int yStart, int dx, int dy, GameObject owner) {
        return new Line(xStart, yStart, dx, dy, owner);
    }

    private Line(int xStart, int yStart, int dx, int dy, GameObject owner) {
        super(xStart, yStart, owner, OpticProperties.create(OpticProperties.IN_SHADE_NO_SHADOW));     /// do poprawy
        xk = dx;
        yk = dy;
        points.add(new Point(-1, -1));
        points.add(new Point(-1, -1));
        points.trimToSize();
        centralize();
    }

    public Line(int dx, int dy, GameObject owner) {
        super(0, 0, owner, OpticProperties.create(OpticProperties.IN_SHADE_NO_SHADOW));     /// do poprawy
        xk = dx;
        yk = dy;
        centralize();
    }

    private void centralize() {
        width = xk;
        height = yk;
        xCentr = xk / 2;
        yCentr = yk / 2;
    }

    @Override
    public Collection<Point> getPoints() {
        points.clear();
        if (startDoubled) {
            points.get(0).set(-1, -1);
        } else {
            points.get(0).set(super.getX(), super.getY());
        }
        if (endDoubled) {
            points.get(1).set(-1, -1);
        } else {
            points.get(1).set(super.getX() + xk, super.getY() + yk);
        }
        return points;
    }

    @Override
    public boolean isCollideSingle(int x, int y, Figure figure) {
        if (figure instanceof Rectangle) {
            ArrayList<Point> points = (ArrayList< Point>) figure.getPoints();
            int[] w = {super.getX(x), super.getY(y), super.getX(x) + xk, super.getY(y) + yk};
            return (Line2D.linesIntersect(w[0], w[1], w[2], w[3], points.get(0).getX(), points.get(0).getY(), points.get(1).getX(), points.get(1).getY())
                    || Line2D.linesIntersect(w[0], w[1], w[2], w[3], points.get(1).getX(), points.get(1).getY(), points.get(2).getX(), points.get(2).getY())
                    || Line2D.linesIntersect(w[0], w[1], w[2], w[3], points.get(2).getX(), points.get(2).getY(), points.get(3).getX(), points.get(3).getY())
                    || Line2D.linesIntersect(w[0], w[1], w[2], w[3], points.get(3).getX(), points.get(3).getY(), points.get(0).getX(), points.get(0).getY()));
        } else if (figure instanceof Circle) {
            Circle l = (Circle) figure;
            return (Line2D.ptSegDist(super.getX(x), super.getY(y), super.getX(x) + xk, super.getY(y) + yk, l.getX(), l.getY()) <= l.getRadius());
        } else if (figure instanceof Line) {
            Line l = (Line) figure;
            return (Line2D.linesIntersect(super.getX(x), super.getY(y), super.getX(x) + xk, super.getY(y) + yk,
                    l.getX(), l.getY(), l.getX() + l.getXk(), l.getY() + l.getYk()));
        }
        return false;
    }

    public int getXk() {
        return xk;
    }

    public int getYk() {
        return yk;
    }

    public void ifStartReturn(boolean a) {
        startDoubled = a;
    }

    public void ifEndReturn(boolean a) {
        endDoubled = a;
    }
}
