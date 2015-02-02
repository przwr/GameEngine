/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.Point;
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

        background = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 8, place);
        background.setDepth(-1);
        isBackground = true;

        switchTiles(background);
    }

    public void setBackground(int xSheet, int ySheet, SpriteSheet tex) {
        background = new Tile(tex, tileSize, xSheet, ySheet, place);
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

    private void switchTiles(Tile bg) {
        for (int y = 0; y < tileheight; y++) {
            for (int x = 0; x < tilewidth; x++) {
                Tile t = tiles[x + y * tileheight];
                if (t == null || t.getPureDepth() == -1)
                    tiles[x + y * tileheight] = bg;
            }
        }
    }
    
    public void addTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex) {
        Tile tile = getTile(x, y);
        if (tile != null && tile.getPureDepth() != -1) {
            tile.addTileToStack(xSheet, ySheet);
        } else {
            Tile newtile = new Tile(tex, tileSize, xSheet, ySheet, place);
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
}
