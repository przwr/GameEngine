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
import sun.security.tools.PathList;

/**
 *
 * @author przemek
 */
public abstract class Mob extends Entity {

    protected final double range;
    protected GameObject prey;
    protected Point[] path, oldPath;

    protected Figure lastColided;
    protected float aligment;
    protected double xAligment, yAligment;
    protected int currentPoint, oldPoint;
    public short mobID;
    public Delay delay = new Delay(250);
    private Point correction = new Point(-1000, -1000);
    private static int xDS, xDE, yDS, yDE;
    private List<PointedValue> correctionPoints = new BlueArray<>();
    private Point pastPosition = new Point();
    private int stuckCount, passedCount;
    private Point destination;
    private Point preyPoint = new Point();
    private Point[] castingPoints = {new Point(), new Point()};
    private Point[] castingDestination = {new Point(), new Point()};
    private Polygon poly = new Polygon();
    private int xPass, yPass;
    protected boolean passing, passed, foundPath;

    {
        delay.start();
    }

    private static final boolean DEBUG = false;

    private static void DEBUG(String message) {
        if (DEBUG) {
            System.out.println(message);
        }
    }

    public abstract void update();

    public Mob(int x, int y, double speed, int range, String name, Place place, String spriteName, boolean solid, short mobID) {
        this.place = place;
        this.solid = solid;
        this.range = range;
        this.setMaxSpeed(speed);
        this.sprite = place.getSprite(spriteName);
        initialize(name, x, y);
        this.mobID = mobID;
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
//        System.out.println("Position : " + getX() + " " + getY() + " XBeg " + collision.getX() + " YBeg " + collision.getY() + " XEnd " + collision.getXEnd() + " YEnd " + collision.getYEnd());
        if (prey != null) {
            if (area != -1) {
                int xRef = map.areas[area].getXInPixels();
                int yRef = map.areas[area].getYInPixels();
                NavigationMeshGenerator.mesh.setPositions(new Point(prey.getX(), prey.getY()), new Point(getX(), getY()), xRef, yRef);
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
                boolean obstacleBettween = area == -1 || map.getArea(area).getNavigationMesh().lineIntersectsMeshBounds(getX(), getY(), prey.getX(), prey.getY());
                if (Methods.pointDistance(getX(), getY(), prey.getX(), prey.getY()) > scope || obstacleBettween || isStuck()) {
                    if (path == null || (Methods.pointDistance(path[path.length - 1].getX(), path[path.length - 1].getY(), prey.getX(), prey.getY()) > maxSpeed) || obstacleBettween || isStuck()) {
                        DEBUG("Looking for a path! ");
                        setPath(map.findPath(getX(), getY(), prey.getX(), prey.getY(), collision));
                    }
                    if (path == null && oldPath != null) {
                        DEBUG("Follow old path!");
                        destination = oldPath[oldPoint];
                    } else {
                        DEBUG("Follow pray - first!");
                        preyPoint.set(prey.getX(), prey.getY());
                        destination = preyPoint;
                    }
                } else {
                    path = null;
                    DEBUG("Follow pray! ");
                    preyPoint.set(prey.getX(), prey.getY());
                    destination = preyPoint;
                }
            }

            if (passing) {
                if (getX() == correction.getX() && getY() == correction.getY() || isStuck()) {
                    passing = false;
                    passed = true;
                    NavigationMeshGenerator.mesh.setCorrection(null, 0, 0, null);
                    DEBUG("PASSED " + correction);
                } else {
                    destination = correction;
                    DEBUG("PASSING " + correction);
                }
            } else {
                if (passed) {
                    DEBUG("PASSED " + correction);
                } else {
                    isSomethingOnTheWay(destination);
                    DEBUG("NORMAL");
                }
            }
            double angle = Methods.pointAngleMax360(getX(), getY(), destination.getX(), destination.getY());
            int destX = Math.abs(getX() - destination.getX());
            int destY = Math.abs(getY() - destination.getY());
            xSpeed = Methods.xRadius(angle, Math.min(maxSpeed, destX));
            ySpeed = Methods.yRadius(angle, Math.min(maxSpeed, destY));
            if (isStuck() || destX < maxSpeed || destY < maxSpeed) {
                if (getX() != destination.getX()) {
                    if (Math.abs(xSpeed) <= 1) {
                        xSpeed = Math.signum(xSpeed);
                    } else if (destX < maxSpeed) {
                        xSpeed = Math.signum(xSpeed) * destX;
                    }
                } else {
                    xSpeed = 0;
                }
                if (getY() != destination.getY()) {
                    if (Math.abs(ySpeed) <= 1) {
                        ySpeed = Math.signum(ySpeed);
                    } else if (destY < maxSpeed) {
                        ySpeed = Math.signum(ySpeed) * destY;
                    }
                } else {
                    ySpeed = 0;
                }
            }
            if (passed) {
                DEBUG("xPass " + xPass + " xSpeed " + Math.signum(xSpeed) + " yPass " + yPass + " ySpeed " + Math.signum(ySpeed));
                if (Math.signum(xSpeed) != xPass) {
                    xSpeed = 0;
                }
                if (Math.signum(ySpeed) != yPass) {
                    ySpeed = 0;
                }
                if (xSpeed != 0 && ySpeed != 0) {
                    // System.out.println("--> OBA NIE ZERA 00 00");
                    // Źle wybiera, jak jest blisko ściany
                    if (Math.random() > 0.5) {
                        ySpeed = 0;
                    } else {
                        xSpeed = 0;
                    }
                } else if (xSpeed == 0 && ySpeed == 0) {
                    // System.out.println("--> ZERA 00 00");
                    // ySpeed = yPass * maxSpeed;
                }
                passedCount++;
                if (passedCount >= 4) {
                    passedCount = 0;
                    passed = false;
                }
            }
            pastPosition.set(getX(), getY());
            DEBUG(" RABBIT " + getX() + " " + getY() + " " + xSpeed + " " + ySpeed);
        }
    }

