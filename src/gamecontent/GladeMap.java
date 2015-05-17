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
import net.jodk.lang.FastMath;

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
        test.placePuzzle(11, 26, this);
        test = new PuzzleObject("smukly", place);
        test.placePuzzle(-1, 0, this);

        WarpPoint warp = new WarpPoint("toKamienna", 20 * tileSize, 20 * tileSize, "Kamienna");
        warp.setCollision(Rectangle.create(0, 0, tileSize, tileSize, IN_SHADE_NO_SHADOW, warp));
        addObject(warp);
        addObject(new WarpPoint("toPolana", 20 * tileSize, 19 * tileSize));
        PuzzleObject portal = new PuzzleObject("portal", place);
        portal.placePuzzle(20, 20, this);

        generateNavigationMeshes();
//		System.out.println("Total time: " + NavigationMeshGenerator.fullTime / 100000000d + " s");
    }

    @Override
    public void populate() {
        addObject(new Rabbit(256, 2048, 128, 28, 6, 1024, "very perverted rabbit", place, true, mobID++));
        addObject(new Tree(384, 960, 54, 27, 1.5, 1024, " ", place, true, mobID++));
        for (int i = 0; i < 1000; i += 2) {
            //  addObject(new Rabbit(192 + 192 * (i % 50), 3072 + 192 * (i / 50), 128, 28, 3, 512, "rabbit", place, true, mobID++));
            addObject(new Tree(192 + 160 * ((i + 1) % 50) + ((int) (FastMath.random() * 120)), 2112 + 160 * ((i + 1) / 50) + ((int) (FastMath.random() * 150)), 54, 27, 1.5, 1024, " ", place, true, mobID++));
        }
        GameObject light = new LightSource(1784, 1296, 206, 64, "lamp", place, "lamp", false);
        light.setDepth(1024);
        addObject(light);
    }
}
