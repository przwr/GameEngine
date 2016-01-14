/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.maps;

import collision.Rectangle;
import game.place.Place;
import game.place.map.*;
import gamecontent.Bush;
import gamecontent.GrassClump;
import gamecontent.SpawnPoint;
import gamecontent.Tree;
import gamecontent.mobs.Blazag;
import gamecontent.mobs.Plurret;
import gamecontent.mobs.Tongub;
import gamecontent.npcs.Melodia;

import static collision.OpticProperties.IN_SHADE_NO_SHADOW;

/**
 * @author Wojtek
 */
public class GladeMap extends Map {

    public GladeMap(short ID, Place place, int width, int height, int tileSize) {
        super(ID, "Polana", place, width, height, tileSize);
        Tile nullTile = new NullTile();
        Tile GRASS = new Tile(place.getSpriteSheet("tlo", "backgrounds"), 1, 8);
//        Tile GRASS = new Tile(place.getSpriteSheet("tlo", "backgrounds"), 2, 12);

        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                setTile(x, y, GRASS);
            }
        }

//        PuzzleObject test = new PuzzleObject("bloczek", place);
//        test.placePuzzle(0, 0, this);
        placePuzzle(5, 3, new PuzzleObject("zatoczka", place));
        placePuzzle(11, 26, new PuzzleObject("test", place));
        placePuzzle(-1, 0, new PuzzleObject("smukly", place));
        placePuzzle(40, 30, new PuzzleObject("tmpDomek", place));
//        test = new PuzzleObject("veryHighRound", place);
//        test.placePuzzle(5, 5, this);
//        test = new PuzzleObject("domek", place);
//        test.placePuzzle(7, 18, this);

        WarpPoint warp = new WarpPoint("toKamienna", 20 * tileSize, 20 * tileSize, "Kamienna");
        warp.setCollision(Rectangle.create(0, 0, tileSize, tileSize, IN_SHADE_NO_SHADOW, warp));
        addObject(warp);
        addObject(new WarpPoint("toPolana", 20 * tileSize, 19 * tileSize));
        placePuzzle(20, 20, new PuzzleObject("portal", place));

        long start = System.nanoTime();
        generateNavigationMeshes();
        long end = System.nanoTime();
        System.out.println("Navigation mesh for " + GladeMap.class.getSimpleName() + " generated in " + (((end - start)) / 1000000) + " ms");
    }

    @Override
    public void populate() {
//      addObject(new Rabbit(256, 2500, 128, 28, 6, 1024, "Rabbit", place, true, mobID++));
//      addObject(new BrainlessShen(356, 768, place, mobID++));
//      addObject(new BrainlessShen(356, 860, place, mobID++));
//      addObject(new Blazag(768, 2048, place, mobID++));
//      addObject(new Blazag(1200, 1024, place, mobID++));
        addObject(new Tongub(1756, 2768, place, mobID++));
        addObject(new Tongub(1756, 2820, place, mobID++));
        addObject(new Tongub(1756, 2900, place, mobID++));
        addObject(new Tongub(1756, 2964, place, mobID++));
        addObject(new Tongub(1756, 3024, place, mobID++));
        addObject(new Tongub(1756, 3100, place, mobID++));
//        addObject(new Tongub(756, 768, place, mobID++));
//        addObject(new Tongub(756, 820, place, mobID++));
//        addObject(new Tongub(756, 900, place, mobID++));
//        addObject(new Tongub(756, 964, place, mobID++));
//        addObject(new Tongub(756, 1024, place, mobID++));
//        addObject(new Tongub(756, 1100, place, mobID++));


        int xBladesCount = 8;
        int yBladesCount = 2;
        int bladeWidth = 8;
        int bladeHeight = 32;

        int grassWidth = (bladeWidth - 2) * xBladesCount;


        int xCount = 2;
        int yCount = xCount * 4;
        int xSpace = xCount * grassWidth;
        int ySpace = yCount * 8;
        int xStart = 0;
        int yStart = 2304;
        for (int x = xStart; x < xStart + 2048; x += xSpace) {
            for (int y = yStart; y < yStart + 2048; y += ySpace) {
                addObject(GrassClump.createRectangle(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight));
            }
        }


        addObject(new Bush(800, 800, 10, 50, 1f));
        addObject(new Bush(1024, 920, 12, 70, 0.8f));
        addObject(Tree.create(250, 1524, 32, 200, 0.8f));
//        addObject(GrassClump.createRound(256, 512, 2, 8, 7, 2, 8, 32));
//        addObject(GrassClump.createRectangle(256, 768, 2, 8, 7, 2, 8, 32));
        addObject(GrassClump.createCorner(256, 864, 2, 8, 8, 2, 8, 32, 0));
        addObject(GrassClump.createCorner(256, 736, 2, 8, 8, 2, 8, 32, 1));
        addObject(GrassClump.createCorner(448, 736, 2, 8, 8, 2, 8, 32, 2));
        addObject(GrassClump.createCorner(448, 864, 2, 8, 8, 2, 8, 32, 3));
        addObject(GrassClump.createRectangle(256, 800, 2, 8, 8, 2, 8, 32));
        addObject(GrassClump.createRectangle(352, 864, 2, 8, 8, 2, 8, 32));
        addObject(GrassClump.createRectangle(352, 736, 2, 8, 8, 2, 8, 32));
        addObject(GrassClump.createRectangle(352, 800, 2, 8, 8, 2, 8, 32));
        addObject(GrassClump.createRectangle(448, 800, 2, 8, 8, 2, 8, 32));
//
//
//        addObject(new Shen(512, 800, place, mobID++));
//        addObject(new Shen(768, 1280, place, mobID++));
//        addObject(new Shen(512, 1500, place, mobID++));
//        addObject(new Shen(648, 1400, place, mobID++));
        addObject(new Plurret(1156, 968, place, mobID++));
        addObject(new Blazag(1600, 2494, place, mobID++));
        addObject(SpawnPoint.createInVisible(1536, 2560, 54, 38, "Blazag spawn", Blazag.class, 15, 5));
        addObject(SpawnPoint.createInVisible(768, 3000, 54, 38, "Tongub spawn", Tongub.class, 10, 10));

//        addObject(SpawnPoint.createVisible(place,2048, 2048, 64, 64, "Shen spawn", Shen.class, 30, 5, place.getSprite("rabbit", "")));
//        addObject(new Shen(512, 1024, place, mobID++));
//        addObject(new Shen(768, 1280, place, mobID++));
//        addObject(new Shen(512, 1500, place, mobID++));
//        addObject(new Shen(648, 1400, place, mobID++));
//        addObject(new Melodia(384, 590, place, mobID++));
//        for (int i = 0; i < 20; i += 2) {
//            addObject(new Shen(192 + 192 * (i % 50), 2048 + 192 * (i / 50), place, mobID++));
//            addObject(new Blazag(192 + 192 * (i % 50), 2048 + 192 * (i / 50), place, mobID++));
//            addObject(new Tongub(192 + 192 * (i % 50), 2048 + 192 * (i / 50), place, mobID++));
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


        addObject(new Melodia(200, 200, place, mobID++));

    }
}
