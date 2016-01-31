/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import collision.Figure;
import engine.Main;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.place.Place;

/**
 * @author Wojtek
 */
public class WarpPoint extends GameObject {

    private final boolean isWarp;   //Czy teleportuje
    private final boolean isStatic;   //Czy teleportuje na wskazany punkt, czy na obiekt
    private int xDestination;
    private int yDestination;
    private Place place;
    private Map destination;
    private String stringDestination = null;

    public WarpPoint(String name, int x, int y, int toX, int toY, Map map) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = true;
        isStatic = true;
        this.xDestination = toX;
        this.yDestination = toY;
        destination = map;
    }

    public WarpPoint(String name, int x, int y, int toX, int toY, String map) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = true;
        isStatic = true;
        this.xDestination = toX;
        this.yDestination = toY;
        stringDestination = map;
    }

    public WarpPoint(String name, int x, int y, Map map) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = true;
        isStatic = false;
        destination = map;
    }

    public WarpPoint(String name, int x, int y, String map) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = true;
        isStatic = false;
        stringDestination = map;
    }

    public WarpPoint(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        isWarp = false;
        isStatic = false;
    }

    public void warp(GameObject object) {
        if (isWarp) {
            if (isStatic) {
                if (destination != null) {
                    object.changeMap(destination, xDestination, yDestination);
                } else {
                    loadMap(object);
                }
            } else {
                WarpPoint warp;
                if (destination != null) {
                    warp = destination.findWarp(name);
                    object.changeMap(destination, warp.getX(), warp.getY());
                } else {
                    loadMap(object);
                }
            }
            if (object instanceof Player && ((Player) object).getCamera() != null) {
                ((Player) object).getCamera().updateStatic();
            }
        }
    }

    private void loadMap(GameObject object) {
        place.game.getMapLoader().requestMap(stringDestination, this);
        if (object.getMap() != null && object.getMap() != place.loadingMap) {
            object.changeMap(place.loadingMap, 0, 0);
            object.setWarp(this);
        } else {
            Map map = place.getMapByName(stringDestination);
            if (map != null && Main.backgroundLoader.allLoaded()) {
                WarpPoint warp = map.findWarp(name);
                object.changeMap(map, warp.getX(), warp.getY());
            }
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xs, int xe) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xs, int xe) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
