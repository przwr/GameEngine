/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.Drawer;
import engine.Point;
import game.Settings;
import game.gameobject.GUIObject;
import game.place.Place;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ObjectUI extends GUIObject {

    private final int tile;
    private SpriteSheet texture;
    private boolean change;

    private final Point coord = new Point(0, 0);

    public ObjectUI(int tile, SpriteSheet tex, Place p) {
        super("OUI", p);
        this.tile = tile;
        this.texture = tex;
    }

    public void setSpriteSheet(SpriteSheet tex) {
        this.texture = tex;
        coord.setX(0);
        coord.setY(0);
    }

    public void changeCoordinates(int x, int y) {
        int xLim = coord.getX() + x;
        int yLim = coord.getY() + y;
        if (xLim < 0) {
            xLim = texture.getXlimit() - 1;
        }
        if (xLim > texture.getXlimit() - 1) {
            xLim = 0;
        }
        if (yLim < 0) {
            yLim = texture.getYlimit() - 1;
        }
        if (yLim > texture.getYlimit() - 1) {
            yLim = 0;
        }
        coord.set(xLim, yLim);
    }

    public SpriteSheet getSpriteSheet() {
        return texture;
    }

    public Point getCoordinates() {
        return coord;
    }

    public void setChange(boolean ch) {
        change = ch;
    }

    public boolean isChanged() {
        return change;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (player != null) {
            glPushMatrix();
            int d = 2;
            int xStart = texture.getXStart();
            int yStart = texture.yStart();
            int wTex = texture.getWidth();
            int hTex = texture.getHeight();
            if (Settings.scaled) {
                glScaled(Settings.scale, Settings.scale, 1);
            }

            glTranslatef(tile / 2 + xEffect, tile / 2 + yEffect, 0);

            if (change) {
                glTranslatef(tile * 4, tile * 4, 0);
                glColor4f(1f, 1f, 1f, 1f);
                glTranslatef(-xStart - coord.getX() * wTex, -yStart - coord.getY() * hTex, 0);
                texture.render();
                glTranslatef(coord.getX() * wTex, coord.getY() * hTex, 0);
            }

            glColor4f(1f, 1f, 1f, 1f);
            glTranslatef(-1, -1, 0);
            Drawer.drawRectangle(0, 0, wTex + 2, hTex + 2);

            glTranslatef(-xStart + 1, -yStart + 1, 0);
            texture.renderPiece(coord.getX(), coord.getY());

            glColor4f(0f, 0f, 0f, 1f);
            Drawer.drawRectangle(-d, -d, wTex + 2 * d, d - 1);
            Drawer.drawRectangle(0, hTex + d + 1, wTex + 2 * d, d - 1);
            Drawer.drawRectangle(0, -hTex - 2, d - 1, hTex + 2);
            Drawer.drawRectangle(wTex + d + 1, 0, d - 1, hTex + 2);

            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }
}
