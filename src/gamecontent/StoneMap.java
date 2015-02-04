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

        Area testa = new Area(6 * tileSize, 5 * tileSize, tileSize, tileSize, 0);
        Area testb = new Area(8 * tileSize, 5 * tileSize, tileSize, tileSize, tileSize);
        Area testc = new Area(7 * tileSize, 7 * tileSize, tileSize, tileSize, tileSize);
        Area testd = new Area(9 * tileSize, 7 * tileSize, tileSize, tileSize, tileSize);
        Area teste = new Area(11 * tileSize, 7 * tileSize, tileSize, tileSize, tileSize);
        Area testf = new Area(5 * tileSize, 9 * tileSize, tileSize, tileSize, tileSize);
        Area testg = new Area(9 * tileSize, 11 * tileSize, tileSize, tileSize, tileSize);
        
          ForeGroundTile fgt;

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 6 * tileSize, 5 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, 0);
        addForegroundTileAndReplace(fgt, 6 * tileSize, 4 * tileSize, 2 * tileSize);
        testa.setCollision(Rectangle.createShadowHeight(0, 0, tileSize, tileSize, OpticProperties.FULL_SHADOW, 0, testa));

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 8 * tileSize, 5 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize);
        addForegroundTileAndReplace(fgt, 8 * tileSize, 4 * tileSize, 2 * tileSize);
        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 5 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 4 * tileSize, 2 * tileSize);
        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 10 * tileSize, 5 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize);
        addForegroundTileAndReplace(fgt, 10 * tileSize, 4 * tileSize, 2 * tileSize);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 7 * tileSize, 7 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize);
        addForegroundTileAndReplace(fgt, 7 * tileSize, 6 * tileSize, 2 * tileSize);

        fgt = ForeGroundTile.createWallShadowHeight(place.getSpriteSheet("tlo"), tileSize, 7, 2, 0);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 8 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinary(place.getSpriteSheet("tlo"), tileSize, 1, 1);
        fgt.setSolid(true);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 7 * tileSize, 2 * tileSize);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 6 * tileSize, 4 * tileSize);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 11 * tileSize, 7 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize);
        addForegroundTileAndReplace(fgt, 11 * tileSize, 6 * tileSize, 2 * tileSize);

        fgt = ForeGroundTile.createWallShadowHeight(place.getSpriteSheet("tlo"), tileSize, 7, 2, 0);
        addForegroundTileAndReplace(fgt, 5 * tileSize, 9 * tileSize, 0);
        fgt = ForeGroundTile.createWallShadowHeight(place.getSpriteSheet("tlo"), tileSize, 7, 2, tileSize);
        fgt.setSolid(false);
        addForegroundTileAndReplace(fgt, 5 * tileSize, 8 * tileSize, 2 * tileSize);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, 2 * tileSize);
        addForegroundTileAndReplace(fgt, 5 * tileSize, 7 * tileSize, 4 * tileSize);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 11 * tileSize, 0);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize);
        addForegroundTileAndReplace(fgt, 9 * tileSize, 10 * tileSize, 2 * tileSize);

        Area border = new Area(6 * tileSize, 5 * tileSize, tileSize, tileSize, 0);

        WarpPoint w = new WarpPoint("toPolana", 20 * tileSize, 20 * tileSize, "Polana");
        w.setCollision(Rectangle.create(0, 0, tileSize, tileSize, OpticProperties.IN_SHADE_NO_SHADOW, w));
        addObject(w);
        addObject(new WarpPoint("toKamienna", 20 * tileSize, 17 * tileSize));
        tiles[20 + 20 * height / tileSize] = PORTAL;

        areas.add(testa);
        areas.add(testb);
        areas.add(testc);
        areas.add(testd);
        areas.add(teste);
        areas.add(testf);
        areas.add(testg);
        areas.add(border);

    }
}
