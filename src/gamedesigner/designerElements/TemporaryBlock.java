/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.designerElements;

import collision.Block;
import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.place.Place;
import game.place.map.ForegroundTile;
import game.place.map.Map;
import game.place.map.Tile;
import gamedesigner.ObjectMap;
import gamedesigner.ObjectPlace;
import sprites.SpriteSheet;

import java.util.ArrayList;

import static collision.OpticProperties.TRANSPARENT;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class TemporaryBlock extends GameObject {

    final int tile;
    final int width;
    final int height;
    final int upHeight;
    final int yTiles;
    final ObjectPlace objPlace;
    private final int xTiles;
    private final ObjectMap objMap;
    private final ArrayList<ForegroundTile> tiles;
    Block block;
    boolean blocked;

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

        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);

        glTranslatef(getX(), getY(), 0);
        int mode = objPlace.getMode();
        if (mode != 2 && (objPlace.isBlocksMode() || mode == 1)) {
            int d = 2;
            Drawer.refreshColor();
            int tmpH = upHeight * tile;
            if (!blocked) {
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
            } else {
                glColor3f(1f, 0.5f, 0.5f);
                Drawer.drawRectangle(0, 0, width, d);
                Drawer.drawRectangle(0, height - d, width, d);
                Drawer.drawRectangle(0, -height + d, d, height);
                Drawer.drawRectangle(width - d, 0, d, height);
            }
        }
        Drawer.refreshForRegularDrawing();
        glPopMatrix();
    }

    public void move(int dx, int dy) {
        if (block != null) {
            x += dx;
            y += dy;
            block.move(dx, dy);
        }
    }

    public boolean isNotBlocked() {
        return !blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
        block.setVisible(!blocked);
    }

    public boolean checkIfContainsTile(int x, int y) {
        return tiles.stream().anyMatch((fgt) -> ((fgt.getX() / tile) == x && (fgt.getY() / tile) == y));
    }

    public ForegroundTile removeTile(int x, int y) {
        ForegroundTile tmp = null;
        int maxDepth = 0;
        for (ForegroundTile fgt : tiles) {
            if ((fgt.getX() / tile) == x && (fgt.getY() / tile) == y && fgt.getDepth() > maxDepth) {
                tmp = fgt;
                maxDepth = fgt.getDepth();
            }
        }
        if (tmp != null) {
            Point p = tmp.popTileFromStack();
            if (tmp.tileStackSize() != 0) {
                return null;
            }
            tiles.remove(tmp);
        }
        return tmp;
    }

    public boolean checkIfContains(int x, int y) {
        int xBegin = (int) (this.x / tile);
        int yBegin = (int) (this.y / tile) - upHeight;
        int xEnd = xBegin + xTiles - 1;
        int yEnd = yBegin + yTiles + upHeight - 1;
        return !(x > xEnd || x < xBegin || y > yEnd || y < yBegin);
    }

    public boolean checkIfBaseContains(int x, int y) {
        int xBegin = (int) (this.x / tile);
        int yBegin = (int) (this.y / tile);
        int xEnd = xBegin + xTiles - 1;
        int yEnd = yBegin + yTiles - 1;
        return !(x > xEnd || x < xBegin || y > yEnd || y < yBegin);
    }

    public void addTile(ForegroundTile fgt) {
        map.addForegroundTile(fgt);
        tiles.add(fgt);
        if (block == null) {
            createBlock();
        }
        block.addForegroundTile(fgt);
    }

    public boolean isInvisible() {
        return upHeight == 0;
    }
    
    public ForegroundTile addTile(int x, int y, int xSheet, int ySheet, SpriteSheet tex, boolean altMode) {
        int yBegin = (int) (this.y / tile) - upHeight;
        int yEnd = yBegin + yTiles + upHeight - 1;

        if (!altMode) {
            ForegroundTile fgt;
            for (int i = tiles.size() - 1; i >= 0; i--) {
                fgt = tiles.get(i);
                if ((fgt.getX() / tile) == x && (fgt.getY() / tile) == y) {
                    fgt.addTileToStack(xSheet, ySheet);
                    return fgt;
                }
            }
        }
        ForegroundTile fgt;
        int level = yEnd - y;
        int maxLevel = Math.min(level, upHeight + 1);
        fgt = createTile(tex, y, tile, xSheet, ySheet, level, altMode);
        if (altMode) {
            map.addForegroundTile(fgt, x * tile, y * tile, (maxLevel) * tile);
            fgt.setDepth(-1);
        } else {
            map.addForegroundTile(fgt, x * tile, y * tile, (maxLevel) * tile);
        }
        tiles.add(fgt);
        if (block == null) {
            createBlock();
        }
        block.addForegroundTile(fgt);
        return fgt;
    }

    public void createBlock() {
        block = Block.create((int) x, (int) y, width, height, (upHeight - yTiles) * tile);
        if (upHeight == 0) {
            block.getCollision().setOpticProperties(TRANSPARENT);
        }
        map.addBlock(block);
    }

    ForegroundTile createTile(SpriteSheet texture, int y, int tile, int xSheet, int ySheet, int level, boolean altMode) {
        if (level + 1 <= upHeight) {
            return ForegroundTile.createWall(texture, tile, xSheet, ySheet);
        } else {
            return ForegroundTile.createOrdinaryShadowHeight(texture, tile, xSheet, ySheet, level * tile);
        }
    }

    public void changeEnvironment() {
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
                        if (p != null) {
                            fgt = createTile(t.getSpriteSheet(), iy, tile, p.getX(), p.getY(), level, false);
                            while ((p = t.popTileFromStackBack()) != null) {
                                fgt.addTileToStack(p.getX(), p.getY());
                            }
                            map.addForegroundTileAndReplace(fgt, ix * tile, iy * tile, Math.min(level, upHeight) * tile);
                            tiles.add(fgt);
                            block.addForegroundTile(fgt);
                            objMap.deleteTile(ix, iy);
                        }
                    }
                }
                level++;
            }
        }
    }

    public void decompose() {
        tiles.stream().forEach((fgt) -> {
            Point p = fgt.popTileFromStackBack();
            Tile t = new Tile(fgt.getSpriteSheet(), p.getX(), p.getY());
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

    public void clear() {
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

    public Block getBlock() {
        return block;
    }

    @Override
    public int getYSpriteBegin() {
        return super.getYSpriteBegin() - upHeight * tile;
    }

    @Override
    public int getYSpriteEnd() {
        return super.getYSpriteEnd() + yTiles * tile;
    }

    @Override
    public int getXSpriteEnd() {
        return super.getXSpriteEnd() + xTiles * tile;
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
