/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import game.Methods;
import game.gameobject.GameObject;
import java.awt.geom.Line2D;

/**
 *
 * @author Wojtek
 */
public class Rectangle extends Figure {

    private final int w;
    private final int h;

    public Rectangle(int xs, int ys, int w, int h, int shadowH, GameObject owner) {  // Środek prostokąta (xs,ys) dla (0,0) jest w lewym górnym rogu prostokąta
        super(xs, ys, owner);
        this.w = w;
        this.h = h;
        ShadowHeight = shadowH;
        this.type = 1;
        centralize();
    }

    public Rectangle(int w, int h, GameObject owner) {  //Środek w środku
        super(w / 2, h / 2, owner);
        this.w = w;
        this.h = h;
        this.type = 1;
        centralize();
    }

    @Override
    public final void centralize() {
        width = w;
        height = h;
        xCentr = w / 2;
        yCentr = h / 2;
    }

    @Override
    public Point[] listPoints() {
        Point[] list = {new Point(super.getX(), super.getY()),
            new Point(super.getX(), super.getY() + h),
            new Point(super.getX() + w, super.getY() + h),
            new Point(super.getX() + w, super.getY())};
        return list;
    }

    @Override
    public boolean ifCollideSngl(int x, int y, Figure f) {

        if (f.getType() == 1) {         // Z Prostokątem

            Rectangle r = (Rectangle) f;
            if (((super.getX(x) > r.getX() && super.getX(x) - r.getX() < r.getWidth()) || (super.getX(x) <= r.getX() && r.getX() - super.getX(x) < w))
                    && ((super.getY(y) > r.getY() && super.getY(y) - r.getY() < r.getHeight()) || (super.getY(y) <= r.getY() && r.getY() - super.getY(y) < h))) {
                return true;
            }

        } else if (f.getType() == 2) {  // Z Okręgiem

            Circle r = (Circle) f;
            int xp = ((super.getX(x) < r.getX() ? -1 : 1) + (r.getX() <= (super.getX(x) + w) ? -1 : 1)) / 2;
            int yp = ((super.getY(y) < r.getY() ? -1 : 1) + (r.getY() <= (super.getY(y) + h) ? -1 : 1)) / 2;

            if (xp == 0 && yp == 0) {
                return true;
            }

            Point[] list = listPoints();

            if (xp != 0 && yp != 0) {
                if (Methods.PointDistance(r.getX(), r.getY(), list[(xp + 1) + 3 * (yp + 1)].getX(), list[(xp + 1) + 3 * (yp + 1)].getY()) <= r.getRadius()) {
                    return true;
                }
            }
            if (yp == 0 && ((xp < 0 && super.getX(x) - r.getX() <= r.getRadius()) || (yp > 0 && r.getX() - super.getX(x) - w <= r.getRadius()))) {
                return true;
            }

            if ((yp < 0 && super.getY(y) - r.getY() <= r.getRadius()) || (yp > 0 && r.getY() - super.getY(y) - h <= r.getRadius())) {
                return true;
            }

        } else if (f.getType() == 3) { // Z Linią

            Line r = (Line) f;
            Point[] list = {new Point(super.getX(x), super.getY(y)),
                new Point(super.getX(x), super.getY(y) + h),
                new Point(super.getX(x) + w, super.getY(y) + h),
                new Point(super.getX(x) + w, super.getY(y))};
            int[] ln = {r.getX(), r.getY(), r.getX() + r.getXk(), r.getY() + r.getYk()};
            return (Line2D.linesIntersect(ln[0], ln[1], ln[2], ln[3], list[0].getX(), list[0].getY(), list[1].getX(), list[1].getY())
                    || Line2D.linesIntersect(ln[0], ln[1], ln[2], ln[3], list[1].getX(), list[1].getY(), list[2].getX(), list[2].getY())
                    || Line2D.linesIntersect(ln[0], ln[1], ln[2], ln[3], list[2].getX(), list[2].getY(), list[3].getX(), list[3].getY())
                    || Line2D.linesIntersect(ln[0], ln[1], ln[2], ln[3], list[3].getX(), list[3].getY(), list[0].getX(), list[0].getY()));

        }
        return false;
    }

    @Override
    public int getWidth() {
        return w;
    }

    @Override
    public int getHeight() {
        return h;
    }
}
