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

    protected final int tile;
    protected final int upHeight, xTiles, yTiles;
    protected final ObjectPlace objPlace;
    protected final ObjectMap objMap;
    protected boolean complete;

    protected Block block;
    protected ArrayList<ForegroundTile> tiles;

    public TemporaryBlock(int x, int y, int upHeight, int width, int height, Map map) {
        this.initialize("tmpBlock", x, y);
        this.tile = map.getTileSize();
        this.upHeight = upHeight;
        this.width = width * tile;
        this.height = height * tile;
        xTiles = width;
        yTiles = height;
        this.map = map;
        onTop = true;
        objMap = (ObjectMap) map;
        objPlace = (ObjectPlace) map.place;
        tiles = new ArrayList<>();
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
                glColor3f(1f, 0f, 0f);
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
                glColor3f(1f, 0f, 0f);
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

    public ForegroundTile addTile(ForegroundTile fgt) {        
        map.addForegroundTile(fgt);
        tiles.add(fgt);
        block.addForegroundTile(fgt);
        return fgt;
    }
    
    public ForegroundTile addTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex, boolean addNew) {
        int yBegin = (int) (this.y / tile) - upHeight;
        int yEnd = yBegin + yTiles + upHeight - 1;

        if (!addNew) {
            for (ForegroundTile fgt : tiles) {
                if ((fgt.getX() / tile) == x && (fgt.getY() / tile) == y) {
                    fgt.addTileToStack(xSheet, ySheet);
                    return fgt;
                }
            }
        }
        ForegroundTile fgt;
        int level = yEnd - y;
        fgt = createTile(tex, y, tile, xSheet, ySheet, level);
        if (addNew) {
            map.addForegroundTile(fgt, x * tile, y * tile, (level + 1) * tile);
            fgt.setDepth(fgt.getPureDepth() + tile);
        } else {
            map.addForegroundTileAndReplace(fgt, x * tile, y * tile, (level + 1) * tile);
        }
        System.out.println(fgt.getPureDepth());
        tiles.add(fgt);
        block.addForegroundTile(fgt);
        return fgt;
    }

    public void createBlock() {
        block = Block.create((int) x, (int) y, width, height, (upHeight - yTiles) * tile);
    }

    protected ForegroundTile createTile(SpriteSheet texture, int x, int tile, int xSheet, int ySheet, int level) {
        if (level + 1 <= upHeight) {
            return ForegroundTile.createWall(texture, tile, xSheet, ySheet);
        } else {
            return ForegroundTile.createOrdinaryShadowHeight(texture, tile, xSheet, ySheet, level * tile);
        }
    }

    public void changeEnvironment() {
        createBlock();
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
                        fgt = createTile(t.getSpriteSheet(), iy, tile, p.getX(), p.getY(), level);
                        while ((p = t.popTileFromStackBack()) != null) {
                            fgt.addTileToStack(p.getX(), p.getY());
                        }
                        map.addForegroundTileAndReplace(fgt, ix * tile, iy * tile, level * tile);
                        tiles.add(fgt);
                        block.addForegroundTile(fgt);
                        objMap.removeTile(ix, iy);
                    }
                }
                level++;
            }
        }
        map.addBlock(block);
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
        map.deleteBlock(block);
        tiles.clear();
        block = null;
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
