/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import engine.Delay;
import engine.Methods;
import game.gameobject.GUIObject;
import game.gameobject.GameObject;
import game.place.Map;
import game.place.Place;
import java.util.ArrayList;

/**
 *
 * @author przemek
 */
public abstract class Camera {

    protected final ArrayList<GameObject> gos = new ArrayList<>();
    protected final ArrayList<GUIObject> gui = new ArrayList<>();
    protected Map map;
    protected Place place;
    protected int Dwidth;
    protected int Dheight;
    protected int XMid, YMid;

    protected int xEffect, yEffect, xLeft, xRight, yDown, yUp;
    protected double xOffset, yOffset;
    protected int delaylenght;
    protected Delay shakeDelay;
    protected int shakeAmp = 8;
    public GameObject[] visibleLights = new GameObject[2048];
    public int visibleLightsCount;
    protected final ArrayList<GameObject> owners = new ArrayList<>();
    protected Map map;
    protected int width, height, widthHalf, heightHalf, XMid, yMiddle, xEffect, yEffect, xLeft, xRight, yDown, yUp, delaylenght, shakeAmplitude = 8;
    protected double xOffset, yOffset;
    protected Delay shakeDelay;
    private boolean shakeUp = true;

    public Camera(GameObject go) {
        gos.add(go);
        place = go.getPlace();
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.start();
    }

    public synchronized void update() {
        if (map != null) {
            xOffset = Methods.interval(-map.getWidth() + 2 * Dwidth, Dwidth - getMidX(), 0);
            yOffset = Methods.interval(-map.getHeight() + 2 * Dheight, Dheight - getMidY(), 0);
        } else {
            xOffset = Methods.interval(-place.getWidth() + 2 * Dwidth, Dwidth - getMidX(), 0);
            yOffset = Methods.interval(-place.getHeight() + 2 * Dheight, Dheight - getMidY(), 0);
        }
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

    public void renderGUI() {
        gui.stream().forEach((go) -> {
            go.render(getSX() + getXOffEffect(), getSY() + getYOffEffect());
        });
    }

    public void addGUI(GUIObject go) {
        if (!gui.contains(go)) {
            gui.add(go);
            go.setCamera(this);
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
