/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import static collision.RoundRectangle.LEFT;
import static collision.RoundRectangle.LEFT_BOTTOM;
import static collision.RoundRectangle.LEFT_BOTTOM_TO_RIGHT_TOP;
import static collision.RoundRectangle.LEFT_TOP;
import static collision.RoundRectangle.LEFT_TOP_TO_RIGHT_BOTTOM;
import static collision.RoundRectangle.RIGHT;
import static collision.RoundRectangle.RIGHT_BOTTOM;
import static collision.RoundRectangle.RIGHT_TOP;
import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.place.Place;
import java.awt.geom.Line2D;
import java.util.List;
import net.jodk.lang.FastMath;

/**
 *
 * @author Wojtek
 */
public class Rectangle extends Figure {

    private static Point[] list = {new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0)};
    private static Point start, end, point;
    private static int firstPushed, secondPushed, caseNumber;
    private static double slideVariable = 1.5;
    private boolean tile = false;

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
        if (owner != null) {
            points.add(new Point(super.getX(), super.getY()));
            points.add(new Point(super.getX(), super.getY() + height));
            points.add(new Point(super.getX() + width, super.getY() + height));
            points.add(new Point(super.getX() + width, super.getY()));
        }
        centralize();
    }

    public static Rectangle createTileRectangle() {
        return new Rectangle(0, 0, Place.tileSize, Place.tileSize);
    }

    public static Rectangle createTileRectangle(int width, int height) {
        return new Rectangle(0, 0, width, height);
    }

    private Rectangle(int xStart, int yStart, int width, int height) {
        super(xStart, yStart, null, null);
        tile = true;
        this.width = width;
        this.height = height;
        points.add(new Point(xStart, yStart));
        points.add(new Point(xStart, yStart + height));
        points.add(new Point(xStart + width, yStart + height));
        points.add(new Point(xStart + width, yStart));
        centralize();
    }

    private void centralize() {
        xCenter = width / 2;
        yCenter = height / 2;
    }

    @Override
    public boolean isCollideSingle(int x, int y, Figure figure) {
        if (figure instanceof Rectangle) {
            return rectangleCollision(x, y, figure);
        } else if (figure instanceof RoundRectangle) {
            return roundRectangle(x, y, figure);
        } else if (figure instanceof Circle) {
            return circleCollision(x, y, figure);
        } else if (figure instanceof Line) {
            return lineCollision(x, y, figure);
        }
        return false;
    }

    private boolean rectangleCollision(int x, int y, Figure figure) {
        Rectangle rectangle = (Rectangle) figure;
        return ((getX(x) > rectangle.getX() && getX(x) - rectangle.getX() < rectangle.getWidth()) || (getX(x) <= rectangle.getX() && rectangle.getX() - getX(x) < width))
                && ((getY(y) > rectangle.getY() && getY(y) - rectangle.getY() < rectangle.getHeight()) || (getY(y) <= rectangle.getY() && rectangle.getY() - getY(y) < height));
    }

    private boolean roundRectangle(int x, int y, Figure figure) {
        RoundRectangle roundRectangle = (RoundRectangle) figure;
        if (((getX(x) > roundRectangle.getX() && getX(x) - roundRectangle.getX() < roundRectangle.getWidth())
                || (getX(x) <= roundRectangle.getX() && roundRectangle.getX() - getX(x) < width))
                && ((getY(y) > roundRectangle.getY() && getY(y) - roundRectangle.getY() < roundRectangle.getHeight())
                || (getY(y) <= roundRectangle.getY() && roundRectangle.getY() - getY(y) < height))) {
            list[LEFT_TOP].set(getX(x), getY(y));
            list[LEFT_BOTTOM].set(getX(x), getY(y) + height);
            list[RIGHT_TOP].set(getX(x) + width, getY(y));
            list[RIGHT_BOTTOM].set(getX(x) + width, getY(y) + height);
            firstPushed = secondPushed = -1;
            for (int i = 0; i < 4; i++) {
                if (roundRectangle.isCornerPushed(i)) {
                    if (roundRectangle.isCornerTriangular(i)) {
                        if (isCollideTriangularCorner(roundRectangle, i)) {
                            return true;
                        }
                    } else if (roundRectangle.isCornerConcave(i)) {
                        if (isCollideConcaveCorner(roundRectangle, i)) {
                            return true;
                        }
                    } else {
                        if (isCollideConvexCorner(roundRectangle, i)) {
                            return true;
                        }
                    }
                    if (firstPushed == -1) {
                        firstPushed = i;
                    } else {
                        secondPushed = i;
                    }
                }
            }
            recognizeCase();
            return isCollideStraightEdgesByCase(roundRectangle);
        }
        return false;
    }

    private boolean isCollideTriangularCorner(RoundRectangle roundRectangle, int corner) {
        start = roundRectangle.getPrevious(corner);
        end = roundRectangle.getNext(corner);
        switch (corner) {
            case LEFT_TOP:
            case RIGHT_TOP:
                if (Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(),
                        list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY())) {
                    if (corner == LEFT_TOP) {
                        setSlideSpeed(-FastMath.abs(getYStartSlideSpeed() / slideVariable), 
                                -FastMath.abs(getXStartSlideSpeed() / slideVariable));
                    } else {
                        setSlideSpeed(FastMath.abs(getYStartSlideSpeed() / slideVariable), 
                                -FastMath.abs(getXStartSlideSpeed() / slideVariable));
                    }
                    return true;
                }
                break;
            case LEFT_BOTTOM:
            case RIGHT_BOTTOM:
                if (Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(),
                        list[LEFT_TOP].getX(), list[LEFT_TOP].getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY())) {
                    if (corner == LEFT_BOTTOM) {
                        setSlideSpeed(-FastMath.abs(getYStartSlideSpeed() / slideVariable), 
                                FastMath.abs(getXStartSlideSpeed() / slideVariable));
                    } else {
                        setSlideSpeed(FastMath.abs(getYStartSlideSpeed() / slideVariable), 
                                FastMath.abs(getXStartSlideSpeed() / slideVariable));
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean isCollideConcaveCorner(RoundRectangle roundRectangle, int corner) {
        start = roundRectangle.getPrevious(corner);
        end = roundRectangle.getNext(corner);
        switch (corner) {
            case LEFT_TOP:
                point = Methods.getRightCircleLineIntersection(0, -list[RIGHT_BOTTOM].getY(), roundRectangle.getX(), roundRectangle.getY());
                if (point != null && point.getX() <= list[RIGHT_TOP].getX() && point.getX() >= list[LEFT_TOP].getX()
                        && point.getX() <= roundRectangle.getXEnd() && point.getX() >= roundRectangle.getX() && point.getY() >= roundRectangle.getY()) {
                    setSlideSpeed(-FastMath.abs(getYStartSlideSpeed() / slideVariable * (point.getX() - roundRectangle.getX()) / Place.tileSize),
                            -FastMath.abs(getXStartSlideSpeed() / slideVariable * (point.getY() - roundRectangle.getY()) / Place.tileSize));
                    return true;
                }
                break;
            case LEFT_BOTTOM:
                point = Methods.getRightCircleLineIntersection(0, -list[RIGHT_TOP].getY(), roundRectangle.getX(), roundRectangle.getYEnd());
                if (point != null && point.getX() <= list[RIGHT_TOP].getX() && point.getX() >= list[LEFT_TOP].getX()
                        && point.getX() <= roundRectangle.getXEnd() && point.getX() >= roundRectangle.getX() && point.getY() <= roundRectangle.getYEnd()) {
                    setSlideSpeed(-FastMath.abs(getYStartSlideSpeed() / slideVariable * (roundRectangle.getX() - point.getX()) / Place.tileSize),
                            FastMath.abs(getXStartSlideSpeed() / slideVariable * (roundRectangle.getYEnd() - point.getY()) / Place.tileSize));
                    return true;
                }
                break;
            case RIGHT_BOTTOM:
                point = Methods.getLeftCircleLineIntersection(0, -list[RIGHT_TOP].getY(), roundRectangle.getXEnd(), roundRectangle.getYEnd());
                if (point != null && point.getX() <= list[RIGHT_TOP].getX() && point.getX() >= list[LEFT_TOP].getX()
                        && point.getX() <= roundRectangle.getXEnd() && point.getX() >= roundRectangle.getX() && point.getY() <= roundRectangle.getYEnd()) {
                    setSlideSpeed(FastMath.abs(getYStartSlideSpeed() / slideVariable * (point.getX() - roundRectangle.getXEnd()) / Place.tileSize),
                            FastMath.abs(getXStartSlideSpeed() / slideVariable * (point.getY() - roundRectangle.getYEnd()) / Place.tileSize));
                    return true;
                }
                break;
            case RIGHT_TOP:
                point = Methods.getLeftCircleLineIntersection(0, -list[RIGHT_BOTTOM].getY(), roundRectangle.getXEnd(), roundRectangle.getY());
                if (point != null && point.getX() <= list[RIGHT_TOP].getX() && point.getX() >= list[LEFT_TOP].getX()
                        && point.getX() <= roundRectangle.getXEnd() && point.getX() >= roundRectangle.getX() && point.getY() >= roundRectangle.getY()) {
                    setSlideSpeed(FastMath.abs(getYStartSlideSpeed() / slideVariable * (roundRectangle.getXEnd() - point.getX()) / Place.tileSize),
                            -FastMath.abs(getXStartSlideSpeed() / slideVariable * (roundRectangle.getY() - point.getY()) / Place.tileSize));
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean isCollideConvexCorner(RoundRectangle roundRectangle, int corner) {
        start = roundRectangle.getPrevious(corner);
        end = roundRectangle.getNext(corner);
        switch (corner) {
            case LEFT_TOP:
                point = Methods.getLeftCircleLineIntersection(0, -list[RIGHT_BOTTOM].getY(), roundRectangle.getXEnd(), roundRectangle.getY() + Place.tileSize);
                if (point != null && point.getX() <= list[RIGHT_TOP].getX() && point.getX() >= list[LEFT_TOP].getX()
                        && point.getX() <= roundRectangle.getXEnd() && point.getX() >= roundRectangle.getX()) {
                    setSlideSpeed(-FastMath.abs(getYStartSlideSpeed() / slideVariable * (roundRectangle.getXEnd() - point.getX()) / Place.tileSize),
                            -FastMath.abs(getXStartSlideSpeed() / slideVariable * (roundRectangle.getY() + Place.tileSize - point.getY()) / Place.tileSize));
                    return true;
                }
                break;
            case LEFT_BOTTOM:
                point = Methods.getLeftCircleLineIntersection(0, -list[RIGHT_TOP].getY(), roundRectangle.getXEnd(), roundRectangle.getYEnd() - Place.tileSize);
                if (point != null && point.getX() <= list[RIGHT_TOP].getX() && point.getX() >= list[LEFT_TOP].getX()
                        && point.getX() <= roundRectangle.getXEnd() && point.getX() >= roundRectangle.getX()) {
                    setSlideSpeed(-FastMath.abs(getYStartSlideSpeed() / slideVariable * (point.getX() - roundRectangle.getXEnd()) / Place.tileSize),
                            FastMath.abs(getXStartSlideSpeed() / slideVariable * (point.getY() - roundRectangle.getYEnd() + Place.tileSize) / Place.tileSize));
                    return true;
                }
                break;
            case RIGHT_BOTTOM:
                point = Methods.getRightCircleLineIntersection(0, -list[RIGHT_TOP].getY(), roundRectangle.getX(), roundRectangle.getYEnd() - Place.tileSize);
                if (point != null && point.getX() <= list[RIGHT_TOP].getX() && point.getX() >= list[LEFT_TOP].getX()
                        && point.getX() <= roundRectangle.getXEnd() && point.getX() >= roundRectangle.getX()) {
                    setSlideSpeed(FastMath.abs(getYStartSlideSpeed() / slideVariable * (roundRectangle.getX() - point.getX()) / Place.tileSize),
                            FastMath.abs(getXStartSlideSpeed() / slideVariable * (roundRectangle.getYEnd() - Place.tileSize - point.getY()) / Place.tileSize));
                    return true;
                }
                break;
            case RIGHT_TOP:
                point = Methods.getRightCircleLineIntersection(0, -list[RIGHT_BOTTOM].getY(), roundRectangle.getX(), roundRectangle.getY() + Place.tileSize);
                if (point != null && point.getX() <= list[RIGHT_TOP].getX() && point.getX() >= list[LEFT_TOP].getX()
                        && point.getX() <= roundRectangle.getXEnd() && point.getX() >= roundRectangle.getX()) {
                    setSlideSpeed(FastMath.abs(getYStartSlideSpeed() / slideVariable * (point.getX() - roundRectangle.getX()) / Place.tileSize),
                            -FastMath.abs(getXStartSlideSpeed() / slideVariable * (point.getY() - roundRectangle.getY() - Place.tileSize) / Place.tileSize));
                    return true;
                }
                break;
        }
        return false;
    }

    private void recognizeCase() {
        caseNumber = firstPushed + secondPushed + 1;
        if (secondPushed != -1) {
            caseNumber += 3;
            if (caseNumber > 5) {
                caseNumber -= 2;
            }
        }
    }

    private boolean isCollideStraightEdgesByCase(RoundRectangle roundRectangle) {
        switch (caseNumber) {
            case LEFT_TOP:
                if (checkCornersTop(roundRectangle) || checkRightTop(roundRectangle)) {
                    return true;
                }
                break;
            case LEFT_BOTTOM:
                if (checkCornersBottom(roundRectangle) || checkRightBottom(roundRectangle)) {
                    return true;
                }
                break;
            case RIGHT_BOTTOM:
                if (checkCornersBottom(roundRectangle) || checkLeftBottom(roundRectangle)) {
                    return true;
                }
                break;
            case RIGHT_TOP:
                if (checkCornersTop(roundRectangle) || checkLeftTop(roundRectangle)) {
                    return true;
                }
                break;
            case LEFT_BOTTOM_TO_RIGHT_TOP:
                if (checkCornersBothWays(roundRectangle) || checkRightTop(roundRectangle) || checkLeftBottom(roundRectangle)) {
                    return true;
                }
                break;
            case LEFT:
                if (checkCornersBothWays(roundRectangle) || checkRightSide(roundRectangle)) {
                    return true;
                }
                break;
            case LEFT_TOP_TO_RIGHT_BOTTOM:
                if (checkCornersBothWays(roundRectangle) || checkLeftTop(roundRectangle) || checkRightBottom(roundRectangle)) {
                    return true;
                }
                break;
            case RIGHT:
                if (checkCornersBothWays(roundRectangle) || checkLeftSide(roundRectangle)) {
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean checkCornersBottom(RoundRectangle roundRectangle) {
        return list[RIGHT_BOTTOM].getX() >= roundRectangle.getX() && list[LEFT_BOTTOM].getX() <= roundRectangle.getXEnd()
                && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() - Place.tileSize && list[LEFT_BOTTOM].getY() >= roundRectangle.getY();
    }

    private boolean checkCornersTop(RoundRectangle roundRectangle) {
        return list[RIGHT_BOTTOM].getX() >= roundRectangle.getX() && list[LEFT_BOTTOM].getX() <= roundRectangle.getXEnd()
                && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() && list[LEFT_BOTTOM].getY() >= roundRectangle.getY() + Place.tileSize;
    }

    private boolean checkCornersBothWays(RoundRectangle roundRectangle) {
        return list[RIGHT_BOTTOM].getX() >= roundRectangle.getX() && list[LEFT_BOTTOM].getX() <= roundRectangle.getXEnd()
                && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() - Place.tileSize && list[LEFT_BOTTOM].getY() >= roundRectangle.getY() + Place.tileSize;
    }

    private boolean checkRightTop(RoundRectangle roundRectangle) {
        return list[RIGHT_BOTTOM].getX() >= roundRectangle.getXEnd()
                && list[LEFT_TOP].getY() <= roundRectangle.getY() + Place.tileSize && list[LEFT_BOTTOM].getY() >= roundRectangle.getY();
    }

    private boolean checkRightBottom(RoundRectangle roundRectangle) {
        return list[RIGHT_BOTTOM].getX() >= roundRectangle.getXEnd()
                && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() && list[LEFT_BOTTOM].getY() >= roundRectangle.getYEnd() - Place.tileSize;
    }

    private boolean checkLeftTop(RoundRectangle roundRectangle) {
        return list[LEFT_BOTTOM].getX() <= roundRectangle.getX()
                && list[LEFT_TOP].getY() <= roundRectangle.getY() + Place.tileSize && list[LEFT_BOTTOM].getY() >= roundRectangle.getY();
    }

    private boolean checkLeftBottom(RoundRectangle roundRectangle) {
        return list[LEFT_BOTTOM].getX() <= roundRectangle.getX()
                && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() && list[LEFT_BOTTOM].getY() >= roundRectangle.getYEnd() - Place.tileSize;
    }

    private boolean checkLeftSide(RoundRectangle roundRectangle) {
        return list[LEFT_BOTTOM].getX() <= roundRectangle.getX()
                && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() && list[LEFT_BOTTOM].getY() >= roundRectangle.getY();
    }

    private boolean checkRightSide(RoundRectangle roundRectangle) {
        return list[RIGHT_BOTTOM].getX() >= roundRectangle.getXEnd()
                && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() && list[LEFT_BOTTOM].getY() >= roundRectangle.getY();
    }

    private boolean circleCollision(int x, int y, Figure figure) {
        Circle circle = (Circle) figure;
        int xPosition = ((circle.getX() < getX(x) ? -1 : 1) + (circle.getX() <= (getX(x) + width) ? -1 : 1)) / 2;
        int yPosition = ((circle.getY() < getY(y) ? -1 : 1) + (circle.getY() <= (getY(y) + height) ? -1 : 1)) / 2;
        if (xPosition == 0 && yPosition == 0) {
            return true;
        }
        getPoints();
        if (xPosition != 0 && yPosition != 0) {
            int xtmp = (xPosition + 1) / 2;
            int ytmp = (yPosition + 1) / 2;
            return (Methods.pointDistance(circle.getX(), circle.getY(), getPoint(xtmp + 2 * ytmp).getX(), getPoint(xtmp + 2 * ytmp).getY()) <= circle.getRadius());
        }
        if (yPosition == 0 && ((xPosition < 0 && getX(x) - circle.getX() <= circle.getRadius()) || (yPosition > 0 && circle.getX() - getX(x) - width <= circle.getRadius()))) {
            return true;
        }
        return (yPosition < 0 && getY(y) - circle.getY() <= circle.getRadius()) || (yPosition > 0 && circle.getY() - getY(y) - height <= circle.getRadius());
    }

    private boolean lineCollision(int x, int y, Figure figure) {
        Line line = (Line) figure;
        list[LEFT_TOP].set(getX(x), getY(y));
        list[LEFT_BOTTOM].set(getX(x), getY(y) + height);
        list[RIGHT_TOP].set(getX(x) + width, getY(y));
        list[RIGHT_BOTTOM].set(getX(x) + width, getY(y) + height);
        int[] linePoints = {line.getX(), line.getY(), line.getX() + line.getXVector(), line.getY() + line.getYVector()};
        return (Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[LEFT_TOP].getX(), list[LEFT_TOP].getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY())
                || Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY())
                || Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[LEFT_TOP].getX(), list[LEFT_TOP].getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY())
                || Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY()));
    }

    @Override
    public List<Point> getPoints() {
        if (isMobile()) {
            updatePoints();
        }
        return points;
    }

    @Override
    public void updatePoints() {
        points.get(0).set(getX(), getY());
        points.get(1).set(getX(), getY() + height);
        points.get(2).set(getX() + width, getY() + height);
        points.get(3).set(getX() + width, getY());
    }

    public void updateTilePoints() {
        points.get(0).set(xStart, yStart);
        points.get(1).set(xStart, yStart + height);
        points.get(2).set(xStart + width, yStart + height);
        points.get(3).set(xStart + width, yStart);
    }

    @Override
    public int getX(int x) {
        if (tile) {
            return xStart;
        } else {
            return super.getX(x);       // Jeśli jest to kolizja tilowa, to x nie ma znaczania
        }
    }

    @Override
    public int getY(int y) {
        if (tile) {
            return yStart;
        } else {
            return super.getY(y);       // Jeśli jest to kolizja tilowa, to y nie ma znaczania - zastępuje go yStart - dzięki temu obiekt nie musi mieć właściciela.
        }
    }

    @Override
    public int getX() {
        if (tile) {
            return xStart;
        } else {
            return super.getX();
        }
    }

    @Override
    public int getY() {
        if (tile) {
            return yStart;
        } else {
            return super.getY();
        }
    }
}
