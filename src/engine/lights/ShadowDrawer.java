/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.lights;

import collision.Figure;
import engine.matrices.MatrixMath;
import engine.utilities.Drawer;
import engine.utilities.Point;
import game.place.Place;
import sprites.Sprite;

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
        float[] data = {
                shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize,
                x, y,
                shaded.getX() + Place.tileSize, shaded.getYEnd(),
        };
        Drawer.spriteShader.start();
        Drawer.spriteShader.loadTextureShift(0, 0);
        Drawer.spriteShader.loadSizeModifier(Sprite.ZERO_VECTOR);
        Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        Drawer.spriteShader.setUseTexture(false);
        Drawer.streamVBO.renderTriangleStream(data);
        Drawer.spriteShader.setUseTexture(true);
        Drawer.spriteShader.stop();
        endDrawingShadow();
    }

    public static void drawConcaveTop(Figure shaded, int x, int y, int lightXCentralShifted, int lightYCentralShifted) {
        startDrawingShadow(BLACK, lightXCentralShifted, lightYCentralShifted);
        float[] data = {
                shaded.getX(), shaded.getYEnd() - Place.tileSize,
                x, y,
                shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize,
        };
        Drawer.spriteShader.start();
        Drawer.spriteShader.loadTextureShift(0, 0);
        Drawer.spriteShader.loadSizeModifier(Sprite.ZERO_VECTOR);
        Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        Drawer.spriteShader.setUseTexture(false);
        Drawer.streamVBO.renderTriangleStream(data);
        Drawer.spriteShader.setUseTexture(true);
        Drawer.spriteShader.stop();
        endDrawingShadow();
    }

    public static void drawRightConcaveBottom(Figure shaded, int x, int y, int lightXCentralShifted, int lightYCentralShifted) {
        startDrawingShadow(BLACK, lightXCentralShifted, lightYCentralShifted);
        float[] data = {
                shaded.getX(), shaded.getYEnd(),
                x, y,
                shaded.getX(), shaded.getYEnd() - Place.tileSize,
        };
        Drawer.spriteShader.start();
        Drawer.spriteShader.loadTextureShift(0, 0);
        Drawer.spriteShader.loadSizeModifier(Sprite.ZERO_VECTOR);
        Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        Drawer.spriteShader.setUseTexture(false);
        Drawer.streamVBO.renderTriangleStream(data);
        Drawer.spriteShader.setUseTexture(true);
        Drawer.spriteShader.stop();
        endDrawingShadow();
    }

    private static void startDrawingShadow(byte color, int lightXCentralShifted, int lightYCentralShifted) {
        Drawer.setColorStatic(color, color, color, 1f);
        glPushMatrix();
        glTranslatef(lightXCentralShifted, lightYCentralShifted, 0);
    }

    private static void endDrawingShadow() {
        Drawer.refreshColor();
        glPopMatrix();
    }

    public static void drawShadowFromConcave(Figure shaded, Point[] shadowPoints, int lightXCentralShifted, int lightYCentralShifted) {
        startDrawingShadow(BLACK, lightXCentralShifted, lightYCentralShifted);
        corner.set(shaded.getX() + (shaded.isLeftBottomRound() ? Place.tileSize : 0), shaded.getY());
        boolean a = false, b = false;

        Drawer.spriteShader.start();
        Drawer.spriteShader.loadTextureShift(0, 0);
        Drawer.spriteShader.loadSizeModifier(Sprite.ZERO_VECTOR);
        Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        Drawer.spriteShader.setUseTexture(false);

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
        Drawer.streamVBO.renderTriangleStream(data);
        Drawer.spriteShader.setUseTexture(true);
        Drawer.spriteShader.stop();
        endDrawingShadow();
    }

    public static void drawShadow(Point[] shadowPoints, int lightXCentralShifted, int lightYCentralShifted) {
        startDrawingShadow(BLACK, lightXCentralShifted, lightYCentralShifted);

        Drawer.spriteShader.start();
        Drawer.spriteShader.loadTextureShift(0, 0);
        Drawer.spriteShader.loadSizeModifier(Sprite.ZERO_VECTOR);
        Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        Drawer.spriteShader.setUseTexture(false);

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
            Drawer.streamVBO.renderTriangleStream(data);
        } else {
            float[] data = {
                    shadowPoints[1].getX(), shadowPoints[1].getY(),
                    shadowPoints[3].getX(), shadowPoints[3].getY(),
                    shadowPoints[2].getX(), shadowPoints[2].getY(),
                    shadowPoints[2].getX(), shadowPoints[2].getY(),
                    shadowPoints[0].getX(), shadowPoints[0].getY(),
                    shadowPoints[1].getX(), shadowPoints[1].getY()
            };
            Drawer.streamVBO.renderTriangleStream(data);
        }
        Drawer.spriteShader.setUseTexture(true);
        Drawer.spriteShader.stop();
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
        Drawer.spriteShader.start();
        Drawer.spriteShader.loadTextureShift(0, 0);
        Drawer.spriteShader.loadSizeModifier(Sprite.ZERO_VECTOR);
        Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        Drawer.spriteShader.setUseTexture(false);
        if (xS < xE) {
            float[] data = {
                    xS, firstShadowPoint,
                    xE, secondShadowPoint,
                    xS, secondShadowPoint,
                    xE, secondShadowPoint,
                    xS, firstShadowPoint,
                    xE, firstShadowPoint,
            };
            Drawer.streamVBO.renderTriangleStream(data);
        } else {
            float[] data = {
                    xS, firstShadowPoint,
                    xS, secondShadowPoint,
                    xE, secondShadowPoint,
                    xE, secondShadowPoint,
                    xE, firstShadowPoint,
                    xS, firstShadowPoint,
            };
            Drawer.streamVBO.renderTriangleStream(data);
        }
        Drawer.spriteShader.setUseTexture(true);
        Drawer.spriteShader.stop();
        endDrawingShadow();
    }

    protected interface shadeRenderer {

        void render(Figure shade, int xS, int xE, int lightXCentralShifted, int lightYCentralShifted);
    }
}
