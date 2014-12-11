/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Drawer;
import engine.Point;
import game.gameobject.GameObject;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 *
 * @author Wojtek
 */
public class Area extends GameObject {

    public ArrayList<Figure> parts;
    public boolean isWhole;
    protected int xCentr;
    protected int yCentr;
    protected int sTile;
    protected boolean isBorder;

    public Area(int x, int y, int sTile) {     //Najlepiej było by gdyby punkt (x, y) był w górnym lewym rogu całego pola
        this.x = x;
        this.y = y;
        this.sTile = sTile;
        this.parts = new ArrayList<>();
        solid = true;
        simpleLighting = true;
    }

    public Area(int x, int y, int sTile, boolean isBorder, boolean isWhole) {     //Najlepiej było by gdyby punkt (x, y) był w górnym lewym rogu całego pola
        this.x = x;
        this.y = y;
        this.parts = new ArrayList<>();
        solid = true;
        simpleLighting = true;
        this.isBorder = isBorder;
        this.isWhole = isWhole;
    }

    public void addFigure(Figure f) {
        if (width < f.getXs() + f.getWidth()) {
            width = f.getXs() + f.getWidth() * 2;
            xCentr = (int) x + width / 2;
        }
        if (height < f.getYs() + f.getHeight()) {
            height = f.getYs() + f.getHeight() * 2;
            yCentr = (int) y + height / 2;
        }
        parts.add(f);
        if (isWhole) {
            upCollision();
        }
    }

    public void addPiece(GameObject g) {
        Figure f = g.getCollision();
        parts.add(f);
        if (g.isSolid()) {
            if (width < f.getXs() + f.getWidth()) {
                width = f.getXs() + f.getWidth() * 2;
                xCentr = (int) x + width / 2;
            }
            if (height < f.getYs() + f.getHeight()) {
                height = f.getYs() + f.getHeight() * 2;
                yCentr = (int) y + height / 2;
            }
            if (isWhole) {
                upCollision();
            }
        }
    }

    public Figure getFigure(int i) {
        return parts.get(i);
    }

    public Figure deleteFigure(int i) {
        return parts.remove(i);
    }

    public boolean ifCollide(int x, int y, Figure f) {
        //if (ifGoodDistance(x, y, f)) {
        for (Figure part : parts) {
            if (f.ifCollideSngl(x, y, part)) {
                return true;
            }
        }
        //}
        return false;
    }

    public Figure whatCollide(int x, int y, Figure f) {
        //if (ifGoodDistance(x, y, f)) {
        for (Figure part : parts) {
            if (f.ifCollideSngl(x, y, part)) {
                return part;
            }
        }
        //}
        return null;
    }

    public boolean ifGoodDistance(int x, int y, Figure f) {
        int dx = Math.abs(xCentr - f.getCentralX(x));
        int dy = Math.abs(yCentr - f.getCentralY(y));
        return (dx <= (getWidth() + f.getWidth()) / 2 && dy <= (getWidth() + f.getWidth()) / 2);
    }

    private void upCollision() {
        int maxX = 0, maxY = 0, minX = 2147483647, minY = 2147483647, shadowH = 0;
        int x, y, sH;
        for (Figure part : parts) {
            if (part.getOwner().isSolid()) {
                sH = part.shadowHeight();
                shadowH = sH != 0 ? sH : shadowH;
                Point[] pList = part.listPoints();
                for (Point p : pList) {
                    x = p.getX();
                    y = p.getY();
                    maxX = maxX > x ? maxX : x;
                    maxY = maxY > y ? maxY : y;
                    minX = minX < x ? minX : x;
                    minY = minY < y ? minY : y;
                }
            }
        }
        collision = new Rectangle(minX - getX(), minY - getY(), maxX - minX, maxY - minY, true, true, shadowH, this);
    }

    public Point[] listPoints() {
        ArrayList<Point> temp = new ArrayList<>();
        for (Figure part : parts) {
            Point[] pList = part.listPoints();
            for (Point p : pList) {
                if (p != null && !temp.contains(p)) {
                    temp.add(p);
                }
            }
        }
        return (Point[]) temp.toArray();
    }

    @Override
    public void render(int xEffect, int yEffect) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f) {
        glPushMatrix();
        glTranslatef(f.getX() + xEffect, f.getY() - f.shadowHeight() + yEffect, 0);
        if (simpleLighting) {
            if (isLit) {
                glColor4f(color, color, color, 1f);
            } else {
                glColor4f(0f, 0f, 0f, 1f);
            }
            Drawer.drawRectangle(0, 0, f.width, f.height + f.shadowHeight());
            glColor4f(1f, 1f, 1f, 1f);
        } else if (sprite != null) {
            glEnable(GL_TEXTURE_2D);
            if (isLit) {
                Drawer.drawShapeInColor(sprite, color, color, color, 1);
            } else {
                Drawer.drawShapeInBlack(sprite);
            }
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    public boolean isBorder() {
        return isBorder;
    }

    public ArrayList<Figure> getParts() {
        return parts;
    }
}
