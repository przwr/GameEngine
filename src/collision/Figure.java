/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.BlueArray;
import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.ForegroundTile;
import game.place.Map;
import game.place.Place;
import game.place.Shadow;
import java.util.List;

/**
 *
 * @author Wojtek
 */
public abstract class Figure implements Comparable<Figure> {

	private static Figure figure;

	private final GameObject owner;
	private final OpticProperties opticProperties;
	protected int xStart, yStart, width, height, xCenter, yCenter;
	protected final BlueArray<Point> points;
	private boolean mobile = false;

	public abstract boolean isCollideSingle(int x, int y, Figure figure);

	public abstract List<Point> getPoints();

	public abstract void updatePoints();

	public Figure(int xStart, int yStart, GameObject owner, OpticProperties opticProperties) {
		this.xStart = xStart;
		this.yStart = yStart;
		this.owner = owner;
		this.opticProperties = opticProperties;
		this.points = new BlueArray<>();
	}

	public boolean isCollideSolid(int x, int y, Map map) {
		for (Point point : map.getNearTiles(this)) {

		}
		if (map.getSolidMobs(getOwner().getArea()).stream().anyMatch((object) -> (checkCollison(x, y, object)))) {
			return true;
		}
		if (map.getSolidObjects(getOwner().getArea()).stream().anyMatch((object) -> (checkCollison(x, y, object)))) {
			return true;
		}
		return map.getBlocks(getOwner().getArea()).stream().anyMatch((object) -> (object.isSolid() && object.isCollide(x, y, this)));
	}

	public GameObject whatCollideSolid(int x, int y, Map map) {
		for (GameObject object : map.getSolidMobs(x, y)) {
			if (checkCollison(x, y, object)) {
				return object;
			}
		}
		for (GameObject object : map.getSolidObjects(x, y)) {
			if (checkCollison(x, y, object)) {
				return object;
			}
		}
		for (Block object : map.getBlocks(x, y)) {
			if (object.isSolid() && object.isCollide(x, y, this)) {
				return object;
			}
		}
		return null;
	}

	public boolean isCollide(int x, int y, List<GameObject> objects) {
		return objects.stream().anyMatch((object) -> (checkCollison(x, y, object)));
	}

	public GameObject whatCollide(int x, int y, List<GameObject> objects) {
		for (GameObject object : objects) {
			if (checkCollison(x, y, object)) {
				return object;
			}
		}
		return null;
	}

	public boolean isCollidePlayer(int x, int y, Place place) {
		for (int i = 0; i < place.playersCount; i++) {
			if (checkCollison(x, y, place.players[i])) {
				return true;
			}
		}
		return false;
	}

	public Player firstPlayerCollide(int x, int y, Place place) {
		if (place.players[0].getMap() == owner.getMap() && checkCollison(x, y, place.players[0])) {
			return (Player) place.players[0];
		}
		return null;
	}

	private boolean checkCollison(int x, int y, GameObject object) {
		figure = object.getCollision();
		if (object == owner || figure == null) {
			return false;
		}
		return isCollideSingle(x, y, figure);
	}

	public void addShadow(int type, int x, int y, Figure source) {
		opticProperties.addShadow(type, x, y, source);
	}

	public void addShadow(int type, int x, int y) {
		opticProperties.addShadow(type, x, y, null);
	}

	public void addShadow(int type) {
		opticProperties.addShadow(type, 0, 0, null);
	}

	public void clearShadows() {
		opticProperties.clearShadows();
	}

	public void removeShadow(Shadow shadow) {
		opticProperties.removeShadow(shadow);
	}

	@Override
	public int compareTo(Figure Figure) { // Check this out
		return ((getDepth() - ((Figure) Figure).getDepth()) << 13) - (getLightDistance() - ((Figure) Figure).getLightDistance());
	}

	private int getDepth() {
		if (getOwner() instanceof Block || getOwner() instanceof ForegroundTile) {
			return getYEnd() - Place.tileSize;
		} else {
			return getY();
		}
		//return getOwner().getDepth();
	}

	public boolean isLittable() {
		return opticProperties.isLitable();
	}

	public boolean isGiveShadow() {
		return opticProperties.isGiveShadow();
	}

	public boolean isMobile() {
		return mobile;
	}

	public boolean isConcave() {
		return false;
	}

	public boolean isTriangular() {
		return false;
	}

	public boolean isBottomRounded() {
		return false;
	}

	public int getX() {
		return owner.getX() + xStart;
	}

	public int getY() {
		return owner.getY() + yStart;
	}

	public int getXEnd() {
		return owner.getX() + xStart + width;
	}

	public int getYEnd() {
		return owner.getY() + yStart + height;
	}

	public int getYOwnerEnd() {
		return owner.getYSpriteEnd();
	}

	public int getYOwnerBegin() {
		return owner.getYSpriteBegin();
	}

	public int getXReferencePointForShadow() {
		return owner.getXReferencePointForShadow();
	}

	public int getXSpriteBegin() {
		return owner.getXSpriteBegin();
	}

	public int getXSpriteEnd() {
		return owner.getXSpriteEnd();
	}

	public int getX(int x) {
		return x + xStart;
	}

	public int getY(int y) {
		return y + yStart;
	}

	public int getXStart() {
		return xStart;
	}

	public int getYStart() {
		return yStart;
	}

	public int getXCentral() {
		return owner.getX() + xStart + xCenter;
	}

	public int getYCentral() {
		return owner.getY() + yStart + yCenter;
	}

	public int getXCentral(int x) {
		return x + yStart + xCenter;
	}

	public int getYCentral(int y) {
		return y + yStart + yCenter;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Point getPoint(int index) {
		return points.get(index);
	}

	public GameObject getOwner() {
		return owner;
	}

	public int getShadowHeight() {
		return opticProperties.getShadowHeight();
	}

	public int getLightDistance() {
		return opticProperties.getLightDistance();
	}

	public int getShadowCount() {
		return opticProperties.getShadowCount();
	}

	public Shadow getShadow(int i) {
		return opticProperties.getShadow(i);
	}

	public int getType() {
		return opticProperties.getType();
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	public void setXStart(int xStart) {
		this.xStart = xStart;
	}

	public void setYStart(int yStart) {
		this.yStart = yStart;
	}

	public void setLightDistance(int lightDistance) {
		opticProperties.setLightDistance(lightDistance);
	}

	public void setOpticProperties(int type) {
		opticProperties.setType(type);
	}

	public void setShadowHeight(int shadowHeight) {
		opticProperties.setType(shadowHeight);
	}
}
