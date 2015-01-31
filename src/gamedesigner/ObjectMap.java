/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import game.place.Map;
import game.place.Place;
import game.place.Tile;

/**
 *
 * @author Wojtek
 */
public class ObjectMap extends Map {
    public Tile background;
    
    public ObjectMap(short id, Place place, int width, int height, int tileSize) {
        super(id, "ObjectMap", place, width, height, tileSize);
        
        background = new Tile(place.getSpriteSheet("tlo"), tileSize, 1, 8, place);
        
        for (int y = 0; y < height / tileSize; y++) {
            for (int x = 0; x < width / tileSize; x++) {
                tiles[x + y * height / tileSize] = background;
            }
        }
    }
}
