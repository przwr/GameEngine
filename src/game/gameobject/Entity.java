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

/**
 *
 * @author przemek
 */
public abstract class Entity extends GameObject {

    public Update up, up2;
    public Update[] ups = new Update[4];
    public int lastAdded;
    public int curUp;
    public int dCount;
    protected double hspeed;      // prędkości ustawiane przez środowisko
    protected double vspeed;
    protected double myHspeed;    // prędkości uzyskiwane przez gracza
    protected double myVspeed;
    protected double weight = 1;  // 1 - idealny balans, im więcej tym gorzej
    protected double maxSpeed;
    protected double jump;
    protected double scale;
    protected boolean jumping, hop;

    public void canMove(int magX, int magY) {
        int xpos = (int) (magX * Time.getDelta());
        int ypos = (int) (magY * Time.getDelta());
        final int xd = Integer.signum(xpos);
        final int yd = Integer.signum(ypos);

        while (xpos != 0 || ypos != 0) {
            if (xpos != 0) {
                if (isColided(xd, 0)) {
                    xpos = 0;
                } else {
                    move(xd, 0);
                    xpos -= xd;
                }
            }
            if (ypos != 0) {
                if (isColided(0, yd)) {
                    ypos = 0;
                } else {
                    move(0, yd);
                    ypos -= yd;
                    depth = (int) y;
                }
            }
        }
    }

    public void movetoPoint(int magX, int magY) {
        int xpos = (int) (magX * Time.getDelta());
        int ypos = (int) (magY * Time.getDelta());
        final int xd = Integer.signum(xpos);
        final int yd = Integer.signum(ypos);

        while (xpos != 0 || ypos != 0) {
            Player colided;
            if (xpos != 0) {
                colided = getCollided(xd, 0);
                move(xd, 0);
                xpos -= xd;
                if (colided != null) {
                    colided.setToLastNotCollided();
                }
                for (int i = 1; i < place.playersLength; i++) {
                    if (collision.ifCollideSngl(getX(), getY(), place.players[i].getCollision())) {
                        place.players[i].setX(getX() + xd * ((width + place.players[i].getWidth()) >> 1));
                    }
                }
            }
            if (ypos != 0) {
                colided = getCollided(0, yd);
                move(0, yd);
                depth = (int) y;
                ypos -= yd;
                if (colided != null) {
                    colided.setToLastNotCollided();
                }
                for (int i = 1; i < place.playersLength; i++) {
                    if (collision.ifCollideSngl(getX(), getY(), place.players[i].getCollision())) {
                        place.players[i].setY(getY() + yd * ((height + place.players[i].getHeight()) >> 1));
                    }
                }
            }
        }
    }

    public void updateHard() {  // przesuwa graczy
        if (ups[3] != null && ((curUp != 3 || lastAdded != 0) && (curUp + 1 != lastAdded))) {
            if (dCount < ups[curUp].delsX().size()) {
                up = ups[curUp];
                movetoPoint(Methods.RoundHU((up.getX() - up.delsX().get(dCount)) * scale) - getX(), Methods.RoundHU((up.getY() - up.delsY().get(dCount)) * scale) - getY());
                dCount++;
            } else {
                if (curUp == 3) {
                    curUp = 0;
                } else {
                    curUp++;
                }
                up = ups[curUp];
                updateRest(up);
                movetoPoint(Methods.RoundHU(up.getX() * scale) - getX(), Methods.RoundHU(up.getY() * scale) - getY());
                dCount = 0;
            }
        }
    }

