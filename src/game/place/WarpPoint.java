/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Figure;
import game.gameobject.GameObject;

/**
 *
 * @author Wojtek
 */
public class WarpPoint extends GameObject {

    private final boolean isWarp;   //Czy teleportuje
    private final boolean isStatic;   //Czy teleportuje na wskazany punkt, czy na obiekt
    private int toX;
    private int toY;
    private Map dest;
    private String strDest = null;

    public WarpPoint(String name, int x, int y, int toX, int toY, Map map) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = true;
        isStatic = true;
        this.toX = toX;
        this.toY = toY;
        dest = map;
    }

    public WarpPoint(String name, int x, int y, int toX, int toY, String map) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = true;
        isStatic = true;
        this.toX = toX;
        this.toY = toY;
        strDest = map;
    }

    public WarpPoint(String name, int x, int y, Map map) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = true;
        isStatic = false;
        dest = map;
    }

    public WarpPoint(String name, int x, int y, String map) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = true;
        isStatic = false;
        strDest = map;
    }

    public WarpPoint(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = false;
        isStatic = false;
    }

    public void Warp(GameObject o) {
        if (isWarp) {
            if (isStatic) {
                if (dest != null) {
                    o.changeMap(dest);
                } else if (strDest != null) {
                    o.changeMap(map.place.getMap(strDest));
                }
                o.setX(toX);
                o.setY(toY);
            } else {
                WarpPoint w;
                if (dest != null) {
                    o.changeMap(dest);
                    w = dest.findWarp(name);
                } else {
                    Map m = map.place.getMap(strDest);
                    o.changeMap(m);
                    w = m.findWarp(name);
                }
                o.setX(w.x);
                o.setY(w.y);
            }
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f, int xs, int xe) {
    }

}
