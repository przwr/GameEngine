/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Block;
import engine.Drawer;
import game.gameobject.GameObject;
import game.gameobject.Mob;
import game.gameobject.Player;
import game.place.cameras.Camera;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.Iterator;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public class Map {

    private final ArrayList<Light> visibleLights = new ArrayList<>();
    public final Place place;

    protected Color color;
    protected final Tile[] tiles;
    protected final ArrayList<Block> blocks = new ArrayList<>();
    protected final String name;
    protected final int width, height, tileSize;
    protected final int widthInTiles, heightInTiles;

    protected final short mapID;
    protected short mobID = 0;
    protected final ArrayList<Mob> solidMobs = new ArrayList<>();
    protected final ArrayList<Mob> flatMobs = new ArrayList<>();
    protected final ArrayList<GameObject> solidObjects = new ArrayList<>();
    protected final ArrayList<GameObject> flatObjects = new ArrayList<>();
    protected final ArrayList<Light> emitters = new ArrayList<>();
    protected final ArrayList<WarpPoint> warps = new ArrayList<>();

    protected final ArrayList<GameObject> foregroundTiles = new ArrayList<>();
    protected final ArrayList<GameObject> objectsOnTop = new ArrayList<>();
    protected final ArrayList<GameObject> depthObjects = new ArrayList<>();
    protected final Comparator<GameObject> depthComparator = (GameObject firstObject, GameObject secondObject)
            -> firstObject.getDepth() - secondObject.getDepth();

    private int cameraXStart, cameraYStart, cameraXEnd, cameraYEnd, cameraXOffEffect, cameraYOffEffect; //Camera's variables for current rendering

    public Map(short mapID, String name, Place place, int width, int height, int tileSize) {
        this.place = place;
        this.name = name;
        this.mapID = mapID;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        widthInTiles = width / tileSize;
        heightInTiles = height / tileSize;
        tiles = new Tile[widthInTiles * heightInTiles];
    }

    public void addForegroundTileAndReplace(GameObject tile) {
        addForegroundTileAndReplace(tile, tile.getX(), tile.getY(), tile.getPureDepth());
    }

    public void addForegroundTileAndReplace(GameObject tile, int x, int y, int depth) {
        if (tile.isSimpleLighting()) {
            tiles[x / tileSize + y / tileSize * heightInTiles] = null;
        }
        GameObject object;
        for (Iterator<GameObject> iterator = foregroundTiles.iterator(); iterator.hasNext();) {
            object = iterator.next();
            if ((object.getX() == x && object.getY() == y)) {
                iterator.remove();
            }
        }
        addForegroundTile(tile, x, y, depth);
    }

    public void addForegroundTile(GameObject tile, int x, int y, int depth) {
        tile.setX(x);
        tile.setY(y);
        tile.setDepth(depth);
        addForegroundTile(tile);
    }

    public void addForegroundTile(GameObject tile) {
        foregroundTiles.add(tile);
        sortObjectsByDepth(foregroundTiles);
    }

    public void deleteForegroundTile(GameObject tile) {
        foregroundTiles.remove(tile);
        sortObjectsByDepth(foregroundTiles);
    }

    public void deleteForegroundTile(int x, int y) {
        foregroundTiles.stream().filter((foregroundTile)
                -> (foregroundTile.getX() == x && foregroundTile.getY() == y)).forEach((foregroundTile) -> {
                    foregroundTiles.remove(foregroundTile);
                });
        sortObjectsByDepth(foregroundTiles);
    }

    public void addVisibleLight(Light light) {
        visibleLights.add(light);
    }

    public void clearVisibleLights() {
        visibleLights.clear();
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public void deleteBlock(Block block) {
        blocks.remove(block);
    }

    public void addObject(GameObject object) {
        object.setMapNotChange(this);
        if (object.isOnTop()) {
            objectsOnTop.add(object);
        } else {
            depthObjects.add(object);
        }
        if (!(object instanceof Player)) {
            addNotPlayerObject(object);
        }
    }

    private void addNotPlayerObject(GameObject object) {

        if (object.isEmitter()) {
            object.getLights().stream().forEach((light) -> {
                emitters.add(light);
            });
        }
        if (object instanceof WarpPoint) {
            addWarpPoint((WarpPoint) object);
        } else if (object instanceof Mob) {
            addMob((Mob) object);
        } else {
            if (object.isSolid()) {
                solidObjects.add(object);
            } else {
                flatObjects.add(object);
            }
        }
    }

    private void addWarpPoint(WarpPoint warp) {
        warps.add(warp);
        warp.setPlace(place);
    }

    private void addMob(Mob mob) {
        if (mob.isSolid()) {
            solidMobs.add(mob);
        } else {
            flatMobs.add(mob);
        }
    }

    public void deleteObject(GameObject object) {  // Nie usuwa świateł przypisanych do gracza, ale gracze ostatecznie nie powinni mieć świateł, więc nie zmieniam tego
        object.setMapNotChange(null);
        if (!(object instanceof Player)) {
            deleteNotPlayerObject(object);
        }
        if (object.isOnTop()) {
            objectsOnTop.remove(object);
        } else {
            depthObjects.remove(object);
        }
    }

    private void deleteNotPlayerObject(GameObject object) {
        if (object.isEmitter()) {
            object.getLights().stream().forEach((light) -> {
                emitters.remove(light);
            });
        }
        if (object instanceof WarpPoint) {
            warps.remove((WarpPoint) object);
        } else if (object instanceof Mob) {
            deleteMob((Mob) object);
        } else {
            if (object.isSolid()) {
                solidObjects.remove(object);
            } else {
                flatObjects.remove(object);
            }
        }
    }

    private void deleteMob(Mob mob) {
        if (mob.isSolid()) {
            solidMobs.remove(mob);
        } else {
            flatMobs.remove(mob);
        }
    }

    public void updateCamerasVariables(Camera camera) {
        cameraXStart = camera.getXStart();
        cameraYStart = camera.getYStart();
        cameraXEnd = camera.getXEnd();
        cameraYEnd = camera.getYEnd();
        cameraXOffEffect = camera.getXOffsetEffect();
        cameraYOffEffect = camera.getYOffsetEffect();
    }

    public void renderBackground(Camera camera) {
        Drawer.drawRectangleInBlack(0, 0, camera.getWidth(), camera.getHeight());
        ShadowRenderer.clearScreen(0);
        Drawer.refreshForRegularDrawing();
        for (int y = 0; y < heightInTiles; y++) {
            if (cameraYStart < (y + 1) * tileSize && cameraYEnd > y * tileSize) {
                for (int x = 0; x < width / tileSize; x++) {
                    if (cameraXStart < (x + 1) * tileSize && cameraXEnd > x * tileSize) {
                        Tile tile = tiles[x + y * heightInTiles];
                        if (tile != null && tile.isVisible()) {
                            tile.renderSpecific(cameraXOffEffect, cameraYOffEffect, x * tileSize, y * tileSize);
                        }
                    }
                }
            }
        }
    }

    public void renderObjects(Camera camera) {
        Drawer.refreshForRegularDrawing();
        renderBottom(camera);
        renderTop(camera);
    }

    public void renderBottom(Camera camera) {
        sortObjectsByDepth(depthObjects);
        int y = 0;
        for (GameObject object : depthObjects) {
            for (; y < foregroundTiles.size() && foregroundTiles.get(y).getDepth() < object.getDepth(); y++) {
                if (foregroundTiles.get(y).isVisible() && isObjectInSight(foregroundTiles.get(y))) {
                    foregroundTiles.get(y).render(cameraXOffEffect, cameraYOffEffect);
                }
            }
            if (object.isVisible() && isObjectInSight(object)) {
                object.render(cameraXOffEffect, cameraYOffEffect);
            }
        }
        for (int i = y; i < foregroundTiles.size(); i++) {
            if (foregroundTiles.get(i).isVisible() && isObjectInSight(foregroundTiles.get(i))) {
                foregroundTiles.get(i).render(cameraXOffEffect, cameraYOffEffect);
            }
        }
    }

    public void renderTop(Camera camera) {
        sortObjectsByDepth(objectsOnTop);
        objectsOnTop.stream().filter((object) -> (object.isVisible()
                && isObjectInSight(object))).forEach((object) -> {
                    object.render(cameraXOffEffect, cameraYOffEffect);
                });
    }

    public void sortObjectsByDepth(ArrayList<GameObject> objects) {
        Collections.sort(objects, depthComparator);
    }

    private boolean isObjectInSight(GameObject object) {
        return cameraYStart <= object.getY() + (object.getHeight())
                && cameraYEnd >= object.getY() - (object.getHeight())
                && cameraXStart <= object.getX() + (object.getWidth())
                && cameraXEnd >= object.getX() - (object.getWidth());
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
        solidMobs.clear();
        flatMobs.clear();
        solidObjects.clear();
        flatObjects.clear();
        emitters.clear();
        visibleLights.clear();
        blocks.clear();
        depthObjects.clear();
        foregroundTiles.clear();
        objectsOnTop.clear();
    }

    public int getTileWidth() {
        return widthInTiles;
    }

    public int getTileHeight() {
        return widthInTiles;
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
        return tiles[x + y * heightInTiles];
    }

    public Tile getTile(int index) {
        return tiles[index];
    }

    public String getName() {
        return name;
    }

    public short getID() {
        return mapID;
    }

    public Collection<Mob> getSolidMobs() {
        return Collections.unmodifiableList(solidMobs);
    }

    public Collection<Mob> getFlatMobs() {
        return Collections.unmodifiableList(flatMobs);
    }

    public Collection<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public Collection<GameObject> getSolidObjects() {
        return Collections.unmodifiableList(solidObjects);
    }

    public Collection<GameObject> getFlatObjects() {
        return Collections.unmodifiableList(flatObjects);
    }

    public Collection<Light> getEmitters() {
        return Collections.unmodifiableList(emitters);
    }

    public Collection<Light> getVisibleLights() {
        return Collections.unmodifiableList(visibleLights);
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

    public Collection<GameObject> getForegroundTiles() {
        return Collections.unmodifiableList(foregroundTiles);
    }

    public Color getColor() {
        if (color != null) {
            return color;
        } else {
            return place.color;
        }
    }

    public void setTile(int x, int y, Tile tile) {
        tiles[x + y * heightInTiles] = tile;
    }

    public void setTile(int index, Tile tile) {
        tiles[index] = tile;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
