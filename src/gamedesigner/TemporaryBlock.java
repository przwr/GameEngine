/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Figure;
import engine.Drawer;
import game.gameobject.GameObject;
import game.place.Map;
import game.place.Place;
import game.place.Tile;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 *
 * @author Wojtek
 */
public class TemporaryBlock extends GameObject {

    private final int tile;
    private final int upHeight, xTiles, yTiles;
    private final ObjectPlace objPlace;
    private boolean complete;

    public TemporaryBlock(int x, int y, int upHeight, int width, int height, Map map, Place place) {
        initialize("tmpBlock", x, y);
        this.tile = map.getTileSize();
        this.upHeight = upHeight;
        this.width = width * tile;
        this.height = height * tile;
        xTiles = width;
        yTiles = height;
        this.map = map;
        objPlace = (ObjectPlace) place;
    }

    private void setColor() {
        if (complete) {
            glColor3f(1f, 0.78f, 0f);
        } else {
            glColor3f(1f, 0f, 0f);
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect, 0);
        int mode = objPlace.getMode();
        if (mode != 2) {
            int d = 2;
            Drawer.refreshColor();
            int tmpH = upHeight * tile;
            if (mode == 1) {
                Drawer.drawRectangle(0, -tmpH, width, height);
            } else {
                glTranslatef(0, -tmpH, 0);
            }
            if (upHeight == 0) {
                setColor();
                Drawer.drawRectangle(0, 0, width, d);
                Drawer.drawRectangle(0, height - d, width, d);
                Drawer.drawRectangle(0, -height + d, d, height);
                Drawer.drawRectangle(width - d, 0, d, height);
            } else {
                if (mode == 1) {
                    glColor3f(0.9f, 0.9f, 0.9f);
                    Drawer.drawRectangle(0, height, width, tmpH);
                    glTranslatef(0, -height, 0);
                }
                setColor();
                Drawer.drawRectangle(0, 0, width, d);
                Drawer.drawRectangle(0, height - d, width, d);
                Drawer.drawRectangle(0, tmpH, width, d);
                Drawer.drawRectangle(0, 0, d, -tmpH - height + d);
                Drawer.drawRectangle(width - d, 0, d, -tmpH - height + d);
            }
        }
        glPopMatrix();
    }

    public void initialize(Tile[] tiles) {
        
    }
    
    public boolean checkCollision(int x, int y, int width, int height) {
        return ((this.x > x && this.x - x < width) || (this.x <= x && x - this.x < this.width))
                && ((this.y > y && this.y - y < height) || (this.y <= y && y - this.y < this.height));
    }
    
    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure, int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
    }

}
