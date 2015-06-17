/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Figure;
import collision.Rectangle;
import static collision.RoundRectangle.LEFT_BOTTOM;
import static collision.RoundRectangle.LEFT_TOP;
import static collision.RoundRectangle.RIGHT_BOTTOM;
import static collision.RoundRectangle.RIGHT_TOP;
import engine.BlueArray;
import engine.Delay;
import engine.Drawer;
import engine.Methods;
import engine.Point;
import engine.PointedValue;
import game.Settings;
import game.place.Place;

import java.awt.Polygon;
import java.util.List;

import navmeshpathfinding.NavigationMeshGenerator;
import net.packets.Update;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 *
 * @author przemek
 */
public abstract class Mob extends Entity {

    protected final double range;
    protected GameObject prey;
    protected Point[] path;

    protected Figure lastcollided;
    protected float aligment;
    protected double xAligment, yAligment;
    protected int currentPoint, oldPoint;
    public short mobID;
    public Delay delay = new Delay(250);
    private Point correction = new Point(-1000, -1000);
    private static int xDS, xDE, yDS, yDE;
    private List<PointedValue> correctionPoints = new BlueArray<>();
    private Point pastPosition = new Point();
    private double pastXSpeed, pastYSpeed;
    private int stuckCount, passedCount, alternateCount;
    private Point destination;
    private Point preyPoint = new Point();
    private Point[] castingPoints = {new Point(), new Point()};
    private Point[] castingDestination = {new Point(), new Point()};
    private Polygon poly = new Polygon();
    private int xPass, yPass, xInAWay, yInAWay;
    private Figure inAWay, lastInAWay;
    protected boolean passing, passed, choice, alternate;

    {
        delay.start();
    }

    private static final boolean DEBUG = false, VDEBUG = false;

    private static void DEBUG(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }

    public abstract void update();

    public Mob(int x, int y, double speed, int range, String name, Place place, String spriteName, boolean solid) {
        this.place = place;
        this.solid = solid;
        this.range = range;
        this.setMaxSpeed(speed);
        this.sprite = place.getSprite(spriteName);
        initialize(name, x, y);
    }

    public synchronized void look(GameObject[] players) {
        GameObject object;
        for (int i = 0; i < getPlace().playersCount; i++) {
            object = players[i];
            if (object.getMap() == map && Methods.pointDistance(object.getX(), object.getY(), getX(), getY()) < range) {
                prey = object;
                break;
            }
        }
    }

