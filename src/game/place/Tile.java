package game.place;

import game.gameobject.GameObject;
import engine.Sprite;
import static org.lwjgl.opengl.GL11.*;

public abstract class Tile extends GameObject {

    public static int SIZE;

    public Tile(String tex, int size, boolean isSolid, boolean isEmitter) {
        SIZE = size;
        this.solid = isSolid;
        this.emitter = isEmitter;
        this.sprite = new Sprite(tex, size, size);
    }

    public void render(int flip, int x, int y) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        sprite.render(flip);
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
