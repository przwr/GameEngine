/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.designerElements;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.Methods;
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
public class TemporaryObject extends GameObject {

    private final GameObject covered;
    private final ObjectPlace objPlace;
    private final Object[] data;

    public TemporaryObject(GameObject covered, ObjectPlace place, Object... data) {
        this.initialize("tmpBlock", Methods.makeDivisibleBy(covered.getX(), Place.tileSize), 
                Methods.makeDivisibleBy(covered.getY(), Place.tileSize));
        this.covered = covered;
        this.onTop = true;
        objPlace = place;
        if (data.length != 0) {
            this.data = data;
        } else {
            this.data = null;
        }
    }

    public GameObject getCovered() {
        return covered;
    }
    
    public Object[] getAdditionalData() {
        return data;
    }
    
    @Override
    public void render(int xEffect, int yEffect) {
        if (objPlace.getMode() != ObjectPlace.MODE_VIEWING) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY(), 0);
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
            glColor4f(0f, 0.5f, 1f, 1f);

            Drawer.drawRing(Place.tileSize / 2, Place.tileSize / 2, Place.tileSize / 2, 3, 10);

            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
    }

}
