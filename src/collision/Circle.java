/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import engine.Methods;
import game.gameobject.GameObject;
import java.awt.geom.Line2D;

/**
 *
 * @author Wojtek
 */
public class Circle extends Figure {

    private final int r;

    public Circle(int xs, int ys, int r, GameObject owner) {
        super(xs, ys, owner);
        this.r = r;
        this.type = 2;
        centralize();
    }

    public Circle(int r, GameObject owner) {
        super(0, 0, owner);
        this.r = r;
        this.type = 2;
        centralize();
    }

    @Override
    public void centralize() {
        width = 0;
        height = 0;
        xCentr = 2 * r;
        yCentr = 2 * r;
    }

    @Override
    public Point[] listPoints() {
        int root = (int) ((double) r * 0.70711);
        Point[] list = {new Point(super.getX() + r, super.getY()),
            new Point(super.getX(), super.getY() + r),
            new Point(super.getX() - r, super.getY()),
            new Point(super.getX(), super.getY() - r),
            new Point(super.getX() + root, super.getY()),
            new Point(super.getX(), super.getY() + root),
            new Point(super.getX() - root, super.getY()),
            new Point(super.getX(), super.getY() - root),};
        return list;
    }

    @Override
    public boolean ifCollideSngl(int x, int y, Figure f) {

        if (f.getType() == 1) {         // Z Prostokątem

            Rectangle t = (Rectangle) f;
            int xp = ((super.getX(x) < t.getX() ? -1 : 1) + (super.getX(x) <= (t.getX() + t.getWidth()) ? -1 : 1)) / 2;
            int yp = ((super.getY(y) < t.getY() ? -1 : 1) + (super.getY(y) <= (t.getY() + t.getHeight()) ? -1 : 1)) / 2;

            if (xp == 0 && yp == 0) {
                return true;
            }

            Point[] list = t.listPoints();

            if (xp != 0 && yp != 0) {
                int xtmp = (xp + 1) / 2;
                int ytmp = (yp + 1) / 2;
                return (Methods.PointDistance(super.getX(x), super.getY(y), list[xtmp + 2 * ytmp].getX(), list[xtmp + 2 * ytmp].getY()) <= r);
            }
            if (yp == 0 && ((xp < 0 && t.getX() - super.getX(x) <= r) || (yp > 0 && super.getX(x) - t.getX() - t.getWidth() <= r))) {
                return true;
            }

            if ((yp < 0 && t.getY() - super.getY(y) <= r) || (yp > 0 && super.getY(y) - t.getY() - t.getHeight() <= r)) {
                return true;
            }

        } else if (f.getType() == 2) {  // Z Okręgiem

            Circle t = (Circle) f;
            if (Methods.PointDistance(super.getX(x), super.getY(y), t.getX(), t.getY()) <= (r + t.getRadius())) {
                return true;
            }

        } else if (f.getType() == 3) { // Z Linią

            Line l = (Line) f;
            return (Line2D.ptSegDist(l.getX(), l.getY(), l.getX() + l.getXk(), l.getY() + l.getYk(), super.getX(x), super.getY(y)) <= r);

        }
        return false;
    }

    public int getRadius() {
        return r;
    }
}
