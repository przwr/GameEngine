/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.Block;
import collision.OpticProperties;
import static collision.OpticProperties.IN_SHADE_NO_SHADOW;
import collision.Rectangle;
import game.gameobject.LightSource;
import game.place.Map;
import game.place.Place;
import game.place.PuzzleObject;
import game.place.Tile;
import game.place.WarpPoint;

/**
 *
 * @author Wojtek
 */
public class GladeMap extends Map {

    public GladeMap(short ID, Place place, int width, int height, int tileSize) {
        super(ID, "Polana", place, width, height, tileSize);
        Tile GRASS = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 8);
        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                tiles[x + y * height / tileSize] = GRASS;
            }
        }

        PuzzleObject test = new PuzzleObject("zatoczka", place);
        test.placePuzzle(5, 3, this);
        test = new PuzzleObject("test", place);
        test.placePuzzle(1, 18, this);
        test = new PuzzleObject("smukly", place);
        test.placePuzzle(-1, 0, this);

        WarpPoint warp = new WarpPoint("toKamienna", 20 * tileSize, 20 * tileSize, "Kamienna");
        warp.setCollision(Rectangle.create(0, 0, tileSize, tileSize, IN_SHADE_NO_SHADOW, warp));
        addObject(warp);
        addObject(new WarpPoint("toPolana", 20 * tileSize, 19 * tileSize));
        PuzzleObject portal = new PuzzleObject("portal", place);
        portal.placePuzzle(20, 20, this);
        for (int i = 0; i < 1; i++) {
            // addObject(new MyMob(192 + 192 * (i % 50), 2048 + 192 * (i / 50), 0, 8, 128, 112, 4, 512, "rabbit", place, true, mobID++));
        }
        addObject(new LightSource(2048, 2048, 0, 0, 206, 300, "lamp", place, "lamp", true));
    }
}