    public synchronized void chase(GameObject prey) {
        int scope = collision.getWidth() + collision.getHeight();
        if (prey != null) {
            if (VDEBUG && area != -1) {
                int xRef = map.areas[area].getXInPixels();
                int yRef = map.areas[area].getYInPixels();
                NavigationMeshGenerator.mesh.setPositions(new Point(prey.getX(), prey.getY()),
                        new Point(getX(), getY()), xRef, yRef);
            }
            if (path != null && !delay.isOver()) {
                destination = path[currentPoint];
                DEBUG("Follow path! " + currentPoint + "/" + (path.length - 1) + " - " + path[currentPoint]);
                if (Methods.pointDistance(getX(), getY(), path[currentPoint].getX(), path[currentPoint].getY()) < maxSpeed) {
                    DEBUG("Get to point! " + currentPoint + "/" + (path.length - 1));
                    if (currentPoint < path.length - 2) {
                        currentPoint++;
                    } else {
                        path = null;
                    }
                }
            } else {
                delay.start();
                boolean obstacleBeetween = false;
                if (area != -1) {
                    int xRef = map.areas[area].getXInPixels();
                    int yRef = map.areas[area].getYInPixels();
                    obstacleBeetween = map
                            .getArea(area)
                            .getNavigationMesh()
                            .lineIntersectsMeshBounds(getX() - xRef, getY() - yRef, prey.getX() - xRef,
                                    prey.getY() - yRef);
                }
                if (Methods.pointDistance(getX(), getY(), prey.getX(), prey.getY()) > scope || obstacleBeetween
                        || isStuck()) {
                    if (path == null
                            || (Methods.pointDistance(path[path.length - 1].getX(), path[path.length - 1].getY(),
                                    prey.getX(), prey.getY()) > maxSpeed) || obstacleBeetween || isStuck()) {
                        DEBUG("Looking for a path! ");
                        setPath(map.findPath(getX(), getY(), prey.getX(), prey.getY(), collision));
                    }

                    if (path != null) {
                        destination = path[currentPoint];
                    } else {
                        DEBUG("Follow pray - first! ");
                        preyPoint.set(prey.getX(), prey.getY());
                        destination = preyPoint;
                    }
                } else {
                    DEBUG("Follow pray! ");
                    preyPoint.set(prey.getX(), prey.getY());
                    destination = preyPoint;
                }
            }

            if (passing) {
                if (getX() == correction.getX() && getY() == correction.getY() || isStuck()
                        || (inAWay != null && (inAWay.getX() != xInAWay || inAWay.getY() != yInAWay))) {
                    passing = false;
                    passed = true;
                    if (VDEBUG) {
                        NavigationMeshGenerator.mesh.setCorrection(null, 0, 0, null);
                    }
                    DEBUG("PASSED " + correction);
                } else {
                    lastInAWay = inAWay;
                    isSomethingOnTheWay(correction);
                    destination = correction;
                    DEBUG("PASSING " + correction);
                }
            } else {
                if (passed) {
                    DEBUG("PASSED " + correction);
                } else {
                    if (path != null || destination.equals(preyPoint)) {
                        lastInAWay = null;
                    }
                    isSomethingOnTheWay(destination);
                    DEBUG("NORMAL");
                }
            }
            double angle = Methods.pointAngle360(getX(), getY(), destination.getX(), destination.getY());
            int destX = Math.abs(getX() - destination.getX());
            int destY = Math.abs(getY() - destination.getY());
            xSpeed = Methods.xRadius(angle, Math.min(maxSpeed, destX));
            ySpeed = Methods.yRadius(angle, Math.min(maxSpeed, destY));
            if (getY() != destination.getY()) {
                if (Math.abs(ySpeed) <= 1) {
                    ySpeed = Math.signum(ySpeed);
                } else if (destY < maxSpeed) {
                    ySpeed = Math.signum(ySpeed) * destY;
                }
            } else {
                ySpeed = 0;
            }
            if (passed) {
                DEBUG("xPass " + xPass + " xSpeed " + Math.signum(xSpeed) + " yPass " + yPass + " ySpeed "
                        + Math.signum(ySpeed));
                if (Math.signum(xSpeed) != xPass) {
                    xSpeed = 0;
                }
                if (Math.signum(ySpeed) != yPass) {
                    ySpeed = 0;
                }
                if (xSpeed != 0 && ySpeed != 0) {
                    if (choice) {
                        ySpeed = 0;
                    } else {
                        xSpeed = 0;
                    }
                } else if (xSpeed == 0 && ySpeed == 0) {
                    if (choice) {
                        xSpeed = maxSpeed;
                    } else {
                        ySpeed = maxSpeed;
                    }
                }
                passedCount++;
                if (passedCount >= 4) {
                    passedCount = 0;
                    passed = false;
                    choice = !choice;
                }
            } else if (!alternate) {
                if (xSpeed * pastXSpeed > 0 && getX() == pastPosition.getX() && Math.abs(ySpeed) > 0) {
                    xSpeed = 0;
                    ySpeed = Math.signum(ySpeed) * maxSpeed;
                    alternate = true;
                } else if (ySpeed * pastYSpeed > 0 && getY() == pastPosition.getY() && Math.abs(xSpeed) > 0) {
                    ySpeed = 0;
                    xSpeed = Math.signum(xSpeed) * maxSpeed;

                }
            } else {
                alternate = true;
                xSpeed = pastXSpeed;
                ySpeed = pastYSpeed;
                alternateCount++;
                int value = Methods
                        .roundDouble((pastYSpeed > pastXSpeed ? collision.getHeight() : collision.getWidth())
                                / maxSpeed);
                if (VDEBUG) {
                    NavigationMeshGenerator.mesh.setNote("Value " + value);
                }
                if (alternateCount > value) {
                    alternateCount = 0;
                    alternate = false;
                }
            }
            if (getX() != destination.getX()) {
                if (Math.abs(xSpeed) <= 1) {
                    xSpeed = Math.signum(xSpeed);
                } else if (destX < maxSpeed) {
                    xSpeed = Math.signum(xSpeed) * destX;
                }
            } else {
                xSpeed = 0;
            }
            pastXSpeed = xSpeed;
            pastYSpeed = ySpeed;
            pastPosition.set(getX(), getY());
            DEBUG(" RABBIT " + getX() + " " + getY() + " " + xSpeed + " " + ySpeed);
        }
    }

