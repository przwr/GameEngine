/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import game.gameobject.GameObject;
import game.place.Place;
import engine.Delay;

/**
 *
 * @author przemek
 */
public abstract class Camera {

    protected GameObject go;
    protected Place place;

    protected int Dwidth, Dheight, xOffset, yOffset, xEffect, yEffect, xLeft, xRight, yDown, yUp;
    protected int delaylenght;
    protected Delay shakeDelay;
    protected int shakeAmp = 8;
    boolean shakeUp = true;

    public abstract void update();

    public abstract void shake();
    
    public GameObject getGo() {
        return go;
    }

    public int getXOffEffect() {
        return xOffset + xEffect;
    }

    public int getYOffEffect() {
        return yOffset + yEffect;
    }

    public int getShakeAmp() {
        return shakeAmp;
    }

    public int getDelay() {
        return delaylenght;
    }

    public void setShakeAmp(int shakeAmp) {
        this.shakeAmp = shakeAmp;
    }

    public void setDelay(int delaylenght) {
        this.delaylenght = delaylenght;
    }

    public int getXOff() {
        return xOffset;
    }

    public int getYOff() {
        return yOffset;
    }

    public int getXEffect() {
        return xEffect;
    }

    public int getYEffect() {
        return yEffect;
    }

    public void setXOff(int xOffset) {
        this.xOffset = xOffset;
    }

    public void setYOff(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setGo(GameObject go) {
        this.go = go;
    }

}
