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
    int attackerDirection;
    double xSpeed, ySpeed, xTmpSpeed, yTmpSpeed;
    byte type;

    public SpeedChanger() {
        super();
    }

    public SpeedChanger(int frames) {
        super(frames);
    }

    public SpeedChanger(double xSpeed, double ySpeed, int frames) {
        super(frames);
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public SpeedChanger setType(byte type) {
        this.type = type;
        return this;
    }

    public SpeedChanger setSpeed(double xSpeed, double ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        return this;
    }

    public SpeedChanger setSpeedInDirection(int direction, double speed) {
        this.xSpeed = Methods.xRadius(direction, speed);
        this.ySpeed = -Methods.yRadius(direction, speed);
        return this;
    }

    @Override
    public void modifyEffect(Entity en) {
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

    public double getXSpeed() {
        return xSpeed;
    }

    public double getYSpeed() {
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
