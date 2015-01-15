/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Area;
import engine.Drawer;
import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.Mob;
import game.gameobject.Player;
import game.place.cameras.Camera;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import gamecontent.MyPlayer;

/**
 *
 * @author Wojtek
 */
public class Map {

    public final String name;
    public final Place place;
    public final Settings settings;
    public final int width, height;
    public final short id;
    public short mobID = 0;

    public int nrVLights;
    public final int sTile;

    public ArrayList<GameObject> objects = new ArrayList<>();
    public ArrayList<Mob> sMobs = new ArrayList<>();
    public ArrayList<Mob> fMobs = new ArrayList<>();
    public ArrayList<GameObject> solidObj = new ArrayList<>();
    public ArrayList<GameObject> flatObj = new ArrayList<>();
    public ArrayList<GameObject> emitters = new ArrayList<>();
    public ArrayList<WarpPoint> warps = new ArrayList<>();
    public GameObject[] visibleLights = new GameObject[2048];
    public ArrayList<Area> areas = new ArrayList<>();

    public ArrayList<GameObject> depthObj = new ArrayList<>();
    public ArrayList<GameObject> foregroundTiles = new ArrayList<>();
    public ArrayList<GameObject> onTopObject = new ArrayList<>();

    public final Tile[] tiles;

    public Map(short id, String name, Place place, int width, int height, int sTile) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.sTile = sTile;
        this.settings = place.settings;
        tiles = new Tile[width / sTile * height / sTile];
        this.place = place;
    }

    public void sortObjects(ArrayList<GameObject> a) {
        Collections.sort(a, new ObjectsComparator());
    }

    public void addFGTile(GameObject tile, int x, int y, int depth) {
        tile.setX(x);
        tile.setY(y);
        tile.setDepth(depth);
        foregroundTiles.add(tile);
        sortObjects(foregroundTiles);
    }

    public void addFGTileAndReplace(GameObject tile, int x, int y, int depth) {
        tiles[x / sTile + y / sTile * height / sTile] = null;
        for (GameObject s : foregroundTiles) {
            if (s.getX() == x && s.getY() == y) {
                foregroundTiles.remove(s);
            }
        }
        tile.setX(x);
        tile.setY(y);
        tile.setDepth(depth);
        foregroundTiles.add(tile);
        sortObjects(foregroundTiles);
    }

    public void addFGTile(GameObject t) {
        foregroundTiles.add(t);
        sortObjects(foregroundTiles);
    }

    public void deleteFGTile(GameObject t) {
        foregroundTiles.remove(t);
        sortObjects(foregroundTiles);
    }

    public void deleteFGTile(int x, int y) {
        for (GameObject s : foregroundTiles) {
            if (s.getX() == x && s.getY() == y) {
                foregroundTiles.remove(s);
            }
        }
        sortObjects(foregroundTiles);
    }

    public void addObj(GameObject go) {
        go.setMap(this);
        objects.add(go);
        if (!Player.class.isAssignableFrom(go.getClass())) {
            if (go.isEmitter()) {
                emitters.add(go);
            }
            if (Mob.class.isAssignableFrom(go.getClass())) {
                if (go.isSolid()) {
                    sMobs.add((Mob) go);
                } else {
                    fMobs.add((Mob) go);
                }
            } else {
                if (go.isSolid()) {
                    solidObj.add(go);
                } else {
                    flatObj.add(go);
                }
            }
            if (WarpPoint.class.isAssignableFrom(go.getClass())) {
                warps.add((WarpPoint) go);
                go.setPlace(place);
            }
        }
        if (go.isOnTop()) {
            onTopObject.add(go);
        } else {
            depthObj.add(go);
        }
    }

    public void deleteObj(GameObject go) {
        go.setMap(null);
        objects.remove(go);
        if (!Player.class.isAssignableFrom(go.getClass())) {
            if (go.isEmitter()) {
                emitters.remove(go);
            }
            if (Mob.class.isAssignableFrom(go.getClass())) {
                if (go.isSolid()) {
                    sMobs.remove((Mob) go);
                } else {
                    fMobs.remove((Mob) go);
                }
            } else {
                if (go.isSolid()) {
                    solidObj.remove(go);
                } else {
                    flatObj.remove(go);
                }
            }
            if (WarpPoint.class.isAssignableFrom(go.getClass())) {
                warps.remove((WarpPoint) go);
            }
        }
        if (go.isOnTop()) {
            onTopObject.remove(go);
        } else {
            depthObj.remove(go);
        }
    }

    public void renderBack(Camera cam) {
        Drawer.refresh();
        for (int y = 0; y < height / sTile; y++) {
            if (cam.getSY() < (y + 1) * sTile && cam.getEY() > y * sTile) {
                for (int x = 0; x < width / sTile; x++) {
                    if (cam.getSX() < (x + 1) * sTile && cam.getEX() > x * sTile) {
                        Tile t = tiles[x + y * height / sTile];
                        if (t != null) {
                            t.renderSpecific(0, cam.getXOffEffect() + x * sTile, cam.getYOffEffect() + y * sTile);
                        }
                    }
                }
            }
        }
    }

    public void renderObj(Camera cam) {
        renderBottom(cam);
        renderTop(cam);
    }

    public void makeShadows() {
        Renderer.initVariables(place);
    }

    public void renderBottom(Camera cam) {
        Drawer.refresh();
        sortObjects(depthObj);
        int y = 0;
        for (GameObject go : depthObj) {
            while (y < foregroundTiles.size() && foregroundTiles.get(y).getDepth() < go.getDepth()) {
                if (cam.getSY() <= foregroundTiles.get(y).getY() + (foregroundTiles.get(y).getCollisionHeight()) & cam.getEY() >= foregroundTiles.get(y).getY() - (foregroundTiles.get(y).getCollisionHeight())
                        && cam.getSX() <= foregroundTiles.get(y).getX() + (foregroundTiles.get(y).getCollisionWidth()) && cam.getEX() >= foregroundTiles.get(y).getX() - (foregroundTiles.get(y).getCollisionWidth())) {
                    foregroundTiles.get(y).render(cam.getXOffEffect(), cam.getYOffEffect());
                }
                y++;
            }
            if (cam.getSY() <= go.getY() + (go.getHeight()) && cam.getEY() >= go.getY() - (go.getHeight())
                    && cam.getSX() <= go.getX() + (go.getWidth()) && cam.getEX() >= go.getX() - (go.getWidth())) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (int i = y; i < foregroundTiles.size(); i++) {
            foregroundTiles.get(i).render(cam.getXOffEffect(), cam.getYOffEffect());
        }
    }

    public void renderTop(Camera cam) {
        Drawer.refresh();
        sortObjects(onTopObject);
        for (GameObject go : onTopObject) {
            if (cam.getSY() <= go.getY() + (go.getHeight()) && cam.getEY() >= go.getY() - (go.getHeight())
                    && cam.getSX() <= go.getX() + (go.getWidth()) && cam.getEX() >= go.getX() - (go.getWidth())) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
    }

    protected void renderText(Camera cam) {
        for (int p = 0; p < place.playersLength; p++) {
            if (place.players[p].getMap().equals(this)) {
                if (cam.getSY() <= place.players[p].getY() + (place.players[p].getHeight() + place.fonts.write(0).getHeight()) && cam.getEY() >= place.players[p].getY() - (place.players[p].getHeight() + place.fonts.write(0).getHeight())
                        && cam.getSX() <= place.players[p].getX() + (place.fonts.write(0).getWidth(place.players[p].getName())) && cam.getEX() >= place.players[p].getX() - (place.fonts.write(0).getWidth(place.players[p].getName()))) {
                    ((MyPlayer) place.players[p]).renderName(place, cam);
                }
            }
        }
        for (Mob mob : sMobs) {
            if (cam.getSY() <= mob.getY() + (mob.getHeight() + place.fonts.write(0).getHeight()) && cam.getEY() >= mob.getY() - (mob.getHeight() + place.fonts.write(0).getHeight())
                    && cam.getSX() <= mob.getX() + (place.fonts.write(0).getWidth(mob.getName())) && cam.getEX() >= mob.getX() - (place.fonts.write(0).getWidth(mob.getName()))) {
                mob.renderName(place, cam);
            }
        }
    }

    public WarpPoint findWarp(String name) {
        for (WarpPoint w : warps) {
            if (w.getName().equals(name)) {
                return w;
            }
        }
        return null;
    }

    public void clear() {
        for (GameObject go : objects) {
            if (go.getMap().equals(this)) {
                go.setMap(null);
            }
        }
        objects.clear();
        sMobs.clear();
        fMobs.clear();
        solidObj.clear();
        flatObj.clear();
        emitters.clear();
        visibleLights = new GameObject[2048];
        areas.clear();
        depthObj.clear();
        foregroundTiles.clear();
        onTopObject.clear();
    }

    private class ObjectsComparator implements Comparator<GameObject> {

        @Override
        public int compare(GameObject o1, GameObject o2) {
            return o1.getDepth() - o2.getDepth();
        }

    }

    public short getId() {
        return id;
    }
}
