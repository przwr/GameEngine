/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.designerElements;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.place.Place;
import gamedesigner.ObjectPlace;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class CentralPoint extends GameObject {

    private final int tile;
    private final ObjectPlace objPlace;

    public CentralPoint(int x, int y, ObjectPlace objPlace) {
        initialize("central", x, y);
        this.objPlace = objPlace;
        tile = Place.tileSize;
        setOnTop(true);
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
    public void render() {
        if (objPlace.getMode() != 2) {
            Drawer.regularShader.translate(getX(), getY());
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
            Drawer.setColorStatic(0f, 0f, 1f, 1f);
            Drawer.drawRectangle(0, 0, tile, 3);
            Drawer.drawRectangle(0, 3, 3, tile - 3);
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
