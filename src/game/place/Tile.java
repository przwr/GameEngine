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
import sprites.Sprite;
import sprites.SpriteSheet;

public class Tile extends GameObject {

    protected final SpriteSheet spriteSheet;
    protected final ArrayList<Point> tileStack;

    public Tile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet) {
        width = size;
        height = size;
        this.spriteSheet = spriteSheet;
        tileStack = new ArrayList<>(1);
        tileStack.add(new Point(xSheet, ySheet));
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
    
    public Point popTileFromStackBack() {
        if (!tileStack.isEmpty()) {
            Point p = tileStack.remove(0);
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
    
    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }
    
    public String saveToString(SpriteSheet s, int x, int y, int xBegin, int yBegin) {
        String txt = "t:" + (x - xBegin) + ":" + (y - yBegin) + ":" + (spriteSheet.equals(s) ? "" : spriteSheet.getKey());
        txt = tileStack.stream().map((p) -> ":" + p.getX() + ":" + p.getY() ).reduce(txt, String::concat);
        return txt;
    }
}
