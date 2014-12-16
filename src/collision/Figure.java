/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import game.place.Shadow;
import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.Place;
import java.util.ArrayList;
import net.jodk.lang.FastMath;

/**
 *
 * @author Wojtek
 */
public abstract class Figure implements Comparable<Object> {

    protected int xs;
    protected int ys;
    protected int width;
    protected int height;
    protected int xCentr;
    protected int yCentr;
    protected boolean canBeLit;
    protected boolean canGiveShadow;
    protected int shadowHeight;
    private int distFromLight;
    public float shadowColor;
    public ArrayList<Shadow> shadows;

    protected int type;

    private final GameObject owner;

    public Figure(int xs, int ys, GameObject owner) {
        this.xs = xs;
        this.ys = ys;
        this.owner = owner;
        shadows = new ArrayList<>();
    }

    public boolean ifCollideSolid(int x, int y, Place p) {
        for (GameObject obj : p.sMobs) {
            if (checkCollison(x, y, obj)) {
                return true;
            }
        }
        for (GameObject obj : p.solidObj) {
            if (checkCollison(x, y, obj)) {
                return true;
            }
        }
        for (Area obj : p.areas) {
            if (obj.isSolid() && obj.ifCollide(x, y, this)) {
                return true;
            }
        }
        return false;
    }

    public GameObject whatCollideSolid(int x, int y, Place p) {
        for (GameObject obj : p.sMobs) {
            if (checkCollison(x, y, obj)) {
                return obj;
            }
        }
        for (GameObject obj : p.solidObj) {
            if (checkCollison(x, y, obj)) {
                return obj;
            }
        }
        for (Area obj : p.areas) {
            if (obj.isSolid() && obj.ifCollide(x, y, this)) {
                return obj;
            }
        }
        return null;
    }

    public boolean ifCollide(int x, int y, ArrayList<GameObject> gos) {
        for (GameObject obj : gos) {
            if (checkCollison(x, y, obj)) {
                return true;
            }
        }
        return false;
    }

    public GameObject whatCollide(int x, int y, ArrayList<GameObject> gos) {
        for (GameObject obj : gos) {
            if (checkCollison(x, y, obj)) {
                return obj;
            }
        }
        return null;
    }

    public boolean ifCollide(int x, int y, Place place) {
        for (int p = 0; p < place.playersLength; p++) {
            if (checkCollison(x, y, place.players[p])) {
                return true;
            }
        }
        return false;
    }

    public Player getCollided(int x, int y, Place place) {
        if (checkCollison(x, y, place.players[0])) {
            return (Player) place.players[0];
        }
        return null;
    }

    public GameObject whatCollide(int x, int y, GameObject[] gos) {
        for (GameObject obj : gos) {
            if (checkCollison(x, y, obj)) {
                return obj;
            }
        }
        return null;
    }

    public int getX() {
        return owner.getX() + xs;
    }

    public int getY() {
        return owner.getY() + ys;
    }

    public int getEndX() {
        return owner.getX() + xs + width;
    }

    public int getEndY() {
        return owner.getY() + ys + height;
    }

    public int getOwnEndY() {
        return owner.getObjectEndY();
    }

    public int getOwnBegY() {
        return owner.getObjectBegY();
    }

    public int getX(int x) {
        return x + xs;
    }

    public int getY(int y) {
        return y + ys;
    }

    public void setXs(int x) {
        xs = x;
    }

    public void setYs(int y) {
        ys = y;
    }

    public int getXs() {
        return xs;
    }

    public int getYs() {
        return ys;
    }

    public int getCentralX() {
        return owner.getX() + xs + xCentr;
    }

    public int getCentralY() {
        return owner.getY() + ys + yCentr;
    }

    public int getCentralX(int x) {
        return x + ys + xCentr;
    }

    public int getCentralY(int y) {
        return y + ys + yCentr;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private boolean checkCollison(int x, int y, GameObject obj) {
        if (obj.equals(owner)) {
            return false;
        }
        Figure f = obj.getCollision();
        if (f == null/* || !ifGoodDistance(x, y, f)*/) {
            return false;
        }

        return ifCollideSngl(x, y, f);
    }

    public boolean ifGoodDistance(int x, int y, Figure f) {
        if (f.getType() == 1) {
            return true;
        }
        int dx = FastMath.abs(getCentralX(x) - f.getCentralX());
        int dy = FastMath.abs(getCentralY(y) - f.getCentralY());
        return (dx <= (getWidth() + f.getWidth()) / 2 && dy <= (getWidth() + f.getWidth()) / 2);
    }

    public abstract boolean ifCollideSngl(int x, int y, Figure f);

    public abstract Point[] listPoints();

    public abstract void centralize();

    public int getType() {
        return type;
    }

    public boolean canBeLit() {
        return canBeLit;
    }

    public boolean canGiveShadow() {
        return canGiveShadow;
    }

    public GameObject own() {
        return owner;
    }

    @Override
    public int compareTo(Object o) {
        if ((getEndY() - ((Figure) o).getEndY()) == 0) {
            if (getDistFromLight() == -1) {
                return 1;
            }
            return (getDistFromLight() - ((Figure) o).getDistFromLight());
        }
        return getEndY() - ((Figure) o).getEndY();
    }

    public int getDistFromLight() {
        return distFromLight;
    }

    public void setDistFromLight(int distFromLight) {
        this.distFromLight = distFromLight;
    }

    public int shadowHeight() {
        return shadowHeight;
    }

}
