/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Block;
import collision.Figure;
import engine.BlueArray;
import engine.Light;
import engine.Methods;
import engine.PointContainer;
import game.gameobject.GameObject;
import game.gameobject.Interactive;
import game.gameobject.Mob;
import game.gameobject.Player;
import navmeshpathfinding.NavigationMesh;
import navmeshpathfinding.NavigationMeshGenerator;
import navmeshpathfinding.PathFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static game.place.Place.xAreaInPixels;
import static game.place.Place.yAreaInPixels;

/**
 * @author przemek
 */
public class Area {

    public static final short X_IN_TILES = 32, Y_IN_TILES = 20;

    private final Place place;
    private final Map map;
    private final int xArea, yArea, xInPixels, yInPixels;
    private final Tile[] tiles;

    private final ArrayList<Block> blocks = new ArrayList<>();
    private final ArrayList<Mob> solidMobs = new ArrayList<>();
    private final ArrayList<Mob> flatMobs = new ArrayList<>();
    private final ArrayList<GameObject> solidObjects = new ArrayList<>();
    private final ArrayList<GameObject> flatObjects = new ArrayList<>();
    private final ArrayList<GameObject> foregroundTiles = new ArrayList<>();
    private final ArrayList<GameObject> topObjects = new ArrayList<>();
    private final ArrayList<GameObject> depthObjects = new ArrayList<>();
    private final ArrayList<WarpPoint> warps = new ArrayList<>();
    private final ArrayList<Light> lights = new ArrayList<>();
    private final ArrayList<Interactive> interactiveObjects = new ArrayList<>();

    private final BlueArray<Block> nearBlocks = new BlueArray<>();
    private final BlueArray<Mob> nearSolidMobs = new BlueArray<>();
    private final BlueArray<Mob> nearFlatMobs = new BlueArray<>();
    private final BlueArray<GameObject> nearSolidObjects = new BlueArray<>();
    private final BlueArray<GameObject> nearFlatObjects = new BlueArray<>();
    private final BlueArray<WarpPoint> nearWarps = new BlueArray<>();
    private final BlueArray<GameObject> nearDepthObjects = new BlueArray<>();

    private NavigationMesh navigationMesh;

    public Area(Place place, Map map, int width, int height, int tilesCount) {
        this.place = place;
        this.map = map;
        tiles = new Tile[tilesCount];
        this.xArea = 1;
        this.yArea = 1;
        this.xInPixels = width;
        this.yInPixels = height;
    }

    public Area(Place place, Map map, int xArea, int yArea) {
        this.place = place;
        this.map = map;
        tiles = new Tile[X_IN_TILES * Y_IN_TILES];
        this.xArea = xArea;
        this.yArea = yArea;
        this.xInPixels = xArea * xAreaInPixels;
        this.yInPixels = yArea * yAreaInPixels;
    }

    public void generateNavigationMesh(Set<Block> blocksForMesh) {
        navigationMesh = NavigationMeshGenerator.generateNavigationMesh(tiles, blocksForMesh, xArea, yArea);
    }

    public void updateContainers(int area) {
        map.updateNearBlocks(area, nearBlocks);
        map.updateNearSolidMobs(area, nearSolidMobs);
        map.updateNearFlatMobs(area, nearFlatMobs);
        map.updateNearSolidObjects(area, nearSolidObjects);
        map.updateNearFlatObjects(area, nearFlatObjects);
        map.updateNearWarps(area, nearWarps);
        map.updateNearDepthObjects(area, nearDepthObjects);
    }

    public PointContainer findPath(int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        return PathFinder.findPath(navigationMesh, xStart, yStart, xDestination, yDestination, collision);
    }

    public void addForegroundTileAndReplace(GameObject tile) {
        addForegroundTileAndReplace(tile, tile.getX(), tile.getY(), tile.getPureDepth());
    }

    public void addForegroundTileAndReplace(GameObject tile, int x, int y, int depth) {
        if (tile.isSimpleLighting()) {
            tiles[x / Place.tileSize + y / Place.tileSize * Y_IN_TILES] = null;
        }
        GameObject object;
        for (Iterator<GameObject> iterator = foregroundTiles.iterator(); iterator.hasNext(); ) {
            object = iterator.next();
            if (object.isVisible() && object.getX() == x && object.getY() == y) {
                iterator.remove();
            }
        }
        addForegroundTile(tile, x, y, depth);
    }

    public void addForegroundTile(GameObject tile, int x, int y, int depth) {
        tile.setPosition(x, y);
        tile.setDepth(depth);
        addForegroundTile(tile);
    }

