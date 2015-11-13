/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.temporalmodifiers;

import game.gameobject.entities.Entity;

/**
 *
 * @author Wojtek
 */
public abstract class TemporalChanger {

    int time, left;

    public TemporalChanger() {
        time = 0;
        left = 0;
    }
    
    public TemporalChanger(int frames) {
        this.time = frames;
        left = 0;
    }

    public void setFrames(int time) {
        this.time = time;
    }
    
    public void start() {
        left = time;
    }

    public void startInfinite() {
        left = -1;
    }

    public void stop() {
        left = 0;
    }

    public boolean isOver() {
        return left == 0;
    }
    
    public int getTotalTime() {
        return time;
    }
    
    public int getCurrentTime() {
        return left;
    }
    
    public int getTimePart(int parts) {
        return Math.min(parts - 1 - (parts * left) / time, parts - 1);
    }
    
    public double getPercentDone() {
        return (double) (time - left) / time;
    }
    
    public double getPercentLeft() {
        return (double) left / time;
    }

    public void modifyEntity(Entity en) {
        if (left != 0) {
            modifyEffect(en);
            if (left > 0) {
                left--;
            }
        }
    }

    @Override
    public String toString() {
        return left + " / " + time;
    }
    
    abstract public void modifyEffect(Entity en);
}
