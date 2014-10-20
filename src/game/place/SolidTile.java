/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Sprite;
import static game.place.Tile.SIZE;

/**
 *
 * @author przemek
 */
public class SolidTile extends Tile {

    public SolidTile(String tex, String name, int size) {
        super(tex, size, true, false);
        init(tex, name, 1, 1, SIZE, SIZE);
        lit = new Sprite("rockw", 64, 64);
        nLit = new Sprite("rockb", 64, 64);
    }

    @Override
    public void render(int xEffect, int yEffect) {
    }
}
