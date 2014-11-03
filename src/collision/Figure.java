/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import game.gameobject.GameObject;
import game.place.Place;
import java.util.ArrayList;

/**
 *
 * @author Wojtek
 */
public abstract class Figure {

    protected int xs;
    protected int ys;
    protected int width;
    protected int ShadowHeight;
    protected int height;
    protected int xCentr;
    protected int yCentr;

    protected int type;

    private GameObject owner;

    public Figure(int xs, int ys, GameObject owner) {
        this.xs = xs;
        this.ys = ys;
        this.owner = owner;
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

    public GameObject whatCollide(int x, int y, GameObject[] gos) {
        for (GameObject obj : gos) {
            if (checkCollison(x, y, obj)) {
                return obj;
            }
        }
        return null;
    }

    public int getX() {
        return getOwner().getX() + xs;
    }

    public int getY() {
        return getOwner().getY() + ys;
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
        return getOwner().getX() + xs + xCentr;
    }

    public int getCentralY() {
        return getOwner().getY() + ys + yCentr;
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
        if (obj.equals(getOwner())) {
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
        int dx = Math.abs(getCentralX(x) - f.getCentralX());
        int dy = Math.abs(getCentralY(y) - f.getCentralY());
        return (dx <= (getWidth() + f.getWidth()) / 2 && dy <= (getWidth() + f.getWidth()) / 2);
    }

    public abstract boolean ifCollideSngl(int x, int y, Figure f);

    public abstract Point[] listPoints();

    public abstract void centralize();

    public int getType() {
        return type;
    }

    public int getShadowHeight() {
        return ShadowHeight;
    }

    public GameObject getOwner() {
        return owner;
    }
}
