/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import collision.Area;
import collision.Line;
import collision.Rectangle;
import game.place.FGTile;
import game.place.Map;
import game.place.Place;
import game.place.Tile;
import game.place.WarpPoint;

/**
 *
 * @author Wojtek
 */
public class KamiennaMap extends Map {

    public KamiennaMap(Place place, int width, int height, int sTile) {
        super("Kamienna", place, width, height, sTile);
        
        FGTile fgt;
        Tile GROUND = new Tile(place.getSpriteSheet("tlo"), sTile, 2, 12, place);
        Tile ROCK = new Tile(place.getSpriteSheet("tlo"), sTile, 1, 1, place);
        Tile PORTAL = new Tile(place.getSpriteSheet("tlo"), sTile, 0, 12, place);
        Area a = new Area(13 * sTile, 13 * sTile, sTile);
        for (int y = 0; y < height / sTile; y++) {
            for (int x = 0; x < width / sTile; x++) {
                if ((x * y) < 600) {
                tiles[x + y * height / sTile] = GROUND;
                } else {
                    if (tiles[x - 1 + y * height / sTile] == GROUND || tiles[x + (y - 1) * height / sTile] == GROUND) {
                        a.addFigure(new Rectangle(x * sTile - 13 * sTile, y * sTile - 13 * sTile, sTile, sTile, false, true, 0, a));
                    }
                    tiles[x + y * height / sTile] = ROCK;
                }
            }
        }
        Area testa = new Area(6 * sTile, 5 * sTile, sTile, false, true);
        Area testb = new Area(8 * sTile, 5 * sTile, sTile, false, true);
        Area testc = new Area(7 * sTile, 7 * sTile, sTile, false, true);
        Area testd = new Area(9 * sTile, 7 * sTile, sTile, false, true);
        Area teste = new Area(11 * sTile, 7 * sTile, sTile, false, true);
        Area testf = new Area(5 * sTile, 9 * sTile, sTile, false, true);
        Area testg = new Area(9 * sTile, 11 * sTile, sTile, false, true);

        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, place);
        addFGTile(fgt, 6 * sTile, 5 * sTile, 0, true);
        testa.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, place);
        addFGTile(fgt, 6 * sTile, 4 * sTile, 2 * sTile, true);
        testa.addPiece(fgt);

        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, place);
        addFGTile(fgt, 8 * sTile, 5 * sTile, 0, true);
        testb.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, place);
        addFGTile(fgt, 8 * sTile, 4 * sTile, 2 * sTile, true);
        testb.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, place);
        addFGTile(fgt, 9 * sTile, 5 * sTile, 0, true);
        testb.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, place);
        addFGTile(fgt, 9 * sTile, 4 * sTile, 2 * sTile, true);
        testb.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, place);
        addFGTile(fgt, 10 * sTile, 5 * sTile, 0, true);
        testb.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, place);
        addFGTile(fgt, 10 * sTile, 4 * sTile, 2 * sTile, true);
        testb.addPiece(fgt);

        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, place);
        addFGTile(fgt, 7 * sTile, 7 * sTile, 0, true);
        testc.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, place);
        addFGTile(fgt, 7 * sTile, 6 * sTile, 2 * sTile, true);
        testc.addPiece(fgt);

        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, 0, -sTile, place);
        addFGTile(fgt, 9 * sTile, 8 * sTile, 0, true);
        testd.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, 0, 0, place);
        fgt.setSolid(true);
        addFGTile(fgt, 9 * sTile, 7 * sTile, 2 * sTile, true);
        testd.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, place);
        addFGTile(fgt, 9 * sTile, 6 * sTile, 4 * sTile, true);
        testd.addPiece(fgt);

        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, place);
        addFGTile(fgt, 11 * sTile, 7 * sTile, 0, true);
        teste.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, place);
        addFGTile(fgt, 11 * sTile, 6 * sTile, 2 * sTile, true);
        teste.addPiece(fgt);

        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, 0, sTile, place);
        addFGTile(fgt, 5 * sTile, 9 * sTile, 0, true);
        testf.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, sTile, 0, place);
        fgt.setSolid(false);
        addFGTile(fgt, 5 * sTile, 8 * sTile, 2 * sTile, true);
        testf.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, 2 * sTile, 0, place);
        addFGTile(fgt, 5 * sTile, 7 * sTile, 4 * sTile, true);
        testf.addPiece(fgt);

        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, place);
        addFGTile(fgt, 9 * sTile, 11 * sTile, 0, true);
        testg.addPiece(fgt);
        fgt = new FGTile(place.getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, place);
        addFGTile(fgt, 9 * sTile, 10 * sTile, 2 * sTile, true);
        testg.addPiece(fgt);
        //tiles[7 + 7 * height / sTile] = ROCK;
        Area border = new Area(0, 0, sTile, true, false);
        border.addFigure(new Line(0, 0, width, 0, border));
        border.addFigure(new Line(0, 0, 0, height, border));
        border.addFigure(new Line(width, 0, 0, height, border));
        border.addFigure(new Line(0, height, width, 0, border));
        WarpPoint w = new WarpPoint("toPolana", 20 * sTile, 20 * sTile, "Polana");
        w.setCollision(new Rectangle(0, 0, sTile, sTile, false, false, 0, w));
        addObj(w);
        addObj(new WarpPoint("toKamienna", 20 * sTile, 17 * sTile));
        tiles[20 + 20 * height / sTile] = PORTAL;
        areas.add(a);
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
