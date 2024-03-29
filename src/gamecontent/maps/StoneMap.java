/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.maps;

import collision.Rectangle;
import game.logic.DayCycle;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.Tile;
import game.place.map.WarpPoint;
import org.newdawn.slick.Color;

import static collision.OpticProperties.TRANSPARENT;

/**
 * @author Wojtek
 */
public class StoneMap extends Map {

    public StoneMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "Kamienna", place, width, height, tileSize);
        setColor(new Color(DayCycle.NIGHT, DayCycle.NIGHT, DayCycle.NIGHT));
        Tile GROUND = new Tile(place.getSpriteSheet("tlo", "backgrounds"), 2, 12);
        Tile GRASS = new Tile(place.getSpriteSheet("tlo", "backgrounds"), 1, 1);

        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                if ((x * y) < 600) {
                    setTile(x, y, GROUND);
                } else {
                    setTile(x, y, GRASS);
                }
            }
        }

        PuzzleObject po = new PuzzleObject("testMap/piramida", place);
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                po.placePuzzle(x * (po.getWidth()), y * (po.getHeight()), this);
            }
        }

        WarpPoint warp = new WarpPoint("toPolana", 20 * tileSize, 20 * tileSize, "Polana");
        warp.setCollision(Rectangle.create(0, 0, tileSize, tileSize, TRANSPARENT, warp));
        addObject(warp);
        addObject(new WarpPoint("toKamienna", 20 * tileSize, 19 * tileSize));
        PuzzleObject portal = new PuzzleObject("testMap/portal", place);
        portal.placePuzzle(20, 20, this);

        generateNavigationMeshes();

//        System.out.println("FullTime: " + (NavigationMeshGenerator.fullTime / 1000000d) + " ms");
    }

    @Override
    public void populate() {
    }
}
