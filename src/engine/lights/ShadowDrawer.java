/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.Point;
import game.place.Place;

import static engine.lights.Shadow.*;

/**
 * @author przemek
 */
public class ShadowDrawer {

    private static final shadeRenderer[] shadeRenderers = new shadeRenderer[6];
    private static final byte BLACK = 0, WHITE = 1;
    private static final Point corner = new Point();

    static {
        shadeRenderers[DARK] = (Figure shade, int xS, int xE) -> shade.getOwner().renderShadow(shade);
        shadeRenderers[BRIGHT] = (Figure shade, int xS, int xE) -> shade.getOwner().renderShadowLit(shade);
        shadeRenderers[BRIGHTEN] = (Figure shade, int xS, int xE) -> {
            if (!shade.isBottomRounded()) {
                drawShadeLit(shade, xS, xE);
            } else {
                shade.getOwner().renderShadowLit(xS, xE);
            }
        };
        shadeRenderers[DARKEN] = (Figure shade, int xS, int xE) -> {
            if (!shade.isBottomRounded()) {
                drawShade(shade, xS, xE);
            } else {
                shade.getOwner().renderShadow(xS, xE);
            }
        };
        shadeRenderers[BRIGHTEN_OBJECT] = (Figure shade, int xS, int xE) -> shade.getOwner().renderShadowLit(xS, xE);
        shadeRenderers[DARKEN_OBJECT] = (Figure shade, int xS, int xE) -> shade.getOwner().renderShadow(xS, xE);
    }


    public static void drawAllShadows(Figure shaded) {
        for (int i = 0; i < shaded.getShadowCount(); i++) {
            shadeRenderers[shaded.getShadow(i).type].render(shaded, shaded.getShadow(i).xS, shaded.getShadow(i).xE);
        }
    }

