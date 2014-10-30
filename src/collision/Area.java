/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import game.gameobject.GameObject;
import java.util.ArrayList;

/**
 *
 * @author Wojtek
 */
public class Area extends GameObject {

    private ArrayList<Figure> parts;
    protected int xCentr;
    protected int yCentr;

    public Area(int x, int y) {     //Najlepiej było by gdyby punkt (x, y) był w górnym lewym rogu całego pola
        this.x = x;
        this.y = y;
        this.parts = new ArrayList<>();
        solid = true;
    }

    public void addFigure(Figure f) {
        if (width < f.getXs() + f.getWidth()) {
            width = f.getXs() + f.getWidth();
            xCentr = (int) x + width / 2;
        }
        if (height < f.getYs() + f.getHeight()) {
            height = f.getYs() + f.getHeight();
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
        if (ifGoodDistance(x, y, f)) {
            for (Figure part : parts) {
                if (f.ifCollideSngl(x, y, part)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Figure whatCollide(int x, int y, Figure f) {
        if (ifGoodDistance(x, y, f)) {
            for (Figure part : parts) {
                if (f.ifCollideSngl(x, y, part)) {
                    return part;
                }
            }
        }
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
        System.err.println("Kick! Punch! It's all in the mind\n"
                + "If you wanna test me, I'm sure you'll find\n"
                + "The things I'll teach ya is sure to beat ya\n"
                + "But nevertheless you'll get a lesson from teacher");
    }
}