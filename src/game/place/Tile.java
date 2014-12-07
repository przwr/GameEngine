package game.place;

import collision.Figure;
import engine.Drawer;
import game.gameobject.GameObject;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
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
        simpleLighting = true;
    }

    public void renderSpecific(int flip, int x, int y) {    //Renderuje w konkretnym miejscu nie 
        glPushMatrix();                                     //patrząc na zmienne wewnętrzne
        glTranslatef(x, y, 0);
        sh.render(xSheet, ySheet);
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        if (simpleLighting) {
            if (isLit) {
                glColor4f(color, color, color, 1f);
            } else {
                glColor4f(0f, 0f, 0f, 1f);
            }
            Drawer.drawRectangle(0, 0, width, height);
            glColor4f(1f, 1f, 1f, 1f);
        } else if (sprite != null) {
            if (isLit) {
                Drawer.drawShapeInColor(sprite, color, color, color, 1);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            } else {
                Drawer.drawShapeInBlack(sprite);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            }
        }
        glPopMatrix();
    }

    public void renderShadow(int x, int y, int xEffect, int yEffect, boolean isLit, float color) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        if (simpleLighting) {
            if (isLit) {
                glColor4f(color, color, color, 1f);
            } else {
                glColor4f(0f, 0f, 0f, 1f);
            }
            Drawer.drawRectangle(0, 0, width, height);
            glColor4f(1f, 1f, 1f, 1f);
        } else if (sprite != null) {
            if (isLit) {
                Drawer.drawShapeInColor(sprite, color, color, color, 1);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            } else {
                Drawer.drawShapeInBlack(sprite);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            }
        }
        glPopMatrix();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect, 0);
        sh.render(xSheet, ySheet);
        glPopMatrix();
    }
}
