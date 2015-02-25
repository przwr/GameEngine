/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Block;
import collision.Figure;
import engine.Drawer;
import engine.Point;
import game.Settings;
import game.gameobject.GameObject;
import game.place.ForegroundTile;
import game.place.Map;
import game.place.Place;
import game.place.Tile;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class TemporaryBlock extends GameObject {

    private final int tile;
    private final int upHeight, xTiles, yTiles;
    private final ObjectPlace objPlace;
    private final ObjectMap objMap;
    private boolean complete;

    private Block area;
    private ArrayList<ForegroundTile> tiles;

    public TemporaryBlock(int x, int y, int upHeight, int width, int height, Map map, Place place) {
        initialize("tmpBlock", x, y);
        this.tile = map.getTileSize();
        this.upHeight = upHeight;
        this.width = width * tile;
        this.height = height * tile;
        xTiles = width;
        yTiles = height;
        this.map = map;
        onTop = true;
        objMap = (ObjectMap) map;
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
        glTranslatef(xEffect, yEffect, 0);
        if (Settings.scaled) {
            glScaled(Settings.scale, Settings.scale, 1);
        }
        glTranslatef(getX(), getY(), 0);
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
        Drawer.refreshForRegularDrawing();
        glPopMatrix();
    }

    public ForegroundTile removeTile(int x, int y) {
        for (ForegroundTile fgt : tiles) {
            if ((fgt.getX() / tile) == x && (fgt.getY() / tile) == y) {
                Point p = fgt.popTileFromStack();
                if (p != null && fgt.tileStackSize() != 0) {
                    return null;
                }
                tiles.remove(fgt);
                return fgt;
            }
        }
        return null;
    }

    public boolean checkTile(int x, int y) {
        int xBegin = (int) (this.x / tile);
        int yBegin = (int) (this.y / tile) - upHeight;
        int xEnd = xBegin + xTiles - 1;
        int yEnd = yBegin + yTiles + upHeight - 1;
        return !(x > xEnd || x < xBegin || y > yEnd || y < yBegin);
    }

    public void addTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex) {
        int yBegin = (int) (this.y / tile) - upHeight;
        int yEnd = yBegin + yTiles + upHeight - 1;

        for (ForegroundTile fgt : tiles) {
            if ((fgt.getX() / tile) == x && (fgt.getY() / tile) == y) {
                fgt.addTileToStack(xSheet, ySheet);
                return;
            }
        }
        ForegroundTile fgt;
        int level = yEnd - y;
        if (level + 1 <= upHeight) {
            fgt = ForegroundTile.createWall(tex, tile, xSheet, ySheet);
        } else {
            fgt = ForegroundTile.createOrdinaryShadowHeight(tex, tile, xSheet, ySheet, level * tile);
        }
        map.addForegroundTileAndReplace(fgt, x * tile, y * tile, level * tile);
        tiles.add(fgt);
        area.addForegroundTile(fgt);
    }

    public void changeEnvironment() {
        tiles = new ArrayList<>();
        area = new Block((int) x, (int) y, width, height, (upHeight - yTiles) * tile);
        if (upHeight > 0) {
            int xBegin = (int) (x / tile);
            int yBegin = (int) (y / tile) - upHeight;
            int xEnd = xBegin + xTiles - 1;
            int yEnd = yBegin + yTiles + upHeight - 1;
            int level = 0;
            ForegroundTile fgt;
            Tile t;
            Point p;
            for (int iy = yEnd; iy >= yBegin; iy--) {
                for (int ix = xBegin; ix <= xEnd; ix++) {
                    t = map.getTile(ix, iy);
                    if (t != null && t.getPureDepth() != -1) {
                        p = t.popTileFromStackBack();
                        if (level + 1 <= upHeight) {
                            fgt = ForegroundTile.createWall(
                                    t.getSpriteSheet(),
                                    tile,
                                    p.getX(),
                                    p.getY());
                        } else {
                            fgt = ForegroundTile.createOrdinaryShadowHeight(
                                    t.getSpriteSheet(),
                                    tile,
                                    p.getX(),
                                    p.getY(),
                                    level * tile);
                        }
                        while ((p = t.popTileFromStackBack()) != null) {
                            fgt.addTileToStack(p.getX(), p.getY());
                        }
                        map.addForegroundTileAndReplace(fgt, ix * tile, iy * tile, level * tile);
                        tiles.add(fgt);
                        area.addForegroundTile(fgt);
                        objMap.removeTile(ix, iy);
                    }
                }
                level++;
            }
        }
        map.addArea(area);
    }

    public void clearMyself() {
        tiles.stream().forEach((fgt) -> {
            Point p = fgt.popTileFromStackBack();
            Tile t = new Tile(fgt.getSpriteSheet(), tile, p.getX(), p.getY());
            while ((p = fgt.popTileFromStackBack()) != null) {
                t.addTileToStack(p.getX(), p.getY());
            }
            map.deleteForegroundTile(fgt);
            map.setTile(fgt.getX() / tile, fgt.getY() / tile, t);
        });
        map.deleteArea(area);
        tiles.clear();
        area = null;
    }

    public boolean checkCollision(int x, int y, int width, int height) {
        return ((this.x > x && this.x - x < width) || (this.x <= x && x - this.x < this.width))
                && ((this.y > y && this.y - y < height) || (this.y <= y && y - this.y < this.height));
    }

    public String saveToString(int xBegin, int yBegin, int tile) {
        return "tb:" + ((int) (x - xBegin) / tile) + ":" + ((int) (y - yBegin) / tile) + ":"
                + (width / tile) + ":" + (height / tile) + ":" + upHeight;
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
