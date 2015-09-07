/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import engine.ErrorHandler;
import engine.Methods;
import engine.Time;
import game.gameobject.temporalmodifiers.TemporalChanger;
import game.place.Place;
import java.util.ArrayList;
import java.util.Iterator;
import navmeshpathfinding.PathData;
import navmeshpathfinding.PathStrategy;
import net.jodk.lang.FastMath;
import net.packets.Update;
import org.newdawn.slick.Color;

/**
 * @author przemek
 */
public abstract class Entity extends GameObject {

    protected static final Color JUMP_SHADOW_COLOR = new Color(0, 0, 0, 51);
    public final Update[] updates = new Update[4];
    public int lastAdded;
    protected double range;
    protected GameObject target;
    protected PathData pathData;
    protected double xEnvironmentalSpeed;
    protected double yEnvironmentalSpeed;
    protected double xSpeed;
    protected double ySpeed;
    protected boolean jumping;
    protected boolean hop;
    protected Place place;
    PathStrategy pathStrategy;
    private double maxSpeed;
    private double resistance = 1;
    private boolean unableToMove;
    private Update currentUpdate;
    private int currentUpdateID, deltasCount, xPosition, yPosition, xDelta, yDelta, xDestination, yDestination;
    private Player collided;

    private ArrayList<TemporalChanger> changers;

    public abstract void updateOnline();

    protected abstract void updateRest(Update update);

    protected abstract boolean isCollided(int xMagnitude, int yMagnitude);

    protected abstract Player getCollided(int xMagnitude, int yMagnitude);

    protected abstract void move(int xPosition, int yPosition);

    protected void setPathStrategy(PathStrategy pathStrategy, int scope) {
        pathData = new PathData(this, scope);
        this.pathStrategy = pathStrategy;
    }

