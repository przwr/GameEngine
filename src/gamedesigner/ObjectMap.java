/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Area;
import engine.Point;
import game.gameobject.GameObject;
import game.place.Map;
import game.place.Place;
import game.place.Tile;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ObjectMap extends Map {

    public Tile background;
    private boolean isBackground;

    public ObjectMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "ObjectMap", place, width, height, tileSize);

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

    public void addTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex) {
        Tile tile = getTile(x, y);
        if (tile != null && tile.getPureDepth() != -1) {
            tile.addTileToStack(xSheet, ySheet);
        } else {
            Tile newtile = new Tile(tex, tileSize, xSheet, ySheet);
            setTile(x, y, newtile);
        }
    }

    public Point removeTile(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile != null && tile.getPureDepth() != -1) {
            Point p = tile.popTileFromStack();
            if (p != null) {
                if (tile.tileStackSize() == 0) {
                    setTile(x, y, getBackground());
                }
                return p;
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
    
    @Override
    public void addObject(GameObject object) {
        if (object instanceof TemporaryBlock)
            ((TemporaryBlock) object).initialize();
        super.addObject(object);
    }
    
    public String saveMap() {
        String map = "";
        for (int i = 0; i < tiles.length; i++) {
            Tile t = tiles[i];
            if (t != null && t.getPureDepth() != -1) {
                map += "t:" + i + ":" + t.toString() + "\n";
            }
        }
        for (GameObject fgt : foregroundTiles) {
            map += fgt.toString() + "\n";
        }
        for (Area a : areas) {
            map += a.toString() + "\n";
        }
        return map;
    }
}