    private boolean isStuck() {
        if (getX() == pastPosition.getX() && getY() == pastPosition.getY()) {
            stuckCount++;
            if (stuckCount >= 12) {
                return true;
            }
        } else {
            stuckCount = 0;
        }
        return false;
    }

    private void isSomethingOnTheWay(Point destination) {
        List<Figure> close = Figure.whatClose(this, getX(), getY(), ((int) (range) >> 2), getX(), getY(), map);
        if (close.isEmpty()) {
            return;
        }

        close.sort((Figure f1, Figure f2) -> f1.getLightDistance() - f2.getLightDistance());

        int xS = collision.getX();
        int xE = collision.getXEnd();
        int yS = collision.getY();
        int yE = collision.getYEnd();

        setPolygonForTesting(xS, xE, yS, yE, destination);
        inAWay = anyFigureInAWay(poly, close);

        if (inAWay == lastInAWay) {
            return;
        }

        int correctionX = (collision.getWidth() / 2) + 1;
        int correctionY = (collision.getHeight() / 2) + 1;

        correctionPoints.clear();

        if (isNeedToPass(inAWay)) {
            xInAWay = inAWay.getX();
            yInAWay = inAWay.getY();
            DEBUG(" IN A WAY: " + inAWay + " " + inAWay.getX() + " " + inAWay.getY() + " " + inAWay.getXEnd() + " "
                    + inAWay.getYEnd());

            correction.set(inAWay.getX() - correctionX, inAWay.getY() - correctionY);
            setPolygonForTesting(xS, xE, yS, yE, correction);
            if (anyFigureInAWay(poly, close) == null) {
                correctionPoints.add(new PointedValue(correction.getX(), correction.getY(), LEFT_TOP));
            }

            correction.set(inAWay.getX() - correctionX, inAWay.getYEnd() + correctionY);
            setPolygonForTesting(xS, xE, yS, yE, correction);
            if (anyFigureInAWay(poly, close) == null) {
                correctionPoints.add(new PointedValue(correction.getX(), correction.getY(), LEFT_BOTTOM));
            }

            correction.set(inAWay.getXEnd() + correctionX, inAWay.getYEnd() + correctionY);
            setPolygonForTesting(xS, xE, yS, yE, correction);
            if (anyFigureInAWay(poly, close) == null) {
                correctionPoints.add(new PointedValue(correction.getX(), correction.getY(), RIGHT_BOTTOM));
            }

            correction.set(inAWay.getXEnd() + correctionX, inAWay.getY() - correctionY);
            setPolygonForTesting(xS, xE, yS, yE, correction);
            if (anyFigureInAWay(poly, close) == null) {
                correctionPoints.add(new PointedValue(correction.getX(), correction.getY(), RIGHT_TOP));
            }

            int min = Integer.MAX_VALUE, temp;
            PointedValue closest = null;
            for (PointedValue point : correctionPoints) {
                temp = Methods.pointDistance(point.getX(), point.getY(), destination.getX(), destination.getY());
                if (temp < min) {
                    min = temp;
                    closest = point;
                }
            }
            if (closest != null) {
                switch (closest.getValue()) {
                    case LEFT_TOP:
                        xPass = 1;
                        yPass = 1;
                        closest.set(closest.getX() + 1, closest.getY() + 1);
                        break;
                    case LEFT_BOTTOM:
                        xPass = 1;
                        yPass = -1;
                        closest.set(closest.getX() + 1, closest.getY() - 1);
                        break;
                    case RIGHT_BOTTOM:
                        xPass = -1;
                        yPass = -1;
                        closest.set(closest.getX() - 1, closest.getY() - 1);
                        break;
                    case RIGHT_TOP:
                        xPass = -1;
                        yPass = 1;
                        closest.set(closest.getX() - 1, closest.getY() + 1);
                        break;
                }
                DEBUG("Czy dobry punkt korygujący? --------- " + closest.getValue());
                if ((closest.getY() >= getY() && closest.getY() <= destination.getY())
                        || (closest.getY() <= getY() && closest.getY() >= destination.getY())) {
                    correction.set(closest.getX(), closest.getY());
                    if (VDEBUG && area != -1) {
                        int xRef = map.areas[area].getXInPixels();
                        int yRef = map.areas[area].getYInPixels();
                        NavigationMeshGenerator.mesh.setCorrection(correction, xRef, yRef, inAWay);
                    }
                    DEBUG("TAK! -------------------------");
                    passing = true;
                    passed = false;
                }
            }
        }
    }

