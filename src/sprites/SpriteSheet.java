/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author przemek
 */
public final class SpriteSheet extends Sprite {

    private final float xTiles;
    private final float yTiles;

    public SpriteSheet(Texture tex, int width, int height, int sx, int sy, SpriteBase base) {
        super(tex, (int) (width * base.getScale()), (int) (height * base.getScale()), sx, sy, base);
        this.xTiles = texture.getImageWidth() / width;
        this.yTiles = texture.getImageHeight() / height;
    }

    public void render(int i) {
        if (i > xTiles * yTiles) {
            return;
        }
        int x = (int) (i % xTiles);
        int y = (int) (i / xTiles);
        render(x, y);
    }

    public void render(int x, int y) {
        if (x > xTiles || y > yTiles) {
            return;
        }
        float bx = (float) x / xTiles;
        float by = (float) y / yTiles;
        float ex = (float) (x + 1) / xTiles;
        float ey = (float) (y + 1) / yTiles;
        renderTexPart(bx, ex, by, ey);
    }

    public void renderMirrored(boolean flip, int x, int y) {
        if (x > xTiles || y > yTiles) {
            return;
        }
        float bx = (float) x / xTiles;
        float by = (float) y / yTiles;
        float ex = (float) (x + 1) / xTiles;
        float ey = (float) (y + 1) / yTiles;
        renderPartMirrored(flip, bx, ex, by, ey);
    }

    public int getXlimit() {
        return (int) xTiles;
    }

    public int getYlimit() {
        return (int) yTiles;
    }

    public int getLenght() {
        return (int) (xTiles * yTiles);
    }
}
