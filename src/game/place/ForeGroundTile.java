/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Drawer;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ForeGroundTile extends Tile {

    private int highness;

    public static ForeGroundTile createOrdinaryShadowHeight(SpriteSheet sh, int size, int xSheet, int ySheet, int yStart, int shadowHeight, Place place) {
        return new ForeGroundTile(sh, size, xSheet, ySheet, false, yStart, shadowHeight, place);
    }

    public static ForeGroundTile createOrdinary(SpriteSheet sh, int size, int xSheet, int ySheet, Place place) {
        return new ForeGroundTile(sh, size, xSheet, ySheet, false, 0, 0, place);
    }

    public static ForeGroundTile createWallShadowHeight(SpriteSheet sh, int size, int xSheet, int ySheet, int yStart, int shadowHeight, Place place) {
        return new ForeGroundTile(sh, size, xSheet, ySheet, true, yStart, shadowHeight, place);
    }

    public static ForeGroundTile createWall(SpriteSheet sh, int size, int xSheet, int ySheet, Place place) {
        return new ForeGroundTile(sh, size, xSheet, ySheet, true, 0, 0, place);
    }

    private ForeGroundTile(SpriteSheet sh, int size, int xSheet, int ySheet, boolean wall, int yStart, int shadowHeight, Place place) {
        super(sh, size, xSheet, ySheet, place);
        simpleLighting = true;
        solid = wall;
        int type = wall ? OpticProperties.FULL_SHADOW : OpticProperties.IN_SHADE_NO_SHADOW;
        setCollision(Rectangle.createShadowHeight(0, yStart, size, size, type, shadowHeight, this));
    }

    public boolean isSimpleLighting() {
        return simpleLighting;
    }

    public void setSimpleLighting(boolean simpleLighting) {
        this.simpleLighting = simpleLighting;
    }

    public int getHighness() {
        return highness;
    }

    public void setHighness(int highness) {
        this.highness = highness;
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() - collision.getShadowHeight() + yEffect, 0);
        if (simpleLighting) {
            glColor3f(color, color, color);
            Drawer.drawRectangle(0, 0, collision.getWidth(), collision.getHeight() + collision.getShadowHeight());
            glColor3f(1f, 1f, 1f);
        } else if (sprite != null) {
            Drawer.drawShapeInColor(sprite, color, color, color);
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        }
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() - collision.getShadowHeight() + yEffect, 0);
        if (simpleLighting) {
            glColor3f(0f, 0f, 0f);
            Drawer.drawRectangle(0, 0, collision.getWidth(), collision.getHeight() + collision.getShadowHeight());
            glColor3f(1f, 1f, 1f);
        } else if (sprite != null) {
            Drawer.drawShapeInBlack(sprite);
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        }
        glPopMatrix();
    }
}
