/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.maps;

import collision.Rectangle;
import game.place.Place;
import game.place.map.Map;
import game.place.map.PuzzleObject;
import game.place.map.Tile;
import game.place.map.WarpPoint;
import gamecontent.Grass;
import gamecontent.SpawnPoint;
import gamecontent.mobs.Blazag;
import gamecontent.mobs.Plurret;
import gamecontent.mobs.Shen;

import static collision.OpticProperties.IN_SHADE_NO_SHADOW;

/**
 * @author Wojtek
 */
public class GladeMap extends Map {

    public GladeMap(short ID, Place place, int width, int height, int tileSize) {
        super(ID, "Polana", place, width, height, tileSize);
        Tile GRASS = new Tile(place.getSpriteSheet("tlo", "backgrounds"), 1, 8);

        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                setTile(x, y, GRASS);
            }
        }

//        PuzzleObject test = new PuzzleObject("bloczek", place);
//        test.placePuzzle(0, 0, this);
        PuzzleObject test = new PuzzleObject("zatoczka", place);
        test.placePuzzle(5, 3, this);
        test = new PuzzleObject("test", place);
        test.placePuzzle(11, 26, this);
        test = new PuzzleObject("smukly", place);
        test.placePuzzle(-1, 0, this);
//        test = new PuzzleObject("veryHighRound", place);
//        test.placePuzzle(5, 5, this);
        test = new PuzzleObject("domek", place);
        test.placePuzzle(7, 18, this);

        WarpPoint warp = new WarpPoint("toKamienna", 20 * tileSize, 20 * tileSize, "Kamienna");
        warp.setCollision(Rectangle.create(0, 0, tileSize, tileSize, IN_SHADE_NO_SHADOW, warp));
        addObject(warp);
        addObject(new WarpPoint("toPolana", 20 * tileSize, 19 * tileSize));
        PuzzleObject portal = new PuzzleObject("portal", place);
        portal.placePuzzle(20, 20, this);

        long start = System.nanoTime();
        generateNavigationMeshes();
        long end = System.nanoTime();
        System.out.println("Navigation mesh for " + GladeMap.class.getSimpleName() + " generated in " + (((end - start)) / 1000000) + " ms");
    }

    @Override
    public void populate() {
        //addObject(new Rabbit(256, 2500, 128, 28, 6, 1024, "Rabbit", place, true, mobID++));
//        addObject(new BrainlessShen(356, 768, place, mobID++));
//        addObject(new BrainlessShen(356, 860, place, mobID++));
//		addObject(new Blazag(768, 2048, place, mobID++));
//        addObject(new Blazag(1200, 1024, place, mobID++));
//        addObject(new Blazag(2048, 2048, place, mobID++));
//		addObject(new Tongub(1256, 768, place, mobID++))ss;
//		addObject(new Tongub(1256, 820, place, mobID++));
//		addObject(new Tongub(1256, 900, place, mobID++));
        addObject(new Grass(400, 600, "Grass"));
        addObject(new Grass(400, 632, "Grass"));
        addObject(new Grass(400, 664, "Grass"));
        addObject(new Grass(400, 696, "Grass"));
        addObject(new Grass(432, 600, "Grass"));
        addObject(new Grass(432, 632, "Grass"));
        addObject(new Grass(432, 664, "Grass"));
        addObject(new Grass(432, 696, "Grass"));
        addObject(new Grass(464, 600, "Grass"));
        addObject(new Grass(464, 632, "Grass"));
        addObject(new Grass(464, 664, "Grass"));
        addObject(new Grass(464, 696, "Grass"));
        addObject(new Grass(496, 600, "Grass"));
        addObject(new Grass(496, 632, "Grass"));
        addObject(new Grass(496, 664, "Grass"));
        addObject(new Grass(496, 696, "Grass"));
        addObject(new Shen(512, 800, place, mobID++));
        addObject(new Shen(768, 1280, place, mobID++));
        addObject(new Shen(512, 1500, place, mobID++));
        addObject(new Shen(648, 1400, place, mobID++));
        addObject(new Plurret(1156, 968, place, mobID++));
//        addObject(SpawnPoint.createVisible(place,2048, 2048, 64, 64, "Shen spawn", Shen.class, 30, 5, place.getSprite("rabbit", "")));
        addObject(SpawnPoint.createInVisible(2048, 2048, 54, 38, "Blazag spawn", Blazag.class, 15, 5));
//        addObject(new Shen(512, 1024, place, mobID++));
//        addObject(new Shen(768, 1280, place, mobID++));
//        addObject(new Shen(512, 1500, place, mobID++));
//        addObject(new Shen(648, 1400, place, mobID++));
//        addObject(new Tree(384, 960, 54, 27, 6, 1024, " ", place, true, mobID++));
//        addObject(new MyNPC(384, 590, place, mobID++));
//        for (int i = 0; i < 20; i += 2) {
//            addObject(new Shen(192 + 192 * (i % 50), 2048 + 192 * (i / 50), place, mobID++));
//            addObject(new Blazag(192 + 192 * (i % 50), 2048 + 192 * (i / 50), place, mobID++));
//            addObject(new Tongub(192 + 192 * (i % 50), 2048 + 192 * (i / 50), place, mobID++));
//            addObject(new Tree(192 + 160 * ((i + 1) % 50) + ((int) (FastMath.random() * 120)), 2112 + 160 * ((i + 1) / 50) + ((int) (FastMath.random() *
//                    150)), 54, 27, 1.5, 1024, " ", place, true, mobID++));
//        }
//        int space = 64;
//        for (int x = 0; x < 2 * space; x += space) {
//            for (int y = 0; y < 3 * space; y += space) {
//                addObject(new Blazag(2048 + x, 2048 + y, place, mobID++));
//            }
//        }
//        GameObject light = new LightSource(1784, 1296, 206, 64, "lamp", place, "lamp", false);
//        light.setDepth(1024);
//        addObject(light);
    }
}
