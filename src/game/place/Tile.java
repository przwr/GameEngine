package game.place;

import collision.Figure;
import engine.Drawer;
import engine.Point;
import game.gameobject.GameObject;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

public class Tile extends GameObject {

    public static int SIZE;
    protected final SpriteSheet spriteSheet;
    protected final ArrayList<Point> tileStack;

    public Tile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, Place place) {
        SIZE = size;
        this.spriteSheet = spriteSheet;
        tileStack = new ArrayList<>(1);
        tileStack.add(new Point(xSheet, ySheet));
        this.place = place;
    }

    public void addTileToStack(int xSheet, int ySheet) {
        Point p = new Point(xSheet, ySheet);
        if (!tileStack.contains(p)) {
            tileStack.add(p);
        }
    }

    public int tileStackSize() {
        return tileStack.size();
    }
    
    public Point popTileFromStack() {
        if (!tileStack.isEmpty()) {
            Point p = tileStack.remove(tileStack.size() - 1);
            tileStack.trimToSize();
            return p;
        }
        return null;
    }

    public void renderSpecific(int x, int y) {    //Renderuje w konkretnym miejscu nie 
        glPushMatrix();                                     //patrząc na zmienne wewnętrzne
        glTranslatef(x, y, 0);
        tileStack.stream().forEach((p) -> {
            spriteSheet.renderPiece(p.getX(), p.getY());
        });
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        Drawer.drawRectangleInShade(0, 0, width, height, color);
        glColor3f(1f, 1f, 1f);
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        Drawer.drawRectangleInBlack(0, 0, width, height);
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f, int xStart, int xEnd) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        Drawer.drawRectangleInShade(0, 0, width, height, color);
        glColor3f(1f, 1f, 1f);
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f, int xStart, int xEnd) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        Drawer.drawRectangleInBlack(0, 0, width, height);
        glPopMatrix();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect, 0);
        tileStack.stream().forEach((p) -> {
            spriteSheet.renderPiece(p.getX(), p.getY());
        });
        glPopMatrix();
    }

}
