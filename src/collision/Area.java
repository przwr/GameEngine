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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.jodk.lang.FastMath;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Wojtek
 */
public class Area extends GameObject {

    private static final Whole WHOLE = new Whole();
    private static final Chunks CHUNKS = new Chunks();
    private final boolean border;
    private int centralX, centralY;
    private final Integration type;
    private final ArrayList<Figure> parts = new ArrayList<>();
    private int maxX, maxY, minX, minY, shadowHeight;
    private int tempX, tempY, tempShadowHeight;

    public static Area createBorder(int x, int y, int tileSize) {
        return new Area(x, y, true, false);
    }

    public static Area createWhole(int x, int y, int tileSize) {
        return new Area(x, y, false, true);
    }

    public static Area createInChunks(int x, int y, int tileSize) {
        return new Area(x, y, false, false);
    }

    private Area(int x, int y, boolean border, boolean whole) {  //Najlepiej było by gdyby punkt (x, y) był w górnym lewym rogu całego pola
        this.x = x;
        this.y = y;
        this.border = border;
        type = whole ? WHOLE : CHUNKS;
        solid = true;
    }

    public void addFigure(Figure figure) {
        parts.add(figure);
        type.upProperties(this, figure);
    }

    public void addPiece(GameObject go) {
        Figure figure = go.getCollision();
        parts.add(figure);
        if (go.isSolid()) {
            type.upProperties(this, figure);
        }
    }

    protected void upCollision() {
        maxX = maxY = shadowHeight = 0;
        minX = minY = Integer.MAX_VALUE;
        parts.stream().forEach((part) -> {
            recalculateCollsion(part);
        });
        collision = Rectangle.createShadowHeight(minX - getX(), minY - getY(), maxX - minX, maxY - minY, OpticProperties.FULL_SHADOW, shadowHeight, this);
    }

    private void recalculateCollsion(Figure part) {
        if (part.getOwner().isSolid()) {
            upShadowHeight(part);
            Collection<Point> points = part.getPoints();
            points.stream().forEach((point) -> {
                findCorners(point);
            });
        }
    }

    private void upShadowHeight(Figure part) {
        tempShadowHeight = part.getShadowHeight();
        if (tempShadowHeight != 0) {
            shadowHeight = tempShadowHeight;
        }
    }

    private void findCorners(Point point) {
        tempX = point.getX();
        tempY = point.getY();
        if (tempX > maxX) {
            maxX = tempX;
        }
        if (tempY > maxY) {
            maxY = tempY;
        }
        if (tempX < minX) {
            minX = tempX;
        }
        if (tempY < minY) {
            minY = tempY;
        }
    }

    protected void upBoundsForWhole() {
        width = collision.width;
        height = collision.height;
    }

    protected void upBoundsForChunks(Figure figure) {
        int newWidth = figure.getXStart() + figure.getWidth();
        int newHeight = figure.getYStart() + figure.getHeight();
        if (width < newWidth) {
            width = newWidth;
        }
        if (height < newHeight) {
            height = newHeight;
        }
    }

    protected void upCenter() {
        centralX = width / 2;
        centralY = height / 2;
    }

    public boolean isCollide(int x, int y, Figure figure) {
        if (isClose(x, y, figure)) {
            return type.isCollide(this, x, y, figure);
        }
        return false;
    }

    protected boolean isCollideWhole(int x, int y, Figure figure) {
        return figure.isCollideSingle(x, y, collision);
    }

    protected boolean isCollideChunks(int x, int y, Figure figure) {
        if (parts.stream().anyMatch((part) -> (figure.isCollideSingle(x, y, part)))) {
            return true;
        }
        return false;
    }

    public Figure whatCollide(int x, int y, Figure figure) {
        if (isClose(x, y, figure)) {
            return type.whatCollide(this, x, y, figure);
        }
        return null;
    }

    protected Figure whatCollideWhole(int x, int y, Figure figure) {
        if (figure.isCollideSingle(x, y, collision)) {
            return collision;
        }
        return null;
    }

    protected Figure whatCollideChunks(int x, int y, Figure figure) {
        for (Figure part : parts) {
            if (figure.isCollideSingle(x, y, part)) {
                return part;
            }
        }
        return null;
    }

    private boolean isClose(int x, int y, Figure figure) {
        int dx = FastMath.abs(getX() + centralX - figure.getCentralX(x));
        int dy = FastMath.abs(getY() + centralY - figure.getCentralY(y));
        return (dx <= (centralX + figure.getWidth()) && dy <= (centralY + figure.getHeight()));
    }

    public Figure deleteFigure(int index) {
        return parts.remove(index);
    }

    public Collection<Point> getPoints() {
        ArrayList<Point> temp = new ArrayList<>();
        parts.stream().map((part) -> part.getPoints()).forEach((points) -> {
            points.stream().filter((p) -> (p != null && !temp.contains(p))).forEach((p) -> {
                temp.add(p);
            });
        });
        return temp;
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure) {
        glPushMatrix();
        glTranslatef(figure.getX() + xEffect, figure.getY() - figure.getShadowHeight() + yEffect, 0);
        Drawer.drawRectangleInShade(0, 0, figure.width, figure.height + figure.getShadowHeight(), color);
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f) {
        glPushMatrix();
        glTranslatef(f.getX() + xEffect, f.getY() - f.getShadowHeight() + yEffect, 0);
        Drawer.drawRectangleInBlack(0, 0, f.width, f.height + f.getShadowHeight());
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure, int xStart, int yStart) {
        glPushMatrix();
        glTranslatef(figure.getX() + xEffect, figure.getY() - figure.getShadowHeight() + yEffect, 0);
        Drawer.drawRectangleInShade(0, 0, figure.width, figure.height + figure.getShadowHeight(), color);
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f, int xStart, int yStart) {
        glPushMatrix();
        glTranslatef(f.getX() + xEffect, f.getY() - f.getShadowHeight() + yEffect, 0);
        Drawer.drawRectangleInBlack(0, 0, f.width, f.height + f.getShadowHeight());
        glPopMatrix();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }

    public boolean isBorder() {
        return border;
    }

    public boolean isWhole() {
        return type.isWhole();
    }

    public List<Figure> getParts() {
        return Collections.unmodifiableList(parts);
    }

    public Figure getFigure(int index) {
        return parts.get(index);
    }
}
