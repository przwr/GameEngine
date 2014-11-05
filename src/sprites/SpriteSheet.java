/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

/**
 *
 * @author przemek
 */
public final class SpriteSheet extends Sprite {

    private final int w;
    private final int h;
    private final float xTiles;
    private final float yTiles;

    public SpriteSheet(String sprite, int width, int height, int w, int h, SpriteBase base) {
        super(sprite, width, height, base);
        this.w = w;
        this.h = h;
        this.xTiles = texture.getImageWidth() / w;
        this.yTiles = texture.getImageHeight() / h;
    }

    public void render(int flip, int i) {
        if (i > xTiles * yTiles) {
            return;
        }
        int x = (int) (i % xTiles);
        int y = (int) (i / yTiles);
        render(flip, x, y);
    }

    public void render(int flip, int x, int y) {
        if (x > xTiles || y > yTiles) {
            return;
        }
        float bx = (float) x / xTiles;
        float by = (float) y / yTiles;
        float ex = (float) (x + 1) / xTiles;
        float ey = (float) (y + 1) / yTiles;
        renderTexPart(bx, ex, by, ey);
    }
}