    private boolean isStuck() {
        if (getX() == pastPosition.getX() && getY() == pastPosition.getY()) {
            stuckCount++;
            if (stuckCount >= 8) {
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
        Figure inAWay = anyFigureInAWay(poly, close);

        int correctionX = collision.getWidth() / 2;
        int correctionY = collision.getHeight() / 2;

//        System.out.println("Size: " + colided.size() + colided);
        correctionPoints.clear();

        if (inAWay != null) {
            if ((path != null && path.length - 1 != currentPoint) || (inAWay.getX() != collision.getXEnd() && inAWay.getXEnd() != collision.getX() && inAWay.getY() != collision.getYEnd() && inAWay.getYEnd() != collision.getY())) {
                DEBUG(" IN A WAY: " + inAWay + " " + inAWay.getX() + " " + inAWay.getY() + " " + inAWay.getXEnd() + " " + inAWay.getYEnd());
                Figure correct;
                correction.set(inAWay.getX() - correctionX, inAWay.getY() - correctionY);
                setPolygonForTesting(xS, xE, yS, yE, correction);
                correct = anyFigureInAWay(poly, close);
                if (correct == null) {
                    correctionPoints.add(new PointedValue(correction.getX(), correction.getY(), LEFT_TOP));
                }

                correction.set(inAWay.getX() - correctionX, inAWay.getYEnd() + correctionY);
                setPolygonForTesting(xS, xE, yS, yE, correction);
                correct = anyFigureInAWay(poly, close);
                if (correct == null) {
                    correctionPoints.add(new PointedValue(correction.getX(), correction.getY(), LEFT_BOTTOM));
                }

                correction.set(inAWay.getXEnd() + correctionX, inAWay.getYEnd() + correctionY);
                setPolygonForTesting(xS, xE, yS, yE, correction);
                correct = anyFigureInAWay(poly, close);
                if (correct == null) {
                    correctionPoints.add(new PointedValue(correction.getX(), correction.getY(), RIGHT_BOTTOM));
                }

                correction.set(inAWay.getXEnd() + correctionX, inAWay.getY() - correctionY);
                setPolygonForTesting(xS, xE, yS, yE, correction);
                correct = anyFigureInAWay(poly, close);
                if (correct == null) {
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
                if (closest != null && (closest.getX() != getX() || closest.getY() != getY())) {
                    if (closest.getY() >= getY() && closest.getY() <= destination.getY() || closest.getY() <= getY() && closest.getY() >= destination.getY()) {
                        correction.set(closest.getX(), closest.getY());

                        int xRef = map.areas[area].getXInPixels();
                        int yRef = map.areas[area].getYInPixels();
                        NavigationMeshGenerator.mesh.setCorrection(correction, xRef, yRef, inAWay);

                        passing = true;
                        passed = false;
                        //System.out.println("CASE: " + closest.getValue());
                        switch (closest.getValue()) {
                            case LEFT_TOP:
                                xPass = 1;
                                yPass = 1;
                                break;
                            case LEFT_BOTTOM:
                                xPass = 1;
                                yPass = -1;
                                break;
                            case RIGHT_BOTTOM:
                                xPass = -1;
                                yPass = -1;
                                break;
                            case RIGHT_TOP:
                                xPass = -1;
                                yPass = 1;
                                break;
                        }
                    }
                }
            }
        }
    }

    private void setPolygonForTesting(int xS, int xE, int yS, int yE, Point destination) {
        Methods.getCastingPoints((2 * getX() - destination.getX()), (2 * getY() - destination.getY()), xS, xE, yS, yE, castingPoints);
        setDestinationCorners(destination);
        Methods.getCastingPoints((2 * destination.getX() - getX()), (2 * destination.getY() - getY()), xDS, xDE, yDS, yDE, castingDestination);
        poly.reset();
        poly.addPoint(castingPoints[0].getX(), castingPoints[0].getY());
        poly.addPoint(castingPoints[1].getX(), castingPoints[1].getY());
        if (castingDestination[0].getY() > castingDestination[1].getY()) {
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
        } else if (castingDestination[0].getY() < castingDestination[1].getY()) {
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
        } else if (castingDestination[0].getX() > castingDestination[1].getX()) {
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
        } else {
            poly.addPoint(castingDestination[1].getX(), castingDestination[1].getY());
            poly.addPoint(castingDestination[0].getX(), castingDestination[0].getY());
        }
    }

    private void setDestinationCorners(Point destination) {
        xDS = destination.getX() - collision.getWidth() / 2;
        xDE = destination.getX() + collision.getWidth() / 2;
        yDS = destination.getY() - collision.getHeight() / 2;
        yDE = destination.getY() + collision.getHeight() / 2;
    }

    private Figure anyFigureInAWay(Polygon poly, List<Figure> colided) {
        for (Figure figure : colided) {
            for (Point point : figure.getPoints()) {
                if (poly.contains(point.getX(), point.getY())) {
                    return figure;
                }
            }
        }
        return null;
    }

    public synchronized void setPath(Point[] path) {
        if (path != null) {
            currentPoint = (path.length > 1) ? 1 : 0;
            foundPath = true;
            correctDestinationPointIfNeeded(path);
            DEBUG("Znaleziono drogę!");
        } else {
            foundPath = false;
            DEBUG("Nie znaleziono drogi!");
            oldPath = this.path;
            oldPoint = this.currentPoint;
        }
        this.path = path;
    }

    private void correctDestinationPointIfNeeded(Point[] path) {
        //TODO kolizja tak jak z tilem - sprawdzenie z czym koliduje i przesunięcie w odpowiednią stronę
        List<Figure> close = Figure.whatClose(this, getX(), getY(), (collision.getWidth() + collision.getHeight()) * 2, destination.getX(), destination.getY(), map);
        if (!close.isEmpty()) {
            close.sort((Figure f1, Figure f2) -> f1.getLightDistance() - f2.getLightDistance());
            Rectangle testing = Rectangle.createTileRectangle(collision.getWidth(), collision.getHeight());
            Point desired = path[path.length - 1];
            setTestingPosition(testing, desired);
            Figure colided = whatColidesWithTesting(close, testing);
            if (colided != null) {
                DEBUG("Correction " + colided + " " + colided.getX() + " " + colided.getY() + " " + desired);
                if (colided.getX() >= desired.getX()) {        // obiekt po LEWEJ
                    if (colided.getY() >= desired.getY()) {      // na górze
                        path[path.length - 1].set(colided.getX() - (collision.getWidth() / 2), colided.getY() - (collision.getHeight() / 2));
                    } else if (colided.getYEnd() <= desired.getY()) {                                                                // na dole
                        path[path.length - 1].set(colided.getX() - (collision.getWidth() / 2), colided.getYEnd() + (collision.getHeight() / 2));
                    } else {      // na środku
                        path[path.length - 1].setX(colided.getX() - (collision.getWidth() / 2));
                    }
                } else if (colided.getXEnd() > desired.getX()) {   // obiekt po ŚRODKU
                    if (colided.getY() >= desired.getY()) {      // na górze
                        path[path.length - 1].setY(colided.getY() - (collision.getHeight() / 2));
                    } else if (colided.getYEnd() <= desired.getY()) {                                                                // na dole
                        path[path.length - 1].setY(colided.getYEnd() + (collision.getHeight() / 2));
                    }      // na środku
                } else {                                        // obiekt po PRAWEJ
                    if (colided.getY() >= desired.getY()) {      // na górze
                        path[path.length - 1].set(colided.getXEnd() + (collision.getWidth() / 2), colided.getY() - (collision.getHeight() / 2));
                    } else if (colided.getYEnd() <= desired.getY()) {  // na dole
                        path[path.length - 1].set(colided.getXEnd() + (collision.getWidth() / 2), colided.getYEnd() + (collision.getHeight() / 2));
                    } else {      // na środku
                        path[path.length - 1].setX(colided.getXEnd() + (collision.getWidth() / 2));
                    }
                }
            }
            int xRef = map.areas[area].getXInPixels();
            int yRef = map.areas[area].getYInPixels();
            NavigationMeshGenerator.mesh.setEnd(path[path.length - 1], xRef, yRef);
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
    protected boolean isColided(int xMagnitude, int yMagnitude) {
        return collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map) || collision.isCollidePlayer(getX() + xMagnitude, getY() + yMagnitude, getPlace());
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
                    (int) ((collision.getHeight() * Place.getCurrentScale()) / 2),
                    place.standardFont,
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
