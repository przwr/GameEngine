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

    protected final String name;
    private final short ID;
    protected final int width, height, tileSize;
    protected final int tilewidth, tileheight;
    private final ArrayList<GameObject> allObjects = new ArrayList<>();
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

    public short mobID = 0; // POWINIEN BYĆ inicjalizowany w mapie

    public Map(short ID, String name, Place place, int width, int height, int tileSize) {
        this.place = place;
        this.settings = place.settings;
        this.name = name;
        this.ID = ID;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        tilewidth = width / tileSize;
        tileheight = height / tileSize;
        tiles = new Tile[tilewidth * tileheight];
    }

    public void sortObjects(ArrayList<GameObject> objects) {
        Collections.sort(objects, depthComparator);
    }

    public void addForegroundTile(GameObject tile, int x, int y, int depth) {
        tile.setX(x);
        tile.setY(y);
        tile.setDepth(depth);
        foregroundTiles.add(tile);
        sortObjectsByDepth(foregroundTiles);
    }

    public void addForegroundTileAndReplace(GameObject tile, int x, int y, int depth) {
        tiles[x / tileSize + y / tileSize * tileheight] = null;
        foregroundTiles.stream().filter((obj) -> (obj.getX() == x && obj.getY() == y)).forEach((obj) -> {
            foregroundTiles.remove(obj);
        });
        tile.setX(x);
        tile.setY(y);
        tile.setDepth(depth);
        foregroundTiles.add(tile);
        sortObjectsByDepth(foregroundTiles);
    }

    public void addForegroundTile(GameObject obj) {
        foregroundTiles.add(obj);
        sortObjectsByDepth(foregroundTiles);
    }

    public void deleteForegroundTile(GameObject obj) {
        foregroundTiles.remove(obj);
        sortObjectsByDepth(foregroundTiles);
    }

    public void deleteForegroundTile(int x, int y) {
        foregroundTiles.stream().filter((fGTile) -> (fGTile.getX() == x && fGTile.getY() == y)).forEach((fGTile) -> {
            foregroundTiles.remove(fGTile);
        });
        sortObjectsByDepth(foregroundTiles);
    }

    public void addObject(GameObject object) {
        object.setMapNotChange(this);
        allObjects.add(object);
        if (!(object instanceof Player)) {
            if (object.isEmitter()) {
                emitters.add(object);
            }
            if (object instanceof WarpPoint) {
                warps.add((WarpPoint) object);
                object.setPlace(place);
            } else if (object instanceof Mob) {
                if (object.isSolid()) {
                    solidMobs.add((Mob) object);
                } else {
                    flatMobs.add((Mob) object);
                }
            } else {
                if (object.isSolid()) {
                    solidObjects.add(object);
                } else {
                    flatObjects.add(object);
                }
            }
        }
        if (object.isOnTop()) {
            objectsOnTop.add(object);
        } else {
            depthObjects.add(object);
        }
    }

    public void deleteObject(GameObject object) {
        object.setMapNotChange(null);
        allObjects.remove(object);
        if (!(object instanceof Player)) {
            if (object.isEmitter()) {
                emitters.remove(object);
            }
            if (object instanceof WarpPoint) {
                warps.remove((WarpPoint) object);
            } else if (object instanceof Mob) {
                if (object.isSolid()) {
                    solidMobs.remove((Mob) object);
                } else {
                    flatMobs.remove((Mob) object);
                }
            } else {
                if (object.isSolid()) {
                    solidObjects.remove(object);
                } else {
                    flatObjects.remove(object);
                }
            }
        }
        if (object.isOnTop()) {
            objectsOnTop.remove(object);
        } else {
            depthObjects.remove(object);
        }
    }

    public void renderBackground(Camera cam) {
        Drawer.refreshForRegularDrawing();
        for (int y = 0; y < tileheight; y++) {
            if (cam.getYStart() < (y + 1) * tileSize && cam.getYEnd() > y * tileSize) {
                for (int x = 0; x < width / tileSize; x++) {
                    if (cam.getXStart() < (x + 1) * tileSize && cam.getXEnd() > x * tileSize) {
                        Tile tile = tiles[x + y * tileheight];
                        if (tile != null) {
                            tile.renderSpecific(cam.getXOffsetEffect() + x * tileSize, cam.getYOffsetEffect() + y * tileSize);
                        }
                    }
                }
            }
        }
    }

    public void renderObjects(Camera cam) {
        renderBottom(cam);
        renderTop(cam);
    }

    public void makeShadows() {
        Renderer.initializeVariables(place);
    }

    public void renderBottom(Camera cam) {
        Drawer.refreshForRegularDrawing();
        sortObjectsByDepth(depthObjects);
        int y = 0;
        for (GameObject object : depthObjects) {
            while (y < foregroundTiles.size() && foregroundTiles.get(y).getDepth() < object.getDepth()) {
                if (cam.getYStart() <= foregroundTiles.get(y).getY() + (foregroundTiles.get(y).getCollisionHeight()) & cam.getYEnd() >= foregroundTiles.get(y).getY() - (foregroundTiles.get(y).getCollisionHeight())
                        && cam.getXStart() <= foregroundTiles.get(y).getX() + (foregroundTiles.get(y).getCollisionWidth()) && cam.getXEnd() >= foregroundTiles.get(y).getX() - (foregroundTiles.get(y).getCollisionWidth())) {
                    foregroundTiles.get(y).render(cam.getXOffsetEffect(), cam.getYOffsetEffect());
                }
                y++;
            }
            if (object.isVisible() && cam.getYStart() <= object.getY() + (object.getHeight()) && cam.getYEnd() >= object.getY() - (object.getHeight())
                    && cam.getXStart() <= object.getX() + (object.getWidth()) && cam.getXEnd() >= object.getX() - (object.getWidth())) {
                object.render(cam.getXOffsetEffect(), cam.getYOffsetEffect());
            }
        }
        for (int i = y; i < foregroundTiles.size(); i++) {
            foregroundTiles.get(i).render(cam.getXOffsetEffect(), cam.getYOffsetEffect());
        }
    }

    public void renderTop(Camera cam) {
        Drawer.refreshForRegularDrawing();
        sortObjectsByDepth(objectsOnTop);
        for (GameObject object : objectsOnTop) {
            if (object.isVisible() && cam.getYStart() <= object.getY() + (object.getHeight()) && cam.getYEnd() >= object.getY() - (object.getHeight())
                    && cam.getXStart() <= object.getX() + (object.getWidth()) && cam.getXEnd() >= object.getX() - (object.getWidth())) {
                object.render(cam.getXOffsetEffect(), cam.getYOffsetEffect());
            }
        }
    }

    public void sortObjectsByDepth(ArrayList<GameObject> objects) {
        Collections.sort(objects, depthComparator);
    }

    protected void renderText(Camera cam) {
        for (int p = 0; p < place.playersLength; p++) {
            if (place.players[p].getMap().equals(this)) {
                if (cam.getYStart() <= place.players[p].getY() + (place.players[p].getHeight() + place.fonts.write(0).getHeight()) && cam.getYEnd() >= place.players[p].getY() - (place.players[p].getHeight() + place.fonts.write(0).getHeight())
                        && cam.getXStart() <= place.players[p].getX() + (place.fonts.write(0).getWidth(place.players[p].getName())) && cam.getXEnd() >= place.players[p].getX() - (place.fonts.write(0).getWidth(place.players[p].getName()))) {
                    ((Player) place.players[p]).renderName(place, cam);
                }
            }
        }
        for (Mob mob : solidMobs) {
            if (cam.getYStart() <= mob.getY() + (mob.getHeight() + place.fonts.write(0).getHeight()) && cam.getYEnd() >= mob.getY() - (mob.getHeight() + place.fonts.write(0).getHeight())
                    && cam.getXStart() <= mob.getX() + (place.fonts.write(0).getWidth(mob.getName())) && cam.getXEnd() >= mob.getX() - (place.fonts.write(0).getWidth(mob.getName()))) {
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
        allObjects.stream().filter((obj) -> (obj.getMap().equals(this))).forEach((obj) -> {
            obj.setMapNotChange(null);
        });
        allObjects.clear();
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

    public int getTileWidth() {
        return tilewidth;
    }

    public int getTileHeight() {
        return tilewidth;
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

    public Tile getTile(int x, int y) {
        return tiles[x + y * tileheight];
    }

    public Tile getTile(int index) {
        return tiles[index];
    }

    public String getName() {
        return name;
    }

    public short getId() {
        return ID;
    }

    public void setTile(int x, int y, Tile tile) {
        tiles[x + y * tileheight] = tile;
    }

    public void setTile(int index, Tile tile) {
        tiles[index] = tile;
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
}
