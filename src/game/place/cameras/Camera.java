/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import engine.lights.Light;
import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.place.map.Map;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.List;

/**
 * @author przemek
 */
public abstract class Camera {

    private static final double[] scales = {0.75, 0.5, 0.5, 0.375, 0.375, 0.25, 1, 0.75, 0.75, 0.5, 0.5, 0.375};
    //    private static final short O7FULL = 0, O7FULL_ZOOMED = 1, O7HALF = 2, O7HALF_ZOOMED = 3, O7QUARTER = 4, O7QUARTER_ZOOMED = 5,
//            FULL = 6, FULL_ZOOMED = 7, HALF = 8, HALF_ZOOMED = 9, QUARTER = 10, QUARTER_ZOOMED = 11;
    final ArrayList<GameObject> owners = new ArrayList<>();
    private final Delay shakeDelay;
    private final NearObjects nearObjects = new NearObjects();
    int widthHalf;
    int heightHalf;
    int xLeft;
    int xRight;
    int yDown;
    int yUp;
    int ownersCount;
    private Map map;
    private int xMiddle;
    private int yMiddle;
    private int xEffect;
    private int yEffect;
    private int delayLength;
    private int shakeAmplitude = 8;
    private int area;
    private double xOffset;
    private double yOffset;
    private double scale;
    private boolean shakeUp = true;
    private boolean zoomed = false;

    Camera(GameObject object) {
        owners.add(object);
        delayLength = 50;
        shakeDelay = new Delay(delayLength);
        shakeDelay.start();
    }

    public synchronized void update() {
        if (map != null) {
            xMiddle = getXMiddle();
            yMiddle = getYMiddle();
            xOffset = Methods.interval(-map.getWidth() * scale + getWidth(), widthHalf - xMiddle * scale, 0);
            yOffset = Methods.interval(-map.getHeight() * scale + getHeight(), heightHalf - yMiddle * scale, 0);
            area = map.getAreaIndex(xMiddle, yMiddle);
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

    public abstract void preRenderGUI();

    public void renderGUI() {
        Drawer.refreshForRegularDrawing();
        owners.stream().forEach((object) -> {
            if (object instanceof Player) {
                ((Player) object).renderGUI(this.getXOffsetEffect(), this.getYOffsetEffect());
            }
        });
    }

    public void switchZoom() {
        zoomed = !zoomed;
        setScale(Display.getWidth() / widthHalf, Display.getHeight() / heightHalf, ownersCount);
        update();
    }

    void setScale(int ssX, int ssY, int ownersCount) {
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
        nearObjects.addVisibleLight(light);
    }

    public void clearVisibleLights() {
        nearObjects.clearVisibleLights();
    }

    private int getXMiddle() {
        xMiddle = 0;
        owners.stream().forEach((owner) -> xMiddle += owner.getX());
        return xMiddle / owners.size();
    }

    private int getYMiddle() {
        yMiddle = 0;
        owners.stream().forEach((owner) -> yMiddle += owner.getY());
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
        return delayLength;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
        update();
    }

    public int getXOffset() {
        return (int) xOffset;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getYOffset() {
        return (int) yOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
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

    public List<Light> getVisibleLights() {
        return nearObjects.getVisibleLights();
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

    public void setShakeAmplitude(int shakeAmplitude) {
        this.shakeAmplitude = shakeAmplitude;
    }

    public void setDelayLength(int delayLength) {
        this.delayLength = delayLength;
    }

    public double getScale() {
        return scale;
    }

    public int getArea() {
        return area;
    }
}
