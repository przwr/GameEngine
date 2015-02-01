/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import engine.Delay;
import engine.Methods;
import game.gameobject.GameObject;
import game.place.Map;
import java.util.ArrayList;

/**
 *
 * @author przemek
 */
public abstract class Camera {

    protected final ArrayList<GameObject> gos = new ArrayList<>();
    protected Map map;
    protected int Dwidth;
    protected int Dheight;
    protected int XMid, YMid;

    protected int xEffect, yEffect, xLeft, xRight, yDown, yUp;
    protected double xOffset, yOffset;
    protected int delaylenght;
    protected Delay shakeDelay;
    protected int shakeAmp = 8;
    public GameObject[] visibleLights = new GameObject[2048];
    public int nrVLights;
    boolean shakeUp = true;

    public Camera(Map map, GameObject go) {
        this.map = map;
        gos.add(go);
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.start();
    }

    public synchronized void update() {
        xOffset = Methods.interval(-map.getWidth() + 2 * Dwidth, Dwidth - getMidX(), 0);
        yOffset = Methods.interval(-map.getHeight() + 2 * Dheight, Dheight - getMidY(), 0);
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
            shakeDelay.start();
        }
    }

    public int getMidX() {
        XMid = 0;
        for (GameObject go : gos) {
            XMid += go.getX();
        }
        return XMid / gos.size();
    }

    public int getMidY() {
        YMid = 0;
        for (GameObject go : gos) {
            YMid += go.getY();
        }
        return YMid / gos.size();
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

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
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

    public int getDwidth() {
        return Dwidth;
    }

    public int getDheight() {
        return Dheight;
    }
    
    public int getWidth() {
        return Dwidth * 2;
    }

    public int getHeight() {
        return Dheight * 2;
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
