/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Area;
import collision.Line;
import game.place.Map;
import game.place.Place;
import game.place.Tile;

/**
 *
 * @author Wojtek
 */
public class ObjectMap extends Map {

    public ObjectMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "ObjectMap", place, width, height, tileSize);
        Tile GRASS = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 8, place);
        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                tiles[x + y * height / tileSize] = GRASS;
            }
        }
                
        Area border = Area.createBorder(0, 0, tileSize);
        border.addFigure(Line.create(0, 0, width, 0, border));
        border.addFigure(Line.create(0, 0, 0, height, border));
        border.addFigure(Line.create(width, 0, 0, height, border));
        border.addFigure(Line.create(0, height, width, 0, border));
        
        areas.add(border);
    }
}
