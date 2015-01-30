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
import java.util.Collection;

/**
 *
 * @author Wojtek
 */
public class Map {

    public ArrayList<GameObject> visibleLights = new ArrayList<>(128);
    public final Place place;
    public final Settings settings;

    protected final Tile[] tiles;
    protected final ArrayList<Area> areas = new ArrayList<>();

    private final String name;
    private final short id;
    private final int width, height, tileSize;
    private final ArrayList<GameObject> objects = new ArrayList<>();
    private final ArrayList<Mob> solidMobs = new ArrayList<>();
    private final ArrayList<Mob> flatMobs = new ArrayList<>();
    private final ArrayList<GameObject> solidObjects = new ArrayList<>();
    private final ArrayList<GameObject> flatObjects = new ArrayList<>();
    private final ArrayList<GameObject> emitters = new ArrayList<>();
    private final ArrayList<WarpPoint> warps = new ArrayList<>();

    private final ArrayList<GameObject> foregroundTiles = new ArrayList<>();
    private final ArrayList<GameObject> objectsOnTop = new ArrayList<>();
    private final ArrayList<GameObject> depthObjects = new ArrayList<>();
    private final Comparator<GameObject> depthComparator = (GameObject obj1, GameObject obj2) -> obj1.getDepth() - obj2.getDepth();

    public short mobId = 0; // POWINIE BYĆ inicjalizowany w mapie

    public Map(short id, String name, Place place, int width, int height, int tileSize) {
        this.place = place;
        this.settings = place.settings;
        this.name = name;
        this.id = id;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        tiles = new Tile[width / tileSize * height / tileSize];
    }

    public void sortObjects(ArrayList<GameObject> objects) {
        Collections.sort(objects, depthComparator);
    }

    public void addFGTile(GameObject tile, int x, int y, int depth) {
        tile.setX(x);
        tile.setY(y);
        tile.setDepth(depth);
        foregroundTiles.add(tile);
        sortObjects(foregroundTiles);
    }

    public void addFGTileAndReplace(GameObject tile, int x, int y, int depth) {
        tiles[x / tileSize + y / tileSize * height / tileSize] = null;
        foregroundTiles.stream().filter((obj) -> (obj.getX() == x && obj.getY() == y)).forEach((obj) -> {
            foregroundTiles.remove(obj);
        });
        tile.setX(x);
        tile.setY(y);
        tile.setDepth(depth);
        foregroundTiles.add(tile);
        sortObjects(foregroundTiles);
    }

    public void addFGTile(GameObject obj) {
        foregroundTiles.add(obj);
        sortObjects(foregroundTiles);
    }

    public void deleteFGTile(GameObject obj) {
        foregroundTiles.remove(obj);
        sortObjects(foregroundTiles);
    }

    public void deleteFGTile(int x, int y) {
        foregroundTiles.stream().filter((fGTile) -> (fGTile.getX() == x && fGTile.getY() == y)).forEach((fGTile) -> {
            foregroundTiles.remove(fGTile);
        });
        sortObjects(foregroundTiles);
    }

    public void addObj(GameObject obj) {
        obj.setMapNotChange(this);
        objects.add(obj);
        if (!(obj instanceof Player)) {
            if (obj.isEmitter()) {
                emitters.add(obj);
            }
            if (obj instanceof WarpPoint) {
                warps.add((WarpPoint) obj);
                obj.setPlace(place);
            } else if (obj instanceof Mob) {
                if (obj.isSolid()) {
                    solidMobs.add((Mob) obj);
                } else {
                    flatMobs.add((Mob) obj);
                }
            } else {
                if (obj.isSolid()) {
                    solidObjects.add(obj);
                } else {
                    flatObjects.add(obj);
                }
            }
        }
        if (obj.isOnTop()) {
            objectsOnTop.add(obj);
        } else {
            depthObjects.add(obj);
        }
    }