    public synchronized void updateSoft() {
        try {
            if (canUpdate()) {
                if (inSameUpdate()) {
                    useCurrentUpdateSoft();
                } else {
                    useNextUpdateSoft();
                }
            }
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in "
                    + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    private void useCurrentUpdateSoft() {
        xDestination = currentUpdate.getX() - currentUpdate.getXDeltas().get(deltasCount);
        yDestination = currentUpdate.getY() - currentUpdate.getYDeltas().get(deltasCount);
        if (collision.isCollideSolid(xDestination, yDestination, map)) {
            moveIfPossible(xDestination - getX(), yDestination - getY());
        } else {
            setPosition(xDestination, yDestination);
        }
        deltasCount++;
    }

    private void useNextUpdateSoft() {
        if (currentUpdateID == 3) {
            currentUpdateID = 0;
        } else {
            currentUpdateID++;
        }
        currentUpdate = updates[currentUpdateID];
        if (currentUpdate != null) {
            updateRest(currentUpdate);
            xDestination = currentUpdate.getX();
            yDestination = currentUpdate.getY();
            if (collision.isCollideSolid(xDestination, yDestination, map)) {
                moveIfPossible(xDestination - getX(), yDestination - getY());
            } else {
                setPosition(xDestination, yDestination);
            }
            deltasCount = 0;
        }
    }

    public synchronized void updateHard() {
        try {
            if (canUpdate()) {
                if (inSameUpdate()) {
                    useCurrentUpdateHard();
                } else {
                    useNextUpdateHard();
                }
            }
        } catch (Exception exception) {
            String error = "ERROR: - " + exception.getMessage() + " in "
                    + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass();
            ErrorHandler.logAndPrint(error);
        }
    }

    private void useCurrentUpdateHard() {
        moveToPoint(currentUpdate.getX() - currentUpdate.getXDeltas().get(deltasCount) - getX(), currentUpdate.getY()
                - currentUpdate.getYDeltas().get(deltasCount) - getY());
        deltasCount++;
    }

    private void useNextUpdateHard() {
        if (currentUpdateID == 3) {
            currentUpdateID = 0;
        } else {
            currentUpdateID++;
        }
        currentUpdate = updates[currentUpdateID];
        if (currentUpdate != null) {
            updateRest(currentUpdate);
            moveToPoint(currentUpdate.getX() - getX(), currentUpdate.getY() - getY());
            deltasCount = 0;
        }
    }

    private boolean canUpdate() {
        return updates[3] != null && ((currentUpdateID != 3 || lastAdded != 0) && (currentUpdateID + 1 != lastAdded));
    }

    private boolean inSameUpdate() {
        return currentUpdate != null && deltasCount < currentUpdate.getXDeltas().size();
    }

    public void updateChangers() {
        if (changers == null) {
            changers = new ArrayList<>();
        }
        TemporalChanger tc;
        for (Iterator<TemporalChanger> iterator = changers.iterator(); iterator.hasNext();) {
            tc = iterator.next();
            tc.modifyEntity(this);
            if (tc.isOver()) {
                iterator.remove();
                break;
            }
        }
    }

    public void addChanger(TemporalChanger tc) {
        if (changers == null) {
            changers = new ArrayList<>();
        }
        changers.add(tc);
    }

    protected void moveWithSliding(double xMagnitude, double yMagnitude) {
        int xTempSpeed = (int) (xMagnitude + collision.getXSlideSpeed());
        int yTempSpeed = (int) (yMagnitude + collision.getYSlideSpeed());
        collision.prepareSlideSpeed(xMagnitude, yMagnitude);
        moveIfPossible(xTempSpeed, yTempSpeed);
        collision.resetSlideSpeed();
    }

    private void moveIfPossible(int xMagnitude, int yMagnitude) {
        calculatePositionsAndDeltas(xMagnitude, yMagnitude);
        while (xPosition != 0 || yPosition != 0) {
            moveXIfPossible();
            moveYIfPossible();
        }
    }

    private void moveXIfPossible() {
        if (xPosition != 0) {
            if (isCollided(xDelta, 0)) {
                xPosition = 0;
            } else {
                move(xDelta, 0);
                xPosition -= xDelta;
            }
        }
    }

    private void moveYIfPossible() {
        if (yPosition != 0) {
            if (isCollided(0, yDelta)) {
                yPosition = 0;
            } else {
                move(0, yDelta);
                yPosition -= yDelta;
            }
        }
    }

    private void moveToPoint(int xMagnitude, int yMagnitude) {
        calculatePositionsAndDeltas(xMagnitude, yMagnitude);
        while (xPosition != 0 || yPosition != 0) {
            moveXToPoint();
            moveYToPoint();
        }
        updateAreaPlacement();
    }

    private void moveXToPoint() {
        if (xPosition != 0) {
            collided = getCollided(xDelta, 0);
            move(xDelta, 0);
            xPosition -= xDelta;
            if (collided != null) {
                collided.setToLastNotCollided();
            }
            for (int i = 1; i < place.playersCount; i++) {
                if (collision.isCollideSingle(getX(), getY(), place.players[i].getCollision())) {
                    place.players[i].setX(getX() + xDelta
                            * ((collision.getWidth() + place.players[i].getCollisionWidth()) / 2));
                }
            }
        }
    }

    private void moveYToPoint() {
        if (yPosition != 0) {
            collided = getCollided(0, yDelta);
            move(0, yDelta);
            yPosition -= yDelta;
            if (collided != null) {
                collided.setToLastNotCollided();
            }
            for (int i = 1; i < place.playersCount; i++) {
                if (collision.isCollideSingle(getX(), getY(), place.players[i].getCollision())) {
                    place.players[i].setY(getY() + yDelta
                            * ((collision.getHeight() + place.players[i].getCollisionHeight()) / 2));
                }
            }
        }
    }

    private void calculatePositionsAndDeltas(int xMagnitude, int yMagnitude) {
        xPosition = (int) (xMagnitude * Time.getDelta());
        yPosition = (int) (yMagnitude * Time.getDelta());
        xDelta = Integer.signum(xPosition);
        yDelta = Integer.signum(yPosition);
    }

    public void brake(int axis) {
        brakeWithModifier(axis, 1);
    }

    public void brakeWithModifier(int axis, double modifier) {
        double maxWeight = FastMath.max(1, resistance * modifier);
        if (axis == 0 || axis == 2) {
            if (FastMath.abs(xSpeed) >= 1) {
                xSpeed -= xSpeed / (1 + maxWeight);
            } else {
                xSpeed = 0;
            }
        }
        if (axis == 1 || axis == 2) {
            if (FastMath.abs(ySpeed) >= 1) {
                ySpeed -= ySpeed / (1 + maxWeight);
            } else {
                ySpeed = 0;
            }
        }
    }

    protected void brakeOthers() {
        double maxWeight = FastMath.max(1, resistance);
        if (FastMath.abs(xEnvironmentalSpeed) >= 1) {
            xEnvironmentalSpeed -= xEnvironmentalSpeed / (1 + maxWeight);
        } else {
            xEnvironmentalSpeed = 0;
        }
        if (FastMath.abs(yEnvironmentalSpeed) >= 1) {
            yEnvironmentalSpeed -= yEnvironmentalSpeed / (1 + maxWeight);
        } else {
            yEnvironmentalSpeed = 0;
        }
    }

    public void addSpeed(double xSpeedDelta, double ySpeedDelta) {
        setAndLimitSpeed(xSpeed + xSpeedDelta / resistance, ySpeed + ySpeedDelta / resistance);
    }

    void changeSpeed(double xSpeedDelta, double ySpeedDelta) {
        if (Math.signum(xSpeed) != Math.signum(xSpeedDelta)) {
            xSpeed = xSpeed + (xSpeedDelta / resistance);
        } else {
            xSpeed = (xSpeed + xSpeedDelta) / 2;
        }
        if (Math.signum(ySpeed) != Math.signum(ySpeedDelta)) {
            ySpeed = ySpeed + (ySpeedDelta / resistance);
        } else {
            ySpeed = (ySpeed + ySpeedDelta) / 2;
        }
        setAndLimitSpeed(xSpeed, ySpeed);
    }

    private void setAndLimitSpeed(double xSpeed, double ySpeed) {
        this.xSpeed = Methods.interval(-maxSpeed, xSpeed, maxSpeed);
        this.ySpeed = Methods.interval(-maxSpeed, ySpeed, maxSpeed);
    }

    public boolean isUnableToMove() {
        return unableToMove;
    }

    public void setUnableToMove(boolean unableToMove) {
        this.unableToMove = unableToMove;
    }

    public boolean isAbleToMove() {
        return !unableToMove;
    }

    public boolean isJumping() {
        return jumping;
    }

    protected void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    protected boolean isHop() {
        return hop;
    }

    public void setHop(boolean hop) {
        this.hop = hop;
    }

    public double getResistance() {
        return resistance;
    }

    protected void setResistance(double weight) {
        this.resistance = FastMath.max(1, weight);
    }

    public double getRange() {
        return range;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getXEnvironmentalSpeed() {
        return xEnvironmentalSpeed;
    }

    public void setXEnvironmentalSpeed(double xEnvironmentalSpeed) {
        this.xEnvironmentalSpeed = xEnvironmentalSpeed;
    }

    public double getYEnvironmentalSpeed() {
        return yEnvironmentalSpeed;
    }

    public void setYEnvironmentalSpeed(double yEnvironmentalSpeed) {
        this.yEnvironmentalSpeed = yEnvironmentalSpeed;
    }

    protected Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    protected GameObject getTarget() {
        return target;
    }

    public PathData getPathData() {
        return pathData;
    }

    public double getSpeed() {
        return Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);
    }

    public void setScope(int scope) {
        pathData.setScope(scope);
    }

}
