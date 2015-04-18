/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import static collision.OpticProperties.IN_SHADE_NO_SHADOW;
import collision.Rectangle;
import game.gameobject.GameObject;
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
                setTile(x, y, GRASS);
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
        for (int i = 0; i < 100; i += 2) {
            addObject(new Rabbit(192 + 192 * (i % 50), 3072 + 192 * (i / 50), 0, 8, 128, 112, 2, 512, "rabbit", place, true, mobID++));
            //addObject(new Tree(192 + 192 * ((i + 1) % 50), 2048 + 192 * ((i + 1) / 50), 0, 200, 256, 162, 1.5, 1024, " ", place, true, mobID++));
        }
        GameObject light = new LightSource(1784, 1296, 0, 0, 206, 300, "lamp", place, "lamp", false);
        light.setDepth(1024);
        addObject(light);
    }
}
