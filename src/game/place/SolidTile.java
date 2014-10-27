/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Rectangle;
import static game.place.Tile.SIZE;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public class SolidTile extends Tile {

    public SolidTile(SpriteSheet sh, String name, int size, int xSheet, int ySheet, Place place) {
        super(sh, size, true, false, xSheet, ySheet, place);
        init(name, 1, 1, SIZE, SIZE, place);
    }

    @Override
    public void render(int xEffect, int yEffect) {
    }
}
