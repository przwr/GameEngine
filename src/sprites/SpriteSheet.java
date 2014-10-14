/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Sprite;

/**
 *
 * @author przemek
 */
public final class SpriteSheet extends Sprite {

    private final int xTiles;
    private final int yTiles;

    public SpriteSheet(String sprite, int sx, int sy, int sTile) {
        super(sprite, sx, sy);
        this.xTiles = sx / sTile;
        this.yTiles = sy / sTile;
        render(0, 15);
    }

    public void render(int flip, int texKey) {
        int y = texKey / yTiles;
        int x = texKey % yTiles;
        System.out.println("x: " + x + " y: " + y);
        float bx = ((float) x) / xTiles;
        float ex = ((float) (x + 1)) / xTiles;
        float by = ((float) y) / yTiles;
        float ey = ((float) (y + 1)) / yTiles;
        System.out.println("bx: " + bx + " ex: " + ex);
        System.out.println("by: " + by + " ey: " + ey);
        render(flip, bx, ex, by, ey);
    }
}
