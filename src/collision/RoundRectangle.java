/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.place.Place;

import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Wojtek
 */
public class RoundRectangle extends Figure {

    public static final byte LEFT_TOP = 0, LEFT_BOTTOM = 1, RIGHT_BOTTOM = 2, RIGHT_TOP = 3, LEFT_BOTTOM_TO_RIGHT_TOP = 4, LEFT = 5, LEFT_TOP_TO_RIGHT_BOTTOM = 6, RIGHT = 7;
    public static final byte PREVIOUS = 0, CORNER = 1, NEXT = 2;
    private static final changer[] changers = new changer[4];
    private Corner[] corners = new Corner[4];
    private boolean concave, triangular, bottomRounded;

    {
        changers[LEFT_TOP] = new changer() {
            @Override
            public Point get(Corner[] corners, RoundRectangle owner) {
                if (corners[LEFT_TOP].changes != null) {
                    return corners[LEFT_TOP].changes[CORNER];
                }
                return new Point(0, 0);
            }

            @Override
            public void push(int xChange, int yChange) {
                if (xChange <= Place.tileSize && xChange >= 0 && yChange <= Place.tileSize && yChange >= 0) {
                    corners[LEFT_TOP].push(LEFT_TOP, xChange, yChange);
                    setTypeOfCorner(LEFT_TOP, xChange, yChange);
                    getOwner().setSimpleLighting(false);
                }
            }

            @Override
            public void set(int xChange, int yChange) {
                corners[LEFT_TOP].changes[NEXT] = new Point(0, Place.tileSize);
                corners[LEFT_TOP].changes[CORNER] = new Point(xChange, yChange);
                corners[LEFT_TOP].changes[PREVIOUS] = new Point(Place.tileSize, 0);
            }
        };
        changers[LEFT_BOTTOM] = new changer() {
            @Override
            public Point get(Corner[] corners, RoundRectangle owner) {
                if (corners[LEFT_BOTTOM].changes != null) {
                    return new Point(corners[LEFT_BOTTOM].changes[CORNER].getX(), -corners[LEFT_BOTTOM].changes[CORNER].getY() + owner.height);
                }
                return new Point(0, 0);
            }

            @Override
            public void push(int xChange, int yChange) {
                if (xChange <= Place.tileSize && xChange >= 0 && yChange <= Place.tileSize && yChange >= 0) {
                    corners[LEFT_BOTTOM].push(LEFT_BOTTOM, xChange, Place.tileSize - yChange);
                    setTypeOfCorner(LEFT_BOTTOM, xChange, yChange);
                    getOwner().setSimpleLighting(false);
                }
            }

            @Override
            public void set(int xChange, int yChange) {
                corners[LEFT_BOTTOM].changes[NEXT] = new Point(Place.tileSize, height);
                corners[LEFT_BOTTOM].changes[CORNER] = new Point(xChange, (height - Place.tileSize) + yChange);
                corners[LEFT_BOTTOM].changes[PREVIOUS] = new Point(0, height - Place.tileSize);
            }
        };
        changers[RIGHT_BOTTOM] = new changer() {
            @Override
            public Point get(Corner[] corners, RoundRectangle owner) {
                if (corners[RIGHT_BOTTOM].changes != null) {
                    return new Point(-corners[RIGHT_BOTTOM].changes[CORNER].getX() + owner.width, -corners[RIGHT_BOTTOM].changes[CORNER].getY() + owner.height);
                }
                return new Point(0, 0);
            }

            @Override
            public void push(int xChange, int yChange) {
                if (xChange <= Place.tileSize && xChange >= 0 && yChange <= Place.tileSize && yChange >= 0) {
                    corners[RIGHT_BOTTOM].push(RIGHT_BOTTOM, Place.tileSize - xChange, Place.tileSize - yChange);
                    setTypeOfCorner(RIGHT_BOTTOM, xChange, yChange);
                    getOwner().setSimpleLighting(false);
                }
            }

            @Override
            public void set(int xChange, int yChange) {
                corners[RIGHT_BOTTOM].changes[NEXT] = new Point(width, height - Place.tileSize);
                corners[RIGHT_BOTTOM].changes[CORNER] = new Point((width - Place.tileSize) + xChange, (height - Place.tileSize) + yChange);
                corners[RIGHT_BOTTOM].changes[PREVIOUS] = new Point(width - Place.tileSize, height);
            }
        };
        changers[RIGHT_TOP] = new changer() {
            @Override
            public Point get(Corner[] corners, RoundRectangle owner) {
                if (corners[RIGHT_TOP].changes != null) {
                    return new Point(-corners[RIGHT_TOP].changes[CORNER].getX() + owner.width, corners[RIGHT_TOP].changes[CORNER].getY());
                }
                return new Point(0, 0);
            }

            @Override
            public void push(int xChange, int yChange) {
                if (xChange <= Place.tileSize && xChange >= 0 && yChange <= Place.tileSize && yChange >= 0) {
                    corners[RIGHT_TOP].push(RIGHT_TOP, (Place.tileSize - xChange), yChange);
                    setTypeOfCorner(RIGHT_TOP, xChange, yChange);
                    getOwner().setSimpleLighting(false);
                }
            }

            @Override
            public void set(int xChange, int yChange) {
                corners[RIGHT_TOP].changes[NEXT] = new Point(width - Place.tileSize, 0);
                corners[RIGHT_TOP].changes[CORNER] = new Point((width - Place.tileSize) + xChange, yChange);
                corners[RIGHT_TOP].changes[PREVIOUS] = new Point(width, Place.tileSize);
            }
        };
    }

