package game.place;

import game.gameobject.GameObject;
import static org.lwjgl.opengl.GL11.*;
import sprites.SpriteSheet;

public abstract class Tile extends GameObject {

    public static int SIZE;
    protected final SpriteSheet sh;
    protected final int xSheet;
    protected final int ySheet;

    public Tile(SpriteSheet sh, int size, boolean isSolid, boolean isEmitter, int xSheet, int ySheet, Place place) {
        SIZE = size;
        this.solid = isSolid;
        this.emitter = isEmitter;
        this.sh = sh;
        this.xSheet = xSheet;
        this.ySheet = ySheet;
        this.place = place;
    }
    
    public void render(int flip, int x, int y) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        sh.render(1, xSheet, ySheet);
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit) {
        if (nLit != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            if (isLit) {
                lit.render();
            } else {
                nLit.render();
            }
            glPopMatrix();
        }
    }

    public void renderShadow(int x, int y, int xEffect, int yEffect, boolean isLit) {
        if (nLit != null) {
            glPushMatrix();
            glTranslatef(x + xEffect, y + yEffect, 0);
            if (isLit) {
                lit.render();
            } else {
                nLit.render();
            }
            glPopMatrix();
        }
    }
}
