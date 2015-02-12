/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.Drawer;
import engine.Methods;
import engine.Point;
import game.Settings;
import game.gameobject.GUIObject;
import game.place.Place;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ObjectUI extends GUIObject {

    private final int tile;
    private SpriteSheet tex;
    private boolean change;
    private String txt;

    private final Point coord = new Point(0, 0);

    public ObjectUI(int tile, SpriteSheet tex, Place p) {
        super("OUI", p);
        this.tile = tile;
        this.tex = tex;
        txt = "";
    }

    public void setSpriteSheet(SpriteSheet tex) {
        this.tex = tex;
        coord.setX(0);
        coord.setY(0);
    }

    public void changeCoordinates(int x, int y) {
        int xLim = coord.getX() + x;
        int yLim = coord.getY() + y;
        if (xLim < 0) {
            xLim = tex.getXlimit() - 1;
        }
        if (xLim > tex.getXlimit() - 1) {
            xLim = 0;
        }
        if (yLim < 0) {
            yLim = tex.getYlimit() - 1;
        }
        if (yLim > tex.getYlimit() - 1) {
            yLim = 0;
        }
        coord.set(xLim, yLim);
    }

    public SpriteSheet getSpriteSheet() {
        return tex;
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
        if (camera != null) {
            glPushMatrix();
            int d = 2;
            int xStart = tex.getXStart();
            int yStart = tex.yStart();
            int wTex = tex.getWidth();
            int hTex = tex.getHeight();
            if (Settings.scaled) {
                glScalef(Settings.scale, Settings.scale, 1);
            }
            
            txt = Methods.editWithKeyboard(txt);
                        
            place.renderMessage(0, tile * 3, tile * 3, "KEY: " + txt, new Color(1f, 1f, 1f));
            glTranslatef(tile / 2 + xEffect, tile / 2 + yEffect, 0);

            if (change) {
                glTranslatef(tile * 4, tile * 4, 0);
                glColor4f(1f, 1f, 1f, 1f);
                glTranslatef(-xStart - coord.getX() * wTex, -yStart - coord.getY() * hTex, 0);
                tex.render();
                glTranslatef(coord.getX() * wTex, coord.getY() * hTex, 0);
            }

            glColor4f(1f, 1f, 1f, 1f);
            glTranslatef(-1, -1, 0);
            Drawer.drawRectangle(0, 0, wTex + 2, hTex + 2);

            glTranslatef(-xStart + 1, -yStart + 1, 0);
            tex.renderPiece(coord.getX(), coord.getY());

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
