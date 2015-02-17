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
import game.place.Map;
import java.util.ArrayList;

/**
 *
 * @author przemek
 */
public abstract class Camera {

	protected final ArrayList<GUIObject> gui = new ArrayList<>();
	public GameObject[] visibleLights = new GameObject[2048];
	public int visibleLightsCount;
	protected final ArrayList<GameObject> owners = new ArrayList<>();
	protected Map map;
	protected int widthHalf, heightHalf, xMiddle, yMiddle, xEffect, yEffect, xLeft, xRight, yDown, yUp, delayLenght, shakeAmplitude = 8;
	protected double xOffset, yOffset;
	protected Delay shakeDelay;
	private boolean shakeUp = true;

	public Camera(GameObject object) {
		owners.add(object);
		delayLenght = 50;
		shakeDelay = new Delay(delayLenght);
		shakeDelay.start();
	}

	public synchronized void update() {
		if (map != null) {
			xOffset = Methods.interval(-map.getWidth() * Settings.scale + getWidth(), widthHalf - getXMiddle(), 0);
			yOffset = Methods.interval(-map.getHeight() * Settings.scale + getHeight(), heightHalf - getYMiddle(), 0);
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
		gui.stream().forEach((go) -> {
			if (go.isVisible()) {
				go.render(getXStart() + getXOffsetEffect(), getYStart() + getYOffsetEffect());
			}
		});
	}

	public void addGUI(GUIObject object) {
		if (!gui.contains(object)) {
			gui.add(object);
			object.setCamera(this);
		}
	}

	public int getXMiddle() {
		xMiddle = 0;
		owners.stream().forEach((owner) -> {
			xMiddle += owner.getX();
		});
		return (int) (xMiddle * Settings.scale) / owners.size();
	}

	public int getYMiddle() {
		yMiddle = 0;
		owners.stream().forEach((owner) -> {
			yMiddle += owner.getY();
		});
		return (int) (yMiddle * Settings.scale) / owners.size();
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

	public int getXStart() {
		return -getXOffsetEffect();
	}

	public int getYStart() {
		return -getYOffsetEffect();
	}

	public int getXEnd() {
		return (int) ((-getXOffsetEffect() + widthHalf * 2) / Settings.scale);
	}

	public int getYEnd() {
		return (int) ((-getYOffsetEffect() + heightHalf * 2) / Settings.scale);
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
}
