/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Block;
import engine.Methods;
import game.gameobject.GameObject;
import game.gameobject.Mob;
import game.gameobject.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import navmeshpathfinding.NavigationMesh;

/**
 *
 * @author przemek
 */
public class Area {

    public static final short X_IN_TILES = 32, Y_IN_TILES = 20;

    private final Place place;
    private final Map map;
    private final Tile[] tiles;
    private final ArrayList<Block> blocks = new ArrayList<>();

    private final ArrayList<Mob> solidMobs = new ArrayList<>();
    private final ArrayList<Mob> flatMobs = new ArrayList<>();
    protected final ArrayList<GameObject> solidObjects = new ArrayList<>();
    protected final ArrayList<GameObject> flatObjects = new ArrayList<>();
    protected final ArrayList<Light> emitters = new ArrayList<>();
    protected final ArrayList<WarpPoint> warps = new ArrayList<>();

    protected final ArrayList<GameObject> foregroundTiles = new ArrayList<>();
    protected final ArrayList<GameObject> topObjects = new ArrayList<>();
    protected final ArrayList<GameObject> depthObjects = new ArrayList<>();

    protected NavigationMesh navigationMesh;

    private static int yStartBound = -1, yEndBound = -1, xStartBound = -1, xEndBound = -1, startIndex, endIndex;

    public Area(Place place, Map map) {
        this.place = place;
        this.map = map;
        tiles = new Tile[X_IN_TILES * Y_IN_TILES];
    }

    public void generateNavigationMesh() {
        if (findBounds()) {
//            System.out.println("(" + xStartBound + ", " + yStartBound + ") (" + xEndBound + ", " + yEndBound + ") " + startIndex + " " + endIndex);
            for (int x = xStartBound; x <= xEndBound; x++) {
                for (int y = yStartBound; y <= yEndBound; y++) {

                }
            }
//            yStartBound *= Place.tileSize;
//            xStartBound *= Place.tileSize;
//            yEndBound = (yEndBound + 1) * Place.tileSize;
//            xEndBound = (xEndBound + 1) * Place.tileSize;
        }
    }

    private boolean findBounds() {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != null) {
                yStartBound = (i / X_IN_TILES);
                break;
            }
        }
        if (yStartBound == -1) {
            return false;
        }
        for (int i = tiles.length - 1; i >= 0; i--) {
            if (tiles[i] != null) {
                yEndBound = (i / X_IN_TILES);
                break;
            }
        }
        XStart:
        for (int x = 0; x < X_IN_TILES; x++) {
            for (int y = 0; y < Y_IN_TILES; y++) {
                if (getTile(x, y) != null) {
                    xStartBound = x;
                    break XStart;
                }
            }
        }
        XEnd:
        for (int x = X_IN_TILES - 1; x >= 0; x--) {
            for (int y = 0; y < Y_IN_TILES; y++) {
                if (getTile(x, y) != null) {
                    xEndBound = x;
                    break XEnd;
                }
            }
        }
        startIndex = getTileIndex(xStartBound, yStartBound);
        endIndex = getTileIndex(xEndBound, yEndBound);
        return true;
    }

    public void addForegroundTileAndReplace(GameObject tile) {
        addForegroundTileAndReplace(tile, tile.getX(), tile.getY(), tile.getPureDepth());
    }

    public void addForegroundTileAndReplace(GameObject tile, int x, int y, int depth) {
        if (tile.isSimpleLighting()) {
            tiles[x / Place.tileSize + y / Place.tileSize * Y_IN_TILES] = null;
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
        tile.setPosition(x, y);
        tile.setDepth(depth);
        addForegroundTile(tile);
    }

    public void addForegroundTile(GameObject tile) {
        tile.setMapNotChange(map);
        Methods.merge(foregroundTiles, tile);

    }

    public void deleteForegroundTile(GameObject tile) {
        tile.setMapNotChange(null);
        foregroundTiles.remove(tile);
    }

    public void deleteForegroundTile(int x, int y) {
        foregroundTiles.stream().filter((foregroundTile)
                -> (foregroundTile.getX() == x && foregroundTile.getY() == y)).forEach((foregroundTile) -> {
                    foregroundTiles.remove(foregroundTile);
                });
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
        } else if (object.isEmitter()) {
            object.getLights().stream().forEach((light) -> {
                emitters.remove(light);
            });
        }
        if (object.isOnTop()) {
            topObjects.remove(object);
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
        emitters.clear();
        blocks.clear();
        depthObjects.clear();
        foregroundTiles.clear();
        topObjects.clear();
    }

    public Tile getTile(int x, int y) {
        return tiles[x + y * X_IN_TILES];
    }

    public Tile getTile(int index) {
        return tiles[index];
    }

    public int getTileIndex(int x, int y) {
        return x + y * X_IN_TILES;
    }

    public List<Mob> getSolidMobs() {
        return Collections.unmodifiableList(solidMobs);
    }

    public List<Mob> getFlatMobs() {
        return Collections.unmodifiableList(flatMobs);
    }

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public List<GameObject> getSolidObjects() {
        return Collections.unmodifiableList(solidObjects);
    }

    public List<GameObject> getFlatObjects() {
        return Collections.unmodifiableList(flatObjects);
    }

    public List<Light> getEmitters() {
        return Collections.unmodifiableList(emitters);
    }

    public List<GameObject> getDepthObjects() {
        return Collections.unmodifiableList(depthObjects);
    }

    public List<GameObject> getTopObjects() {
        return Collections.unmodifiableList(topObjects);
    }

    public List<WarpPoint> getWarps() {
        return Collections.unmodifiableList(warps);
    }

    public List<GameObject> getForegroundTiles() {
        return Collections.unmodifiableList(foregroundTiles);
    }

    public GameObject getForegroundTile(int i) {
        return foregroundTiles.get(i);
    }

    public void setTile(int x, int y, Tile tile) {
        tiles[x + y * X_IN_TILES] = tile;
    }

    public void setForegroundTiles(int i, ForegroundTile foregroundTile) {
        foregroundTiles.set(i, foregroundTile);
    }
}
