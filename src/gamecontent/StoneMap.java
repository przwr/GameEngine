/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.Area;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Point;
import game.place.ForeGroundTile;
import game.place.Map;
import game.place.Place;
import game.place.PuzzleObject;
import game.place.Tile;
import game.place.WarpPoint;

/**
 *
 * @author Wojtek
 */
public class StoneMap extends Map {

    public StoneMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "Kamienna", place, width, height, tileSize);
        Tile GROUND = new Tile(place.getSpriteSheet("tlo"), tileSize, 2, 12);
        Tile GRASS = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 1);
        Tile PORTAL = new Tile(place.getSpriteSheet("tlo"), tileSize, 0, 12);
        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                if ((x * y) < 600) {
                    tiles[x + y * height / tileSize] = GROUND;
                } else {
                    tiles[x + y * height / tileSize] = GRASS;
                }
            }
        }

        PuzzleObject po = new PuzzleObject("piramida", place);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                po.placePuzzle(x * (po.getWidth()), y * (po.getHeight()), this);
            }
        }

        WarpPoint w = new WarpPoint("toPolana", 20 * tileSize, 20 * tileSize, "Polana");
        w.setCollision(Rectangle.create(0, 0, tileSize, tileSize, OpticProperties.IN_SHADE_NO_SHADOW, w));
        addObject(w);
        addObject(new WarpPoint("toKamienna", 20 * tileSize, 17 * tileSize));
        tiles[20 + 20 * height / tileSize] = PORTAL;
    }
}
