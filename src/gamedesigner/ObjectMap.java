/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

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

    public ObjectMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "ObjectMap", place, width, height, tileSize);

        background = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 8, place);
        background.setDepth(-1);

        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                tiles[x + y * height / tileSize] = background;
            }
        }
    }

    public void addTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex) {
        Tile tile = getTile(x, y);
        if (tile != null) {
            if (tile.getPureDepth() != -1) {
                tile.addTileToStack(xSheet, ySheet);
            } else {
                Tile newtile = new Tile(tex, place.tileSize, xSheet, ySheet, place);
                setTile(x, y, newtile);
            }
        } else {
            tile = new Tile(tex, place.tileSize, xSheet, ySheet, place);
            setTile(x, y, tile);
        }
    }
}
