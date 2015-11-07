/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.utilities.Drawer;
import engine.utilities.Point;
import game.Settings;
import game.gameobject.GUIObject;
import game.place.Place;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class ObjectUI extends GUIObject {

    private final int tile;
    private final Point coordinates = new Point(0, 0);
    private final Point playerPosition = new Point(0, 0);
    private final Point selection = new Point(1, 1);
    private SpriteSheet texture;
    private boolean change;
    private int mode;

    public ObjectUI(int tile, SpriteSheet tex, Place p) {
        super("OUI", p);
        this.tile = tile;
        this.texture = tex;
        mode = 0;
    }

    public void changeCoordinates(int x, int y) {
        int xLim = coordinates.getX() + x;
        int yLim = coordinates.getY() + y;
        if (xLim < 0) {
            xLim = texture.getXLimit() - 1;
        }
        if (xLim > texture.getXLimit() - 1) {
            xLim = 0;
        }
        if (yLim < 0) {
            yLim = texture.getYLimit() - 1;
        }
        if (yLim > texture.getYLimit() - 1) {
            yLim = 0;
        }
        coordinates.set(xLim, yLim);
    }

    public void setCursorStatus(int x, int y, int xs, int ys) {
        playerPosition.set(x, y);
        selection.set(xs, ys);
    }

    public SpriteSheet getSpriteSheet() {
        return texture;
    }

    public void setSpriteSheet(SpriteSheet tex) {
        this.texture = tex;
        coordinates.setX(0);
        coordinates.setY(0);
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setChange(boolean ch) {
        change = ch;
    }

    public boolean isChanged() {
        return change;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (player != null) {
            glPushMatrix();
            int d = 2;
            int xStart = texture.getXStart();
            int yStart = texture.getYStart();
            int wTex = texture.getWidth();
            int hTex = texture.getHeight();

            glScaled(Settings.nativeScale, Settings.nativeScale, 1);
            Drawer.translate(tile / 2, tile / 2);
            Drawer.setCentralPoint();
            if (mode == ObjectPlace.MODE_TILE) {
                if (change) {
                    Drawer.translate(player.getCamera().getWidthHalf() / 2, player.getCamera().getHeightHalf() / 2);
                    glColor4f(1f, 1f, 1f, 1f);
                    Drawer.translate(-xStart - coordinates.getX() * wTex, -yStart - coordinates.getY() * hTex);
                    texture.render();
                    Drawer.translate(coordinates.getX() * wTex, coordinates.getY() * hTex);
                }

                glColor4f(1f, 1f, 1f, 1f);
                Drawer.drawRectangle(-1, -1, wTex + 2, hTex + 2);

                Drawer.translate(-xStart + 1, -yStart + 1);
                texture.renderPiece(coordinates.getX(), coordinates.getY());

                glColor4f(0f, 0f, 0f, 1f);
                Drawer.drawRectangle(-d, -d, wTex + 2 * d, d - 1);
                Drawer.drawRectangle(0, hTex + d + 1, wTex + 2 * d, d - 1);
                Drawer.drawRectangle(0, -hTex - 2, d - 1, hTex + 2);
                Drawer.drawRectangle(wTex + d + 1, 0, d - 1, hTex + 2);
            }
            if (mode != ObjectPlace.MODE_VIEWING) {
                Drawer.returnToCentralPoint();
                glScaled(1 / Settings.nativeScale, 1 / Settings.nativeScale, 1);
                Drawer.renderString(playerPosition.getX() + ":" + playerPosition.getY() + " - "
                        + selection.getX() + ":" + selection.getY(), tile, 0, place.standardFont, new Color(1f, 1f, 1f));
            }
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }
}
