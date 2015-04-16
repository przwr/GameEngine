/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import engine.Delay;
import engine.Drawer;
import engine.Methods;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.Light;
import game.place.Map;
import game.place.Place;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public abstract class Camera {

    private static final double[] scales = {0.75, 0.5, 0.5, 0.375, 0.375, 0.25, 1, 0.75, 0.75, 0.5, 0.5, 0.375};
//    private static final short O7FULL = 0, O7FULL_ZOOMED = 1, O7HALF = 2, O7HALF_ZOOMED = 3, O7QUARTER = 4, O7QUARTER_ZOOMED = 5,
//            FULL = 6, FULL_ZOOMED = 7, HALF = 8, HALF_ZOOMED = 9, QUARTER = 10, QUARTER_ZOOMED = 11;
    private static final short TOP_LEFT = 0, TOP_CENTER = 1, TOP_RIGHT = 2, LEFT = 3, CENTER = 4, RIGHT = 5, LEFT_BOTTOM = 6, BOTTOM_CENTER = 7, BOTTOM_RIGHT = 8;
    protected final ArrayList<GUIObject> gui = new ArrayList<>();
    protected final ArrayList<Light> visibleLights = new ArrayList<>();
    protected final ArrayList<GameObject> owners = new ArrayList<>();
    protected Map map;
    protected Delay shakeDelay;
    protected int widthHalf, heightHalf, xMiddle, yMiddle, xEffect, yEffect, xLeft, xRight, yDown, yUp, delayLenght, shakeAmplitude = 8, ownersCount, centerArea, prevCenterArea = -1;
    protected double xOffset, yOffset, scale;
    protected boolean shakeUp = true, zoomed = false;
    protected int[] nearAreas = new int[9];

    public Camera(GameObject object) {
        owners.add(object);
        delayLenght = 50;
        shakeDelay = new Delay(delayLenght);
        shakeDelay.start();
    }

    public synchronized void update() {
        if (map != null) {
            xMiddle = getXMiddle();
            yMiddle = getYMiddle();
            xOffset = Methods.interval(-map.getWidth() * scale + getWidth(), widthHalf - xMiddle * scale, 0);
            yOffset = Methods.interval(-map.getHeight() * scale + getHeight(), heightHalf - yMiddle * scale, 0);
            centerArea = map.getAreaIndex(xMiddle / Place.tileSize, yMiddle / Place.tileSize);

//            System.out.println(xMiddle + " " + yMiddle);
            if (centerArea != prevCenterArea) {
                updateNearAreas();
                prevCenterArea = centerArea;
            }
//            for (int i : nearAreas) {
//                if (i == centerArea) {
//                    System.out.print("[" + i + "] ");
//                } else {
//                    System.out.print(i + " ");
//                }
//            }
//            System.out.println();
        }
    }

    private void updateNearAreas() {
        nearAreas[TOP_LEFT] = centerArea - map.getXAreas() - 1;
        nearAreas[TOP_CENTER] = centerArea - map.getXAreas();
        nearAreas[TOP_RIGHT] = centerArea - map.getXAreas() + 1;
        nearAreas[LEFT] = centerArea - 1;
        nearAreas[CENTER] = centerArea;
        nearAreas[RIGHT] = centerArea + 1;
        nearAreas[LEFT_BOTTOM] = centerArea + map.getXAreas() - 1;
        nearAreas[BOTTOM_CENTER] = centerArea + map.getXAreas();
        nearAreas[BOTTOM_RIGHT] = centerArea + map.getXAreas() + 1;
        if (centerArea % map.getXAreas() == 0) {
            for (int i = 0; i < nearAreas.length; i++) {
                if (nearAreas[i] % map.getXAreas() == map.getXAreas() - 1) {
                    nearAreas[i] = -1;
                }
            }
        }
        if (centerArea % map.getXAreas() == map.getXAreas() - 1) {
            for (int i = 0; i < nearAreas.length; i++) {
                if (nearAreas[i] % map.getXAreas() == 0) {
                    nearAreas[i] = -1;
                }
            }
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
        Drawer.refreshForRegularDrawing();
        owners.stream().forEach((object) -> {
            if (object instanceof Player) {
                ((Player) object).renderGUI();
            }
        });
    }

    public void switchZoom() {
        zoomed = !zoomed;
        setScale(Display.getWidth() / widthHalf, Display.getHeight() / heightHalf, ownersCount);
        update();
    }

    protected void setScale(int ssX, int ssY, int ownersCount) {
        switch (ownersCount) {
            case 0:
                scale = scales[((int) (Settings.nativeScale) * 6) + (zoomed ? 1 : 0)];
                break;
            case 1:
            case 3:
                scale = scales[(int) (Settings.nativeScale) * 6 + (ownersCount + 1) + (zoomed ? 1 : 0)];
                break;
            case 2:
                scale = scales[(int) (Settings.nativeScale) * 6 + (ssX == ssY ? 4 : 2) + (zoomed ? 1 : 0)];
                break;
        }
    }

    public void addVisibleLight(Light light) {
        visibleLights.add(light);
    }

    public void clearVisibleLights() {
        visibleLights.clear();
    }

    public int getXMiddle() {
        xMiddle = 0;
        owners.stream().forEach((owner) -> {
            xMiddle += owner.getX();
        });
        return xMiddle / owners.size();
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
        return delayLenght;
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

    public int getWidth() {
        return widthHalf * 2;
    }

    public int getHeight() {
        return heightHalf * 2;
    }

    public int getWidthHalf() {
        return widthHalf;
    }

    public int getHeightHalf() {
        return heightHalf;
    }

    public Collection<Light> getVisibleLights() {
        return Collections.unmodifiableList(visibleLights);
    }

    public int getXStart() {
        return (int) (-getXOffsetEffect() / scale);
    }

    public int getYStart() {
        return (int) (-getYOffsetEffect() / scale);
    }

    public int getXEnd() {
        return (int) ((-getXOffsetEffect() + widthHalf * 2) / scale);
    }

    public int getYEnd() {
        return (int) ((-getYOffsetEffect() + heightHalf * 2) / scale);
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public void setMap(Map map) {
        this.map = map;
        update();
    }

    public void setShakeAmplitude(int shakeAmplitude) {
        this.shakeAmplitude = shakeAmplitude;
    }

    public void setDelayLength(int delaylenght) {
        this.delayLenght = delaylenght;
    }

    public double getScale() {
        return scale;
    }

    public int[] getNearAreas() {
        return nearAreas;
    }
}
