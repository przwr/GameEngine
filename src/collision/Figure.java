/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.Map;
import game.place.Place;
import game.place.Shadow;
import java.util.ArrayList;
import java.util.Collection;
import net.jodk.lang.FastMath;

/**
 *
 * @author Wojtek
 */
public abstract class Figure implements Comparable<Object> {

    private final GameObject owner;
    private final OpticProperties opticProperties;
    protected int xStart, yStart, width, height, xCentr, yCentr;
    protected final ArrayList<Point> points;

    public Figure(int xStart, int yStart, GameObject owner, OpticProperties opticProperties) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.owner = owner;
        this.opticProperties = opticProperties;
        this.points = new ArrayList<>();
    }

    public abstract boolean isCollideSingle(int x, int y, Figure f);

    public abstract Collection<Point> getPoints();

    public boolean isCollideSolid(int x, int y, Map map) {
        if (map.sMobs.stream().anyMatch((obj) -> (checkCollison(x, y, obj)))) {
            return true;
        }
        if (map.solidObj.stream().anyMatch((obj) -> (checkCollison(x, y, obj)))) {
            return true;
        }
        if (map.areas.stream().anyMatch((obj) -> (obj.isSolid() && obj.isCollide(x, y, this)))) {
            return true;
        }
        return false;
    }

    public GameObject whatCollideSolid(int x, int y, Map map) {
        for (GameObject obj : map.sMobs) {
            if (checkCollison(x, y, obj)) {
                return obj;
            }
        }
        for (GameObject obj : map.solidObj) {
            if (checkCollison(x, y, obj)) {
                return obj;
            }
        }
        for (Area obj : map.areas) {
            if (obj.isSolid() && obj.isCollide(x, y, this)) {
                return obj;
            }
        }
        return null;
    }

    public boolean isCollide(int x, int y, Collection<GameObject> objects) {
        if (objects.stream().anyMatch((obj) -> (checkCollison(x, y, obj)))) {
            return true;
        }
        return false;
    }

    public GameObject whatCollide(int x, int y, Collection<GameObject> objects) {
        for (GameObject obj : objects) {
            if (checkCollison(x, y, obj)) {
                return obj;
            }
        }
        return null;
    }

    public boolean isCollidePlayer(int x, int y, Place place) {
        for (int p = 0; p < place.playersLength; p++) {
            if (checkCollison(x, y, place.players[p])) {
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

    private boolean checkCollison(int x, int y, GameObject obj) {
        if (obj == owner) {
            return false;
        }
        Figure figure = obj.getCollision();
        if (figure == null/* || !ifGoodDistance(x, y, f)*/) {                /// WHAT? CHECK!
            return false;
        }
        return isCollideSingle(x, y, figure);
    }

    private boolean ifGoodDistance(int x, int y, Figure f) {              /// WHAT? CHECK!
        if (f instanceof Rectangle) {
            return true;
        }
        int dx = FastMath.abs(getCentralX(x) - f.getCentralX());
        int dy = FastMath.abs(getCentralY(y) - f.getCentralY());
        return (dx <= (getWidth() + f.getWidth()) / 2 && dy <= (getWidth() + f.getWidth()) / 2);
    }

    public void addShadow(Shadow shadow) {
        opticProperties.addShadow(shadow);
    }

    public void clearShadows() {
        opticProperties.clearShadows();
    }

    @Override
    public int compareTo(Object o) {
        if (getEndY() == ((Figure) o).getEndY()) {
            if (opticProperties.getDistFromLight() == -1) {
                return 1;
            }
            return (opticProperties.getDistFromLight() - ((Figure) o).opticProperties.getDistFromLight());
        }
        return getEndY() - ((Figure) o).getEndY();
    }

    public int getX() {
        return owner.getX() + xStart;
    }

    public int getY() {
        return owner.getY() + yStart;
    }

    public int getEndX() {
        return owner.getX() + xStart + width;
    }

    public int getEndY() {
        return owner.getY() + yStart + height;
    }

    public int getOwnEndY() {
        return owner.getObjEndOfY();
    }

    public int getOwnBegY() {
        return owner.getObjBegOfY();
    }

    public int getOwnBegX() {
        return owner.getObjBegOfX();
    }

    public int getOwnEndX() {
        return owner.getObjEndOfX();
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

    public int getCentralX() {
        return owner.getX() + xStart + xCentr;
    }

    public int getCentralY() {
        return owner.getY() + yStart + yCentr;
    }

    public int getCentralX(int x) {
        return x + yStart + xCentr;
    }

    public int getCentralY(int y) {
        return y + yStart + yCentr;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public Point getPoint(int index){
        return points.get(index);
    }

    public GameObject getOwner() {
        return owner;
    }

    public boolean isLittable() {
        return opticProperties.isLitable();
    }

    public boolean isGiveShadow() {
        return opticProperties.isGiveShadow();
    }

    public int getDistFromLight() {
        return opticProperties.getDistFromLight();
    }

    public int getShadowHeight() {
        return opticProperties.getShadowHeight();
    }

    public float getShadowColor() {
        return opticProperties.getShadowColor();
    }

    public Collection<Shadow> getShadows() {
        return opticProperties.getShadows();
    }

    public void setXs(int x) {
        xStart = x;
    }

    public void setYs(int y) {
        yStart = y;
    }

    public void setDistFromLight(int distFromLight) {
        opticProperties.setDistFromLight(distFromLight);
    }

    public void setShadowColor(float shadowColor) {
        opticProperties.setShadowColor(shadowColor);
    }
}
