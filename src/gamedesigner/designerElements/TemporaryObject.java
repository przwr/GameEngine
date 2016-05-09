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

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class TemporaryObject extends GameObject {

    private final GameObject covered;
    private final ObjectPlace objPlace;
    private final String[] data;

    public TemporaryObject(GameObject covered, ObjectPlace place, String... data) {
        this.initialize("tmpBlock", Methods.makeDivisibleBy(covered.getX(), Place.tileSize),
                Methods.makeDivisibleBy(covered.getY(), Place.tileSize));
        this.covered = covered;
        this.setOnTop(true);
        objPlace = place;
        if (data != null && data.length != 0) {
            this.data = data;
        } else {
            this.data = null;
        }
    }

    public GameObject getCovered() {
        return covered;
    }

    public String[] getAdditionalData() {
        return data;
    }

    @Override
    public void render() {
        if (objPlace.getMode() != ObjectPlace.MODE_VIEWING) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
            Drawer.setColorStatic(0f, 0.5f, 1f, 1f);
            Drawer.drawRing(Place.tileSize / 2, Place.tileSize / 2, Place.tileSize / 2, 3, 10);
            Drawer.refreshForRegularDrawing();
        }
    }

    @Override
    public void renderShadowLit(Figure figure) {
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(Figure figure) {
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
    }

}
