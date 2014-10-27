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

    protected int type;
    
    protected GameObject owner;

    public Figure(int xs, int ys, GameObject owner) {
        this.xs = xs;
        this.ys = ys;
        this.owner = owner;
    }
    
    public boolean ifCollide(int x, int y, Place p) {
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
        for (GameObject obj : p.tiles) {
            if (obj.isSolid() && checkCollison(x, y, obj)) {
                return true;
            }
        }
        return false;
    }

    public GameObject whatCollide(int x, int y, Place p) {
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
        for (GameObject obj : p.tiles) {
            if (obj.isSolid() && checkCollison(x, y, obj)) {
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
    
    public int getX() {
        return owner.getX() + xs;
    }
    
    public int getY() {
        return owner.getY() + ys;
    }
    
    public int getX(int x) {
        return x + xs;
    }
    
    public int getY(int y) {
        return y + ys;
    }
    
    private boolean checkCollison(int x, int y, GameObject obj) {
        if (obj.equals(owner))
            return false;
        Figure f = obj.getCollision();
        if (f == null)
            return false;
        
        return ifCollideSngl(x, y, f);
    }
    
    public abstract boolean ifCollideSngl(int x, int y, Figure f);

    public abstract Point[] listPoints();

    public int getType() {
        return type;
    }
}
