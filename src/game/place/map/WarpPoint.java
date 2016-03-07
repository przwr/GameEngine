/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import collision.Figure;
import engine.Main;
import engine.utilities.Delay;
import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.temporalmodifiers.CameraJoiner;
import game.gameobject.temporalmodifiers.LockChanger;
import game.gameobject.temporalmodifiers.TemporalChanger;
import game.place.Place;
import gamecontent.MyPlayer;

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
    private Delay delay = Delay.createInMilliseconds(250);
    private Delay secondDelay = Delay.createInMilliseconds(125);
    private boolean loading, joined;

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
            if (object.getWarp() != this && object instanceof Entity && !(((Entity) object).getChangers().isEmpty())) {
                return;
            }
            if (object instanceof Player) {
                loadMap(object);
            } else if (isStatic) {
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
        }
    }

    private void loadMap(GameObject object) {
        place.game.getMapLoader().requestMap(stringDestination, this);
        if (object.getMap() != null && object.getWarp() != this) {
            if (object instanceof Player) {
                if (((Player) object).getCamera() != null) {
                    ((Player) object).getCamera().fade(250, true);
                    joined = Settings.joinSplitScreen;
                }
                loading = true;
                delay.start();
                ((Player) object).setAbleToMove(false);
            }
            object.setWarp(this);
        } else if (delay.isOver()) {
            if (object.isVisible()) {
                object.setVisible(false);
            }
            Map map = place.getMapByName(stringDestination);
            if (map != null) {
                if (object instanceof Player) {
                    MyPlayer player = (MyPlayer) object;
                    WarpPoint warp = map.findWarp(name);
                    player.changeMap(map, warp.getX(), warp.getY());
                    player.setCurrentLocationAsSpawnPosition();
                    TemporalChanger lockChanger = new LockChanger(8, (Entity) object);
                    lockChanger.start();
                    if (joined) {
                        TemporalChanger joiner = new CameraJoiner(45);
                        joiner.start();
                        player.addChanger(joiner);
                    }
                    player.addChanger(lockChanger);
                    player.getGUI().setVisible(true);
                    if (player.getCamera() != null) {
                        player.getCamera().updateStatic();
                        player.getCamera().fade(250, false);
                    }
                } else {
                    WarpPoint warp = map.findWarp(name);
                    object.changeMap(map, warp.getX(), warp.getY());
                    if (object instanceof Entity) {
                        TemporalChanger lockChanger = new LockChanger(8, (Entity) object);
                        lockChanger.start();
                        ((Entity) object).addChanger(lockChanger);
                    } else {
                        object.setVisible(true);
                    }
                }
                object.setWarp(null);
            } else if (object instanceof MyPlayer) {
                if (loading) {
                    ((MyPlayer) object).getGUI().setVisible(false);
                    secondDelay.start();
                    loading = false;
                } else if (secondDelay.isOver()) {
                    object.changeMap(place.loadingMap, 0, 0);
                }
            } else {
                object.changeMap(place.loadingMap, 0, 0);
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
