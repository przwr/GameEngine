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
    private final ArrayList<Figure> parts = new ArrayList<>();
    private final boolean border;
    private final Integration type;
    private int centralX, centralY;

    public static Area createBorder(int x, int y, int tileSize) {
        return new Area(x, y, true, false);
    }

    public static Area createWhole(int x, int y, int tileSize) {
        return new Area(x, y, false, true);
    }

    public static Area createInChunks(int x, int y, int tileSize) {
        return new Area(x, y, false, false);
    }

    private Area(int x, int y, boolean border, boolean whole) {     //Najlepiej było by gdyby punkt (x, y) był w górnym lewym rogu całego pola
        this.x = x;
        this.y = y;
        this.border = border;
        type = whole ? WHOLE : CHUNKS;
        solid = simpleLighting = true;
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
        int maxX = 0, maxY = 0, minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, shadowHeight = 0;
        int x, y, tempShadowHeight;
        for (Figure part : parts) {
            if (part.getOwner().isSolid()) {
                tempShadowHeight = part.getShadowHeight();
                shadowHeight = tempShadowHeight != 0 ? tempShadowHeight : shadowHeight;
                Collection<Point> points = part.getPoints();
                for (Point point : points) {
                    x = point.getX();
                    y = point.getY();
                    maxX = maxX > x ? maxX : x;
                    maxY = maxY > y ? maxY : y;
                    minX = minX < x ? minX : x;
                    minY = minY < y ? minY : y;
                }
            }
        }
        collision = Rectangle.createShadowHeight(minX - getX(), minY - getY(), maxX - minX, maxY - minY, OpticProperties.FULL_SHADOW, shadowHeight, this);
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

    public Figure getFigure(int i) {
        return parts.get(i);
    }

    public Figure deleteFigure(int i) {
        return parts.remove(i);
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
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f) {
        glPushMatrix();
        glTranslatef(f.getX() + xEffect, f.getY() - f.getShadowHeight() + yEffect, 0);
        if (simpleLighting) {
            glColor3f(color, color, color);
            Drawer.drawRectangle(0, 0, f.width, f.height + f.getShadowHeight());
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glColor3f(1f, 1f, 1f);
        } else if (sprite != null) {
            glEnable(GL_TEXTURE_2D);
            Drawer.drawShapeInColor(sprite, color, color, color);
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f) {
        glPushMatrix();
        glTranslatef(f.getX() + xEffect, f.getY() - f.getShadowHeight() + yEffect, 0);
        if (simpleLighting) {
            glColor3f(0f, 0f, 0f);
            Drawer.drawRectangle(0, 0, f.width, f.height + f.getShadowHeight());
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glColor3f(1f, 1f, 1f);
        } else if (sprite != null) {
            glEnable(GL_TEXTURE_2D);
            Drawer.drawShapeInBlack(sprite);
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f, int xStart, int yStart) {
        glPushMatrix();
        glTranslatef(f.getX() + xEffect, f.getY() - f.getShadowHeight() + yEffect, 0);
        if (simpleLighting) {
            glColor3f(color, color, color);
            Drawer.drawRectangle(0, 0, f.width, f.height + f.getShadowHeight());
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glColor3f(1f, 1f, 1f);
        } else if (sprite != null) {
            glEnable(GL_TEXTURE_2D);
            Drawer.drawShapeInColor(sprite, color, color, color);
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_TEXTURE_2D);
        }
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f, int xStart, int yStart) {
        glPushMatrix();
        glTranslatef(f.getX() + xEffect, f.getY() - f.getShadowHeight() + yEffect, 0);
        if (simpleLighting) {
            glColor3f(0f, 0f, 0f);
            Drawer.drawRectangle(0, 0, f.width, f.height + f.getShadowHeight());
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glColor3f(1f, 1f, 1f);
        } else if (sprite != null) {
            glEnable(GL_TEXTURE_2D);
            Drawer.drawShapeInBlack(sprite);
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_TEXTURE_2D);
        }
        glPopMatrix();
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

    @Override
    public void render(int xEffect, int yEffect) {
        if (Main.DEBUG) {
            System.err.println("Empty method - " + Thread.currentThread().getStackTrace()[1].getMethodName() + " - from " + this.getClass());
        }
    }
}
