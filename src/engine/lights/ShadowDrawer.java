/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Figure;
import collision.RoundRectangle;
import engine.utilities.Point;
import game.place.Place;

import static engine.lights.Shadow.*;
import static engine.utilities.Drawer.displayHeight;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class ShadowDrawer {

    private static final shadeRenderer[] shadeRenderers = new shadeRenderer[6];
    private static final byte BLACK = 0;
    private static final Point corner = new Point();

    static {
        shadeRenderers[DARK] = (Light emitter, Figure shade, Point point) -> shade.getOwner().renderShadow((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade);
        shadeRenderers[BRIGHT] = (Light emitter, Figure shade, Point point) -> shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), shade);
        shadeRenderers[BRIGHTEN] = (Light emitter, Figure shade, Point point) -> {
            if (!shade.isBottomRounded()) {
                drawShadeLit(emitter, shade, point);
            } else {
                shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), point.getX(), point.getY());
            }
        };
        shadeRenderers[DARKEN] = (Light emitter, Figure shade, Point point) -> {
            if (!shade.isBottomRounded()) {
                drawShade(emitter, shade, point);
            } else {
                shade.getOwner().renderShadow((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), point.getX(), point.getY());
            }
        };
        shadeRenderers[BRIGHTEN_OBJECT] = (Light emitter, Figure shade, Point point) -> shade.getOwner().renderShadowLit((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), point.getX(), point.getY());
        shadeRenderers[DARKEN_OBJECT] = (Light emitter, Figure shade, Point point) -> shade.getOwner().renderShadow((emitter.getXCenterShift()) - (emitter.getX()), (emitter.getYCenterShift()) - (emitter.getY()) + displayHeight - emitter.getHeight(), point.getX(), point.getY());
    }

    public static void drawAllShadows(Light light, Figure shaded) {
        for (int i = 0; i < shaded.getShadowCount(); i++) {
            shadeRenderers[shaded.getShadow(i).type].render(light, shaded, shaded.getShadow(i).point);
        }
    }

    public static void drawLeftConcaveBottom(Light emitter, RoundRectangle shaded, int x, int y) {
        startDrawingShadow(emitter, BLACK);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd());
        glVertex2f(x, y);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize);
        endDrawingShadow();
    }

    public static void drawConcaveTop(Light emitter, RoundRectangle shaded, int x, int y) {
        startDrawingShadow(emitter, BLACK);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX(), shaded.getYEnd() - Place.tileSize);
        glVertex2f(x, y);
        glVertex2f(shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize);
        endDrawingShadow();
    }

    public static void drawRightConcaveBottom(Light emitter, RoundRectangle shaded, int x, int y) {
        startDrawingShadow(emitter, BLACK);
        glBegin(GL_TRIANGLES);
        glVertex2f(shaded.getX(), shaded.getYEnd());
        glVertex2f(x, y);
        glVertex2f(shaded.getX(), shaded.getYEnd() - Place.tileSize);
        endDrawingShadow();
    }

    private static void startDrawingShadow(Light emitter, byte color) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(color, color, color);
        glPushMatrix();
        glTranslatef((emitter.getXCenterShift()) - emitter.getX(), (emitter.getYCenterShift()) - emitter.getY() + displayHeight - emitter.getHeight(), 0);

    }

    private static void endDrawingShadow() {
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawShadowFromConcave(Light emitter, RoundRectangle shaded, Point[] shadowPoints) {
        startDrawingShadow(emitter, BLACK);
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

    public static void drawShadow(Light emitter, Point[] shadowPoints) {
        startDrawingShadow(emitter, BLACK);
        glBegin(GL_QUADS);
        glVertex2f(shadowPoints[0].getX(), shadowPoints[0].getY());
        glVertex2f(shadowPoints[2].getX(), shadowPoints[2].getY());
        glVertex2f(shadowPoints[3].getX(), shadowPoints[3].getY());
        glVertex2f(shadowPoints[1].getX(), shadowPoints[1].getY());
        endDrawingShadow();
    }

    private static void drawShade(Light emitter, Figure shade, Point point) {
        drawShadeInColor(BLACK, emitter, shade, point);
    }

    private static void drawShadeLit(Light emitter, Figure shade, Point point) {
        byte WHITE = 1;
        drawShadeInColor(WHITE, emitter, shade, point);
    }

    private static void drawShadeInColor(byte color, Light emitter, Figure shade, Point point) {
        int firstShadowPoint = shade.getYEnd();
        int secondShadowPoint = shade.getY() - shade.getShadowHeight();
        startDrawingShadow(emitter, color);
        glBegin(GL_QUADS);
        glVertex2f(point.getX(), firstShadowPoint);
        glVertex2f(point.getX(), secondShadowPoint);
        glVertex2f(point.getY(), secondShadowPoint);
        glVertex2f(point.getY(), firstShadowPoint);
        endDrawingShadow();
    }

    protected interface shadeRenderer {

        void render(Light emitter, Figure shade, Point point);
    }
}
