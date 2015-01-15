/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.Area;
import collision.Line;
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
public class KamiennaMap extends Map {

    public KamiennaMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "Kamienna", place, width, height, tileSize);
        Tile GROUND = new Tile(place.getSpriteSheet("tlo"), tileSize, 2, 12, place);
        Tile ROCK = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 1, place);
        Tile PORTAL = new Tile(place.getSpriteSheet("tlo"), tileSize, 0, 12, place);
        Area rocks = Area.createInChunks(13 * tileSize, 13 * tileSize, tileSize);
        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                if ((x * y) < 600) {
                    tiles[x + y * height / tileSize] = GROUND;
                } else {
                    if (tiles[x - 1 + y * height / tileSize] == GROUND || tiles[x + (y - 1) * height / tileSize] == GROUND) {
                        rocks.addFigure(Rectangle.create(x * tileSize - 13 * tileSize, y * tileSize - 13 * tileSize, tileSize, tileSize, OpticProperties.IN_SHADE, rocks));
                    }
                    tiles[x + y * height / tileSize] = ROCK;
                }
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
        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2, place);
        addFGTileAndReplace(fgt, 6 * tileSize, 5 * tileSize, 0);
        testa.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0, place);
        addFGTileAndReplace(fgt, 6 * tileSize, 4 * tileSize, 2 * tileSize);
        testa.addPiece(fgt);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2, place);
        addFGTileAndReplace(fgt, 8 * tileSize, 5 * tileSize, 0);
        testb.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0, place);
        addFGTileAndReplace(fgt, 8 * tileSize, 4 * tileSize, 2 * tileSize);
        testb.addPiece(fgt);
        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2, place);
        addFGTileAndReplace(fgt, 9 * tileSize, 5 * tileSize, 0);
        testb.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0, place);
        addFGTileAndReplace(fgt, 9 * tileSize, 4 * tileSize, 2 * tileSize);
        testb.addPiece(fgt);
        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2, place);
        addFGTileAndReplace(fgt, 10 * tileSize, 5 * tileSize, 0);
        testb.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0, place);
        addFGTileAndReplace(fgt, 10 * tileSize, 4 * tileSize, 2 * tileSize);
        testb.addPiece(fgt);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2, place);
        addFGTileAndReplace(fgt, 7 * tileSize, 7 * tileSize, 0);
        testc.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0, place);
        addFGTileAndReplace(fgt, 7 * tileSize, 6 * tileSize, 2 * tileSize);
        testc.addPiece(fgt);

        fgt = ForeGroundTile.createWallShadowHeight(place.getSpriteSheet("tlo"), tileSize, 7, 2, 0, -tileSize, place);
        addFGTileAndReplace(fgt, 9 * tileSize, 8 * tileSize, 0);
        testd.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinary(place.getSpriteSheet("tlo"), tileSize, 1, 1, place);
        fgt.setSolid(true);
        addFGTileAndReplace(fgt, 9 * tileSize, 7 * tileSize, 2 * tileSize);
        testd.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0, place);
        addFGTileAndReplace(fgt, 9 * tileSize, 6 * tileSize, 4 * tileSize);
        testd.addPiece(fgt);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2, place);
        addFGTileAndReplace(fgt, 11 * tileSize, 7 * tileSize, 0);
        teste.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0, place);
        addFGTileAndReplace(fgt, 11 * tileSize, 6 * tileSize, 2 * tileSize);
        teste.addPiece(fgt);

        fgt = ForeGroundTile.createWallShadowHeight(place.getSpriteSheet("tlo"), tileSize, 7, 2, 0, tileSize, place);
        addFGTileAndReplace(fgt, 5 * tileSize, 9 * tileSize, 0);
        testf.addPiece(fgt);
        fgt = ForeGroundTile.createWallShadowHeight(place.getSpriteSheet("tlo"), tileSize, 7, 2, tileSize, 0, place);
        fgt.setSolid(false);
        addFGTileAndReplace(fgt, 5 * tileSize, 8 * tileSize, 2 * tileSize);
        testf.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, 2 * tileSize, 0, place);
        addFGTileAndReplace(fgt, 5 * tileSize, 7 * tileSize, 4 * tileSize);
        testf.addPiece(fgt);

        fgt = ForeGroundTile.createWall(place.getSpriteSheet("tlo"), tileSize, 7, 2, place);
        addFGTileAndReplace(fgt, 9 * tileSize, 11 * tileSize, 0);
        testg.addPiece(fgt);
        fgt = ForeGroundTile.createOrdinaryShadowHeight(place.getSpriteSheet("tlo"), tileSize, 1, 1, tileSize, 0, place);
        addFGTileAndReplace(fgt, 9 * tileSize, 10 * tileSize, 2 * tileSize);
        testg.addPiece(fgt);
        Area border = Area.createBorder(0, 0, tileSize);
        border.addFigure(Line.create(0, 0, width, 0, border));
        border.addFigure(Line.create(0, 0, 0, height, border));
        border.addFigure(Line.create(width, 0, 0, height, border));
        border.addFigure(Line.create(0, height, width, 0, border));
        WarpPoint w = new WarpPoint("toPolana", 20 * tileSize, 20 * tileSize, "Polana");
        w.setCollision(Rectangle.create(0, 0, tileSize, tileSize, OpticProperties.IN_SHADE_NO_SHADOW, w));
        addObj(w);
        addObj(new WarpPoint("toKamienna", 20 * tileSize, 17 * tileSize));
        tiles[20 + 20 * height / tileSize] = PORTAL;
        areas.add(rocks);
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
