/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import engine.ErrorHandler;
import engine.Methods;
import engine.Time;
import game.place.Place;
import navmeshpathfinding.PathData;
import navmeshpathfinding.PathStrategy;
import net.jodk.lang.FastMath;
import net.packets.Update;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public abstract class Entity extends GameObject {

    protected double range;
    public Update[] updates = new Update[4];
    public int lastAdded;
    protected GameObject target;
    protected PathData pathData;
    protected PathStrategy pathStrategy;
    protected static final Color JUMP_SHADOW_COLOR = new Color(0, 0, 0, 51);
    protected double xEnvironmentalSpeed, yEnvironmentalSpeed, xSpeed, ySpeed, maxSpeed, jumpHeight, resistance = 1;
    protected boolean jumping, hop, unableToMove;
    protected Place place;
    private Update currentUpdate;
    private int currentUpdateID, deltasCount, xPosition, yPosition, xDelta, yDelta, xDestination, yDestination;
    private Player colided;
    protected int direction;  //Obecny, badz ostatni kierunek ruchu (stopnie)

    private int xTempSpeed, yTempSpeed;

    public abstract void updateOnline();

    public abstract void updateRest(Update update);

    protected abstract boolean isCollided(int xMagnitude, int yMagnitude);

    public abstract Player getCollided(int xMagnitude, int yMagnitude);

    protected abstract void move(int xPosition, int yPosition);

    public void setPathStrategy(PathStrategy pathStrategy, int scope) {
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

    public void moveWithSliding(double xMagnitude, double yMagnitude) {
        xTempSpeed = (int) (xMagnitude + collision.getXSlideSpeed());
        yTempSpeed = (int) (yMagnitude + collision.getYSlideSpeed());
        collision.prepareSlideSpeed(xMagnitude, yMagnitude);
        moveIfPossible(xTempSpeed, yTempSpeed);
        collision.resetSlideSpeed();
    }

    public void moveIfPossible(int xMagnitude, int yMagnitude) {
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

    public void moveToPoint(int xMagnitude, int yMagnitude) {
        calculatePositionsAndDeltas(xMagnitude, yMagnitude);
        while (xPosition != 0 || yPosition != 0) {
            moveXToPoint();
            moveYToPoint();
        }
        updateAreaPlacement();
    }

    private void moveXToPoint() {
        if (xPosition != 0) {
            colided = getCollided(xDelta, 0);
            move(xDelta, 0);
            xPosition -= xDelta;
            if (colided != null) {
                colided.setToLastNotCollided();
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
            colided = getCollided(0, yDelta);
            move(0, yDelta);
            yPosition -= yDelta;
            if (colided != null) {
                colided.setToLastNotCollided();
            }
            for (int i = 1; i < place.playersCount; i++) {
                if (collision.isCollideSingle(getX(), getY(), place.players[i].getCollision())) {
                    place.players[i].setY(getY() + yDelta
                            * ((collision.getHeight() + place.players[i].getCollisionHeight()) / 2));
                }
            }
        }
    }

    private void calculatePositionsAndDeltas(int xMagnitiude, int yMagnitiude) {
        xPosition = (int) (xMagnitiude * Time.getDelta());
        yPosition = (int) (yMagnitiude * Time.getDelta());
        xDelta = Integer.signum(xPosition);
        yDelta = Integer.signum(yPosition);
    }

    public void brake(int axis) {
        double maxWeight = FastMath.max(1, resistance);
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

    public void brakeOthers() {
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

    public void changeSpeed(double xSpeedDelta, double ySpeedDelta) {
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

    public boolean isAbleToMove() {
        return !unableToMove;
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isHop() {
        return hop;
    }

    public double getResistance() {
        return resistance;
    }

    public double getRange() {
        return range;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getJumpHeight() {
        return jumpHeight;
    }

    public double getXEnvironmetalSpeed() {
        return xEnvironmentalSpeed;
    }

    public double getYEnvironmentalSpeed() {
        return yEnvironmentalSpeed;
    }

    public Place getPlace() {
        return place;
    }

    public GameObject getTarget() {
        return target;
    }

    public int getDirection() {
        return direction;
    }

    public PathData getPathData() {
        return pathData;
    }

    public double getSpeed() {
        return Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed);
    }

    public void setUnableToMove(boolean unableToMove) {
        this.unableToMove = unableToMove;
    }

    public void setDirection(int d) {
        direction = d;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public void setHop(boolean hop) {
        this.hop = hop;
    }

    public void setResistance(double weight) {
        this.resistance = FastMath.max(1, weight);
    }

    public void setJumpHeight(double jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public void setXEnvironmetalSpeed(double xEnvironmentalSpeed) {
        this.xEnvironmentalSpeed = xEnvironmentalSpeed;
    }

    public void setYEnvironmentalSpeed(double yEnvironmentalSpeed) {
        this.yEnvironmentalSpeed = yEnvironmentalSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setScope(int scope) {
        pathData.setScope(scope);
    }

}
