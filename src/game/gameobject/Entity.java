/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import engine.Methods;
import engine.Time;
import game.place.Place;
import game.place.cameras.Camera;
import net.jodk.lang.FastMath;
import net.packets.Update;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public abstract class Entity extends GameObject {

    public Update[] updates = new Update[4];
    public int lastAdded;
    protected static final Color JUMP_SHADOW_COLOR = new Color(0, 0, 0, 51);
    protected double scale, xEnvironmentalSpeed, yEnvironmentalSpeed, xSpeed, ySpeed, maxSpeed, jumpHeight, resistance = 1;
    protected boolean jumping, hop;
    private Update currentUpdate;
    private int currentUpdateID, deltasCount, xPosition, yPosition, xDelta, yDelta, destinationX, destinationY;
    private Player colided;

    public abstract void updateOnline();

    public abstract void updateRest(Update update);

    protected abstract boolean isColided(int xMagnitude, int yMagnitude);

    public abstract Player getCollided(int xMagnitude, int yMagnitude);

    protected abstract void move(int xPosition, int yPosition);

    protected abstract void setPosition(int xPosition, int yPosition);

    public abstract void renderName(Place place, Camera cam);

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
            System.out.println("ERROR: UpdateSoft" + exception);
        }
    }

    private void useCurrentUpdateSoft() {
        destinationX = Methods.roundHalfUp((currentUpdate.getX() - currentUpdate.getXDeltas().get(deltasCount)) * scale);
        destinationY = Methods.roundHalfUp((currentUpdate.getY() - currentUpdate.getYDeltas().get(deltasCount)) * scale);
        if (collision.isCollideSolid(destinationX, destinationY, map)) {
            moveIfPossible(destinationX - getX(), destinationY - getY());
        } else {
            setPosition(destinationX, destinationY);
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
            destinationX = Methods.roundHalfUp(currentUpdate.getX() * scale);
            destinationY = Methods.roundHalfUp(currentUpdate.getY() * scale);
            if (collision.isCollideSolid(destinationX, destinationY, map)) {
                moveIfPossible(destinationX - getX(), destinationY - getY());
            } else {
                setPosition(destinationX, destinationY);
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
            System.out.println("ERROR: UpdateHard" + exception.getMessage());
        }
    }

    private void useCurrentUpdateHard() {
        moveToPoint(Methods.roundHalfUp((currentUpdate.getX() - currentUpdate.getXDeltas().get(deltasCount)) * scale) - getX(), Methods.roundHalfUp((currentUpdate.getY() - currentUpdate.getYDeltas().get(deltasCount)) * scale) - getY());
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
            moveToPoint(Methods.roundHalfUp(currentUpdate.getX() * scale) - getX(), Methods.roundHalfUp(currentUpdate.getY() * scale) - getY());
            deltasCount = 0;
        }
    }

    private boolean canUpdate() {
        return updates[3] != null && ((currentUpdateID != 3 || lastAdded != 0) && (currentUpdateID + 1 != lastAdded));
    }

    private boolean inSameUpdate() {
        return currentUpdate != null && deltasCount < currentUpdate.getXDeltas().size();
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
            if (isColided(xDelta, 0)) {
                xPosition = 0;
            } else {
                move(xDelta, 0);
                xPosition -= xDelta;
            }
        }
    }

    private void moveYIfPossible() {
        if (yPosition != 0) {
            if (isColided(0, yDelta)) {
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
    }

    private void moveXToPoint() {
        if (xPosition != 0) {
            colided = getCollided(xDelta, 0);
            move(xDelta, 0);
            xPosition -= xDelta;
            if (colided != null) {
                colided.setToLastNotCollided();
            }
            for (int i = 1; i < place.playersLength; i++) {
                if (collision.isCollideSingle(getX(), getY(), place.players[i].getCollision())) {
                    place.players[i].setX(getX() + xDelta * ((width + place.players[i].getCollisionWidth()) / 2));
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
            for (int i = 1; i < place.playersLength; i++) {
                if (collision.isCollideSingle(getX(), getY(), place.players[i].getCollision())) {
                    place.players[i].setY(getY() + yDelta * ((height + place.players[i].getCollisionHeight()) / 2));
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
        setAndLimitSpeed(xSpeed + xSpeedDelta / resistance * scale, ySpeed + ySpeedDelta / resistance * scale);
    }

    private void setAndLimitSpeed(double xSpeed, double ySpeed) {
        this.xSpeed = Methods.interval(-maxSpeed, xSpeed, maxSpeed);
        this.ySpeed = Methods.interval(-maxSpeed, ySpeed, maxSpeed);
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
        this.maxSpeed = maxSpeed * scale;
    }
}
