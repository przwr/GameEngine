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
import net.jodk.lang.FastMath;

/**
 *
 * @author Wojtek
 */
public class Quadrangle extends Figure {

    public static final int LEFT_TOP = 0, RIGHT_TOP = 1, RIGHT_BOTTOM = 2, LEFT_BOTTOM = 3;
    private Point[] corners = new Point[4];

    public static Quadrangle createShadowHeight(int width, int height, int opticPropertiesType, int shadowHeight, GameObject owner) {
        return new Quadrangle(-(width / 2), -(height / 2), width, height, opticPropertiesType, shadowHeight, owner);
    }

    public static Quadrangle createShadowHeight(int xStart, int yStart, int width, int height, int opticPropertiesType, int shadowHeight, GameObject owner) {
        return new Quadrangle(xStart, yStart, width, height, opticPropertiesType, shadowHeight, owner);
    }

    public static Quadrangle create(int width, int height, int opticPropertiesType, GameObject owner) {
        return new Quadrangle(-(width / 2), -(height / 2), width, height, opticPropertiesType, 0, owner);
    }

    public static Quadrangle create(int xStart, int yStart, int width, int height, int opticPropertiesType, GameObject owner) {
        return new Quadrangle(xStart, yStart, width, height, opticPropertiesType, 0, owner);
    }

    private Quadrangle(int xStart, int yStart, int width, int height, int OpticPropertiesType, int shadowHeight, GameObject owner) {
        super(xStart, yStart, owner, OpticProperties.create(OpticPropertiesType, shadowHeight));
        this.width = width;
        this.height = height;
        initializeCorners();
        points.add(new Point(getX() + corners[LEFT_TOP].getX(), getY() + corners[LEFT_TOP].getY()));
        points.add(new Point(getX() + corners[RIGHT_TOP].getX(), getY() + corners[RIGHT_TOP].getY()));
        points.add(new Point(getX() + corners[RIGHT_BOTTOM].getX(), getY() + corners[RIGHT_BOTTOM].getY()));
        points.add(new Point(getX() + corners[LEFT_BOTTOM].getX(), getY() + corners[LEFT_BOTTOM].getY()));
        points.trimToSize();
        centralize();
    }

    private void initializeCorners() {
        corners[LEFT_TOP] = new Point(0, 0);
        corners[RIGHT_TOP] = new Point(width, 0);
        corners[RIGHT_BOTTOM] = new Point(width, height);
        corners[LEFT_BOTTOM] = new Point(0, height);

    }

    private void centralize() {
        xCenter = width / 2;
        yCenter = height / 2;
    }

    // Wciska się o tyle % jaki jest ułamek im większy ułamek, tym więcej się wciska, maksymalnie można wcisnąć do połowy.
    public void pushAllCorners(float xChange, float yChange) {
        pushLeftTopCorner(xChange, yChange);
        pushRightTopCorner(xChange, yChange);
        pushRightBottomCorner(xChange, yChange);
        pushLeftBottomCorner(xChange, yChange);
    }

    public void pushLeftTopCorner(float xChange, float yChange) {
        if (xChange <= 0.5f && xChange >= 0 && yChange <= 0.5f && yChange >= 0) {
            corners[RIGHT_TOP].set(FastMath.round(width * xChange), FastMath.round(height * yChange));
            getOwner().setSimpleLighting(false);
        }
    }

    public void pushRightTopCorner(float xChange, float yChange) {
        if (xChange <= 0.5f && xChange >= 0 && yChange <= 0.5f && yChange >= 0) {
            corners[RIGHT_TOP].set(FastMath.round(width * (1f - xChange)), FastMath.round(height * yChange));
            getOwner().setSimpleLighting(false);
        }
    }

    public void pushRightBottomCorner(float xChange, float yChange) {
        if (xChange <= 0.5f && xChange >= 0 && yChange <= 0.5f && yChange >= 0) {
            corners[RIGHT_BOTTOM].set(FastMath.round(width * (1f - xChange)), FastMath.round(height * (1f - yChange)));
            getOwner().setSimpleLighting(false);
        }
    }

    public void pushLeftBottomCorner(float xChange, float yChange) {
        if (xChange <= 0.5f && xChange >= 0 && yChange <= 0.5f && yChange >= 0) {
            corners[LEFT_BOTTOM].set(FastMath.round(width * xChange), FastMath.round(height * (1f - yChange)));
            getOwner().setSimpleLighting(false);
        }
    }

    @Override
    public boolean isCollideSingle(int x, int y, Figure figure) {
        if (figure instanceof Quadrangle) {
            return rectangleCollsion(x, y, figure);
        } else if (figure instanceof Quadrangle) {
            return quadrangleCollsion(x, y, figure);
        } else if (figure instanceof Circle) {
            return circleCollision(x, y, figure);
        } else if (figure instanceof Line) {
            return lineCollision(x, y, figure);
        }
        return false;
    }

    private boolean rectangleCollsion(int x, int y, Figure figure) {
        Quadrangle rectangle = (Quadrangle) figure;
        return ((getX(x) > rectangle.getX() && getX(x) - rectangle.getX() < rectangle.getWidth()) || (getX(x) <= rectangle.getX() && rectangle.getX() - getX(x) < width))
                && ((getY(y) > rectangle.getY() && getY(y) - rectangle.getY() < rectangle.getHeight()) || (getY(y) <= rectangle.getY() && rectangle.getY() - getY(y) < height));
    }

    private boolean quadrangleCollsion(int x, int y, Figure figure) {
        Quadrangle rectangle = (Quadrangle) figure;
        return ((getX(x) > rectangle.getX() && getX(x) - rectangle.getX() < rectangle.getWidth()) || (getX(x) <= rectangle.getX() && rectangle.getX() - getX(x) < width))
                && ((getY(y) > rectangle.getY() && getY(y) - rectangle.getY() < rectangle.getHeight()) || (getY(y) <= rectangle.getY() && rectangle.getY() - getY(y) < height));
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
        return (yPosition < 0 && getY(y) - circle.getY() <= circle.getRadius()) || (yPosition > 0 && circle.getY() - getY(y) - height <= circle.getRadius());
    }

    private boolean lineCollision(int x, int y, Figure figure) {
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
        if (isMobile()) {
            points.get(LEFT_TOP).set(getX() + corners[LEFT_TOP].getX(), getY() + corners[LEFT_TOP].getY());
            points.get(RIGHT_TOP).set(getX() + corners[RIGHT_TOP].getX(), getY() + corners[RIGHT_TOP].getY());
            points.get(RIGHT_BOTTOM).set(getX() + corners[RIGHT_BOTTOM].getX(), getY() + corners[RIGHT_BOTTOM].getY());
            points.get(LEFT_BOTTOM).set(getX() + corners[LEFT_BOTTOM].getX(), getY() + corners[LEFT_BOTTOM].getY());
        }
        return Collections.unmodifiableCollection(points);
    }

    private interface bottomSetter {

        void set();
    }
}