    public void updateSoft() {  // nie przesuwa graczy
        if (ups[3] != null && ((curUp != 3 || lastAdded != 0) && (curUp + 1 != lastAdded))) {
            if (dCount < ups[curUp].delsX().size()) {
                up = ups[curUp];
                final int x = Methods.RoundHU((up.getX() - up.delsX().get(dCount)) * scale);
                final int y = Methods.RoundHU((up.getY() - up.delsY().get(dCount)) * scale);
                if (collision.ifCollideSolid(x, y, place)) {
                    canMove(x - getX(), y - getY());
                } else {
                    setPosition(x, y);
                    upDepth();
                }
                dCount++;
            } else {
                if (curUp == 3) {
                    curUp = 0;
                } else {
                    curUp++;
                }
                up = ups[curUp];
                updateRest(up);
                final int x = Methods.RoundHU(up.getX() * scale);
                final int y = Methods.RoundHU(up.getY() * scale);
                if (collision.ifCollideSolid(x, y, place)) {
                    canMove(x - getX(), y - getY());
                } else {
                    setPosition(x, y);
                    upDepth();
                }
                dCount = 0;
            }
        }
    }

    public abstract void updateOnline();

    public abstract void updateRest(Update up);

    protected abstract boolean isColided(int magX, int magY);

    public abstract Player getCollided(int magX, int magY);

    protected abstract void move(int xPos, int yPos);

    protected abstract void setPosition(int xPos, int yPos);

    protected abstract void renderName(Place place, Camera cam);

    public double getHSpeed() {
        return hspeed;
    }

    public void setHSpeed(double speed) {
        this.hspeed = speed;
    }

    public double getVSpeed() {
        return hspeed;
    }

    public void setVSpeed(double speed) {
        this.hspeed = speed;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = FastMath.max(1, weight);
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed * scale;
    }

    public double getJump() {
        return jump;
    }

    public void setJump(double jump) {
        this.jump = jump;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setIsJumping(boolean jump) {
        this.jumping = jump;
    }

    public boolean isHop() {
        return jumping;
    }

    public void setIsHop(boolean hop) {
        this.hop = hop;
    }

    public void brake(int axis) {  // 0 OX, 1 OY, 2 oba
        brake(weight, axis);
    }

    public void brake(double i, int axis) {
        final double w = FastMath.max(1, i);
        if (axis == 0 || axis == 2) {
            if (FastMath.abs(myHspeed) >= 1) {
                myHspeed -= myHspeed / (1 + w);
            } else {
                myHspeed = 0;
            }
        }
        if (axis == 1 || axis == 2) {
            if (FastMath.abs(myVspeed) >= 1) {
                myVspeed -= myVspeed / (1 + w);
            } else {
                myVspeed = 0;
            }
        }
    }

    public void brakeOthers() {
        brakeOthers(weight);
    }

    public void brakeOthers(double i) {
        final double w = FastMath.max(1, i);
        if (FastMath.abs(hspeed) >= 1) {
            hspeed -= hspeed / (1 + w);
        } else {
            hspeed = 0;
        }
        if (FastMath.abs(vspeed) >= 1) {
            vspeed -= vspeed / (1 + w);
        } else {
            vspeed = 0;
        }
    }

    public void addSpeed(double mhspeed, double mvspeed, boolean isLimited) {
        setSpeed(myHspeed + mhspeed / weight * scale, myVspeed + mvspeed / weight * scale, isLimited);
    }

    public void setSpeed(double mhspeed, double mvspeed, boolean isLimited) {
        if (isLimited) {
            /*double angle = Methods.PointAngle360(0, 0, (int) xmove,
             (int) ymove);
             double xMsp = FastMath.abs(Methods.xRadius(angle, maxSpeed));
             double yMsp = FastMath.abs(Methods.yRadius(angle, maxSpeed));
             myHspeed = Methods.Interval(-xMsp, xmove, xMsp);
             myVspeed = Methods.Interval(-yMsp, ymove, yMsp);*/
            myHspeed = Methods.Interval(-maxSpeed, mhspeed, maxSpeed);
            myVspeed = Methods.Interval(-maxSpeed, mvspeed, maxSpeed);
        } else {
            myHspeed = mhspeed;
            myVspeed = mvspeed;
        }
    }
}
