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

/**
 *
 * @author przemek
 */
public abstract class Entity extends GameObject {

    protected double hspeed;      // prędkości ustawiane przez środowisko
    protected double vspeed;
    protected double myHspeed;    // prędkości uzyskiwane przez gracza
    protected double myVspeed;
    protected double weight = 1;  // 1 - idealny balans, im więcej tym gorzej
    protected double maxSpeed;
    protected double jump;
    protected double scale;

    public void canMove(int magX, int magY) {
        int xpos = (int) (magX * Time.getDelta());
        int ypos = (int) (magY * Time.getDelta());
        int xd = Integer.signum(xpos);
        int yd = Integer.signum(ypos);

        while (xpos != 0 || ypos != 0) {
            if (xpos != 0) {
                if (!isColided(xd, 0)) {
                    move(xd, 0);
                    xpos -= xd;
                } else {
                    xpos = 0;
                }
            }
            if (ypos != 0) {
                if (!isColided(0, yd)) {
                    move(0, yd);
                    depth = (int) y;
                    ypos -= yd;
                } else {
                    ypos = 0;
                }
            }
        }
    }

    protected abstract boolean isColided(int magX, int magY);

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
        this.weight = Math.max(1, weight);
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

    public void brake(int axis) {  // 0 OX, 1 OY, 2 oba
        brake(weight, axis);
    }

    public void brake(double i, int axis) {
        double w = Math.max(1, i);
        if (axis == 0 || axis == 2) {
            if (Math.abs(myHspeed) >= 1) {
                myHspeed -= myHspeed / (1 + w);
            } else {
                myHspeed = 0;
            }
        }
        if (axis == 1 || axis == 2) {
            if (Math.abs(myVspeed) >= 1) {
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
        double w = Math.max(1, i);
        if (Math.abs(hspeed) >= 1) {
            hspeed -= hspeed / (1 + w);
        } else {
            hspeed = 0;
        }
        if (Math.abs(vspeed) >= 1) {
            vspeed -= vspeed / (1 + w);
        } else {
            vspeed = 0;
        }
    }

    public void addSpeed(double mhspeed, double mvspeed, boolean isLimited) {
        double xmove = myHspeed + mhspeed / weight * scale;
        double ymove = myVspeed + mvspeed / weight * scale;
        setSpeed(xmove, ymove, isLimited);
    }

    public void setSpeed(double mhspeed, double mvspeed, boolean isLimited) {
        double xmove = mhspeed;
        double ymove = mvspeed;

        if (isLimited) {
            /*double angle = Methods.PointAngle360(0, 0, (int) xmove,
             (int) ymove);
             double xMsp = Math.abs(Methods.xRadius(angle, maxSpeed));
             double yMsp = Math.abs(Methods.yRadius(angle, maxSpeed));
             myHspeed = Methods.Interval(-xMsp, xmove, xMsp);
             myVspeed = Methods.Interval(-yMsp, ymove, yMsp);*/
            myHspeed = Methods.Interval(-maxSpeed, xmove, maxSpeed);
            myVspeed = Methods.Interval(-maxSpeed, ymove, maxSpeed);
        } else {
            myHspeed = xmove;
            myVspeed = ymove;
        }
    }

}
