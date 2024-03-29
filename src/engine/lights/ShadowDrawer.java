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
import net.jodk.lang.FastMath;

import static engine.lights.Shadow.*;

/**
 * @author przemek
 */
public class ShadowDrawer {

    private static final shadeRenderer[] shadeRenderers = new shadeRenderer[6];
    private static final float WHITE = 1;
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
                drawDark(shade, xS, xE);
            } else {
                shade.getOwner().renderShadow(xS, xE);
            }
        };
        shadeRenderers[BRIGHTEN_OBJECT] = (Figure shade, int xS, int xE) -> shade.getOwner().renderShadowLit(xS, xE);
        shadeRenderers[DARKEN_OBJECT] = (Figure shade, int xS, int xE) -> shade.getOwner().renderShadow(xS, xE);
    }

    public static void prepareVBO() {
        Drawer.shadowShader.resetTransformationMatrix();
        Drawer.streamVertexData.clear();
        Drawer.streamColorData.clear();
    }

    public static void renderCurrentVBO() {
        if (!Drawer.streamVertexData.isEmpty()) {
            Drawer.shadowShader.setUseTexture(false);
            Drawer.shadowShader.resetTransformationMatrix();
            Drawer.shadowVBO.renderShadedTriangleStream(Drawer.streamVertexData.toArray(), Drawer.streamColorData.toArray());
            Drawer.streamVertexData.clear();
            Drawer.streamColorData.clear();
            Drawer.shadowShader.setUseTexture(true);
        }
    }

    public static void addShadowToRender(float color, float... vertices) {
        Drawer.streamVertexData.add(vertices);
        Drawer.streamColorData.add(color, vertices.length / 2);
    }

    public static void drawAllShadows(Figure shaded) {
        for (int i = 0; i < shaded.getShadowCount(); i++) {
            shadeRenderers[shaded.getShadow(i).type].render(shaded, shaded.getShadow(i).xS, shaded.getShadow(i).xE);
        }
    }

    public static void drawLeftConcaveBottom(Figure shaded, int x, int y) {
        addShadowToRender(ShadowRenderer.maxDarkness, shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize,
                x, y, shaded.getX() + Place.tileSize, shaded.getYEnd());
    }

    public static void drawConcaveTop(Figure shaded, int x, int y) {
        addShadowToRender(ShadowRenderer.maxDarkness, shaded.getX(), shaded.getYEnd() - Place.tileSize,
                x, y, shaded.getX() + Place.tileSize, shaded.getYEnd() - Place.tileSize);
    }

    public static void drawRightConcaveBottom(Figure shaded, int x, int y) {
        addShadowToRender(ShadowRenderer.maxDarkness, shaded.getX(), shaded.getYEnd(),
                x, y, shaded.getX(), shaded.getYEnd() - Place.tileSize);
    }

    public static void drawShadowFromConcave(Figure shaded, Point[] shadowPoints) {
        corner.set(shaded.getX() + (shaded.isLeftBottomRound() ? Place.tileSize : 0), shaded.getY());
        boolean a = false, b = false;
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
        addShadowToRender(ShadowRenderer.maxDarkness, data);
    }

    public static void drawShadow(Light light, Figure shaded, Point[] shadowPoints) {
        if (((shadowPoints[1].getX() - shadowPoints[0].getX()) * (shadowPoints[2].getY() - shadowPoints[0].getY()))
                - ((shadowPoints[1].getY() - shadowPoints[0].getY()) * (shadowPoints[2].getX() - shadowPoints[0].getX())) > 0) {
            addShadowToRender(ShadowRenderer.maxDarkness, shadowPoints[0].getX(), shadowPoints[0].getY(),
                    shadowPoints[2].getX(), shadowPoints[2].getY(),
                    shadowPoints[3].getX(), shadowPoints[3].getY(),
                    shadowPoints[3].getX(), shadowPoints[3].getY(),
                    shadowPoints[1].getX(), shadowPoints[1].getY(),
                    shadowPoints[0].getX(), shadowPoints[0].getY());
            if (!shaded.getOwner().isSimpleLighting() && !shaded.getOwner().getCollision().isBottomRounded()) {
                if (light.getY() < shaded.getYEnd()) {
                    if (light.getX() < shaded.getX()) {
                        addShadowToRender(ShadowRenderer.maxDarkness, shaded.getX(), shaded.getY(),
                                shaded.getX(), shaded.getYEnd(),
                                shaded.getXEnd(), shaded.getY()
                        );
                    } else if (light.getX() > shaded.getXEnd()) {
                        addShadowToRender(ShadowRenderer.maxDarkness, shaded.getX(), shaded.getY(),
                                shaded.getXEnd(), shaded.getYEnd(),
                                shaded.getXEnd(), shaded.getY()
                        );
                    }
                }
            }
        } else {
            addShadowToRender(ShadowRenderer.maxDarkness, shadowPoints[1].getX(), shadowPoints[1].getY(),
                    shadowPoints[3].getX(), shadowPoints[3].getY(),
                    shadowPoints[2].getX(), shadowPoints[2].getY(),
                    shadowPoints[2].getX(), shadowPoints[2].getY(),
                    shadowPoints[0].getX(), shadowPoints[0].getY(),
                    shadowPoints[1].getX(), shadowPoints[1].getY());
            if (!shaded.getOwner().isSimpleLighting() && !shaded.getOwner().getCollision().isBottomRounded()) {
                if (light.getY() > shaded.getYEnd()) {
                    if (light.getX() < shaded.getX()) {
                        addShadowToRender(1f, shaded.getX(), shaded.getYEnd() - Place.tileSize,
                                shaded.getXEnd(), shaded.getYEnd(),
                                shaded.getXEnd(), shaded.getYEnd() - Place.tileSize
                        );
                    } else if (light.getX() > shaded.getXEnd()) {
                        addShadowToRender(1f, shaded.getX(), shaded.getYEnd() - Place.tileSize,
                                shaded.getX(), shaded.getYEnd(),
                                shaded.getXEnd(), shaded.getYEnd() - Place.tileSize
                        );
                    } else {
                        addShadowToRender(1f, shaded.getX(), shaded.getYEnd(),
                                shaded.getXEnd(), shaded.getYEnd(),
                                shaded.getX(), shaded.getYEnd() - Place.tileSize,
                                shaded.getX(), shaded.getYEnd() - Place.tileSize,
                                shaded.getXEnd(), shaded.getYEnd(),
                                shaded.getXEnd(), shaded.getYEnd() - Place.tileSize
                        );
                    }
                }
            }
        }
    }

    private static void drawDark(Figure shade, int xS, int xE) {
        drawShadeInColor(shade.getDarkValue(), shade, xS, xE);
    }

    private static void drawShadeLit(Figure shade, int xS, int xE) {
        drawShadeInColor(WHITE, shade, xS, xE);
    }

    private static void drawShadeInColor(float color, Figure shade, int xS, int xE) {
        int firstShadowPoint = shade.getYEnd();
        int secondShadowPoint = shade.getY() - shade.getShadowHeight();
        if (xS < xE) {
            addShadowToRender((float) FastMath.sqrt(color),
                    xS, firstShadowPoint,
                    xE, secondShadowPoint,
                    xS, secondShadowPoint,
                    xE, secondShadowPoint,
                    xS, firstShadowPoint,
                    xE, firstShadowPoint);
        } else {
            addShadowToRender((float) FastMath.sqrt(color),
                    xS, firstShadowPoint,
                    xS, secondShadowPoint,
                    xE, secondShadowPoint,
                    xE, secondShadowPoint,
                    xE, firstShadowPoint,
                    xS, firstShadowPoint);
        }
    }


    protected interface shadeRenderer {

        void render(Figure shade, int xS, int xE);
    }
}
