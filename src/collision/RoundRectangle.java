/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.place.Place;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Wojtek
 */
public class RoundRectangle extends Figure {

    public static final int LEFT_TOP = 0, LEFT_BOTTOM = 1, RIGHT_BOTTOM = 2, RIGHT_TOP = 3;
    public static final int PREVOIUS = 0, CORNER = 1, NEXT = 2;
    private static final changer[] changers = new changer[4];
    private static final pusher[] pushers = new pusher[4];
    private static final geter[] geters = new geter[4];
    private Corner[] corners = new Corner[4];
    private ArrayList<Point> bottomPoints = new ArrayList<>(3);
    private boolean concave, triangular, bottomRounded;

    {
        changers[LEFT_TOP] = (int xChange, int yChange) -> {
            corners[LEFT_TOP].changes[NEXT] = new Point(0, Place.tileSize);
            corners[LEFT_TOP].changes[CORNER] = new Point(xChange, yChange);
            corners[LEFT_TOP].changes[PREVOIUS] = new Point(Place.tileSize, 0);
        };
        changers[LEFT_BOTTOM] = (int xChange, int yChange) -> {
            corners[LEFT_BOTTOM].changes[NEXT] = new Point(Place.tileSize, height);
            corners[LEFT_BOTTOM].changes[CORNER] = new Point(xChange, (height - Place.tileSize) + yChange);
            corners[LEFT_BOTTOM].changes[PREVOIUS] = new Point(0, height - Place.tileSize);
        };
        changers[RIGHT_BOTTOM] = (int xChange, int yChange) -> {
            corners[RIGHT_BOTTOM].changes[NEXT] = new Point(width, height - Place.tileSize);
            corners[RIGHT_BOTTOM].changes[CORNER] = new Point((width - Place.tileSize) + xChange, (height - Place.tileSize) + yChange);
            corners[RIGHT_BOTTOM].changes[PREVOIUS] = new Point(width - Place.tileSize, height);
        };
        changers[RIGHT_TOP] = (int xChange, int yChange) -> {
            corners[RIGHT_TOP].changes[NEXT] = new Point(width - Place.tileSize, 0);
            corners[RIGHT_TOP].changes[CORNER] = new Point((width - Place.tileSize) + xChange, yChange);
            corners[RIGHT_TOP].changes[PREVOIUS] = new Point(width, Place.tileSize);
        };
        pushers[LEFT_TOP] = (int xChange, int yChange) -> {
            if (xChange <= Place.tileSize && xChange >= 0 && yChange <= Place.tileSize && yChange >= 0) {
                corners[LEFT_TOP].push(LEFT_TOP, xChange, yChange);
                getOwner().setSimpleLighting(false);
            }
        };
        pushers[LEFT_BOTTOM] = (int xChange, int yChange) -> {
            if (xChange <= Place.tileSize && xChange >= 0 && yChange <= Place.tileSize && yChange >= 0) {
                corners[LEFT_BOTTOM].push(LEFT_BOTTOM, xChange, Place.tileSize - yChange);
                getOwner().setSimpleLighting(false);
            }
        };
        pushers[RIGHT_BOTTOM] = (int xChange, int yChange) -> {
            if (xChange <= Place.tileSize && xChange >= 0 && yChange <= Place.tileSize && yChange >= 0) {
                corners[RIGHT_BOTTOM].push(RIGHT_BOTTOM, Place.tileSize - xChange, Place.tileSize - yChange);
                getOwner().setSimpleLighting(false);
            }
        };
        pushers[RIGHT_TOP] = (int xChange, int yChange) -> {
            if (xChange <= Place.tileSize && xChange >= 0 && yChange <= Place.tileSize && yChange >= 0) {
                corners[RIGHT_TOP].push(RIGHT_TOP, (Place.tileSize - xChange), yChange);
                getOwner().setSimpleLighting(false);
            }
        };
        geters[LEFT_TOP] = (Corner[] corners, RoundRectangle owner) -> {
            if (corners[LEFT_TOP].changes != null) {
                return corners[LEFT_TOP].changes[CORNER];
            }
            return new Point(0, 0);
        };
        geters[LEFT_BOTTOM] = (Corner[] corners, RoundRectangle owner) -> {
            if (corners[LEFT_BOTTOM].changes != null) {
                return new Point(corners[LEFT_BOTTOM].changes[CORNER].getX(), -corners[LEFT_BOTTOM].changes[CORNER].getY() + owner.height);
            }
            return new Point(0, 0);
        };
        geters[RIGHT_BOTTOM] = (Corner[] corners, RoundRectangle owner) -> {
            if (corners[RIGHT_BOTTOM].changes != null) {
                return new Point(-corners[RIGHT_BOTTOM].changes[CORNER].getX() + owner.width, -corners[RIGHT_BOTTOM].changes[CORNER].getY() + owner.height);
            }
            return new Point(0, 0);
        };
        geters[RIGHT_TOP] = (Corner[] corners, RoundRectangle owner) -> {
            if (corners[RIGHT_TOP].changes != null) {
                return new Point(-corners[RIGHT_TOP].changes[CORNER].getX() + owner.width, corners[RIGHT_TOP].changes[CORNER].getY());
            }
            return new Point(0, 0);
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
        corners[LEFT_BOTTOM] = new Corner(new Point(getX(), getY() + height));
        corners[RIGHT_BOTTOM] = new Corner(new Point(getX() + width, getY() + height));
        corners[RIGHT_TOP] = new Corner(new Point(getX() + width, getY()));
    }

    private void updateCorners() {
        corners[LEFT_TOP].setCornerPoint(getX(), getY());
        corners[LEFT_BOTTOM].setCornerPoint(getX(), getY() + height);
        corners[RIGHT_BOTTOM].setCornerPoint(getX() + width, getY() + height);
        corners[RIGHT_TOP].setCornerPoint(getX() + width, getY());
        for (Corner corner : corners) {
            corner.updateCornerPoints();
        }
    }

    private void updatePoints() {
        points.clear();
        for (Corner corner : corners) {
            corner.getPoints().stream().filter((point) -> (!points.contains(point))).forEach((point) -> {
                points.add(point);
            });
        }
        points.trimToSize();

        bottomPoints.clear();
        corners[LEFT_BOTTOM].getPoints().stream().filter((point) -> (!bottomPoints.contains(point))).forEach((point) -> {
            bottomPoints.add(point);
        });
        corners[RIGHT_BOTTOM].getPoints().stream().filter((point) -> (!bottomPoints.contains(point))).forEach((point) -> {
            bottomPoints.add(point);
        });
        bottomPoints.trimToSize();
    }

    private void centralize() {
        xCenter = width / 2;
        yCenter = height / 2;
    }

    public void pushCorner(int corner, int xChange, int yChange) {
        if ((corner == LEFT_BOTTOM || corner == RIGHT_BOTTOM)) {
            if (xChange > Place.tileSize / 2 && yChange > Place.tileSize / 2) {
                concave = true;
            } else if (xChange == Place.tileSize / 2 && yChange == Place.tileSize / 2) {
                triangular = true;
            }
            bottomRounded = true;
        }
        pushers[corner].push(xChange, yChange);
        updatePoints();
    }

    public Point getPushValueOfCorner(int corner) {
        return geters[corner].get(corners, this);
    }

    @Override
    public boolean isCollideSingle(int x, int y, Figure figure) {
        if (figure instanceof RoundRectangle) {
            return rectangleCollsion(x, y, figure);
        } else if (figure instanceof RoundRectangle) {
            return roundRectangleCollsion(x, y, figure);
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

    private boolean roundRectangleCollsion(int x, int y, Figure figure) { //TO DO
        RoundRectangle roundRectangle = (RoundRectangle) figure;
        return ((getX(x) > roundRectangle.getX() && getX(x) - roundRectangle.getX() < roundRectangle.getWidth()) || (getX(x) <= roundRectangle.getX() && roundRectangle.getX() - getX(x) < width))
                && ((getY(y) > roundRectangle.getY() && getY(y) - roundRectangle.getY() < roundRectangle.getHeight()) || (getY(y) <= roundRectangle.getY() && roundRectangle.getY() - getY(y) < height));
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
            updateCorners();
            updatePoints();
        }
        return Collections.unmodifiableCollection(points);
    }

    public int getBottomPointSize() {
        return bottomPoints.size();
    }

    public Point getBottomPoint(int i) {
        return bottomPoints.get(i);
    }

    public boolean isLeftBottomRound() {
        return corners[LEFT_BOTTOM].changes != null;
    }

    public boolean isRightBottomRound() {
        return corners[RIGHT_BOTTOM].changes != null;
    }

    @Override
    public boolean isConcave() {
        return concave;
    }

    @Override
    public boolean isTriangular() {
        return triangular;
    }

    @Override
    public boolean isBottomRounded() {
        return bottomRounded;
    }

    private interface changer {

        void set(int xChange, int yChange);
    }

    private interface pusher {

        void push(int xChange, int yChange);
    }

    private interface geter {

        Point get(Corner[] corners, RoundRectangle owner);
    }

    private class Corner {

        private Point[] points = new Point[3];
        private Point[] changes;

        public Corner(Point point) {
            points[CORNER] = point;
        }

        public void push(int corner, int xChange, int yChange) {
            changes = new Point[3];
            changers[corner].set(xChange, yChange);
            setPoints();
        }

        private void setPoints() {
            points[NEXT] = new Point(getX() + changes[NEXT].getX(), getY() + changes[NEXT].getY());
            points[CORNER].set(getX() + changes[CORNER].getX(), getY() + changes[CORNER].getY());
            points[PREVOIUS] = new Point(getX() + changes[PREVOIUS].getX(), getY() + changes[PREVOIUS].getY());
        }

        private void setCornerPoint(int x, int y) {
            points[CORNER].set(x, y);
        }

        private void updateCornerPoints() {
            if (this.changes != null) {
                for (int i = 0; i < points.length; i++) {
                    if (points[i] != null) {
                        points[i].set(getX() + changes[i].getX(), getY() + changes[i].getY());
                    }
                }
            }
        }

        public Point getCorner() {
            return this.points[CORNER];
        }

        public Collection<Point> getPoints() {
            if (this.points[NEXT] == null) {
                return Arrays.asList(this.points[CORNER]);
            } else {
                return Arrays.asList(this.points);
            }
        }
    }
}
