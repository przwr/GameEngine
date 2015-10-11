/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.temporalmodifiers;

import engine.utilities.Methods;
import game.gameobject.entities.Entity;

/**
 * @author Wojtek
 */
public class SpeedChanger extends TemporalChanger {

    public final static byte NORMAL = 0, INCREASING = 1, DECREASING = 2;
    int xSpeed, ySpeed, xTmpSpeed, yTmpSpeed, attackerDirection;
    byte type;

    public SpeedChanger() {
        super();
    }

    public SpeedChanger(int frames) {
        super(frames);
    }

    public SpeedChanger(int xSpeed, int ySpeed, int frames) {
        super(frames);
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setSpeed(int xSpeed, int ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void setSpeedInDirection(int direction, int speed) {
        this.xSpeed = (int) Methods.xRadius(direction, speed);
        this.ySpeed = (int) -Methods.yRadius(direction, speed);
    }

    @Override
    void modifyEffect(Entity en) {
        double tmp;
        switch (type) {
            case NORMAL:
                xTmpSpeed = xSpeed;
                yTmpSpeed = ySpeed;
                break;
            case INCREASING:
                tmp = 1 - (double) left / time;
                xTmpSpeed = (int) (xSpeed * tmp);
                yTmpSpeed = (int) (ySpeed * tmp);
                break;
            case DECREASING:
                tmp = (double) left / time;
                xTmpSpeed = (int) (xSpeed * tmp);
                yTmpSpeed = (int) (ySpeed * tmp);
                break;
        }
        en.addXEnvironmentalSpeed(xTmpSpeed);
        en.addYEnvironmentalSpeed(yTmpSpeed);
    }

    public int getXSpeed() {
        return xSpeed;
    }

    public int getYSpeed() {
        return ySpeed;
    }

    public int getAttackerDirection() {
        return attackerDirection;
    }

    public void setAttackerDirection(int attackerDirection) {
        this.attackerDirection = attackerDirection;
    }

    @Override
    public String toString() {
        return "Speed : " + xSpeed + " " + ySpeed + " : " + super.toString();
    }

}
