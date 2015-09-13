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

    public TemporalChanger(int frames) {
        this.time = frames;
        left = 0;
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
        return time;
    }
    
    public int getTimePart(int parts) {
        return Math.min(parts - 1 - (parts * left) / time, parts - 1);
    }

    public void modifyEntity(Entity en) {
        if (left != 0) {
            modifyEffect(en);
            if (left > 0) {
                left--;
            }
        }
    }

    abstract void modifyEffect(Entity en);
}
