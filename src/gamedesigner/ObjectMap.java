/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Block;
import engine.Point;
import game.gameobject.GameObject;
import game.place.ForegroundTile;
import game.place.Map;
import game.place.Place;
import game.place.Tile;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ObjectMap extends Map {

    public Tile background;
    private boolean isBackground;
    private final CentralPoint centralPoint;
    private final ObjectPlace objPlace;

    public ObjectMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "ObjectMap", place, width, height, tileSize);
        objPlace = (ObjectPlace) place;
        
        centralPoint = new CentralPoint(0, 0, objPlace);
        addObject(centralPoint);

        background = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 8);
        background.setDepth(-1);
        isBackground = true;

        switchTiles(background);
    }

    public void setBackground(int xSheet, int ySheet, SpriteSheet texture) {
        background = new Tile(texture, tileSize, xSheet, ySheet);
        background.setDepth(-1);
        switchTiles(background);
    }

    private Tile getBackground() {
        return isBackground ? background : null;
    }

    public void switchBackground() {
        Tile tmp = null;
        if (!isBackground) {
            tmp = background;
        }
        switchTiles(tmp);
        isBackground = !isBackground;
    }

    private void switchTiles(Tile background) {
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                Tile t = tiles[x + y * heightInTiles];
                if (t == null || t.getPureDepth() == -1) {
                    tiles[x + y * heightInTiles] = background;
                }
            }
        }
    }

    @Override
    public void clear() {
        Tile bg = getBackground();
        for (int y = 0; y < heightInTiles; y++) {
            for (int x = 0; x < widthInTiles; x++) {
                tiles[x + y * heightInTiles] = bg;
            }
        }
        foregroundTiles.clear();
        areas.clear();
        GameObject object;
        for (Iterator<GameObject> iterator = objectsOnTop.iterator(); iterator.hasNext();) {
            object = iterator.next();
            if (object instanceof TemporaryBlock) {
                iterator.remove();
            }
        }
    }

    public void addTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex) {
        Tile tile = getTile(x, y);
        if (tile != null && tile.getPureDepth() != -1) {
            tile.addTileToStack(xSheet, ySheet);
        } else {
            TemporaryBlock lowest = null;
            int max = 0;
            for (GameObject tb : objectsOnTop) {
                if (tb instanceof TemporaryBlock) {
                    TemporaryBlock tmp = (TemporaryBlock) tb;
                    if (tmp.checkTile(x, y) && tmp.getY() > max) {
                        lowest = tmp;
                        max = tmp.getY();
                    }
                }
            }
            if (lowest != null) {
                lowest.addTile(x, y, xSheet, ySheet, tex, objPlace.isAltMode());
                setTile(x, y, getBackground());
            } else {
                Tile newtile = new Tile(tex, tileSize, xSheet, ySheet);
                setTile(x, y, newtile);
            }
        }
    }

    public Tile removeTile(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile != null && tile.getPureDepth() != -1) {
            Point p = tile.popTileFromStack();
            if (p != null) {
                if (tile.tileStackSize() == 0) {
                    setTile(x, y, getBackground());
                }
                return tile;
            }
        } else {
            ForegroundTile fgt;
            TemporaryBlock tmp;
            for (GameObject tb : objectsOnTop) {
                if (tb instanceof TemporaryBlock) {
                    tmp = (TemporaryBlock) tb;
                    if ((fgt = tmp.removeTile(x, y)) != null) {
                        foregroundTiles.remove(fgt);
                        tmp.area.removeForegroundTile(fgt);
                        return fgt;
                    }
                }
            }
        }
        setTile(x, y, getBackground());
        return null;
    }

    public boolean checkBlockCollision(int x, int y, int width, int height) {
        for (GameObject go : flatObjects) {
            if (go instanceof TemporaryBlock) {
                TemporaryBlock tb = (TemporaryBlock) go;
                if (tb.checkCollision(x, y, width, height)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void deleteBlocks(int x, int y, int width, int height) {
        Object[] tmpTab = flatObjects.toArray();
        for (Object go : tmpTab) {
            if (go instanceof TemporaryBlock) {
                TemporaryBlock tb = (TemporaryBlock) go;
                if (tb.checkCollision(x, y, width, height)) {
                    tb.clearMyself();
                    deleteObject((GameObject) go);
                }
            }
        }
    }

    public void setCentralPoint(int x, int y) {
        centralPoint.setCentralPoint(x, y);
    }

    @Override
    public void addObject(GameObject object) {
        if (object instanceof TemporaryBlock) {
            if (!objPlace.isAltMode()) {
                ((TemporaryBlock) object).changeEnvironment();
            }
        }
        super.addObject(object);
    }

    public ArrayList<String> saveMap() {
        ArrayList<String> map = new ArrayList<>();
        SpriteSheet repeated = null;
        Point center = centralPoint.getCentralPoint();
        map.add(center.getX() + ":" + center.getY());
        for (int x = 0; x < widthInTiles; x++) {
            for (int y = 0; y < heightInTiles; y++) {
                Tile t = getTile(x, y);
                if (t != null && t.getPureDepth() != -1) {
                    map.add(t.saveToString(repeated, x, y, center.getX(), center.getY()));
                    repeated = t.getSpriteSheet();
                }
            }
        }
        for (GameObject go : foregroundTiles) {
            ForegroundTile fgt = (ForegroundTile) go;
            if (!fgt.isInBlock()) {
                map.add(fgt.saveToString(repeated, center.getX() * tileSize, center.getY() * tileSize, tileSize));
                repeated = fgt.getSpriteSheet();
            }
        }
        for (Block a : areas) {
            map.add(a.saveToString(center.getX() * tileSize, center.getY() * tileSize, tileSize));
            for (ForegroundTile fgt : a.getTopForegroundTiles()) {
                map.add(fgt.saveToString(repeated, center.getX() * tileSize, center.getY() * tileSize, tileSize));
                repeated = fgt.getSpriteSheet();
            }
            for (ForegroundTile fgt : a.getWallForegroundTiles()) {
                map.add(fgt.saveToString(repeated, center.getX() * tileSize, center.getY() * tileSize, tileSize));
                repeated = fgt.getSpriteSheet();
            }
        }
        return map;
    }
}
