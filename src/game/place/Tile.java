package game.place;

import collision.Figure;
import engine.Drawer;
import game.gameobject.GameObject;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

public class Tile extends GameObject {

    public static int SIZE;
    protected final SpriteSheet spriteSheet;
    protected final int xSheet;
    protected final int ySheet;

    public Tile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, Place place) {
        SIZE = size;
        this.spriteSheet = spriteSheet;
        this.xSheet = xSheet;
        this.ySheet = ySheet;
        this.place = place;
    }

    public void renderSpecific(int x, int y) {    //Renderuje w konkretnym miejscu nie 
        glPushMatrix();                                     //patrząc na zmienne wewnętrzne
        glTranslatef(x, y, 0);
        spriteSheet.render(xSheet, ySheet);
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        Drawer.drawRectangleInShade(0, 0, width, height, color);
        glColor3f(1f, 1f, 1f);
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        Drawer.drawRectangleInBlack(0, 0, width, height);
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f, int xStart, int xEnd) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        Drawer.drawRectangleInShade(0, 0, width, height, color);
        glColor3f(1f, 1f, 1f);
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f, int xStart, int xEnd) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        Drawer.drawRectangleInBlack(0, 0, width, height);
        glPopMatrix();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect, 0);
        spriteSheet.render(xSheet, ySheet);
        glPopMatrix();
    }
}
