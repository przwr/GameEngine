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
import java.util.Collection;
import net.jodk.lang.FastMath;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Wojtek
 */
public class Area extends GameObject {

    private final boolean border;
    private int xCentral, yCentral;

    public static Area createWhole(int x, int y, int tileSize) {
        return new Area(x, y, false);
    }

    private Area(int x, int y, boolean border) {  //Point (x, y) should be in left top corner of Area
        this.x = x;
        this.y = y;
        this.border = border;
        solid = true;
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

    public Collection<Point> getPoints() {
        return collision.getPoints();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure) {
        glPushMatrix();
        glTranslatef(figure.getX() + xEffect, figure.getY() - figure.getShadowHeight() + yEffect, 0);
        Drawer.drawRectangleInShade(0, 0, figure.width, figure.height + figure.getShadowHeight(), color);
        glPopMatrix();
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        glPushMatrix();
        glTranslatef(figure.getX() + xEffect, figure.getY() - figure.getShadowHeight() + yEffect, 0);
        Drawer.drawRectangleInBlack(0, 0, figure.width, figure.height + figure.getShadowHeight());
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
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int yStart) {
        glPushMatrix();
        glTranslatef(figure.getX() + xEffect, figure.getY() - figure.getShadowHeight() + yEffect, 0);
        Drawer.drawRectangleInBlack(0, 0, figure.width, figure.height + figure.getShadowHeight());
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
}
