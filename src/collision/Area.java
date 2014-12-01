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
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 *
 * @author Wojtek
 */
public class Area extends GameObject {

    public ArrayList<Figure> parts;
    protected int xCentr;
    protected int yCentr;
    protected boolean isBorder;

    public Area(int x, int y, int sTile) {     //Najlepiej było by gdyby punkt (x, y) był w górnym lewym rogu całego pola
        this.x = x;
        this.y = y;
        this.parts = new ArrayList<>();
        solid = true;
        simpleLighting = true;
    }

    public Area(int x, int y, int sTile, boolean isBorder) {     //Najlepiej było by gdyby punkt (x, y) był w górnym lewym rogu całego pola
        this.x = x;
        this.y = y;
        this.parts = new ArrayList<>();
        solid = true;
        simpleLighting = true;
        this.isBorder = isBorder;
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
    }

    public Figure getFigure(int i) {
        return parts.get(i);
    }

    public Figure deleteFigure(int i) {
        return parts.remove(i);
    }

    public void setSolid(boolean s) {
        solid = s;
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
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        if (simpleLighting) {
            if (isLit) {
                glColor4f(color, color, color, 1f);
            } else {
                glColor4f(0f, 0f, 0f, 1f);
            }
            Drawer.drawRectangle(0, 0, parts.get(0).width, parts.get(0).height);
            glColor4f(1f, 1f, 1f, 1f);
        } else if (sprite != null) {
            if (isLit) {
                Drawer.drawShapeInColor(sprite, color, color, color, 1);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            } else {
                Drawer.drawShapeInBlack(sprite);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            }
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
