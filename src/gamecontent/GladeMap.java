/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.Area;
import collision.OpticProperties;
import collision.Rectangle;
import game.place.ForeGroundTile;
import game.place.Map;
import game.place.Place;
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
        Tile PORTAL = new Tile(place.getSpriteSheet("tlo"), tileSize, 0, 12);
        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                tiles[x + y * height / tileSize] = GRASS;
            }
        }
        Area testa = Area.createWhole(6 * tileSize, 5 * tileSize, tileSize);
        Area testb = Area.createWhole(8 * tileSize, 5 * tileSize, tileSize);
        Area testc = Area.createWhole(7 * tileSize, 7 * tileSize, tileSize);
        Area testd = Area.createWhole(9 * tileSize, 7 * tileSize, tileSize);
        Area teste = Area.createWhole(11 * tileSize, 7 * tileSize, tileSize);
        Area testf = Area.createWhole(5 * tileSize, 9 * tileSize, tileSize);
        Area testg = Area.createWhole(9 * tileSize, 11 * tileSize, tileSize);

        ForeGroundTile fgt;

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 6 * tileSize, 5 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0);
        addForegroundTileAndReplace(fgt, 6 * tileSize, 4 * tileSize, 2 * tileSize);
        testa.setCollision(Rectangle.createShadowHeight(0, 0, tileSize, tileSize, OpticProperties.FULL_SHADOW, 0, testa));

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 8 * tileSize, 5 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0);
        addForegroundTileAndReplace(fgt, 8 * tileSize, 4 * tileSize, 2 * tileSize);
        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 5 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 4 * tileSize, 2 * tileSize);
        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 10 * tileSize, 5 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0);
        addForegroundTileAndReplace(fgt, 10 * tileSize, 4 * tileSize, 2 * tileSize);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 7 * tileSize, 7 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0);
        addForegroundTileAndReplace(fgt, 7 * tileSize, 6 * tileSize, 2 * tileSize);

        fgt = ForeGroundTile.createWallShadowHeight(place.getSpriteSheet("tlo"), tileSize, 7, 2, 0, -tileSize);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 8 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinary(place.getSpriteSheet("tlo"), tileSize, 1, 1);
        fgt.setSolid(true);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 7 * tileSize, 2 * tileSize);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 6 * tileSize, 4 * tileSize);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 11 * tileSize, 7 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0);
        addForegroundTileAndReplace(fgt, 11 * tileSize, 6 * tileSize, 2 * tileSize);

        fgt = ForeGroundTile.createWallShadowHeight(place.getSpriteSheet("tlo"), tileSize, 7, 2, 0, tileSize);
        addForegroundTileAndReplace(fgt, 5 * tileSize, 9 * tileSize, 0);
        fgt = ForeGroundTile.createWallShadowHeight(place.getSpriteSheet("tlo"), tileSize, 7, 2, tileSize, 0);
        fgt.setSolid(false);
        addForegroundTileAndReplace(fgt, 5 * tileSize, 8 * tileSize, 2 * tileSize);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, 2 * tileSize, 0);
        addForegroundTileAndReplace(fgt, 5 * tileSize, 7 * tileSize, 4 * tileSize);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 11 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 10 * tileSize, 2 * tileSize);

        Area border = Area.createWhole(0, 0, tileSize);

        WarpPoint warp = new WarpPoint("toKamienna", 20 * tileSize, 20 * tileSize, "Kamienna");
        warp.setCollision(Rectangle.create(0, 0, tileSize, tileSize, OpticProperties.IN_SHADE_NO_SHADOW, warp));
        addObject(warp);
        addObject(new WarpPoint("toPolana", 20 * tileSize, 17 * tileSize));

        tiles[20 + 20 * height / tileSize] = PORTAL;
        areas.add(testa);
        areas.add(testb);
        areas.add(testc);
        areas.add(testd);
        areas.add(teste);
        areas.add(testf);
        areas.add(testg);
        areas.add(border);

        for (int i = 0; i < 1; i++) {
            addObject(new MyMob(192 + 192 * (i % 50), 2048 + 192 * (i / 50), 0, 8, 128, 112, 4, 512, "rabbit", place, true, mobID++));
        }
    }
}
