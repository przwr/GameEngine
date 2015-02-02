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

    public GameObject[] visibleLights = new GameObject[2048];
    public int visibleLightsCount;
    protected final ArrayList<GameObject> owners = new ArrayList<>();
    protected Map map;
    protected int width, height, widthHalf, heightHalf, XMid, yMiddle, xEffect, yEffect, xLeft, xRight, yDown, yUp, delaylenght, shakeAmplitude = 8;
    protected double xOffset, yOffset;
    protected Delay shakeDelay;
    private boolean shakeUp = true;

    public Camera(Map map, GameObject owner) {
        this.map = map;
        owners.add(owner);
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.start();
    }

    public synchronized void update() {
        xOffset = Methods.interval(-map.getWidth() + width, widthHalf - getXMiddle(), 0);
        yOffset = Methods.interval(-map.getHeight() + height, heightHalf - getYMiddle(), 0);
    }

    public synchronized void shake() {
        if (shakeDelay.isOver()) {
            if (shakeUp) {
                xEffect += shakeAmplitude;
                yEffect += shakeAmplitude / 2;
                shakeUp = false;
            } else {
                xEffect -= shakeAmplitude;
                yEffect -= shakeAmplitude / 2;
                shakeUp = true;
            }
            shakeDelay.start();
        }
    }

    public int getXMiddle() {
        XMid = 0;
        owners.stream().forEach((owner) -> {
            XMid += owner.getX();
        });
        return XMid / owners.size();
    }

    public int getYMiddle() {
        yMiddle = 0;
        owners.stream().forEach((owner) -> {
            yMiddle += owner.getY();
        });
        return yMiddle / owners.size();
    }

    public int getXOffsetEffect() {
        return (int) (xOffset + xEffect);
    }

    public int getYOffsetEffect() {
        return (int) (yOffset + yEffect);
    }

    public int getShakeAmp() {
        return shakeAmplitude;
    }

    public int getDelay() {
        return delaylenght;
    }

    public Map getMap() {
        return map;
    }

    public int getXOffset() {
        return (int) xOffset;
    }

    public int getYOffset() {
        return (int) yOffset;
    }

    public int getXEffect() {
        return xEffect;
    }

    public int getYEffect() {
        return yEffect;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidthHalf() {
        return widthHalf;
    }

    public int getHeightHalf() {
        return heightHalf;
    }

    public int getXStart() {
        return getXMiddle() - (getXMiddle() + getXOffsetEffect());
    }

    public int getYStart() {
        return getYMiddle() - (getYMiddle() + getYOffsetEffect());
    }

    public int getXEnd() {
        return getXMiddle() - (getXMiddle() + getXOffsetEffect()) + widthHalf * 2;
    }

    public int getYEnd() {
        return getYMiddle() - (getYMiddle() + getYOffsetEffect()) + heightHalf * 2;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setShakeAmplitude(int shakeAmplitude) {
        this.shakeAmplitude = shakeAmplitude;
    }

    public void setDelayLength(int delaylenght) {
        this.delaylenght = delaylenght;
    }

}
