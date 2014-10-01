/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

/**
 *
 * @author przemek
 */
public class BasicTile extends Tile {

    public BasicTile(String tex, String name, int size) {
        super(tex, size, false, false);
        init(tex, name, 1, 1, SIZE, SIZE);
    }

    @Override
    public void render(int xEffect, int yEffect) {
    }
}
