/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import collision.Block;
import collision.Figure;
import engine.lights.Light;
import engine.utilities.PointContainer;
import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.interactive.Interactive;
import game.logic.navmeshpathfinding.PathFinder;
import game.logic.navmeshpathfinding.navigationmesh.NavigationMesh;
import game.logic.navmeshpathfinding.navigationmesh.NavigationMeshGenerator;
import game.place.Place;
import gamecontent.environment.Corpse;

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

    public static short X_IN_TILES = 32, Y_IN_TILES = 24; // Należy również zmienić w MyPlace

    private final Place place;
    private final Map map;
    private final int xArea, yArea, xInPixels, yInPixels;
    private final Tile[] tiles;

    private final ArrayList<Block> blocks = new ArrayList<>();
    private final ArrayList<Entity> entities = new ArrayList<>();
    private final ArrayList<Mob> solidMobs = new ArrayList<>();
    private final ArrayList<Mob> flatMobs = new ArrayList<>();
    private final ArrayList<GameObject> solidObjects = new ArrayList<>();
    private final ArrayList<GameObject> flatObjects = new ArrayList<>();
    private final ArrayList<GameObject> foregroundTiles = new ArrayList<>();
    private final ArrayList<GameObject> topObjects = new ArrayList<>();
    private final ArrayList<GameObject> depthObjects = new ArrayList<>();
    private final ArrayList<Corpse> corpses = new ArrayList<>();
    private final ArrayList<WarpPoint> warps = new ArrayList<>();
    private final ArrayList<GameObject> interactiveObjects = new ArrayList<>();
    private final ArrayList<Light> lights = new ArrayList<>();
    private final ArrayList<Interactive> interactives = new ArrayList<>();
    private final ArrayList<Block> nearBlocks = new ArrayList<>();
    private final ArrayList<Mob> nearSolidMobs = new ArrayList<>();
    private final ArrayList<Mob> nearFlatMobs = new ArrayList<>();
    private final ArrayList<GameObject> nearSolidObjects = new ArrayList<>();
    private final ArrayList<GameObject> nearFlatObjects = new ArrayList<>();
    private final ArrayList<WarpPoint> nearWarps = new ArrayList<>();
    private final ArrayList<GameObject> nearDepthObjects = new ArrayList<>();
    private final ArrayList<GameObject> nearForegroundTiles = new ArrayList<>();

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
        map.updateNearForegroundTiles(area, nearForegroundTiles);
    }

    public PointContainer findPath(int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        return PathFinder.findPath(navigationMesh, xStart, yStart, xDestination, yDestination, collision);
    }

    public boolean pathExists(int xStart, int yStart, int xDestination, int yDestination, Figure collision) {
        return PathFinder.pathExists(navigationMesh, xStart, yStart, xDestination, yDestination, collision);
    }

    public void addForegroundTileAndReplace(GameObject tile) {
        addForegroundTileAndReplace(tile, tile.getX(), tile.getY(), tile.getPureDepth());
    }

    public void addForegroundTileAndReplace(GameObject tile, int x, int y, int depth) {
        /*if (tile.isSimpleLighting()) {
         tiles[x / Place.tileSize + y / Place.tileSize * Y_IN_TILES] = null;
         }*/
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
        foregroundTiles.add(tile);
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
        Figure col = block.getCollision();
        if (col.isBottomRounded() && !col.isTriangular()) {
            map.updateNearBlocks(xArea + yArea * map.getXAreas(), nearBlocks);
            if (col.isLeftBottomRound()) {
                for (Block bl : nearBlocks) {
                    if (bl.getCollision().isBottomRounded() && !bl.getCollision().isTriangular() && bl.getX() == col.getXEnd()
                            && bl.getCollision().getYEnd() == col.getYEnd()) {
                        bl.getCollision().setColumn(true);
                        col.setColumn(true);
                    }
                }
            } else {
                for (Block bl : nearBlocks) {
                    if (bl.getCollision().isBottomRounded() && !bl.getCollision().isTriangular() && bl.getCollision().getXEnd() == col.getX()
                            && bl.getCollision().getYEnd() == col.getYEnd()) {
                        bl.getCollision().setColumn(true);
                        col.setColumn(true);
                    }
                }
            }
        }
        blocks.add(block);
    }

    public Block getBlock(int x, int y) {
        for (Block block : blocks) {
            if (block.getX() == x && block.getY() == y) {
                return block;
            }
        }
        return null;
    }

    public void deleteBlock(Block block) {
        List<ForegroundTile> all = block.getAllForegroundTiles();
        for (ForegroundTile tile : all) {
            tile.setMapNotChange(null);
        }
        foregroundTiles.removeAll(all);
        block.setMapNotChange(null);
        blocks.remove(block);
    }

    public void addObject(GameObject object) {
        object.setMapNotChange(map);
        if (object.isOnTop()) {
            topObjects.add(object);
        } else {
            depthObjects.add(object);
        }
        if (object.isInteractive()) {
            ((Entity) object).getInteractiveObjects().stream().forEach(interactives::add);
        }
        if (!(object instanceof Player)) {
            addNotPlayerObject(object);
        }
    }

    private void addNotPlayerObject(GameObject object) {
        if (object.isEmitter()) {
            object.getLights().stream().forEach(lights::add);
        }
        if (object.canInteract()) {
            interactiveObjects.add(object);
        }
        if (object instanceof WarpPoint) {
            addWarpPoint((WarpPoint) object);
        } else if (object instanceof Mob) {
            addMob((Mob) object);
        } else {
            if (object instanceof Entity) {
                entities.add((Entity) object);
            }
            if (object instanceof Corpse) {
                corpses.add((Corpse) object);
            } else if (object.isSolid()) {
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

    public boolean deleteObject(GameObject object) {
        boolean removed;
        if (object instanceof Corpse) {
            removed = corpses.remove(object);
        } else if (object.isOnTop()) {
            removed = topObjects.remove(object);
        } else {
            removed = depthObjects.remove(object);
        }
        if (removed) {
            if (object.isEmitter()) {
                object.getLights().stream().forEach(lights::remove);
            }
            if (object.isInteractive()) {
                ((Entity) object).getInteractiveObjects().stream().forEach(interactives::remove);
            }
            if (!(object instanceof Player)) {
                deleteNotPlayerObject(object);
            }
        }
        return removed;
    }

    private void deleteNotPlayerObject(GameObject object) {
        if (object.isEmitter()) {
            object.getLights().stream().forEach(lights::remove);
        }
        if (object.canInteract()) {
            interactiveObjects.remove(object);
        }
        if (object instanceof WarpPoint) {
            warps.remove(object);
        } else if (object instanceof Mob) {
            deleteMob((Mob) object);
        } else {
            if (object instanceof Entity) {
                entities.remove(object);
            }
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
        entities.clear();
        flatMobs.clear();
        solidObjects.clear();
        flatObjects.clear();
        lights.clear();
        blocks.clear();
        depthObjects.clear();
        corpses.clear();
        foregroundTiles.clear();
        topObjects.clear();
        nearBlocks.clear();
        nearSolidMobs.clear();
        nearFlatMobs.clear();
        nearSolidObjects.clear();
        nearFlatObjects.clear();
        nearWarps.clear();
        nearDepthObjects.clear();
        nearForegroundTiles.clear();
    }

    public Tile getTile(int x, int y) {
        return tiles[x + y * X_IN_TILES];
    }

    public List<Mob> getSolidMobs() {
        return solidMobs;
    }

    public List<Entity> getEntities() {
        return entities;
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

    public List<Interactive> getInteractives() {
        return interactives;
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

    public List<GameObject> getNearForegroundTiles() {
        return nearForegroundTiles;
    }

    public ArrayList<WarpPoint> getNearWarps() {
        return nearWarps;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public ArrayList<GameObject> getInteractiveObjects() {
        return interactiveObjects;
    }

    public ArrayList<Corpse> getCorpses() {
        return corpses;
    }
}
