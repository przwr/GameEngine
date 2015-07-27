/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Block;
import collision.Figure;
import engine.BlueArray;
import engine.Drawer;
import engine.Light;
import engine.Methods;
import engine.PointContener;
import game.gameobject.GameObject;
import game.gameobject.Mob;
import static game.place.Area.X_IN_TILES;
import static game.place.Area.Y_IN_TILES;
import static game.place.Place.xAreaInPixels;
import static game.place.Place.yAreaInPixels;
import game.place.cameras.Camera;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public abstract class Map {

    private static final PointContener DIFFRENT_AREAS = new PointContener(0);
    private final BlueArray<Light> visibleLights = new BlueArray<>();

    public final Place place;
    protected Color lightColor;
    protected final String name;
    protected final int width, height, tileSize;
    protected final int widthInTiles, heightInTiles;
    protected final short mapID;
    protected short mobID = 0;

    protected int xAreas, yAreas;
    public final Area[] areas;
    private static Tile tempTile;
    private final Set<Integer> areasToUpdate = new HashSet<>(36);

    protected final PointContener tempTilePositions = new PointContener();
    protected final BlueArray<WarpPoint> tempWarps = new BlueArray<>();
    protected final Set<Block> tempBlocks = new HashSet<>();
    protected final BlueArray<Mob> tempMobs = new BlueArray<>();

    protected final BlueArray<GameObject> topObjects = new BlueArray<>();
    protected final BlueArray<GameObject> foregroundTiles = new BlueArray<>();
    protected final ArrayList<WarpPoint> warps = new ArrayList<>();
    protected final BlueArray<Light> lights = new BlueArray<>();
    protected final BlueArray<Block> blocks = new BlueArray<>();
    protected final BlueArray<Mob> mobs = new BlueArray<>();

    protected List<GameObject> depthObjects;

    public abstract void populate();

    private final Placement placement;
    private int cameraXStart, cameraYStart, cameraXEnd, cameraYEnd, cameraXOffEffect, cameraYOffEffect;     //Camera's variables for current rendering

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
        for (int areaIndex = 0; areaIndex < areas.length; areaIndex++) {
            areas[areaIndex] = new Area(place, this, areaIndex % xAreas, areaIndex / xAreas);
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
            area.generateNavigationMesh(tempBlocks);
            areaIndex++;
        }
    }

    private List<Block> getBlocks(int area) {
        blocks.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                blocks.addAll(areas[i].getBlocks());
            }
        }
        return blocks;
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

    public PointContener findPath(int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        int area = getAreaIndex(xStart, yStart);
        if (area != getAreaIndex(xDestination, yDestination)) {
            return DIFFRENT_AREAS;
        }
        int x = areas[area].getXInPixels();
        int y = areas[area].getYInPixels();
        return areas[area].findPath(xStart - x, yStart - y, xDestination - x, yDestination - y, collision);
    }

    public void addAreasToUpdate(int[] newAreas) {
        for (int area : newAreas) {
            areasToUpdate.add(area);
        }
    }

    public void clearAreasToUpdate() {
        areasToUpdate.clear();
    }

    public void updateAreasToUpdate() {
        areasToUpdate.stream().filter((area) -> (area >= 0 && area < areas.length)).forEach((area) -> {
            areas[area].updateContainers(area);
        });
    }

    public void updateNearBlocks(int area, List<Block> blocks) {
        blocks.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                blocks.addAll(areas[i].getBlocks());
            }
        }
    }

    public void updateNearSolidMobs(int area, List<Mob> mobs) {
        mobs.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                mobs.addAll(areas[i].getSolidMobs());
            }
        }
    }

    public void updateNearFlatMobs(int area, List<Mob> mobs) {
        mobs.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                mobs.addAll(areas[i].getFlatMobs());
            }
        }
    }

    public void updateNearSolidObjects(int area, List<GameObject> objects) {
        objects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                objects.addAll(areas[i].getSolidObjects());
            }
        }
    }

    public void updateNearFlatObjects(int area, List<GameObject> objects) {
        objects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                objects.addAll(areas[i].getFlatObjects());
            }
        }
    }

    public void updateNearWarps(int area, List<WarpPoint> warps) {
        warps.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                warps.addAll(areas[i].getWarps());
            }
        }
    }

    public void updateNearDepthObjects(int area, List<GameObject> depthObjects) {
        depthObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                Methods.merge(depthObjects, areas[i].getDepthObjects());
            }
        }
    }

    private void updateNearForegroundTiles(int area) {
        foregroundTiles.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                Methods.merge(foregroundTiles, areas[i].getForegroundTiles());
            }
        }
    }

    private void updateNearTopObjects(int area) {
        topObjects.clear();
        for (int i : placement.getNearAreas(area)) {
            if (i >= 0 && i < areas.length) {
                Methods.merge(topObjects, areas[i].getTopObjects());
            }
        }
    }

    public PointContener getNearNullTiles(Figure collision) {
        tempTilePositions.clear();
        int xs = (collision.getX() / Place.tileSize) - 1;
        int ys = (collision.getY() / Place.tileSize) - 1;
        int xe = (collision.getXEnd() / Place.tileSize) + 1;
        int ye = (collision.getYEnd() / Place.tileSize) + 1;
        for (int x = xs; x <= xe; x++) {
            for (int y = ys; y <= ye; y++) {
                if (getTile(x, y) == null) {
                    tempTilePositions.add(x, y);
                }
            }
        }
        return tempTilePositions;
    }

    public void updateMobsFromAreasToUpdate() {
        prepareMobsToUpdate();
        tempMobs.stream().forEach((mob) -> {
            mob.update();
            mob.weapon.checkCollision(place.players);
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
        areasToUpdate.stream().filter((area) -> (area >= 0 && area < areas.length)).forEach((area) -> {
            tempMobs.addAll(areas[area].getSolidMobs());
            tempMobs.addAll(areas[area].getFlatMobs());
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

    public void deleteObject(GameObject object) {
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
        Drawer.clearScreen(0);
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
        updateNearForegroundTiles(camera.getArea());
        depthObjects = areas[camera.getArea()].getNearDepthObjects();
        Methods.insort(depthObjects);
        int y = 0;
        for (GameObject object : areas[camera.getArea()].getNearDepthObjects()) {
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
        updateNearTopObjects(camera.getArea());
        topObjects.stream().filter((object) -> (object.isVisible() && isObjectInSight(object))).forEach((object) -> {
            object.render(cameraXOffEffect, cameraYOffEffect);
        });
    }

    private boolean isObjectInSight(GameObject object) {
        return cameraYStart <= object.getYSpriteEnd()
                && cameraYEnd >= object.getYSpriteBegin()
                && cameraXStart <= object.getXSpriteEnd()
                && cameraXEnd >= object.getXSpriteBegin();
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
        foregroundTiles.clear();
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
        if (x >= 0 && x < widthInTiles && y >= 0 && y < heightInTiles) {
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

    public List<Light> getLightsFromAreasToUpdate() {
        lights.clear();
        areasToUpdate.stream().filter((i) -> (i >= 0 && i < areas.length)).forEach((i) -> {
            lights.addAll(areas[i].getLights());
        });
        return lights;
    }

    public List<Light> getVisibleLights() {
        return visibleLights;
    }

    public Area getArea(int i) {
        return areas[i];
    }

    public Area getArea(int x, int y) {
        return areas[getAreaIndex(x, y)];
    }

    public void setTile(int x, int y, Tile tile) {
        if (x < widthInTiles && y < heightInTiles) {
            areas[getAreaIndexCoordinatesInTiles(x, y)].setTile(x % (X_IN_TILES), y % (Y_IN_TILES), tile);
        }
    }

    public void setColor(Color color) {
        this.lightColor = color;
    }
}
