/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.jodk.lang.FastMath;

/**
 *
 * @author Wojtek
 */
public class RoundRectangle extends Figure {

    public static final int LEFT_TOP = 0, RIGHT_TOP = 1, RIGHT_BOTTOM = 2, LEFT_BOTTOM = 3, PREVIOUS = 0, CORNER = 1, NEXT = 2;
    private static final changer[] changers = new changer[4];
    private Corner[] corners = new Corner[4];
    private Polygon polygon = new Polygon();

    {
        changers[LEFT_TOP] = (int tileSize, int xChange, int yChange) -> {
            corners[LEFT_TOP].changes[PREVIOUS] = new Point(0, tileSize);
            corners[LEFT_TOP].changes[CORNER] = new Point(xChange, yChange);
            corners[LEFT_TOP].changes[NEXT] = new Point(tileSize, 0);
        };
        changers[RIGHT_TOP] = (int tileSize, int xChange, int yChange) -> {
            corners[RIGHT_TOP].changes[PREVIOUS] = new Point(0, 0);
            corners[RIGHT_TOP].changes[CORNER] = new Point(xChange, yChange);
            corners[RIGHT_TOP].changes[NEXT] = new Point(tileSize, tileSize);
        };
        changers[RIGHT_BOTTOM] = (int tileSize, int xChange, int yChange) -> {
            corners[RIGHT_BOTTOM].changes[PREVIOUS] = new Point(0, tileSize);
            corners[RIGHT_BOTTOM].changes[CORNER] = new Point(xChange, yChange);
            corners[RIGHT_BOTTOM].changes[NEXT] = new Point(tileSize, 0);
        };
        changers[LEFT_BOTTOM] = (int tileSize, int xChange, int yChange) -> {
            corners[LEFT_BOTTOM].changes[PREVIOUS] = new Point(tileSize, tileSize);
            corners[LEFT_BOTTOM].changes[CORNER] = new Point(xChange, yChange);
            corners[LEFT_BOTTOM].changes[NEXT] = new Point(0, 0);
        };
    }

    public static RoundRectangle createShadowHeight(int width, int height, int opticPropertiesType, int shadowHeight, GameObject owner) {
        return new RoundRectangle(-(width / 2), -(height / 2), width, height, opticPropertiesType, shadowHeight, owner);
    }

    public static RoundRectangle createShadowHeight(int xStart, int yStart, int width, int height, int opticPropertiesType, int shadowHeight, GameObject owner) {
        return new RoundRectangle(xStart, yStart, width, height, opticPropertiesType, shadowHeight, owner);
    }

    public static RoundRectangle create(int width, int height, int opticPropertiesType, GameObject owner) {
        return new RoundRectangle(-(width / 2), -(height / 2), width, height, opticPropertiesType, 0, owner);
    }

    public static RoundRectangle create(int xStart, int yStart, int width, int height, int opticPropertiesType, GameObject owner) {
        return new RoundRectangle(xStart, yStart, width, height, opticPropertiesType, 0, owner);
    }

    private RoundRectangle(int xStart, int yStart, int width, int height, int OpticPropertiesType, int shadowHeight, GameObject owner) {
        super(xStart, yStart, owner, OpticProperties.create(OpticPropertiesType, shadowHeight));
        this.width = width;
        this.height = height;
        initializeCorners();
        updatePoints();
        centralize();
    }

    private void initializeCorners() {
        corners[LEFT_TOP] = new Corner(new Point(getX(), getY()));
        corners[RIGHT_TOP] = new Corner(new Point(getX() + width, getY()));
        corners[RIGHT_BOTTOM] = new Corner(new Point(getX() + width, getY() + height));
        corners[LEFT_BOTTOM] = new Corner(new Point(getX(), getY() + height));
    }

    private void updatePoints() {
        points.clear();
        for (Corner corner : corners) {
            corner.getPoints().stream().filter((point) -> (!points.contains(point))).forEach((point) -> {
                points.add(point);
            });
        }
        points.trimToSize();
        polygon.reset();
        points.stream().forEach((point) -> {
            polygon.addPoint(point.getX(), point.getY());
        });
    }

