/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Drawer;
import engine.Main;
import engine.Point;
import game.gameobject.GameObject;
import game.place.ForegroundTile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Wojtek
 */
public class Block extends GameObject {

    private final ArrayList<Figure> top = new ArrayList<>(1);
    private final ArrayList<ForegroundTile> topForegroundTiles = new ArrayList<>();
    private final ArrayList<ForegroundTile> wallForegroundTiles = new ArrayList<>();
    private static Figure colision;

    public static Block create(int x, int y, int width, int height, int shadowHeight) {
        return new Block(x, y, width, height, shadowHeight, false);
    }

    public static Block createRound(int x, int y, int width, int height, int shadowHeight) {
        return new Block(x, y, width, height, shadowHeight, true);
    }

    private Block(int x, int y, int width, int height, int shadowHeight, boolean round) {  //Point (x, y) should be in left top corner of Block
        this.x = x;
        this.y = y;
        name = "area";
        solid = visible = true;
        if (round) {
            setCollision(RoundRectangle.createShadowHeight(0, 0, width, height, OpticProperties.FULL_SHADOW, shadowHeight, this));
        } else {
            setCollision(Rectangle.createShadowHeight(0, 0, width, height, OpticProperties.FULL_SHADOW, shadowHeight, this));
            top.add(Rectangle.createShadowHeight(0, 0, width, height, OpticProperties.IN_SHADE_NO_SHADOW, shadowHeight + height, this));
            simpleLighting = true;
        }
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        topForegroundTiles.stream().forEach((fgt) -> {
            fgt.setX(fgt.getX() + dx);
            fgt.setY(fgt.getY() + dy);
        });
        wallForegroundTiles.stream().forEach((fgt) -> {
            fgt.setX(fgt.getX() + dx);
            fgt.setY(fgt.getY() + dy);
        });
        if (!collision.isMobile()) {
            collision.updatePoints();
        }
        map.sortForegroundTiles();
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        topForegroundTiles.stream().forEach((fgt) -> {
            fgt.setVisible(visible);
        });
        wallForegroundTiles.stream().forEach((fgt) -> {
            fgt.setVisible(visible);
        });
    }

    public void setTop(Figure top) {
        this.top.clear();
        this.top.add(top);
        this.top.trimToSize();
    }

    public void addForegroundTile(ForegroundTile foregroundTile) {
        if (foregroundTile.isWall()) {
            wallForegroundTiles.add(foregroundTile);
        } else {
            topForegroundTiles.add(foregroundTile);
            if (collision instanceof RoundRectangle) {
                top.add(foregroundTile.getCollision());
            }
        }
        foregroundTile.setBlockPart(true);
    }

    public void removeForegroundTile(ForegroundTile foregroundTile) {
        wallForegroundTiles.remove(foregroundTile);
        topForegroundTiles.remove(foregroundTile);
        top.remove(foregroundTile.getCollision());
    }

    public void pushCorner(int corner, int xChange, int yChange) {
        if (collision instanceof RoundRectangle) {
            ((RoundRectangle) collision).pushCorner(corner, xChange, yChange);
        }
    }

    public boolean isCollide(int x, int y, Figure figure) {
        return figure.isCollideSingle(x, y, collision);
    }

    public Figure whatCollide(int x, int y, Figure figure) {
        if (figure.isCollideSingle(x, y, collision)) {
            return collision;
        }
        return null;
    }

