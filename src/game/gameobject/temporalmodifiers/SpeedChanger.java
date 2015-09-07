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
    int xSpeed, ySpeed;
    
    public SpeedChanger(int frames) {
        super(frames);
    }
    
    public SpeedChanger(int xSpeed, int ySpeed, int frames) {
        super(frames);
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public void setSpeed(int xSpeed, int ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;        
    }
    
    @Override
    void modifyEffect(Entity en) {
        en.setXEnvironmentalSpeed(xSpeed);
        en.setYEnvironmentalSpeed(ySpeed);
    }
    
}
