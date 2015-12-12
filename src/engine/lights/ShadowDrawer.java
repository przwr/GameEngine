/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Figure;
import engine.utilities.Point;
import game.place.Place;

import static engine.lights.Shadow.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class ShadowDrawer {

    private static final shadeRenderer[] shadeRenderers = new shadeRenderer[6];
    private static final byte BLACK = 0, WHITE = 1;
    private static final Point corner = new Point();

    static {
        shadeRenderers[DARK] = (Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted) -> shade.getOwner().renderShadow
                (lightXCentralShifted, lightYCentralShifted, shade);
        shadeRenderers[BRIGHT] = (Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted) -> shade.getOwner().renderShadowLit
                (lightXCentralShifted, lightYCentralShifted, shade);
        shadeRenderers[BRIGHTEN] = (Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted) -> {
            if (!shade.isBottomRounded()) {
                drawShadeLit(shade, xS, xE, lightXCentralShifted, lightYCentralShifted);
            } else {
                shade.getOwner().renderShadowLit(lightXCentralShifted, lightYCentralShifted, xS, xE);
            }
        };
        shadeRenderers[DARKEN] = (Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted) -> {
            if (!shade.isBottomRounded()) {
                drawShade(shade, xS, xE, lightXCentralShifted, lightYCentralShifted);
            } else {
                shade.getOwner().renderShadow(lightXCentralShifted, lightYCentralShifted, xS, xE);
            }
        };
        shadeRenderers[BRIGHTEN_OBJECT] = (Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted) -> shade.getOwner()
                .renderShadowLit(lightXCentralShifted, lightYCentralShifted, xS, xE);
        shadeRenderers[DARKEN_OBJECT] = (Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted) -> shade.getOwner()
                .renderShadow(lightXCentralShifted, lightYCentralShifted, xS, xE);
    }

    public static void drawAllShadows(Figure shaded, int lightXCentralShifted, int lightYCentralShifted) {
        for (int i = 0; i < shaded.getShadowCount(); i++) {
            shadeRenderers[shaded.getShadow(i).type].render(shaded, shaded.getShadow(i).xS, shaded.getShadow(i).xE, lightXCentralShifted, lightYCentralShifted);
        }
    }

    public static void drawLeftConcaveBottom(Figure shaded, int x, int y, int lightXCentralShifted, int lightYCentralShifted) {
        startDrawingShadow(BLACK, lightXCentralShifted, lightYCentralShifted);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd());
        glVertex2f(x, y);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize);
        endDrawingShadow();
    }

    public static void drawConcaveTop(Figure shaded, int x, int y, int lightXCentralShifted, int lightYCentralShifted) {
        startDrawingShadow(BLACK, lightXCentralShifted, lightYCentralShifted);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX(), shaded.getYEnd() - Place.tileSize);
        glVertex2f(x, y);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize);
        endDrawingShadow();
    }

    public static void drawRightConcaveBottom(Figure shaded, int x, int y, int lightXCentralShifted, int lightYCentralShifted) {
        startDrawingShadow(BLACK, lightXCentralShifted, lightYCentralShifted);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX(), shaded.getYEnd());
        glVertex2f(x, y);
        glVertex2f(shaded.getX(), shaded.getYEnd() - Place.tileSize);
        endDrawingShadow();
    }

    private static void startDrawingShadow(byte color, int lightXCentralShifted, int lightYCentralShifted) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(color, color, color);
        glPushMatrix();
        glTranslatef(lightXCentralShifted, lightYCentralShifted, 0);
    }

    private static void endDrawingShadow() {
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawShadowFromConcave(Figure shaded, Point[] shadowPoints, int lightXCentralShifted, int lightYCentralShifted) {
        startDrawingShadow(BLACK, lightXCentralShifted, lightYCentralShifted);
        corner.set(shaded.getX() + (shaded.isLeftBottomRound() ? Place.tileSize : 0), shaded.getY());
        glBegin(GL_TRIANGLES);
        glVertex2f(shadowPoints[0].getX(), shadowPoints[0].getY());
        glVertex2f(corner.getX(), corner.getY());
        glVertex2f(shadowPoints[2].getX(), shadowPoints[2].getY());

        glVertex2f(shadowPoints[1].getX(), shadowPoints[1].getY());
        glVertex2f(corner.getX(), corner.getY());
        glVertex2f(shadowPoints[3].getX(), shadowPoints[3].getY());

        glVertex2f(shadowPoints[2].getX(), shadowPoints[2].getY());
        glVertex2f(corner.getX(), corner.getY());
        glVertex2f(shadowPoints[3].getX(), shadowPoints[3].getY());
        endDrawingShadow();
    }

    public static void drawShadow(Point[] shadowPoints, int lightXCentralShifted, int lightYCentralShifted) {
        startDrawingShadow(BLACK, lightXCentralShifted, lightYCentralShifted);
        glBegin(GL_QUADS);
        glVertex2f(shadowPoints[0].getX(), shadowPoints[0].getY());
        glVertex2f(shadowPoints[2].getX(), shadowPoints[2].getY());
        glVertex2f(shadowPoints[3].getX(), shadowPoints[3].getY());
        glVertex2f(shadowPoints[1].getX(), shadowPoints[1].getY());
        endDrawingShadow();
    }

    private static void drawShade(Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted) {
        drawShadeInColor(BLACK, shade, xS, xE, lightXCentralShifted, lightYCentralShifted);
    }

    private static void drawShadeLit(Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted) {
        drawShadeInColor(WHITE, shade, xS, xE, lightXCentralShifted, lightYCentralShifted);
    }

    private static void drawShadeInColor(byte color, Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted) {
        int firstShadowPoint = shade.getYEnd();
        int secondShadowPoint = shade.getY() - shade.getShadowHeight();
        startDrawingShadow(color, lightXCentralShifted, lightYCentralShifted);
        glBegin(GL_QUADS);
        glVertex2f(xS, firstShadowPoint);
        glVertex2f(xS, secondShadowPoint);
        glVertex2f(xE, secondShadowPoint);
        glVertex2f(xE, firstShadowPoint);
        endDrawingShadow();
    }

    protected interface shadeRenderer {

        void render(Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted);
    }
}
