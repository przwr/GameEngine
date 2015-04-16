/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.ShadowRenderer;
import collision.Block;
import engine.Drawer;
import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Mob;
import game.gameobject.Player;
import static game.place.Area.X_IN_TILES;
import static game.place.Area.Y_IN_TILES;
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

    protected Color lightColor;
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
    protected final static Comparator<GameObject> depthComparator = (GameObject firstObject, GameObject secondObject)
            -> firstObject.getDepth() - secondObject.getDepth();

    private int xAreas, yAreas;
    private final Area[] areas;
    private static Tile tempTile;

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

        xAreas = (widthInTiles / Area.X_IN_TILES) + (widthInTiles % Area.X_IN_TILES != 0 ? 1 : 0);
        yAreas = (heightInTiles / Area.Y_IN_TILES) + (heightInTiles % Area.Y_IN_TILES != 0 ? 1 : 0);
        areas = new Area[xAreas * yAreas];
//        System.out.println(xAreas + " " + yAreas + " " + areas.length);
        for (int i = 0; i < areas.length; i++) {
            areas[i] = new Area();
        }
    }

    public void addForegroundTileAndReplace(GameObject tile) {
        addForegroundTileAndReplace(tile, tile.getX(), tile.getY(), tile.getPureDepth());
    }

    public void addForegroundTileAndReplace(GameObject tile, int x, int y, int depth) {
        if (tile.isSimpleLighting()) {
            areas[getAreaIndex(x / tileSize, y / tileSize)].setTile(x % X_IN_TILES, y % Y_IN_TILES, null);
        }
        GameObject object;
        for (Iterator<GameObject> iterator = foregroundTiles.iterator(); iterator.hasNext();) {
            object = iterator.next();
            if (object.isVisible() && object.getX() == x && object.getY() == y) {
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
        tile.setMapNotChange(this);
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
        block.setMapNotChange(this);
    }

    public void deleteBlock(Block block) {
        blocks.remove(block);
        block.setMapNotChange(null);
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
        ShadowRenderer.clearScreen(0);
        Drawer.refreshForRegularDrawing();
        for (int i : camera.getNearAreas()) {
            if (i >= 0 && i < areas.length) {
                int yTemp = (i / xAreas) * Y_IN_TILES;
                int xTemp = (i % xAreas) * X_IN_TILES;
                for (int yTiles = 0; yTiles < Y_IN_TILES; yTiles++) {
                    int y = yTemp + yTiles;
                    if (cameraYStart < (y + 1) * tileSize && cameraYEnd > y * tileSize) {
                        for (int xTiles = 0; xTiles < X_IN_TILES; xTiles++) {
                            int x = xTemp + xTiles;
                            if (cameraXStart < (x + 1) * tileSize && cameraXEnd > x * tileSize) {
                                tempTile = areas[i].getTile(xTiles, yTiles);
                                if (tempTile != null && tempTile.isVisible()) {
                                    tempTile.renderSpecific(cameraXOffEffect, cameraYOffEffect, x * tileSize, y * tileSize);
                                }
                            }
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

    public void sortForegroundTiles() {
        Collections.sort(foregroundTiles, depthComparator);
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

    public int getWidthInTIles() {
        return widthInTiles;
    }

    public int getHeightInTiles() {
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

    public String getName() {
        return name;
    }

    public short getID() {
        return mapID;
    }

    public int getXAreas() {
        return xAreas;
    }

    public int getYAreas() {
        return yAreas;
    }

    public Tile getTile(int x, int y) {
        if (x < widthInTiles && y < heightInTiles) {
            return areas[getAreaIndex(x, y)].getTile(x % (X_IN_TILES), y % (Y_IN_TILES));
        }
        return null;
    }

    public int getAreasSize() {
        return areas.length;
    }

    public int getAreaIndex(double x, double y) {
        return (int) (x / X_IN_TILES) + (int) (y / Y_IN_TILES) * xAreas;
    }

    public Color getLightColor() {
        if (lightColor != null) {
            return lightColor;
        } else {
            return place.getLightColor();
        }
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

    public void setTile(int x, int y, Tile tile) {
        if (x < widthInTiles && y < heightInTiles) {
            areas[getAreaIndex(x, y)].setTile(x % (X_IN_TILES), y % (Y_IN_TILES), tile);
        }
    }

    public void setColor(Color color) {
        this.lightColor = color;
    }
}
