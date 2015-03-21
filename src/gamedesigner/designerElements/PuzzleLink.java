/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.designerElements;

import collision.Figure;
import engine.Drawer;
import game.Settings;
import game.gameobject.GameObject;
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
public class PuzzleLink extends GameObject {

    private final int tile, radius;
    private final ObjectPlace objPlace;

    public PuzzleLink(int x, int y, int radius, ObjectPlace objPlace) {
        initialize("link", x, y);
        this.objPlace = objPlace;
        tile = objPlace.getTileSize();
        onTop = true;
        depth = -100;
        this.radius = radius;
    }
        
    public int getRadius() {
        return radius;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (objPlace.getMode() == 3) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(Settings.scale, Settings.scale, 1);
            }
            glTranslatef(getX(), getY(), 0);
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
            glColor4f(0f, 0f, 1f, 1f);
            int d = 2;
            Drawer.drawRing(tile / 2, tile / 2, tile / 3, d, 10);
            Drawer.drawRing(0, 0, tile / 5, d, 10);
            int complex = radius * 2 + 10;
            if (radius % 2 == 1) {
                Drawer.drawRing(0, 0, radius * tile / 2, d, complex);
            } else {
                Drawer.drawRing(-tile / 2, -tile / 2, radius * tile / 2, d, complex);
            }
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

    //0  1 2 4     5       6    7      8     9          10
    //ft:x:y:depth:texture:wall:yStart:round:TileXSheet:TileYSheet...
    public String saveToString(int xBegin, int yBegin) {
        return ((getX() - xBegin) / tile) + ":" + ((getY() - yBegin) / tile) + ":" + radius;
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