    private boolean isNeedToPass(Figure inAWay) {
        if (inAWay != null) {
            if (path != null) {
                if (path.length - 1 != currentPoint) {
                    return true;
                } else {
                    return isPassingRequired(inAWay);
                }
            } else {
                return isPassingRequired(inAWay);
            }
        }
        return false;
    }

    private boolean isPassingRequired(Figure inAWay) {
        if (inAWay.getY() == collision.getYEnd()) {
            return yDE > inAWay.getY();
        } else if (inAWay.getYEnd() == collision.getY()) {
            return yDS < collision.getY();
        } else if (inAWay.getX() == collision.getXEnd()) {
            return xDE > inAWay.getX();
        } else if (inAWay.getXEnd() == collision.getX()) {
            return xDS < collision.getX();
        }
        return false;
    }

    private void setPolygonForTesting(int xS, int xE, int yS, int yE, Point destination) {
        Methods.getCastingPoints((2 * getX() - destination.getX()), (2 * getY() - destination.getY()), xS, xE, yS, yE,
                castingPoints);
        setDestinationCorners(destination);
        Methods.getCastingPoints((2 * destination.getX() - getX()), (2 * destination.getY() - getY()), xDS, xDE, yDS,
                yDE, castingDestination);
        poly.reset();
        poly.addPoint(castingPoints[0].getX(), castingPoints[0].getY());
        poly.addPoint(castingPoints[1].getX(), castingPoints[1].getY());
        if (castingDestination[0].getY() != castingDestination[1].getY()) {
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
        } else if (castingDestination[0].getX() > castingDestination[1].getX()) {
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
        } else {
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
        }
        if (VDEBUG && area != -1) {
            int xRef = map.areas[area].getXInPixels();
            int yRef = map.areas[area].getYInPixels();
            NavigationMeshGenerator.mesh.setPoly(poly, xRef, yRef);
        }
    }

    private void setDestinationCorners(Point destination) {
        xDS = destination.getX() - collision.getWidth() / 2;
        xDE = destination.getX() + collision.getWidth() / 2;
        yDS = destination.getY() - collision.getHeight() / 2;
        yDE = destination.getY() + collision.getHeight() / 2;
    }

    private Figure anyFigureInAWay(Polygon poly, List<Figure> close) {
        for (Figure figure : close) {
            if (poly.contains(figure.getXCentral(), figure.getYCentral())) {
                return figure;
            }
            for (Point point : figure.getPoints()) {
                if (poly.contains(point.getX(), point.getY())) {
                    return figure;
                }
            }
        }
        return null;
    }

    public synchronized void setPath(Point[] path) {
        if (path != null && path.length > 1) {
            currentPoint = 1;
            correctDestinationPointIfNeeded(path);
            this.path = path;
            DEBUG("Znaleziono drogę!");
        } else if (isStuck()) {
            this.path = null;
            DEBUG("Nie znaleziono drogi!");
        }
    }