    public static void drawLeftConcaveBottom(Figure shaded, int x, int y) {
        float[] data = {
                shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize,
                x, y,
                shaded.getX() + Place.tileSize, shaded.getYEnd(),
        };
        Drawer.shadowShader.resetUniform();
        Drawer.shadowShader.setUseTexture(false);
        float[] colors = new float[3];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = 0f;
        }
        Drawer.shadowVBO.renderShadedTriangleStream(data, colors);
        Drawer.shadowShader.setUseTexture(true);
    }

    public static void drawConcaveTop(Figure shaded, int x, int y) {
        float[] data = {
                shaded.getX(), shaded.getYEnd() - Place.tileSize,
                x, y,
                shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize,
        };
        Drawer.shadowShader.resetUniform();
        Drawer.shadowShader.setUseTexture(false);
        float[] colors = new float[3];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = 0f;
        }
        Drawer.shadowVBO.renderShadedTriangleStream(data, colors);
        Drawer.shadowShader.setUseTexture(true);
    }

    public static void drawRightConcaveBottom(Figure shaded, int x, int y) {
        float[] data = {
                shaded.getX(), shaded.getYEnd(),
                x, y,
                shaded.getX(), shaded.getYEnd() - Place.tileSize,
        };
        Drawer.shadowShader.resetUniform();
        Drawer.shadowShader.setUseTexture(false);
        float[] colors = new float[3];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = 0f;
        }
        Drawer.shadowVBO.renderShadedTriangleStream(data, colors);
        Drawer.shadowShader.setUseTexture(true);
    }

    public static void drawShadowFromConcave(Figure shaded, Point[] shadowPoints, int lightXCentralShifted, int lightYCentralShifted) {
        corner.set(shaded.getX() + (shaded.isLeftBottomRound() ? Place.tileSize : 0), shaded.getY());
        boolean a = false, b = false;

        Drawer.shadowShader.resetUniform();
        Drawer.shadowShader.setUseTexture(false);
        float[] data = new float[18];
        if (((corner.getX() - shadowPoints[0].getX()) * (shadowPoints[2].getY() - shadowPoints[0].getY()))
                - ((corner.getY() - shadowPoints[0].getY()) * (shadowPoints[2].getX() - shadowPoints[0].getX())) > 0) {
            data[0] = shadowPoints[2].getX();
            data[1] = shadowPoints[2].getY();
            data[2] = corner.getX();
            data[3] = corner.getY();
            data[4] = shadowPoints[0].getX();
            data[5] = shadowPoints[0].getY();
            a = true;
        } else {
            data[0] = shadowPoints[0].getX();
            data[1] = shadowPoints[0].getY();
            data[2] = corner.getX();
            data[3] = corner.getY();
            data[4] = shadowPoints[2].getX();
            data[5] = shadowPoints[2].getY();
        }

        if (((corner.getX() - shadowPoints[3].getX()) * (shadowPoints[1].getY() - shadowPoints[3].getY()))
                - ((corner.getY() - shadowPoints[3].getY()) * (shadowPoints[1].getX() - shadowPoints[3].getX())) > 0) {
            data[6] = shadowPoints[1].getX();
            data[7] = shadowPoints[1].getY();
            data[8] = corner.getX();
            data[9] = corner.getY();
            data[10] = shadowPoints[3].getX();
            data[11] = shadowPoints[3].getY();
            b = true;
        } else {
            data[6] = shadowPoints[3].getX();
            data[7] = shadowPoints[3].getY();
            data[8] = corner.getX();
            data[9] = corner.getY();
            data[10] = shadowPoints[1].getX();
            data[11] = shadowPoints[1].getY();
        }

        if (a || b) {
            data[12] = shadowPoints[3].getX();
            data[13] = shadowPoints[3].getY();
            data[14] = corner.getX();
            data[15] = corner.getY();
            data[16] = shadowPoints[2].getX();
            data[17] = shadowPoints[2].getY();
        } else {
            data[12] = shadowPoints[2].getX();
            data[13] = shadowPoints[2].getY();
            data[14] = corner.getX();
            data[15] = corner.getY();
            data[16] = shadowPoints[3].getX();
            data[17] = shadowPoints[3].getY();
        }

        float[] colors = new float[9];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = 0f;
        }
        Drawer.shadowVBO.renderShadedTriangleStream(data, colors);
        Drawer.shadowShader.setUseTexture(true);
    }

    public static void drawShadow(Point[] shadowPoints, int lightXCentralShifted, int lightYCentralShifted) {
        Drawer.shadowShader.resetUniform();
        Drawer.shadowShader.setUseTexture(false);

        if (((shadowPoints[1].getX() - shadowPoints[0].getX()) * (shadowPoints[2].getY() - shadowPoints[0].getY()))
                - ((shadowPoints[1].getY() - shadowPoints[0].getY()) * (shadowPoints[2].getX() - shadowPoints[0].getX())) > 0) {
            float[] data = {
                    shadowPoints[0].getX(), shadowPoints[0].getY(),
                    shadowPoints[2].getX(), shadowPoints[2].getY(),
                    shadowPoints[3].getX(), shadowPoints[3].getY(),
                    shadowPoints[3].getX(), shadowPoints[3].getY(),
                    shadowPoints[1].getX(), shadowPoints[1].getY(),
                    shadowPoints[0].getX(), shadowPoints[0].getY(),
            };
            float[] colors = new float[6];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = 0f;
            }
            Drawer.shadowVBO.renderShadedTriangleStream(data, colors);
        } else {
            float[] data = {
                    shadowPoints[1].getX(), shadowPoints[1].getY(),
                    shadowPoints[3].getX(), shadowPoints[3].getY(),
                    shadowPoints[2].getX(), shadowPoints[2].getY(),
                    shadowPoints[2].getX(), shadowPoints[2].getY(),
                    shadowPoints[0].getX(), shadowPoints[0].getY(),
                    shadowPoints[1].getX(), shadowPoints[1].getY()
            };
            float[] colors = new float[6];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = 0f;
            }
            Drawer.shadowVBO.renderShadedTriangleStream(data, colors);
        }
        Drawer.shadowShader.setUseTexture(true);
    }

    private static void drawShade(Figure shade, int xS, int xE) {
        drawShadeInColor(BLACK, shade, xS, xE);
    }

    private static void drawShadeLit(Figure shade, int xS, int xE) {
        drawShadeInColor(WHITE, shade, xS, xE);
    }

    private static void drawShadeInColor(byte color, Figure shade, int xS, int xE) {
        int firstShadowPoint = shade.getYEnd();
        int secondShadowPoint = shade.getY() - shade.getShadowHeight();
        Drawer.shadowShader.resetUniform();
        Drawer.shadowShader.setUseTexture(false);
        if (xS < xE) {
            float[] data = {
                    xS, firstShadowPoint,
                    xE, secondShadowPoint,
                    xS, secondShadowPoint,
                    xE, secondShadowPoint,
                    xS, firstShadowPoint,
                    xE, firstShadowPoint,
            };
            float[] colors = new float[6];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = color;
            }
            Drawer.shadowVBO.renderShadedTriangleStream(data, colors);
//            Drawer.streamVBO.renderTriangleStream(data);
        } else {
            float[] data = {
                    xS, firstShadowPoint,
                    xS, secondShadowPoint,
                    xE, secondShadowPoint,
                    xE, secondShadowPoint,
                    xE, firstShadowPoint,
                    xS, firstShadowPoint,
            };
            float[] colors = new float[6];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = color;
            }
            Drawer.shadowVBO.renderShadedTriangleStream(data, colors);
//            Drawer.streamVBO.renderTriangleStream(data);
        }
        Drawer.shadowShader.setUseTexture(true);
    }

    protected interface shadeRenderer {

        void render(Figure shade, int xS, int xE);
    }
}
