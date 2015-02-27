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
import java.util.Collection;

/**
 *
 * @author Wojtek
 */
public class Circle extends Figure {

    private final static int PRECISION = 16;
    private final static int step = 360 / PRECISION;
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
            points.add(new Point((int) Methods.xRadius(i * step, radius), (int) Methods.yRadius(i * step, radius)));
        }
        points.trimToSize();
        centralize();
    }

    private void centralize() {
        width = height = 0;
        xCenter = 2 * radius;
        yCenter = 2 * radius;
    }

    @Override
    public boolean isCollideSingle(int x, int y, Figure figure) {
        if (figure instanceof Rectangle) {
            return rectangleCollision(x, y, figure);
        } else if (figure instanceof RoundRectangle) {
            return roundRectangleCollision(x, y, figure);
        } else if (figure instanceof Circle) {
            return circleCollision(x, y, figure);
        } else if (figure instanceof Line) {
            return lineCollision(x, y, figure);
        }
        return false;
    }

    private boolean rectangleCollision(int x, int y, Figure figure) {
        Rectangle rectangle = (Rectangle) figure;
        int xPosition = ((getX(x) < rectangle.getX() ? -1 : 1) + (getX(x) <= (rectangle.getX() + rectangle.getWidth()) ? -1 : 1)) / 2;
        int yPosition = ((getY(y) < rectangle.getY() ? -1 : 1) + (getY(y) <= (rectangle.getY() + rectangle.getHeight()) ? -1 : 1)) / 2;
        if (xPosition == 0 && yPosition == 0) {
            return true;
        }
        if (xPosition != 0 && yPosition != 0) {
            int xtmp = (xPosition + 1) / 2;
            int ytmp = (yPosition + 1) / 2;
            return (Methods.pointDistance(getX(x), getY(y), rectangle.getPoint(xtmp + 2 * ytmp).getX(), rectangle.getPoint(xtmp + 2 * ytmp).getY()) <= radius);
        }
        if (yPosition == 0 && ((xPosition < 0 && rectangle.getX() - getX(x) <= radius) || (yPosition > 0 && getX(x) - rectangle.getX() - rectangle.getWidth() <= radius))) {
            return true;
        }
        return (yPosition < 0 && rectangle.getY() - getY(y) <= radius) || (yPosition > 0 && getY(y) - rectangle.getY() - rectangle.getHeight() <= radius);
    }

    private boolean roundRectangleCollision(int x, int y, Figure figure) { //TO DO
        RoundRectangle roundRectangle = (RoundRectangle) figure;
        int xPosition = ((getX(x) < roundRectangle.getX() ? -1 : 1) + (getX(x) <= (roundRectangle.getX() + roundRectangle.getWidth()) ? -1 : 1)) / 2;
        int yPosition = ((getY(y) < roundRectangle.getY() ? -1 : 1) + (getY(y) <= (roundRectangle.getY() + roundRectangle.getHeight()) ? -1 : 1)) / 2;
        if (xPosition == 0 && yPosition == 0) {
            return true;
        }
        if (xPosition != 0 && yPosition != 0) {
            int xtmp = (xPosition + 1) / 2;
            int ytmp = (yPosition + 1) / 2;
            return (Methods.pointDistance(getX(x), getY(y), roundRectangle.getPoint(xtmp + 2 * ytmp).getX(), roundRectangle.getPoint(xtmp + 2 * ytmp).getY()) <= radius);
        }
        if (yPosition == 0 && ((xPosition < 0 && roundRectangle.getX() - getX(x) <= radius) || (yPosition > 0 && getX(x) - roundRectangle.getX() - roundRectangle.getWidth() <= radius))) {
            return true;
        }
        return (yPosition < 0 && roundRectangle.getY() - getY(y) <= radius) || (yPosition > 0 && getY(y) - roundRectangle.getY() - roundRectangle.getHeight() <= radius);
    }

    private boolean circleCollision(int x, int y, Figure figure) {
        Circle circle = (Circle) figure;
        return Methods.pointDistance(getX(x), getY(y), circle.getX(), circle.getY()) <= (radius + circle.getRadius());
    }

    private boolean lineCollision(int x, int y, Figure figure) {
        Line line = (Line) figure;
        return (Line2D.ptSegDist(line.getX(), line.getY(), line.getX() + line.getXVector(), line.getY() + line.getYVector(), getX(x), getY(y)) <= radius);
    }

    @Override
    public Collection<Point> getPoints() {
        if (isMobile()) {
            for (int i = 0; i < PRECISION; i++) {
                points.get(i).set((int) Methods.xRadius(i * step, radius), (int) Methods.yRadius(i * step, radius));
            }
        }
        return points;
    }

    public int getRadius() {
        return radius;
    }
}