    private void addForegroundTile(GameObject tile) {
        tile.setMapNotChange(map);
        Methods.merge(foregroundTiles, tile);

    }

    public void deleteForegroundTile(GameObject tile) {
        tile.setMapNotChange(null);
        foregroundTiles.remove(tile);
    }

    public void deleteForegroundTile(int x, int y) {
        foregroundTiles.stream().filter((foregroundTile) -> (foregroundTile.getX() == x && foregroundTile.getY() == y)).forEach(foregroundTiles::remove);
    }

    public void addBlock(Block block) {
        block.setMapNotChange(map);
        blocks.add(block);
    }

    public void deleteBlock(Block block) {
        block.setMapNotChange(null);
        blocks.remove(block);
    }

    public void addObject(GameObject object) {
        object.setMapNotChange(map);
        if (object.isOnTop()) {
            Methods.merge(topObjects, object);
        } else {
            Methods.merge(depthObjects, object);
        }
        if (object.isInteractive()) {
            object.getInteractiveObjects().stream().forEach(interactiveObjects::add);
        }
        if (!(object instanceof Player)) {
            addNotPlayerObject(object);
        }
    }

    private void addNotPlayerObject(GameObject object) {
        if (object.isEmitter()) {
            object.getLights().stream().forEach(lights::add);
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

    public void deleteObject(GameObject object) {
        if (object.isOnTop()) {
            topObjects.remove(object);
        } else {
            depthObjects.remove(object);
        }
        if (object.isEmitter()) {
            object.getLights().stream().forEach(lights::remove);
        }
        if (object.isInteractive()) {
            object.getInteractiveObjects().stream().forEach(interactiveObjects::remove);
        }
        if (!(object instanceof Player)) {
            deleteNotPlayerObject(object);
        }
    }

    private void deleteNotPlayerObject(GameObject object) {
        if (object.isEmitter()) {
            object.getLights().stream().forEach(lights::remove);
        }
        if (object instanceof WarpPoint) {
            warps.remove(object);
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

    public void removeForegroundTile(GameObject foregroundTile) {
        foregroundTiles.remove(foregroundTile);
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
        lights.clear();
        blocks.clear();
        depthObjects.clear();
        foregroundTiles.clear();
        topObjects.clear();
        nearBlocks.clearReally();
        nearSolidMobs.clearReally();
        nearFlatMobs.clearReally();
        nearSolidObjects.clearReally();
        nearFlatObjects.clearReally();
        nearWarps.clearReally();
        nearDepthObjects.clearReally();
    }

    public Tile getTile(int x, int y) {
        return tiles[x + y * X_IN_TILES];
    }

    public List<Mob> getSolidMobs() {
        return solidMobs;
    }

    public List<Mob> getFlatMobs() {
        return flatMobs;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public List<GameObject> getSolidObjects() {
        return solidObjects;
    }

    public List<GameObject> getFlatObjects() {
        return flatObjects;
    }

    public List<Light> getLights() {
        return lights;
    }

    public List<GameObject> getDepthObjects() {
        return depthObjects;
    }

    public List<GameObject> getTopObjects() {
        return topObjects;
    }

    public List<WarpPoint> getWarps() {
        return warps;
    }

    public List<GameObject> getForegroundTiles() {
        return foregroundTiles;
    }

    public GameObject getForegroundTile(int i) {
        return foregroundTiles.get(i);
    }

    public List<Interactive> getInteractiveObjects() {
        return interactiveObjects;
    }

    public int getXArea() {
        return xArea;
    }

    public int getYArea() {
        return yArea;
    }

    public int getXInPixels() {
        return xInPixels;
    }

    public int getYInPixels() {
        return yInPixels;
    }

    public NavigationMesh getNavigationMesh() {
        return navigationMesh;
    }

    public void setTile(int x, int y, Tile tile) {
        tiles[x + y * X_IN_TILES] = tile;
    }

    public void setForegroundTiles(int i, ForegroundTile foregroundTile) {
        foregroundTiles.set(i, foregroundTile);
    }

    public List<Block> getNearBlocks() {
        return nearBlocks;
    }

    public List<Mob> getNearSolidMobs() {
        return nearSolidMobs;
    }

    public List<Mob> getNearFlatMobs() {
        return nearFlatMobs;
    }

    public List<GameObject> getNearSolidObjects() {
        return nearSolidObjects;
    }

    public List<GameObject> getNearFlatObjects() {
        return nearFlatObjects;
    }

    public List<GameObject> getNearDepthObjects() {
        return nearDepthObjects;
    }

    public BlueArray<WarpPoint> getNearWarps() {
        return nearWarps;
    }
}
