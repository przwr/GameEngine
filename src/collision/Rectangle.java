/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import static collision.RoundRectangle.LEFT_BOTTOM;
import static collision.RoundRectangle.LEFT_TOP;
import static collision.RoundRectangle.RIGHT_BOTTOM;
import static collision.RoundRectangle.RIGHT_TOP;
import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.place.Place;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author Wojtek
 */
public class Rectangle extends Figure {

    private static Point[] list = {new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0)};
    private static Point start, end;
    private static int caseNumber;

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

    private boolean roundRectangle(int x, int y, Figure figure) { //TO DO
        RoundRectangle roundRectangle = (RoundRectangle) figure;
        if (((getX(x) > roundRectangle.getX() && getX(x) - roundRectangle.getX() < roundRectangle.getWidth()) || (getX(x) <= roundRectangle.getX() && roundRectangle.getX() - getX(x) < width))
                && ((getY(y) > roundRectangle.getY() && getY(y) - roundRectangle.getY() < roundRectangle.getHeight()) || (getY(y) <= roundRectangle.getY() && roundRectangle.getY() - getY(y) < height))) {
            list[LEFT_TOP].set(getX(x), getY(y));
            list[LEFT_BOTTOM].set(getX(x), getY(y) + height);
            list[RIGHT_TOP].set(getX(x) + width, getY(y));
            list[RIGHT_BOTTOM].set(getX(x) + width, getY(y) + height);
            int firstPushed = -1;
            int secondPushed = -1;
            for (int i = 0; i < 4; i++) {
                if (roundRectangle.isCornerPushed(i)) {
                    if (roundRectangle.isCornerTriangular(i)) {
                        start = roundRectangle.getPrevious(i);
                        end = roundRectangle.getNext(i);
                        if (Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[LEFT_TOP].getX(), list[LEFT_TOP].getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY())
                                || Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY())
                                || Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY(), list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY())
                                || Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY(), list[LEFT_TOP].getX(), list[LEFT_TOP].getY())) {
                            return true;
                        }
                    } else if (roundRectangle.isCornerConcave(i)) {
                        start = roundRectangle.getPrevious(i);
                        end = roundRectangle.getNext(i);
                        if (Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[LEFT_TOP].getX(), list[LEFT_TOP].getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY())
                                || Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY())
                                || Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY(), list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY())
                                || Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY(), list[LEFT_TOP].getX(), list[LEFT_TOP].getY())) {
                            return true;
                        }
                    } else {
                        start = roundRectangle.getPrevious(i);
                        end = roundRectangle.getNext(i);
                        if (Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[LEFT_TOP].getX(), list[LEFT_TOP].getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY())
                                || Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY())
                                || Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY(), list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY())
                                || Line2D.linesIntersect(start.getX(), start.getY(), end.getX(), end.getY(), list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY(), list[LEFT_TOP].getX(), list[LEFT_TOP].getY())) {
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
            caseNumber = firstPushed + secondPushed + 1;
            if (secondPushed != -1) {
                caseNumber += 3;
                if (caseNumber > 5) {
                    caseNumber -= 2;
                }
            }
            return (checkStraightEdgesForCase(caseNumber, roundRectangle));
        }
        return false;
    }

    private boolean checkStraightEdgesForCase(int caseNumber, RoundRectangle roundRectangle) {
        switch (caseNumber) {
            case LEFT_TOP:
                if (checkCutFromTop(roundRectangle) || checkRightTop(roundRectangle)) {
                    return true;
                }
                break;
            case LEFT_BOTTOM:
                if (checkCutFromBottom(roundRectangle) || checkRightBottom(roundRectangle)) {
                    return true;
                }
                break;
            case RIGHT_BOTTOM:
                if (checkCutFromBottom(roundRectangle) || checkLeftBottom(roundRectangle)) {
                    return true;
                }
                break;
            case RIGHT_TOP:
                if (checkCutFromTop(roundRectangle) || checkLeftTop(roundRectangle)) {
                    return true;
                }
                break;
            case 4: // LEFT BOTTOM TO RIGHT TOP
                if (checkCutBothWays(roundRectangle) || checkRightTop(roundRectangle) || checkLeftBottom(roundRectangle)) {
                    return true;
                }
                break;
            case 5: // LEFT
                if (checkCutBothWays(roundRectangle) || checkRightSide(roundRectangle)) {
                    return true;
                }
                break;
            case 6: // LEFT TOP TO RIGHT BOTTOM
                if (checkCutBothWays(roundRectangle) || checkLeftTop(roundRectangle) || checkRightBottom(roundRectangle)) {
                    return true;
                }
                break;
            case 7: // RIGHT
                if (checkCutBothWays(roundRectangle) || checkLeftSide(roundRectangle)) {
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean checkCutFromBottom(RoundRectangle roundRectangle) {
        return list[RIGHT_BOTTOM].getX() >= roundRectangle.getX() && list[LEFT_BOTTOM].getX() <= roundRectangle.getXEnd()
                && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() - Place.tileSize && list[LEFT_BOTTOM].getY() >= roundRectangle.getY();
    }

    private boolean checkCutFromTop(RoundRectangle roundRectangle) {
        return list[RIGHT_BOTTOM].getX() >= roundRectangle.getX() && list[LEFT_BOTTOM].getX() <= roundRectangle.getXEnd()
                && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() && list[LEFT_BOTTOM].getY() >= roundRectangle.getY() + Place.tileSize;
    }

    private boolean checkCutBothWays(RoundRectangle roundRectangle) {
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
        return list[RIGHT_BOTTOM].getX() >= roundRectangle.getXEnd() && list[LEFT_TOP].getY() <= roundRectangle.getYEnd() && list[LEFT_BOTTOM].getY() >= roundRectangle.getY();
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
        list[LEFT_TOP].set(getX(x), getY(y));
        list[LEFT_BOTTOM].set(getX(x), getY(y) + height);
        list[RIGHT_TOP].set(getX(x) + width, getY(y));
        list[RIGHT_BOTTOM].set(getX(x) + width, getY(y) + height);
        int[] linePoints = {line.getX(), line.getY(), line.getX() + line.getXVector(), line.getY() + line.getYVector()};
        return (Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[LEFT_TOP].getX(), list[LEFT_TOP].getY(), list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY())
                || Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[LEFT_BOTTOM].getX(), list[LEFT_BOTTOM].getY(), list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY())
                || Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[RIGHT_TOP].getX(), list[RIGHT_TOP].getY(), list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY())
                || Line2D.linesIntersect(linePoints[0], linePoints[1], linePoints[2], linePoints[3], list[RIGHT_BOTTOM].getX(), list[RIGHT_BOTTOM].getY(), list[LEFT_TOP].getX(), list[LEFT_TOP].getY()));
    }

    @Override
    public Collection<Point> getPoints() {
        if (isMobile()) {
            points.get(0).set(getX(), getY());
            points.get(1).set(getX(), getY() + height);
            points.get(2).set(getX() + width, getY() + height);
            points.get(3).set(getX() + width, getY());
        }
        return Collections.unmodifiableCollection(points);
    }
}