    private void centralize() {
        xCenter = width / 2;
        yCenter = height / 2;
    }

    // Wciska się o tyle % jaki jest ułamek im większy ułamek, tym więcej się wciska
    public void pushLeftTopCorner(int tileSize, float xChange, float yChange) {
        if (xChange <= 1f && xChange >= 0 && yChange <= 1f && yChange >= 0) {
            corners[LEFT_TOP].push(LEFT_TOP, tileSize, FastMath.round(tileSize * xChange), FastMath.round(tileSize * yChange));
            getOwner().setSimpleLighting(false);
        }
    }

    public void pushRightTopCorner(int tileSize, float xChange, float yChange) {
        if (xChange <= 1f && xChange >= 0 && yChange <= 1f && yChange >= 0) {
            corners[RIGHT_TOP].push(RIGHT_TOP, tileSize, FastMath.round(tileSize * (1f - xChange)), FastMath.round(tileSize * yChange));
            getOwner().setSimpleLighting(false);
        }
    }

    public void pushRightBottomCorner(int tileSize, float xChange, float yChange) {
        if (xChange <= 1f && xChange >= 0 && yChange <= 1f && yChange >= 0) {
            corners[RIGHT_BOTTOM].push(RIGHT_BOTTOM, tileSize, FastMath.round(tileSize * (1f - xChange)), FastMath.round(tileSize * (1f - yChange)));
            getOwner().setSimpleLighting(false);
        }
    }

    public void pushLeftBottomCorner(int tileSize, float xChange, float yChange) {
        if (xChange <= 1f && xChange >= 0 && yChange <= 1f && yChange >= 0) {
            corners[LEFT_BOTTOM].push(LEFT_BOTTOM, tileSize, FastMath.round(tileSize * xChange), FastMath.round(tileSize * (1f - yChange)));
            getOwner().setSimpleLighting(false);
        }
    }

    @Override
    public boolean isCollideSingle(int x, int y, Figure figure) {
        if (figure instanceof RoundRectangle) {
            return rectangleCollsion(x, y, figure);
        } else if (figure instanceof RoundRectangle) {
            return quadrangleCollsion(x, y, figure);
        } else if (figure instanceof Circle) {
            return circleCollision(x, y, figure);
        } else if (figure instanceof Line) {
            return lineCollision(x, y, figure);
        }
        return false;
    }

    private boolean rectangleCollsion(int x, int y, Figure figure) {
        RoundRectangle rectangle = (RoundRectangle) figure;
        return ((getX(x) > rectangle.getX() && getX(x) - rectangle.getX() < rectangle.getWidth()) || (getX(x) <= rectangle.getX() && rectangle.getX() - getX(x) < width))
                && ((getY(y) > rectangle.getY() && getY(y) - rectangle.getY() < rectangle.getHeight()) || (getY(y) <= rectangle.getY() && rectangle.getY() - getY(y) < height));
    }

    private boolean quadrangleCollsion(int x, int y, Figure figure) {
        RoundRectangle rectangle = (RoundRectangle) figure;
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
            updatePoints();
        }
        return Collections.unmodifiableCollection(points);
    }

    private interface changer {

        void set(int tileSize, int xChange, int yChange);
    }

    private class Corner {

        private Point[] points = new Point[3];
        private Point[] changes;

        public Corner(Point point) {
            points[CORNER] = point;
        }

        public void push(int corner, int tileSize, int xChange, int yChange) {
            changes = new Point[3];
            changers[corner].set(tileSize, xChange, yChange);
            setPoints();
        }

        private void setPoints() {
            points[PREVIOUS] = new Point(getX() + changes[PREVIOUS].getX(), getY() + changes[PREVIOUS].getY());
            points[CORNER].set(getX() + changes[CORNER].getX(), getY() + +changes[CORNER].getY());
            points[NEXT] = new Point(getX() + changes[NEXT].getX(), getY() + changes[NEXT].getY());
        }

        public Collection<Point> getPoints() {
            if (points[PREVIOUS] == null) {
                return Arrays.asList(points[CORNER]);
            } else {
                return Arrays.asList(points);
            }
        }
    }
}
