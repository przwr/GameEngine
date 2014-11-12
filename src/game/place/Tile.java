package game.place;

import game.gameobject.GameObject;
import static org.lwjgl.opengl.GL11.*;
import sprites.SpriteSheet;

public class Tile extends GameObject {

    public static int SIZE;
    protected final SpriteSheet sh;
    protected final int xSheet;
    protected final int ySheet;

    public Tile(SpriteSheet sh, int size, int xSheet, int ySheet, Place place) {
        SIZE = size;
        this.sh = sh;
        this.xSheet = xSheet;
        this.ySheet = ySheet;
        this.place = place;
    }

    public void renderSpecific(int flip, int x, int y) {    //Renderuje w konkretnym miejscu nie 
        glPushMatrix();                                     //patrząc na zmienne wewnętrzne
        glTranslatef(x, y, 0);
        sh.render(xSheet, ySheet);
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

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect, 0);
        sh.render(xSheet, ySheet);
        glPopMatrix();
    }
}
