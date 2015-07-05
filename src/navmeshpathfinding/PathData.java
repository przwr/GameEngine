/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import collision.Figure;
import engine.PointContener;
import collision.Rectangle;
import engine.BlueArray;
import engine.Delay;
import engine.Methods;
import engine.Point;
import engine.PointedValue;
import engine.PointedValueContener;
import game.gameobject.Entity;
import java.awt.Polygon;
import java.util.BitSet;
import java.util.List;

/**
 *
 * @author przemek
 */
public class PathData {

    protected final static int PASSING = 0, PASSED = 1, CHOICE = 2, BLOCKED = 3, STUCK = 4, OBSTACLE_BEETWEEN = 5, PATH_REQUESTED = 6, PATH_RETURNED = 7;
    protected final int width, height, widthHalf, heightHalf, xCorrection, yCorrection;
    protected final Point finalDestination = new Point(), pastPosition = new Point(), preyPoint = new Point(), correction = new Point(), last1CorrectionPoint = new Point(), last2CorrectionPoint = new Point(), tempCorrection = new Point();
    protected final Rectangle testing;

    protected BitSet flags = new BitSet();
    protected int x, y, xS, xE, yS, yE, xRef, yRef, stuckCount, passedCount, alternateCount, min, temp;
    protected int xDS, xDE, yDS, yDE, xPass, yPass, xInAWay, yInAWay, currentPoint, xDistance, yDistance, scope, lastCorner = -1;
    protected double xSpeed, ySpeed, pastXSpeed, pastYSpeed;
    protected Point destination, desired;
    protected PointedValue closest;
    protected Point[] castingPoints = {new Point(), new Point()};
    protected Point[] castingDestination = {new Point(), new Point()};
    protected PointContener path = new PointContener(16);
    protected PointContener newPath;
    protected PointedValueContener correctionPoints = new PointedValueContener(4);
    protected List<Figure> close = new BlueArray<>();
    protected Figure inAWay, lastInAWay, collided;
    protected Polygon poly = new Polygon();
    protected Delay delay = new Delay(250);

    public PathData(Entity owner, int scope) {
        this.scope = scope;
        width = owner.getCollision().getWidth();
        widthHalf = width / 2;
        height = owner.getCollision().getHeight();
        heightHalf = height / 2;
        xCorrection = widthHalf + 1;
        yCorrection = heightHalf + 1;
        testing = Rectangle.createTileRectangle(width, height);
        delay.start();
    }

    public void update(Entity owner, int xDest, int yDest) {
        x = owner.getX();
        y = owner.getY();
        xS = owner.getCollision().getX();
        xE = owner.getCollision().getXEnd();
        yS = owner.getCollision().getY();
        yE = owner.getCollision().getYEnd();
        finalDestination.set(xDest, yDest);
        if (owner.getArea() != -1) {
            xRef = owner.getMap().areas[owner.getArea()].getXInPixels();
            yRef = owner.getMap().areas[owner.getArea()].getYInPixels();
        }
        Figure.updateWhatClose(owner, x, y, ((int) (owner.getRange()) >> 2), x, y, owner.getMap(), close);
        close.sort((Figure f1, Figure f2) -> f1.getLightDistance() - f2.getLightDistance());
        flags.set(OBSTACLE_BEETWEEN, isObstacleBeetween());
        updateStuck();
    }

    private void updateStuck() {
        if (x == pastPosition.getX() && y == pastPosition.getY()) {
            stuckCount++;
            if (stuckCount >= 2) {
                flags.set(STUCK);
                return;
            }
        } else {
            stuckCount = 0;
        }
        flags.clear(STUCK);
    }

    private boolean isObstacleBeetween() {
        if (close.isEmpty()) {
            return false;
        }
        PathStrategyCore.setPolygonForTesting(this, finalDestination);
        return PathStrategyCore.anyFigureInAWay(poly, close) != null;
    }

    public void calculateSpeed(double maxSpeed) {
        if (!flags.get(PASSED)) {
            double angle = Methods.pointAngleMax360(x, y, destination.getX(), destination.getY());
            xDistance = Math.abs(x - destination.getX());
            yDistance = Math.abs(y - destination.getY());
            xSpeed = Methods.xRadius(angle, Math.min(maxSpeed, xDistance));
            ySpeed = Methods.yRadius(angle, Math.min(maxSpeed, yDistance));
        }
    }

    public void rememberPast() {
        pastXSpeed = xSpeed;
        pastYSpeed = ySpeed;
        pastPosition.set(x, y);
    }

    public Point getCurrentPoint() {
        return path.get(currentPoint);
    }

    public Point getLastPoint() {
        return path.get(path.size() - 1);
    }

    public void clearPath() {
        path.clear();
    }

    public double getXSpeed() {
        return xSpeed;
    }

    public double getYSpeed() {
        return ySpeed;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

}
