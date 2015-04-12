/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.designerElements;

import collision.Figure;
import engine.Drawer;
import engine.Point;
import game.Settings;
import game.gameobject.GameObject;
import game.place.Place;
import gamedesigner.ObjectPlace;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 *
 * @author Wojtek
 */
public class CentralPoint extends GameObject {

    private final int tile;
    private final ObjectPlace objPlace;

    public CentralPoint(int x, int y, ObjectPlace objPlace) {
        initialize("central", x, y);
        this.objPlace = objPlace;
        tile = Place.tileSize;
        onTop = true;
        depth = -100;
    }

    public Point getCentralPoint() {
        return new Point((int) x / tile, (int) y / tile);
    }

    public void setCentralPoint(int x, int y) {
        this.x = x * tile;
        this.y = y * tile;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (objPlace.getMode() != 2) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            }
            glTranslatef(getX(), getY(), 0);
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
            glColor4f(0f, 0f, 1f, 1f);
            Drawer.drawRectangle(0, 0, tile, 3);
            Drawer.drawRectangle(0, 3, 3, tile - 3);
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect,  Figure figure, int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
    }
}