    private RoundRectangle(int xStart, int yStart, int width, int height, int OpticPropertiesType, int shadowHeight, GameObject owner) {
        super(xStart, yStart, owner, OpticProperties.create(OpticPropertiesType, shadowHeight));
        this.width = width;
        this.height = height;
        initializeCorners();
        updatePoints();
        centralize();
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

    private void initializeCorners() {
        corners[LEFT_TOP] = new Corner(new Point(getX(), getY()));
        corners[LEFT_BOTTOM] = new Corner(new Point(getX(), getY() + height));
        corners[RIGHT_BOTTOM] = new Corner(new Point(getX() + width, getY() + height));
        corners[RIGHT_TOP] = new Corner(new Point(getX() + width, getY()));
    }

    private void centralize() {
        xCenter = width / 2;
        yCenter = height / 2;
    }

    public void pushCorner(int corner, int xChange, int yChange) {
        if ((corner == LEFT_BOTTOM || corner == RIGHT_BOTTOM)) {
            if (xChange > Place.tileHalf && yChange > Place.tileHalf) {
                concave = true;
            } else if (xChange == Place.tileHalf && yChange == Place.tileHalf) {
                triangular = true;
            }
            bottomRounded = true;
        }
        changers[corner].push(xChange, yChange);
        updatePoints();
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
        return ((getX(x) > rectangle.getX() && getX(x) - rectangle.getX() < rectangle.getWidth())
                || (getX(x) <= rectangle.getX() && rectangle.getX() - getX(x) < width))
                && ((getY(y) > rectangle.getY() && getY(y) - rectangle.getY() < rectangle.getHeight())
                || (getY(y) <= rectangle.getY() && rectangle.getY() - getY(y) < height));
    }

    private boolean roundRectangleCollision(int x, int y, Figure figure) {       //TO DO
        System.out.println("Simplified Version of Collision with RoundRectangle. In RoundRectangle");
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
        return xPosition != 0 && yPosition != 0 && Methods.pointDistance(circle.getX(), circle.getY(), points.get((xPosition + 1) + 3 * (yPosition + 1)).getX(), points.get((xPosition + 1) + 3 * (yPosition + 1)).getY()) <= circle.getRadius() || yPosition == 0 && ((xPosition < 0 && getX(x) - circle.getX() <= circle.getRadius())) || (yPosition < 0 && getY(y) - circle.getY() <= circle.getRadius()) || (yPosition > 0 && circle.getY() - getY(y) - height <= circle.getRadius());
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

    private void setTypeOfCorner(int corner, int xChange, int yChange) {
        if (xChange > Place.tileHalf && yChange > Place.tileHalf) {
            corners[corner].concave = true;
        } else if (xChange == Place.tileHalf && yChange == Place.tileHalf) {
            corners[corner].triangular = true;
        }
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
        updateCorners();
        updatePointsFromCorners();
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

    private void updatePointsFromCorners() {
        points.clear();
        for (Corner corner : corners) {
            corner.getPoints().stream().filter((point) -> (!points.contains(point))).forEach(points::add);
        }
        points.trimToSize();
    }

    public Point getPushValueOfCorner(int corner) {
        return changers[corner].get(corners, this);
    }

    public Point getCorner(int i) {
        return corners[i].getCorner();
    }

    public Point getNext(int i) {
        return corners[i].getNext();
    }

    public Point getPrevious(int i) {
        return corners[i].getPrevious();
    }

    public boolean isCornerPushed(int i) {
        return corners[i].changes != null;
    }

    public boolean isLeftBottomRound() {
        return corners[LEFT_BOTTOM].changes != null;
    }

    public boolean isRightBottomRound() {
        return corners[RIGHT_BOTTOM].changes != null;
    }

    public boolean isLeftTopRound() {
        return corners[LEFT_TOP].changes != null;
    }

    public boolean isRightTopRound() {
        return corners[RIGHT_TOP].changes != null;
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

    public boolean isCornerTriangular(int i) {
        return corners[i].isTriangular();
    }

    public boolean isCornerConcave(int i) {
        return corners[i].isConcave();
    }

    private interface changer {

        Point get(Corner[] corners, RoundRectangle owner);

        void push(int xChange, int yChange);

        void set(int xChange, int yChange);
    }

    private class Corner {

        private boolean triangular, concave;
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
            points[PREVIOUS] = new Point(getX() + changes[PREVIOUS].getX(), getY() + changes[PREVIOUS].getY());
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

        public List<Point> getPoints() {
            if (points[NEXT] == null) {
                return Collections.singletonList(points[CORNER]);
            } else {
                if (triangular) {
                    return Arrays.asList(points[PREVIOUS], points[NEXT]);
                } else {
                    return Arrays.asList(points);
                }
            }
        }

        public Point getCorner() {
            return points[CORNER];
        }

        public Point getPrevious() {
            return points[PREVIOUS];
        }

        public Point getNext() {
            return points[NEXT];
        }

        public boolean isTriangular() {
            return triangular;
        }

        public boolean isConcave() {
            return concave;
        }
    }
}
