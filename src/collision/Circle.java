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
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Wojtek
 */
public class Circle extends Figure {

    private final static int PRECISION = 16;
    private final int radius;

    public static Circle create(int xStart, int yStart, int radius, int OpticPropertiesType, GameObject owner) {
        return new Circle(xStart, yStart, radius, OpticPropertiesType, owner);
    }

    public static Circle create(int radius, int OpticPropertiesType, GameObject owner) {
        return new Circle(0, 0, radius, OpticPropertiesType, owner);
    }

    private Circle(int xStart, int yStart, int radius, int OpticPropertiesType, GameObject owner) {
        super(xStart, yStart, owner, OpticProperties.create(OpticPropertiesType));
        this.radius = radius;
        for (int i = 0; i < PRECISION; i++) {
            points.add(new Point(0, 0));
        }
        points.trimToSize();
        centralize();
    }

    private void centralize() {
        width = height = 0;
        xCentr = 2 * radius;
        yCentr = 2 * radius;
    }

    @Override
    public boolean isCollideSingle(int x, int y, Figure figure) {
        if (figure instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) figure;
            int xp = ((super.getX(x) < rectangle.getX() ? -1 : 1) + (super.getX(x) <= (rectangle.getX() + rectangle.getWidth()) ? -1 : 1)) / 2;
            int yp = ((super.getY(y) < rectangle.getY() ? -1 : 1) + (super.getY(y) <= (rectangle.getY() + rectangle.getHeight()) ? -1 : 1)) / 2;
            if (xp == 0 && yp == 0) {
                return true;
            }
            if (xp != 0 && yp != 0) {
                int xtmp = (xp + 1) / 2;
                int ytmp = (yp + 1) / 2;
                return (Methods.PointDistance(super.getX(x), super.getY(y), rectangle.getPoint(xtmp + 2 * ytmp).getX(), rectangle.getPoint(xtmp + 2 * ytmp).getY()) <= radius);
            }
            if (yp == 0 && ((xp < 0 && rectangle.getX() - super.getX(x) <= radius) || (yp > 0 && super.getX(x) - rectangle.getX() - rectangle.getWidth() <= radius))) {
                return true;
            }
            if ((yp < 0 && rectangle.getY() - super.getY(y) <= radius) || (yp > 0 && super.getY(y) - rectangle.getY() - rectangle.getHeight() <= radius)) {
                return true;
            }
        } else if (figure instanceof Circle) {
            Circle circle = (Circle) figure;
            if (Methods.PointDistance(super.getX(x), super.getY(y), circle.getX(), circle.getY()) <= (radius + circle.getRadius())) {
                return true;
            }
        } else if (figure instanceof Line) {
            Line l = (Line) figure;
            return (Line2D.ptSegDist(l.getX(), l.getY(), l.getX() + l.getXk(), l.getY() + l.getYk(), super.getX(x), super.getY(y)) <= radius);
        }
        return false;
    }

    @Override
    public Collection<Point> getPoints() {
        int step = 360 / PRECISION;
        for (int i = 0; i < PRECISION; i++) {
            points.get(i).set((int) Methods.xRadius(i * step, radius), (int) Methods.yRadius(i * step, radius));
        }
        return points;
    }

    public int getRadius() {
        return radius;
    }
}
