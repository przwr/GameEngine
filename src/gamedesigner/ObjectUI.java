/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Figure;
import engine.Drawer;
import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.place.Place;
import game.place.cameras.Camera;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ObjectUI extends GameObject {

    private Camera cam;
    private final int tile;
    private SpriteSheet tex;
    private boolean change;

    private final Point coord = new Point(0, 0);

    public ObjectUI(int tile, SpriteSheet tex, Place p) {
        this.cam = null;
        this.tile = tile;
        this.tex = tex;
        place = p;
        onTop = true;
        alwaysVisible = true;
    }

    public void setSpriteSheet(SpriteSheet tex) {
        this.tex = tex;
        coord.setX(0);
        coord.setY(0);
    }

    public void changeCoordinates(int x, int y) {
        int xLim = Methods.interval(0, coord.getX() + x, tex.getXlimit() - 1);
        int yLim = Methods.interval(0, coord.getY() + y, tex.getYlimit() - 1);
        coord.set(xLim, yLim);
    }

    public SpriteSheet getSpriteSheet() {
        return tex;
    }
    
    public Point getCoordinates() {
        return coord;
    }
    
    public void setCamera(Camera cam) {
        this.cam = cam;
    }

    public void setChange(boolean ch) {
        change = ch;
    }
    
    public boolean isChanged() {
        return change;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (cam != null) {
            glPushMatrix();
            int d = 2;
            int xStart = tex.getSx();
            int yStart = tex.getSy();
            int wTex = tex.getWidth();
            int hTex = tex.getHeight();
            glTranslatef(cam.getSX() + tile / 2 + xEffect, cam.getSY() + tile / 2 + yEffect, 0);

            if (change) {
                glColor4f(1f, 1f, 1f, 0.7f);
                glTranslatef(-xStart - coord.getX() * wTex, -yStart - coord.getY() * hTex, 0);
                tex.render();
                glTranslatef(coord.getX() * wTex, coord.getY() * hTex, 0);
            }

            glColor4f(1f, 1f, 1f, 1f);
            Drawer.drawRectangle(0, 0, wTex, hTex);

            glTranslatef(-xStart, -yStart, 0);
            tex.renderPiece(coord.getX(), coord.getY());

            glColor4f(0f, 0f, 0f, 1f);
            Drawer.drawRectangle(-d, -d, wTex + 2 * d, d - 1);
            Drawer.drawRectangle(0, hTex + d + 1, wTex + 2 * d, d - 1);
            Drawer.drawRectangle(0, -hTex - 2, d - 1, hTex + 2);
            Drawer.drawRectangle(wTex + d + 1, 0, d - 1, hTex + 2);

            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        } else {
            ObjectPlayer pl = (ObjectPlayer) place.players[0];
            cam = pl.getCam();
            pl.addUI(this);
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f, int xs, int xe) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f, int xs, int xe) {
    }

}