    private void correctDestinationPointIfNeeded(Point[] path) {
        try {
            if (destination != null) {
                List<Figure> close = Figure
                        .whatClose(this, getX(), getY(), (collision.getWidth() + collision.getHeight()) * 2,
                                destination.getX(), destination.getY(), map);
                if (!close.isEmpty()) {
                    close.sort((Figure f1, Figure f2) -> f1.getLightDistance() - f2.getLightDistance());
                    Rectangle testing = Rectangle.createTileRectangle(collision.getWidth(), collision.getHeight());
                    Point desired = path[path.length - 1];
                    setTestingPosition(testing, desired);
                    Figure collided = whatColidesWithTesting(close, testing);
                    if (collided != null) {
                        DEBUG("Correction " + collided + " " + collided.getX() + " " + collided.getY() + " " + desired);
                        if (collided.getX() >= desired.getX()) {
                            if (collided.getY() >= desired.getY()) {
                                path[path.length - 1].set(collided.getX() - (collision.getWidth() / 2), collided.getY()
                                        - (collision.getHeight() / 2));
                            } else if (collided.getYEnd() <= desired.getY()) {
                                path[path.length - 1].set(collided.getX() - (collision.getWidth() / 2),
                                        collided.getYEnd() + (collision.getHeight() / 2));
                            } else {
                                path[path.length - 1].setX(collided.getX() - (collision.getWidth() / 2));
                            }
                        } else if (collided.getXEnd() > desired.getX()) {
                            if (collided.getY() >= desired.getY()) {
                                path[path.length - 1].setY(collided.getY() - (collision.getHeight() / 2));
                            } else if (collided.getYEnd() <= desired.getY()) {
                                path[path.length - 1].setY(collided.getYEnd() + (collision.getHeight() / 2));
                            }
                        } else {
                            if (collided.getY() >= desired.getY()) {
                                path[path.length - 1].set(collided.getXEnd() + (collision.getWidth() / 2),
                                        collided.getY() - (collision.getHeight() / 2));
                            } else if (collided.getYEnd() <= desired.getY()) {
                                path[path.length - 1].set(collided.getXEnd() + (collision.getWidth() / 2),
                                        collided.getYEnd() + (collision.getHeight() / 2));
                            } else {
                                path[path.length - 1].setX(collided.getXEnd() + (collision.getWidth() / 2));
                            }
                        }
                    }
                    int xRef = map.areas[area].getXInPixels();
                    int yRef = map.areas[area].getYInPixels();
                    NavigationMeshGenerator.mesh.setEnd(path[path.length - 1], xRef, yRef);
                }
            }
        } catch (NullPointerException exception) {
            System.out.println("destination: " + destination + " map " + map);
            Methods.swallowLogAndPrint(exception);
        }
    }

    private Figure whatColidesWithTesting(List<Figure> close, Figure testing) {
        for (Figure figure : close) {
            if (testing.isCollideSingle(0, 0, figure)) {
                return figure;
            }
        }
        return null;
    }

    private void setTestingPosition(Rectangle testing, Point desired) {
        testing.setXStart(desired.getX() - (testing.getWidth() / 2));
        testing.setYStart(desired.getY() - (testing.getHeight() / 2));
        testing.updateTilePoints();
    }

    @Override
    protected boolean isCollided(int xMagnitude, int yMagnitude) {
        return collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map)
                || collision.isCollidePlayer(getX() + xMagnitude, getY() + yMagnitude, getPlace());
    }

    @Override
    public Player getCollided(int xMagnitude, int yMagnitude) {
        return collision.firstPlayerCollide(getX() + xMagnitude, getY() + yMagnitude, getPlace());
    }

    @Override
    protected void move(int xPosition, int yPosition) {
        setPosition(x + xPosition, y + yPosition);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            }
            glTranslatef(getX(), getY(), 0);
            sprite.render();

            if (Settings.scaled) {
                glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            }
            Drawer.renderStringCentered(name, (int) ((collision.getWidth() * Place.getCurrentScale()) / 2),
                    (int) ((collision.getHeight() * Place.getCurrentScale()) / 2), place.standardFont,
                    map.getLightColor());
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapeInShade(sprite, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapeInBlack(sprite);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapePartInShade(sprite, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapePartInBlack(sprite, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void updateRest(Update update) {
    }

    @Override
    public void updateOnline() {
    }
}
