/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.ShadowRenderer;
import collision.Block;
import collision.Figure;
import static collision.OpticProperties.TRANSPARENT;
import collision.Rectangle;
import engine.BlueArray;
import engine.Drawer;
import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Mob;
import static game.place.Area.X_IN_TILES;
import static game.place.Area.Y_IN_TILES;
import static game.place.Place.xAreaInPixels;
import static game.place.Place.yAreaInPixels;
import game.place.cameras.Camera;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public abstract class Map {

//    protected final static Comparator<GameObject> depthComparator = (GameObject firstObject, GameObject secondObject) -> firstObject.getDepth() - secondObject.getDepth();
    private final BlueArray<Light> visibleLights = new BlueArray<>();

    public final Place place;
    protected Color lightColor;
    protected final String name;
    protected final int width, height, tileSize;
    protected final int widthInTiles, heightInTiles;
    protected final short mapID;
    protected short mobID = 0;

    protected int xAreas, yAreas;
    protected final Area[] areas;
    private static Tile tempTile;
    private final Set<Integer> areasToUpdate = new HashSet<>(36);

    protected final static BlueArray<Point> tempTilePositions = new BlueArray<>();
    protected final BlueArray<GameObject> topObjects = new BlueArray<>();
    protected final BlueArray<GameObject> depthObjects = new BlueArray<>();
    protected final BlueArray<GameObject> gameObjects = new BlueArray<>();
    protected final ArrayList<WarpPoint> warps = new ArrayList<>();
    protected final BlueArray<WarpPoint> tempWarps = new BlueArray<>();
    protected final BlueArray<Light> lights = new BlueArray<>();
    protected final Set<Block> tempBlocks = new HashSet<>();
    protected final BlueArray<Block> blocks = new BlueArray<>();
    protected final BlueArray<Mob> mobs = new BlueArray<>();
    protected final List<Mob> tempMobs = new BlueArray<>();

    public abstract void populate();

    // TO DO - kolizja z NULL Tile'ami - tylko te nieszczęsne getX() i getY()
    private final Placement placement;
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
        for (int i = 0; i < areas.length; i++) {
            areas[i] = new Area(place, this);
        }
        placement = new Placement(this);
    }

    public void generateNavigationMeshes() {     // call after adding All blocks and tiles
        int areaIndex = 0;
        for (Area area : areas) {
            tempBlocks.clear();
            for (Block block : getBlocks(areaIndex)) {
                if (isOnArea(block, areaIndex)) {
                    tempBlocks.add(block);
                }
            }
            area.generateNavigationMesh(tempBlocks, areaIndex % xAreas, areaIndex / xAreas);
            areaIndex++;
        }
    }

    private boolean isOnArea(Block block, int area) {
        Figure collision = block.getCollision();
        if (getAreaIndex(collision.getX(), collision.getY()) == area) {
            return true;
        }
        if (getAreaIndex(collision.getX(), collision.getYEnd() - Place.tileSize) == area) {
            return true;
        }
        if (getAreaIndex(collision.getXEnd() - Place.tileSize, collision.getYEnd() - Place.tileSize) == area) {
            return true;
        }
        if (getAreaIndex(collision.getXEnd() - Place.tileSize, collision.getY()) == area) {
            return true;
        }
        return false;
    }

    public void addAreasToUpdate(int[] newAreas) {
        for (int area : newAreas) {
            areasToUpdate.add(area);
        }
    }

    public void clearAreasToUpdate() {
        areasToUpdate.clear();
    }

    public void updateMobsFromAreasToUpdate() {
        prepareMobsToUpdate();
        tempMobs.stream().forEach((mob) -> {
            mob.update();
        });
    }

    public void hardUpdateMobsFromAreasToUpdate() {
        prepareMobsToUpdate();
        tempMobs.stream().forEach((mob) -> {
            mob.updateHard();
        });
    }

    private void prepareMobsToUpdate() {
        tempMobs.clear();
        areasToUpdate.stream().filter((i) -> (i >= 0 && i < areas.length)).forEach((i) -> {
            tempMobs.addAll(areas[i].getSolidMobs());
            tempMobs.addAll(areas[i].getFlatMobs());
        });
    }

    public void addForegroundTileAndReplace(GameObject tile) {
        addForegroundTileAndReplace(tile, tile.getX(), tile.getY(), tile.getPureDepth());
    }

    public void addForegroundTileAndReplace(GameObject tile, int x, int y, int depth) {
        areas[getAreaIndex(x, y)].addForegroundTileAndReplace(tile, x, y, depth);
    }

    public void addForegroundTile(GameObject tile) {
        addForegroundTile(tile, tile.getX(), tile.getY(), tile.getPureDepth());
    }

    public void addForegroundTile(GameObject tile, int x, int y, int depth) {
        areas[getAreaIndex(x, y)].addForegroundTile(tile, x, y, depth);
    }

    public void deleteForegroundTile(GameObject tile) {
        deleteForegroundTile(tile, tile.getX(), tile.getY());
    }

    private void deleteForegroundTile(GameObject tile, int x, int y) {
        areas[getAreaIndex(x, y)].deleteForegroundTile(tile);
    }

    public void addVisibleLight(Light light) {
        visibleLights.add(light);
    }

    public void clearVisibleLights() {
        visibleLights.clear();
    }

    public void addBlock(Block block) {
        areas[getAreaIndex(block.getX(), block.getY())].addBlock(block);
    }

    public void deleteBlock(Block block) {
        areas[getAreaIndex(block.getX(), block.getY())].deleteBlock(block);
    }

    public void addObject(GameObject object) {
        object.setMapNotChange(this);
        if (object instanceof WarpPoint) {
            warps.add((WarpPoint) object);
        }
        areas[getAreaIndex(object.getX(), object.getY())].addObject(object);
    }

    public void deleteObject(GameObject object) {  // Nie usuwa świateł przypisanych do gracza, ale gracze ostatecznie nie powinni mieć świateł, więc nie zmieniam tego
        object.setMapNotChange(null);
        if (object instanceof WarpPoint) {
            warps.remove((WarpPoint) object);
        }
        areas[getAreaIndex(object.getX(), object.getY())].deleteObject(object);
    }

    protected void removeForegroundTile(GameObject foregroundTile) {
        areas[getAreaIndex(foregroundTile.getX(), foregroundTile.getY())].removeForegroundTile(foregroundTile);
    }

    public void changeAreaIfNeeded(int area, int prevArea, GameObject object) {
        if (area != prevArea) {
            changeArea(area, prevArea, object);
        }
    }

    public void changeArea(int area, int prevArea, GameObject object) {
        areas[prevArea].deleteObject(object);
        areas[area].addObject(object);
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
        for (int i : placement.getNearAreas(camera.getArea())) {
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
        getForegroundTiles(camera.getArea());
        getDepthObjects(camera.getArea());
        Methods.insort(depthObjects);
        int y = 0;
        for (GameObject object : depthObjects) {
            for (; y < gameObjects.size() && gameObjects.get(y).getDepth() < object.getDepth(); y++) {
                if (gameObjects.get(y).isVisible() && isObjectInSight(gameObjects.get(y))) {
                    gameObjects.get(y).render(cameraXOffEffect, cameraYOffEffect);
                }
            }
            if (object.isVisible() && isObjectInSight(object)) {
                object.render(cameraXOffEffect, cameraYOffEffect);
            }
        }
        for (int i = y; i < gameObjects.size(); i++) {
            if (gameObjects.get(i).isVisible() && isObjectInSight(gameObjects.get(i))) {
                gameObjects.get(i).render(cameraXOffEffect, cameraYOffEffect);
            }
        }
    }

    public void sortDepthObjects() {
        Methods.insort(depthObjects);
    }

    public void renderTop(Camera camera) {
        getTopObjects(camera.getArea());
        topObjects.stream().filter((object) -> (object.isVisible() && isObjectInSight(object))).forEach((object) -> {
            object.render(cameraXOffEffect, cameraYOffEffect);
        });
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
        for (Area area : areas) {
            area.clear();
        }
        visibleLights.clear();
        topObjects.clear();
        depthObjects.clear();
        gameObjects.clear();
        warps.clear();
        lights.clear();
        blocks.clear();
        mobs.clear();
        tempMobs.clear();
        tempWarps.clear();
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

    public Color getLightColor() {
        if (lightColor != null) {
            return lightColor;
        } else {
            return place.getLightColor();
        }
    }

    public int getXAreas() {
        return xAreas;
    }

    public int getYAreas() {
        return yAreas;
    }

    public Tile getTile(int x, int y) {
        if (x < widthInTiles && y < heightInTiles) {
            return areas[getAreaIndexCoordinatesInTiles(x, y)].getTile(x % (X_IN_TILES), y % (Y_IN_TILES));
        }
        return null;
    }

    public int getAreasSize() {
        return areas.length;
    }

    public int getAreaIndexCoordinatesInTiles(int x, int y) {
        return (int) (x / X_IN_TILES) + (int) (y / Y_IN_TILES) * xAreas;
    }

    public int getAreaIndex(int x, int y) {
        return (int) (x / xAreaInPixels) + (int) (y / yAreaInPixels) * xAreas;
    }

    public int[] getNearAreas(int area) {
        return placement.getNearAreas(area);
    }

    public List<Mob> getSolidMobs(int x, int y) {
        return getSolidMobs(getAreaIndex(x, y));
    }

    public List<Mob> getSolidMobs(int area) {
        mobs.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                mobs.addAll(areas[i].getSolidMobs());
            }
        }
        return Collections.unmodifiableList(mobs);
    }

    public List<Mob> getFlatMobs(int x, int y) {
        return getFlatMobs(getAreaIndex(x, y));
    }

    public List<Mob> getFlatMobs(int area) {
        mobs.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                mobs.addAll(areas[i].getFlatMobs());
            }
        }
        return Collections.unmodifiableList(mobs);
    }

    public List<GameObject> getSolidObjects(int x, int y) {
        return getSolidObjects(getAreaIndex(x, y));
    }

    public List<GameObject> getSolidObjects(int area) {
        gameObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                gameObjects.addAll(areas[i].getSolidObjects());
            }
        }
        return Collections.unmodifiableList(gameObjects);
    }

    public List<GameObject> getFlatObjects(int x, int y) {
        return getFlatObjects(getAreaIndex(x, y));
    }

    public List<GameObject> getFlatObjects(int area) {
        gameObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                gameObjects.addAll(areas[i].getFlatObjects());
            }
        }
        return Collections.unmodifiableList(gameObjects);
    }

    public List<Light> getLightsFromAreasToUpdate() {
        lights.clear();
        areasToUpdate.stream().filter((i) -> (i >= 0 && i < areas.length)).forEach((i) -> {
            lights.addAll(areas[i].getEmitters());
        });
        return Collections.unmodifiableList(lights);
    }

    public List<Light> getVisibleLights() {
        return Collections.unmodifiableList(visibleLights);
    }

    public List<GameObject> getDepthObjects(int x, int y) {
        return getDepthObjects(getAreaIndex(x, y));
    }

    public List<GameObject> getDepthObjects(int area) {
        depthObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                Methods.merge(depthObjects, areas[i].getDepthObjects());
            }
        }
        return Collections.unmodifiableList(depthObjects);
    }

    public List<GameObject> getTopObjects(int x, int y) {
        return getTopObjects(getAreaIndex(x, y));
    }

    public List<GameObject> getTopObjects(int area) {
        topObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                Methods.merge(topObjects, areas[i].getTopObjects());
            }
        }
        return Collections.unmodifiableList(topObjects);
    }

    public List<WarpPoint> getWarps(int x, int y) {
        return getWarps(getAreaIndex(x, y));
    }

    public List<WarpPoint> getWarps(int area) {
        tempWarps.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                tempWarps.addAll(areas[i].getWarps());
            }
        }
        return Collections.unmodifiableList(tempWarps);
    }

    public List<GameObject> getForegroundTiles(int x, int y) {
        return getForegroundTiles(getAreaIndex(x, y));
    }

    public List<GameObject> getForegroundTiles(int area) {
        gameObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                Methods.merge(gameObjects, areas[i].getForegroundTiles());
            }
        }
        return Collections.unmodifiableList(gameObjects);
    }

    public List<Block> getBlocks(int x, int y) {
        return getBlocks(getAreaIndex(x, y));
    }

    public List<Block> getBlocks(int area) {
        blocks.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                blocks.addAll(areas[i].getBlocks());
            }
        }
        return Collections.unmodifiableList(blocks);
    }

    public void setTile(int x, int y, Tile tile) {
        if (x < widthInTiles && y < heightInTiles) {
            areas[getAreaIndexCoordinatesInTiles(x, y)].setTile(x % (X_IN_TILES), y % (Y_IN_TILES), tile);
        }
    }

    public void setColor(Color color) {
        this.lightColor = color;
    }

    public List<Point> getNearTiles(Figure collision) {
        tempTilePositions.clear();
        return tempTilePositions;
    }
}