    public Point getPushValueOfCorner(int corner) {
        if (collision instanceof RoundRectangle) {
            return ((RoundRectangle) collision).getPushValueOfCorner(corner);
        }
        return null;
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        glPushMatrix();
        if (isSimpleLighting()) {
            Drawer.drawRectangleInShade(figure.getX() + xEffect, figure.getY() - figure.getShadowHeight() + yEffect,
                    figure.width, figure.height + figure.getShadowHeight(), 1);
        } else {
            glTranslatef(xEffect + getX(), yEffect + getY(), 0);
            Drawer.setCentralPoint();
            wallForegroundTiles.stream().forEach((wall) -> {
                Figure col = wall.getCollision();
                Drawer.returnToCentralPoint();
                Drawer.translate(col.getX() - getX(), col.getY() - getY() - col.getShadowHeight());
                if (wall.isSimpleLighting()) {
                    Drawer.drawRectangleInShade(0, 0, col.width, col.height + col.getShadowHeight(), 1);
                } else {
                    Drawer.drawShapeInShade(wall, 1);
                }
            });

        }
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        glPushMatrix();
        if (isSimpleLighting()) {
            Drawer.drawRectangleInBlack(figure.getX() + xEffect, figure.getY() - figure.getShadowHeight() + yEffect,
                    figure.width, figure.height + (top.contains(figure) ? 0 : figure.getShadowHeight()));
        } else {
            glTranslatef(xEffect + getX(), yEffect + getY(), 0);
            Drawer.setCentralPoint();
            wallForegroundTiles.stream().forEach((wall) -> {
                colision = wall.getCollision();
                Drawer.returnToCentralPoint();
                Drawer.translate(colision.getX() - getX(), colision.getY() - getY() - colision.getShadowHeight());
                if (wall.isSimpleLighting()) {
                    Drawer.drawRectangleInBlack(0, 0, colision.width, colision.height + colision.getShadowHeight());
                } else {
                    Drawer.drawShapeInBlack(wall);
                }
            });
        }
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
        glPushMatrix();
        if (isSimpleLighting() || !collision.isBottomRounded()) {
            if (Main.DEBUG) {
                System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
            }
        } else {
            glTranslatef(xEffect + getX(), yEffect + getY(), 0);
            Drawer.setCentralPoint();
            wallForegroundTiles.stream().forEach((wall) -> {
                colision = wall.getCollision();
                Drawer.returnToCentralPoint();
                Drawer.translate(colision.getX() - getX(), colision.getY() - getY() - colision.getShadowHeight());
                if (wall.isSimpleLighting()) {
                    Drawer.drawRectangleInShade(xStart, 0, xEnd - xStart, colision.height + colision.getShadowHeight(), 1);
                } else {
                    Drawer.drawShapePartInShade(wall, 1, xStart, xEnd);
                }
            });
        }
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
        glPushMatrix();
        if (isSimpleLighting()) {
            if (Main.DEBUG) {
                System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
            }
        } else {
            glTranslatef(xEffect + getX(), yEffect + getY(), 0);
            Drawer.setCentralPoint();
            wallForegroundTiles.stream().forEach((wall) -> {
                colision = wall.getCollision();
                Drawer.returnToCentralPoint();
                Drawer.translate(colision.getX() - getX(), colision.getY() - getY() - colision.getShadowHeight());
                if (wall.isSimpleLighting()) {
                    Drawer.drawRectangleInBlack(xStart, 0, xEnd - xStart, colision.height + colision.getShadowHeight());
                } else {
                    Drawer.drawShapePartInBlack(wall, xStart, xEnd);
                }
            });
        }
        glPopMatrix();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    //b:x:y:width:height:shadowHeight:round
    public String saveToString(int xBegin, int yBegin, int tile) {
        String string = ((collision instanceof RoundRectangle) ? "rb:" : "b:") + ((int) (x - xBegin) / tile) + ":" + ((int) (y - yBegin) / tile) + ":"
                + (collision.width / tile) + ":" + (collision.height / tile) + ":" + (collision.getShadowHeight() / tile) + ":"
                + (simpleLighting ? "0" : "1");
        if (collision instanceof RoundRectangle) {
            for (int i = 0; i < 4; i++) {
                Point temp = getPushValueOfCorner(i);
                string += ":" + (temp.getX() != 0 ? temp.getX() : "") + ":" + (temp.getY() != 0 ? temp.getY() : "");
            }
            string += ":0";
        }
        return string;
    }

    public Collection<ForegroundTile> getTopForegroundTiles() {
        return Collections.unmodifiableCollection(topForegroundTiles);
    }

    public Collection<ForegroundTile> getWallForegroundTiles() {
        return Collections.unmodifiableCollection(wallForegroundTiles);
    }

    public Collection<Figure> getTop() {
        return Collections.unmodifiableCollection(top);
    }

    public Collection<Point> getPoints() {
        return collision.getPoints();
    }
}
