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
import net.packets.Update;

/**
 *
 * @author przemek
 */
public abstract class Entity extends GameObject {

//    public int prevX1, prevX2, prevY1, prevY2;
//    public ArrayList<Short> dX1, dX2, dY1, dY2;
    public Update up1, up2;
    protected boolean isFirst;
    public int dCount;
    protected double hspeed;      // prędkości ustawiane przez środowisko
    protected double vspeed;
    protected double myHspeed;    // prędkości uzyskiwane przez gracza
    protected double myVspeed;
    protected double weight = 1;  // 1 - idealny balans, im więcej tym gorzej
    protected double maxSpeed;
    protected double jump;
    protected double scale;
    protected boolean isJumping, hop;

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
                    ypos -= yd;
                    depth = (int) y;
                } else {
                    ypos = 0;
                }
            }
        }
    }

    public void movetoPoint(int magX, int magY) {
        int xpos = (int) (magX * Time.getDelta());
        int ypos = (int) (magY * Time.getDelta());
        int xd = Integer.signum(xpos);
        int yd = Integer.signum(ypos);

        while (xpos != 0 || ypos != 0) {
            Player colided;
            if (xpos != 0) {
                colided = getCollided(xd, 0);
                if (colided == null) {
                    move(xd, 0);
                    xpos -= xd;
                } else {
                    colided.setX(colided.getX() + xd);
                    if (colided.getCam() != null) {
                        colided.getCam().update();
                    }
                    move(xd, 0);
                    xpos -= xd;
                }
            }
            if (ypos != 0) {
                colided = getCollided(0, yd);
                if (colided == null) {
                    move(0, yd);
                    depth = (int) y;
                    ypos -= yd;
                } else {
                    colided.setY(colided.getY() + yd);
                    colided.upDepth();
                    if (colided.getCam() != null) {
                        colided.getCam().update();
                    }
                    move(0, yd);
                    depth = (int) y;
                    ypos -= yd;
                }
            }
        }
    }

    public void updateHard() {  // przesuwa graczy
        int deltX = 0, deltY = 0, pX, pY;
        if (isFirst) {
            if (up1 != null && dCount < up1.delsX().size()) {
                if (dCount >= 0) {
                    deltX = up1.delsX().get(dCount);
                    deltY = up1.delsY().get(dCount);
                }
                pX = up1.getX();
                pY = up1.getY();
                movetoPoint(Methods.RoundHU((pX - deltX) * scale) - getX(), Methods.RoundHU((pY - deltY) * scale) - getY());
                dCount++;
            }
        } else {
            if (up2 != null && dCount < up2.delsX().size()) {
                if (dCount >= 0) {
                    deltX = up2.delsX().get(dCount);
                    deltY = up2.delsY().get(dCount);
                }
                pX = up2.getX();
                pY = up2.getY();
                movetoPoint(Methods.RoundHU((pX - deltX) * scale) - getX(), Methods.RoundHU((pY - deltY) * scale) - getY());
                dCount++;
            }
        }
    }

    public void updateSoft() {  // nie przesuwa graczy
        int deltX = 0, deltY = 0, pX, pY;
        if (isFirst) {
            if (up1 != null && dCount < up1.delsX().size()) {
                if (dCount >= 0) {
                    deltX = up1.delsX().get(dCount);
                    deltY = up1.delsY().get(dCount);
                }
                pX = up1.getX();
                pY = up1.getY();
                System.out.println("1: " + pX + " " + deltX);
                canMove(Methods.RoundHU((pX - deltX) * scale) - getX(), Methods.RoundHU((pY - deltY) * scale) - getY());
                dCount++;
                return;
            }
            System.out.println("Lipa");
        } else {
            if (up2 != null && dCount < up2.delsX().size()) {
                if (dCount >= 0) {
                    deltX = up2.delsX().get(dCount);
                    deltY = up2.delsY().get(dCount);
                }
                pX = up2.getX();
                pY = up2.getY();
                System.out.println("2: " + pX + " " + deltX);
                canMove(Methods.RoundHU((pX - deltX) * scale) - getX(), Methods.RoundHU((pY - deltY) * scale) - getY());
                dCount++;
                return;
            }
            System.out.println("Lipa2");
        }
    }

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

    public boolean isJumping() {
        return isJumping;
    }

    public void setIsJumping(boolean jump) {
        this.isJumping = jump;
    }

    public boolean isHop() {
        return isJumping;
    }

    public void setIsHop(boolean hop) {
        this.hop = hop;
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

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isFirst() {
        return isFirst;
    }
}
