/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import game.gameobject.GameObject;
import game.place.Place;
import engine.Delay;
import game.Methods;

/**
 *
 * @author przemek
 */
public abstract class Camera {

    protected GameObject go;
    protected Place place;
    protected int Dwidth;
    protected int Dheight;

    protected int xEffect, yEffect, xLeft, xRight, yDown, yUp;
    protected double xOffset, yOffset;
    protected int delaylenght, SX, EX, SY, EY;
    protected Delay shakeDelay;
    protected int shakeAmp = 8;
    boolean shakeUp = true;

    public Camera(Place place, GameObject go) {
        this.place = place;
        this.go = go;
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.restart();
    }

    public final synchronized void update() {
        xOffset = Methods.Interval(-place.width + 2 * Dwidth, Dwidth - getMidX(), 0);
        yOffset = Methods.Interval(-place.height + 2 * Dheight, Dheight - getMidY(), 0);
    }

    public synchronized void shake() {
        if (shakeDelay.isOver()) {
            if (shakeUp) {
                xEffect += shakeAmp;
                yEffect += shakeAmp / 2;
                shakeUp = false;
            } else {
                xEffect -= shakeAmp;
                yEffect -= shakeAmp / 2;
                shakeUp = true;
            }
            shakeDelay.restart();
        }
    }

    public abstract int getMidX();

    public abstract int getMidY();

    public GameObject getGo() {
        return go;
    }

    public int getXOffEffect() {
        return (int) (xOffset + xEffect);
    }

    public int getYOffEffect() {
        return (int) (yOffset + yEffect);
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
        return (int) xOffset;
    }

    public int getYOff() {
        return (int) yOffset;
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

    public int getDwidth() {
        return Dwidth;
    }

    public int getDheight() {
        return Dheight;
    }

    public int getSX() {
        return getMidX() - (getMidX() + getXOffEffect());
    }

    public int getEX() {
        return getMidX() - (getMidX() + getXOffEffect()) + Dwidth * 2;
    }

    public int getSY() {
        return getMidY() - (getMidY() + getYOffEffect());
    }

    public int getEY() {
        return getMidY() - (getMidY() + getYOffEffect()) + Dheight * 2;
    }

}
