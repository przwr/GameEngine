/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import static game.place.Tile.SIZE;

/**
 *
 * @author przemek
 */
public class SolidTile extends Tile {

    public SolidTile(String tex, int size) {
        super(tex, size, true, false);
        init(tex, 1, 1, SIZE, SIZE);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
