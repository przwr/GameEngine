/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Wojtek
 */
public class Rectangle extends Figure {

    public static Rectangle createShadowHeight(int width, int height, int opticPropertiesType, int shadowHeight, GameObject owner) {
        return new Rectangle(-(width / 2), -(height / 2), width, height, opticPropertiesType, shadowHeight, owner);
    }

    public static Rectangle createShadowHeight(int xStart, int yStart, int width, int height, int opticPropertiesType, int shadowHeight, GameObject owner) {
        return new Rectangle(xStart, yStart, width, height, opticPropertiesType, shadowHeight, owner);
    }

    public static Rectangle create(int width, int height, int opticPropertiesType, GameObject owner) {
        return new Rectangle(-(width / 2), -(height / 2), width, height, opticPropertiesType, 0, owner);
    }

    public static Rectangle create(int xStart, int yStart, int width, int height, int opticPropertiesType, GameObject owner) {
        return new Rectangle(xStart, yStart, width, height, opticPropertiesType, 0, owner);
    }

    private Rectangle(int xStart, int yStart, int width, int height, int OpticPropertiesType, int shadowHeight, GameObject owner) {
        super(xStart, yStart, owner, OpticProperties.create(OpticPropertiesType, shadowHeight));
        this.width = width;
        this.height = height;
        points.add(new Point(getX(), getY()));
        points.add(new Point(getX(), getY() + height));
        points.add(new Point(getX() + width, getY() + height));
        points.add(new Point(getX() + width, getY()));
        points.trimToSize();
        centralize();
    }

    private void centralize() {
        xCentr = width / 2;
        yCentr = height / 2;
    }

    @Override
    public boolean isCollideSingle(int x, int y, Figure figure) {
        if (figure instanceof Rectangle) {
            return rectangleCollsion(x, y, figure);
        } else if (figure instanceof Circle) {
            return circleCollision(x, y, figure);
        } else if (figure instanceof Line) {
            return lineCollision(x, y, figure);
        }
        return false;
    }

    private boolean rectangleCollsion(int x, int y, Figure figure) {
        Rectangle rectangle = (Rectangle) figure;
        if (((getX(x) > rectangle.getX() && getX(x) - rectangle.getX() < rectangle.getWidth()) || (getX(x) <= rectangle.getX() && rectangle.getX() - getX(x) < width))
                && ((getY(y) > rectangle.getY() && getY(y) - rectangle.getY() < rectangle.getHeight()) || (getY(y) <= rectangle.getY() && rectangle.getY() - getY(y) < height))) {
            return true;
        }
        return false;
    }

    private boolean circleCollision(int x, int y, Figure figure) {
        Circle circle = (Circle) figure;
        int xPosition = ((getX(x) < circle.getX() ? -1 : 1) + (circle.getX() <= (getX(x) + width) ? -1 : 1)) / 2;
        int yPosition = ((getY(y) < circle.getY() ? -1 : 1) + (circle.getY() <= (getY(y) + height) ? -1 : 1)) / 2;
        if (xPosition == 0 && yPosition == 0) {
            return true;
        }
        getPoints();
        if (xPosition != 0 && yPosition != 0 && Methods.pointDistance(circle.getX(), circle.getY(), points.get((xPosition + 1) + 3 * (yPosition + 1)).getX(),
                points.get((xPosition + 1) + 3 * (yPosition + 1)).getY()) <= circle.getRadius()) {
            return true;
        }
        if (yPosition == 0 && ((xPosition < 0 && getX(x) - circle.getX() <= circle.getRadius()) || (yPosition > 0 && circle.getX() - getX(x) - width <= circle.getRadius()))) {
            return true;
        }
        if ((yPosition < 0 && getY(y) - circle.getY() <= circle.getRadius()) || (yPosition > 0 && circle.getY() - getY(y) - height <= circle.getRadius())) {
            return true;
        }
        return false;
    }

    private boolean lineCollision(int x, int y, Figure figure) {        // Dlaczego getX(x) a nie zwyczajnie getX()
        Line line = (Line) figure;
        Point[] list = {new Point(getX(x), getY(y)),
            new Point(getX(x), getY(y) + height),
            new Point(getX(x) + width, getY(y) + height),
            new Point(getX(x) + width, getY(y))};
        int[] linePoints = {line.getX(), line.getY(), line.getX() + line.getXVector(), line.getY() + line.getYVector()};
        return (Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[0].getX(), list[0].getY(), list[1].getX(), list[1].getY())
                || Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[1].getX(), list[1].getY(), list[2].getX(), list[2].getY())
                || Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[2].getX(), list[2].getY(), list[3].getX(), list[3].getY())
                || Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[3].getX(), list[3].getY(), list[0].getX(), list[0].getY()));
    }

    @Override
    public Collection<Point> getPoints() {
        points.get(0).set(getX(), getY());
        points.get(1).set(getX(), getY() + height);
        points.get(2).set(getX() + width, getY() + height);
        points.get(3).set(getX() + width, getY());
        return Collections.unmodifiableCollection(points);
    }
}
