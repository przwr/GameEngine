/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.temporalmodifiers;

import game.gameobject.Entity;

/**
 *
 * @author Wojtek
 */
public class SpeedChanger extends TemporalChanger {

    int xSpeed, ySpeed, xTmpSpeed, yTmpSpeed;
    byte type;
    public final static byte NORMAL = 0, INCREASING = 1, DECREASING = 2;

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
        en.setXEnvironmentalSpeed(xTmpSpeed);
        en.setYEnvironmentalSpeed(yTmpSpeed);
    }

}