    public void deleteObj(GameObject obj) {
        obj.setMapNotChange(null);
        objects.remove(obj);
        if (!(obj instanceof Player)) {
            if (obj.isEmitter()) {
                emitters.remove(obj);
            }
            if (obj instanceof WarpPoint) {
                warps.remove((WarpPoint) obj);
            } else if (obj instanceof Mob) {
                if (obj.isSolid()) {
                    solidMobs.remove((Mob) obj);
                } else {
                    flatMobs.remove((Mob) obj);
                }
            } else {
                if (obj.isSolid()) {
                    solidObjects.remove(obj);
                } else {
                    flatObjects.remove(obj);
                }
            }
        }
        if (obj.isOnTop()) {
            objectsOnTop.remove(obj);
        } else {
            depthObjects.remove(obj);
        }
    }

    public void renderBack(Camera cam) {
        Drawer.refreshForRegularDrawing();
        for (int y = 0; y < height / tileSize; y++) {
            if (cam.getSY() < (y + 1) * tileSize && cam.getEY() > y * tileSize) {
                for (int x = 0; x < width / tileSize; x++) {
                    if (cam.getSX() < (x + 1) * tileSize && cam.getEX() > x * tileSize) {
                        Tile tile = tiles[x + y * height / tileSize];
                        if (tile != null) {
                            tile.renderSpecific(0, cam.getXOffEffect() + x * tileSize, cam.getYOffEffect() + y * tileSize);
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
        Drawer.refreshForRegularDrawing();
        sortObjects(depthObjects);
        int y = 0;
        for (GameObject go : depthObjects) {
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
        Drawer.refreshForRegularDrawing();
        sortObjects(objectsOnTop);
        for (GameObject go : objectsOnTop) {
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
                    ((Player) place.players[p]).renderName(place, cam);
                }
            }
        }
        for (Mob mob : solidMobs) {
            if (cam.getSY() <= mob.getY() + (mob.getHeight() + place.fonts.write(0).getHeight()) && cam.getEY() >= mob.getY() - (mob.getHeight() + place.fonts.write(0).getHeight())
                    && cam.getSX() <= mob.getX() + (place.fonts.write(0).getWidth(mob.getName())) && cam.getEX() >= mob.getX() - (place.fonts.write(0).getWidth(mob.getName()))) {
                mob.renderName(place, cam);
            }
        }
    }

    public void renderAdditional(Camera cam) {
        //TODO <(^.^<) COS CIEKAWEGO  (Dodatkowe rysowanie, jak sie chce...)
    }
    
    public void updateAdditional(Camera cam) {
        //TODO (>O.o)> COS FAJNEGO  (Dodatkowy update, jak sie chce...)
    }

    public WarpPoint findWarp(String name) {
        for (WarpPoint warp : warps) {
            if (warp.getName().equals(name)) {
                return warp;
            }
        }
        return null;
    }

    public void clear() {
        objects.stream().filter((obj) -> (obj.getMap().equals(this))).forEach((obj) -> {
            obj.setMapNotChange(null);
        });
        objects.clear();
        solidMobs.clear();
        flatMobs.clear();
        solidObjects.clear();
        flatObjects.clear();
        emitters.clear();
        visibleLights.clear();
        areas.clear();
        depthObjects.clear();
        foregroundTiles.clear();
        objectsOnTop.clear();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileSize() {
        return tileSize;
    }

    public Tile getTile(int index) {
        return tiles[index];
    }

    public String getName() {
        return name;
    }

    public short getId() {
        return id;
    }

    public Collection<Mob> getSolidMobs() {
        return Collections.unmodifiableList(solidMobs);
    }

    public Collection<Mob> getFlatMobs() {
        return Collections.unmodifiableList(flatMobs);
    }

    public Collection<Area> getAreas() {
        return Collections.unmodifiableList(areas);
    }

    public Collection<GameObject> getSolidObjects() {
        return Collections.unmodifiableList(solidObjects);
    }

    public Collection<GameObject> getFlatObjects() {
        return Collections.unmodifiableList(flatObjects);
    }

    public Collection<GameObject> getEmitters() {
        return Collections.unmodifiableList(emitters);
    }

    public Collection<GameObject> getDepthObjects() {
        return Collections.unmodifiableList(depthObjects);
    }

    public Collection<GameObject> getObjectsOnTop() {
        return Collections.unmodifiableList(objectsOnTop);
    }

    public Collection<WarpPoint> getWarps() {
        return Collections.unmodifiableList(warps);
    }

    public void setTile(int index, Tile tile) {
        tiles[index] = tile;
    }
}
