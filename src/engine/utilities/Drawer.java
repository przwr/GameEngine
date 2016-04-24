/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utilities;

import engine.lights.ShadowDrawer;
import game.ScreenPlace;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import sprites.Appearance;
import sprites.fbo.FrameBufferObject;
import sprites.shaders.FontShader;
import sprites.shaders.RegularShader;
import sprites.shaders.ShadowShader;
import sprites.vbo.VertexBufferObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class Drawer {

    public static final Texture font = loadFontTexture();
    public static VertexBufferObject streamVBO;
    public static VertexBufferObject tileVBO;
    public static VertexBufferObject grassVBO;
    public static VertexBufferObject shadowVBO;
    public static VertexBufferObject screenVBO;
    public static FloatContainer streamVertexData = new FloatContainer(30000);
    public static FloatContainer streamColorData = new FloatContainer(30000);
    public static IntegerContainer streamIndexData = new IntegerContainer(30000);
    public static RegularShader regularShader;
    public static ShadowShader shadowShader;
    public static FontShader fontShader;
    public static int displayWidth, displayHeight;
    private static Color currentColor = Color.white;
    private static Timer t = new Timer("Test", 200);
    private static float[] data3 = new float[6];
    private static float[] data4 = new float[8];

    private static Texture loadFontTexture() {
        try {
            InputStream stream = ResourceLoader.getResourceAsStream("/res/textures/white.png");
            Texture t = TextureLoader.getTexture("png", stream, GL_NEAREST);
            stream.close();
            return t;
        } catch (IOException exception) {
            Logger.getLogger(ScreenPlace.class.getName()).log(Level.SEVERE, null, exception);
            ErrorHandler.javaError(exception.getMessage());
        }
        return null;
    }

    public static void setUpDisplay() {
        displayWidth = Display.getWidth();
        displayHeight = Display.getHeight();
        fontShader = new FontShader();
    }

    public static void bindFontTexture() {
        font.bind();
    }

    public static void clearScreen(float color) {
        glClearColor(color, color, color, 0f);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public static void refreshForRegularDrawing() {
        refreshColor();
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void refreshColor() {
        regularShader.loadColorModifier(new Vector4f(currentColor.r, currentColor.g, currentColor.b, 1.0f));
    }

    public static void setColorAlpha(float alpha) {
        regularShader.loadColorModifier(new Vector4f(currentColor.r, currentColor.g, currentColor.b, alpha));
    }

    public static void setColorStatic(Color color) {
        regularShader.loadColorModifier(new Vector4f(color.r, color.g, color.b, color.a));
    }

    public static void setColorBlended(Color color) {
        regularShader.loadColorModifier(new Vector4f(color.r * currentColor.r, color.g * currentColor.g, color.b * currentColor.b, color.a));
    }

    public static void setColorStatic(float r, float g, float b, float a) {
        regularShader.loadColorModifier(new Vector4f(r, g, b, a));
    }

    public static Color getCurrentColor() {
        return currentColor;
    }

    public static void setCurrentColor(Color color) {
        currentColor = color;
    }

    public static Color setPercentToRGBColor(int percent, Color color) {
        if (percent == 100) {
            percent = 99;
        }
        float r, g;
        if (percent < 50) {
            r = (percent / 50f);
            g = 1f;
        } else {
            r = 1f;
            g = ((50f - percent % 50f) / 50f);
        }
        color.r = r;
        color.g = g;
        color.b = 0f;
        return color;
    }

    public static void drawRectangleLit(int xStart, int yStart, int width, int height) {
        if (width < 0) {
            width = -width;
            xStart -= width;
        }
        ShadowDrawer.addShadowToRender(1, xStart, yStart,
                xStart, yStart + height,
                xStart + width, yStart,
                xStart + width, yStart,
                xStart, yStart + height,
                xStart + width, yStart + height);
    }

    public static void drawRectangleBlack(int xStart, int yStart, int width, int height, float darkValue) {
        if (width < 0) {
            width = -width;
            xStart -= width;
        }
        ShadowDrawer.addShadowToRender(darkValue, xStart, yStart,
                xStart, yStart + height,
                xStart + width, yStart,
                xStart + width, yStart,
                xStart, yStart + height,
                xStart + width, yStart + height);
    }

    public static void drawTextureTriangle(int xA, int yA, int xB, int yB, int xC, int yC) {
        data4[0] = xA;
        data4[1] = yA;
        data4[2] = xB;
        data4[3] = yB;
        data4[4] = 0;
        data4[5] = 0;
        data4[6] = xC;
        data4[7] = yC;
        streamVBO.updateVerticesStream(data4);
        streamVBO.renderTextured(0, 3);
    }

    public static void drawTriangle(int xA, int yA, int xB, int yB, int xC, int yC) {
        data3[0] = xA;
        data3[1] = yA;
        data3[2] = xB;
        data3[3] = yB;
        data3[4] = xC;
        data3[5] = yC;
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStream(data3);
        regularShader.setUseTexture(true);
    }

    public static void drawRectangle(int xStart, int yStart, int width, int height) {
        data4[0] = xStart;
        data4[1] = yStart;
        data4[2] = xStart;
        data4[3] = yStart + height;
        data4[4] = xStart + width;
        data4[5] = yStart;
        data4[6] = xStart + width;
        data4[7] = yStart + height;
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(data4);
        regularShader.setUseTexture(true);
    }

    public static void drawRectangleBorder(int xStart, int yStart, int width, int height) {
        data4[0] = xStart;
        data4[1] = yStart;
        data4[2] = xStart;
        data4[3] = yStart + height;
        data4[4] = xStart + width;
        data4[5] = yStart + height;
        data4[6] = xStart + width;
        data4[7] = yStart;
        regularShader.setUseTexture(false);
        streamVBO.renderLineLoopStream(data4);
        regularShader.setUseTexture(true);
    }

    public static void drawTextureQuad(int xA, int yA, int xB, int yB, int xC, int yC, int xD, int yD) {
        data4[0] = xD;
        data4[1] = yD;
        data4[2] = xA;
        data4[3] = yA;
        data4[4] = xB;
        data4[5] = yB;
        data4[6] = xC;
        data4[7] = yC;
        streamVBO.updateVerticesStream(data4);
        streamVBO.renderTextured(0, 6);
    }

    public static void drawCircle(int xStart, int yStart, int radius, int precision) {
        drawEllipse(xStart, yStart, radius, radius, precision);
    }

    public static void drawEllipse(int xStart, int yStart, int xRadius, int yRadius, int precision) {
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleFanStream(getEllipseVertices(xStart, yStart, xRadius, yRadius, precision));
        regularShader.setUseTexture(true);
    }

    public static float[] getCircleVertices(int xStart, int yStart, int radius, int precision) {
        return getEllipseVertices(xStart, yStart, radius, radius, precision);
    }

    public static float[] getEllipseVertices(int xStart, int yStart, int xRadius, int yRadius, int precision) {
        int step = 360 / precision;
        if (step < 1) {
            step = 1;
        }
        float[] vertices = new float[(360 / step) * 2 + 2 + (360 % step == 0 ? 0 : 2)];
        int j = 1;
        vertices[0] = xStart + xRadius;
        vertices[1] = yStart;
        for (int i = 360; i > 0; i -= step) {
            vertices[j * 2] = xStart + (float) Methods.xRadius(i, xRadius);
            vertices[j * 2 + 1] = yStart + (float) Methods.yRadius(i, yRadius);
            j++;
        }
        return vertices;
    }

    public static void drawCircleSector(int xStart, int yStart, int radius, int startAngle, int endAngle, int precision) {
        drawEllipseSector(xStart, yStart, radius, radius, startAngle, endAngle, precision);
    }

    public static void drawEllipseSector(int xStart, int yStart, int xRadius, int yRadius, int startAngle, int endAngle, int precision) {
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleFanStream(getEllipseSectorVertices(xStart, yStart, xRadius, yRadius, startAngle, endAngle, precision));
        regularShader.setUseTexture(true);
    }

    public static float[] getCircleSectorVertices(int xStart, int yStart, int radius, int startAngle, int endAngle, int precision) {
        return getEllipseSectorVertices(xStart, yStart, radius, radius, startAngle, endAngle, precision);
    }

    public static float[] getEllipseSectorVertices(int xStart, int yStart, int xRadius, int yRadius, int startAngle, int endAngle, int precision) {
        if (endAngle < startAngle) {
            endAngle += 360;
        }
        int step = (endAngle - startAngle) / precision;
        if (step <= 0) {
            step = 1;
        }
        float[] vertices = new float[((endAngle - startAngle) / step) * 2 + 2 + ((endAngle - startAngle) % step == 0 ? 0 : 2)];
        int j = 1;
        vertices[0] = xStart;
        vertices[1] = yStart;
        for (int i = endAngle; i > startAngle; i -= step) {
            vertices[j * 2] = xStart + (float) Methods.xRadius(i, xRadius);
            vertices[j * 2 + 1] = yStart + (float) Methods.yRadius(i, yRadius);
            j++;
        }
        return vertices;
    }

    public static void drawEllipseBow(int xStart, int yStart, int xRadius, int yRadius, int width, int startAngle, int endAngle, int precision) {
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(getEllipseBowVertices(xStart, yStart, xRadius, yRadius, width, startAngle, endAngle, precision));
        regularShader.setUseTexture(true);
    }

    public static void drawBow(int xStart, int yStart, int radius, int width, int startAngle, int endAngle, int precision) {
        drawEllipseBow(xStart, yStart, radius, radius, width, startAngle, endAngle, precision);
    }

    public static float[] getBowVertices(int xStart, int yStart, int radius, int width, int startAngle, int endAngle, int precision) {
        return getEllipseBowVertices(xStart, yStart, radius, radius, width, startAngle, endAngle, precision);
    }

    public static float[] getEllipseBowVertices(int xStart, int yStart, int xRadius, int yRadius, int width, int startAngle, int endAngle, int precision) {
        if (startAngle > endAngle) {
            int tmp = startAngle;
            startAngle = endAngle;
            endAngle = tmp;
        }
        int step = (endAngle - startAngle) / precision;
        if (step <= 0) {
            step = 1;
        }
        float[] vertices = new float[((endAngle - startAngle) / step) * 4 + 4 + ((endAngle - startAngle) % step == 0 ? 0 : 4)];
        int j = 0;
        for (int i = startAngle; i < endAngle; i += step) {
            vertices[j * 4] = xStart + (float) Methods.xRadius(i, xRadius);
            vertices[j * 4 + 1] = yStart + (float) Methods.yRadius(i, yRadius);
            vertices[j * 4 + 2] = xStart + (float) Methods.xRadius(i, xRadius - width);
            vertices[j * 4 + 3] = yStart + (float) Methods.yRadius(i, yRadius - width);
            j++;
        }
        vertices[j * 4] = xStart + (float) Methods.xRadius(endAngle, xRadius);
        vertices[j * 4 + 1] = yStart + (float) Methods.yRadius(endAngle, yRadius);
        vertices[j * 4 + 2] = xStart + (float) Methods.xRadius(endAngle, xRadius - width);
        vertices[j * 4 + 3] = yStart + (float) Methods.yRadius(endAngle, yRadius - width);
        return vertices;
    }

    public static void drawRing(int xStart, int yStart, int radius, int width, int precision) {
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(getRingVertices(xStart, yStart, radius, width, precision));
        regularShader.setUseTexture(true);
    }

    public static float[] getRingVertices(int xStart, int yStart, int radius, int width, int precision) {
        int step = 360 / precision;
        if (step < 1) {
            step = 1;
        }
        float[] vertices = new float[(360 / step) * 4 + 4 + (360 % step == 0 ? 0 : 4)];
        int j = 0;
        for (int i = 0; i < 360; i += step) {
            vertices[j * 4] = xStart + (float) Methods.xRadius(i, radius);
            vertices[j * 4 + 1] = yStart + (float) Methods.yRadius(i, radius);
            vertices[j * 4 + 2] = xStart + (float) Methods.xRadius(i, radius - width);
            vertices[j * 4 + 3] = yStart + (float) Methods.yRadius(i, radius - width);
            j++;
        }
        vertices[j * 4] = xStart + (float) Methods.xRadius(360, radius);
        vertices[j * 4 + 1] = yStart + (float) Methods.yRadius(360, radius);
        vertices[j * 4 + 2] = xStart + (float) Methods.xRadius(360, radius - width);
        vertices[j * 4 + 3] = yStart + (float) Methods.yRadius(360, radius - width);
        return vertices;
    }

    public static void drawLineWidth(int xStart, int yStart, int xDelta, int yDelta, int width) {
        int angle = (int) Methods.pointAngleClockwise(xStart, yStart, xStart + xDelta, yStart + yDelta) + 90;
        int xWidth = (int) Methods.xRadius(angle, width / 2);
        int yWidth = (int) Methods.yRadius(angle, width / 2);
        data4[0] = xStart + xWidth;
        data4[1] = yStart + yWidth;
        data4[2] = xStart + xDelta + xWidth;
        data4[3] = yStart + yDelta + yWidth;
        data4[4] = xStart - xWidth;
        data4[5] = yStart - yWidth;
        data4[6] = xStart + xDelta - xWidth;
        data4[7] = yStart + yDelta - yWidth;
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(data4);
        regularShader.setUseTexture(true);
    }

    public static void drawLine(int xStart, int yStart, int xDelta, int yDelta) {
        drawLineWidth(xStart, yStart, xDelta, yDelta, 1);
    }

    public static void drawShapeLit(Appearance appearance, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadow(1);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeLitFromVbo(FrameBufferObject appearance, VertexBufferObject vbo, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowFromVbo(1, vbo);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBlackFromVbo(FrameBufferObject appearance, VertexBufferObject vbo, float darkValue, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowFromVbo(darkValue, vbo);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBottomLit(FrameBufferObject appearance, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottom(1);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBottomLitFromVBO(FrameBufferObject appearance, VertexBufferObject vbo, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottomFromVbo(1, vbo);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapePartLit(Appearance appearance, float xPosition, float yPosition, int partXStart, int partXEnd) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowPart(partXStart, partXEnd, 1);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBottomPartLit(FrameBufferObject appearance, float xPosition, float yPosition, int partXStart, int partXEnd) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottomPart(partXStart, partXEnd, 1);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapePartLitFromVbo(FrameBufferObject appearance, VertexBufferObject vbo, float xPosition, float yPosition, int partXStart, int
            partXEnd) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowPartFromVbo(partXStart, partXEnd, 1, vbo);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapePartBlackFromVbo(FrameBufferObject appearance, VertexBufferObject vbo, float darkValue, float xPosition, float yPosition, int
            partXStart, int partXEnd) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowPartFromVbo(partXStart, partXEnd, darkValue, vbo);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBottomPartLitFromVbo(FrameBufferObject appearance, VertexBufferObject vbo, float xPosition, float yPosition, int partXStart,
                                                     int partXEnd) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottomPartFromVbo(partXStart, partXEnd, 1, vbo);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBlack(Appearance appearance, float darkValue, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadow(darkValue);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeTopBlack(FrameBufferObject appearance, float darkValue, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowTop(darkValue);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeTopBlackFromVbo(FrameBufferObject appearance, VertexBufferObject vbo, float darkValue, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowTopFromVbo(darkValue, vbo);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBottomBlack(FrameBufferObject appearance, float darkValue, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottom(darkValue);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBottomBlackFromVbo(FrameBufferObject appearance, VertexBufferObject vbo, float darkValue, float xPosition, float yPosition) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottomFromVbo(darkValue, vbo);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }


    public static void drawShapePartBlack(Appearance appearance, float darkValue, float xPosition, float yPosition, int partXStart, int partXEnd) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowPart(partXStart, partXEnd, darkValue);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBottomPartBlack(FrameBufferObject appearance, float darkValue, float xPosition, float yPosition, int partXStart, int partXEnd) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottomPart(partXStart, partXEnd, darkValue);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void drawShapeBottomPartBlackFromVbo(FrameBufferObject appearance, VertexBufferObject vbo, float darkValue, float xPosition, float
            yPosition, int partXStart, int partXEnd) {
        ShadowDrawer.renderCurrentVBO();
        shadowShader.translate(xPosition, yPosition);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottomPartFromVbo(partXStart, partXEnd, darkValue, vbo);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        shadowShader.resetTransformationMatrix();
    }

    public static void setShaders() {
        regularShader = new RegularShader();
        shadowShader = new ShadowShader();
        regularShader.start();
        float[] vertices = {
                0, 0,
                0, 20,
                20, 0,
                20, 20,
        };
        float[] textureCoords = {
                0, 0,
                0, 1f,
                1f, 1f,
                1f, 0,
        };
        int[] indices = {0, 1, 3, 2};
        streamVBO = VertexBufferObject.create(vertices, textureCoords, indices);
        tileVBO = VertexBufferObject.create(vertices, textureCoords, indices);
        float[] positions = {0, 0, 0, 1, 1, 0,};
        float[] colors = {1, 1, 1, 1, 1, 1, 1, 1, 1};
        grassVBO = VertexBufferObject.createColored(positions, colors);
        shadowVBO = VertexBufferObject.createShaded(positions, colors);
        float[] screenVertices = {
                0, 0, 0, 0, 0, 0, 0, 0
        };
        float[] textureCoordinates = {
                0, 0, 0, 0, 0, 0, 0, 0
        };
        int[] screenIndices = {0, 1, 3, 2};
        screenVBO = VertexBufferObject.create(screenVertices, textureCoordinates, screenIndices);
    }

    public static void cleanUp() {
        if (regularShader != null) {
            regularShader.cleanUp();
            regularShader = null;
        }
        if (shadowShader != null) {
            shadowShader.cleanUp();
            shadowShader = null;
        }
        streamVertexData.clear();
        streamColorData.clear();
        streamIndexData.clear();
        if (streamVBO != null) {
            streamVBO.clear();
            streamVBO = null;
        }
        if (tileVBO != null) {
            tileVBO.clear();
            tileVBO = null;
        }
        if (grassVBO != null) {
            grassVBO.clear();
            grassVBO = null;
        }
        if (shadowVBO != null) {
            shadowVBO.clear();
            shadowVBO = null;
        }
        if (screenVBO != null) {
            screenVBO.clear();
            screenVBO = null;
        }
    }

    public static void setOrtho(float left, float right, float bottom, float top) {
        shadowShader.setOrtho(left, right, bottom, top);
        regularShader.setOrtho(left, right, bottom, top);
    }

    public static void resetOrtho() {
        shadowShader.resetOrtho();
        regularShader.resetOrtho();
    }
}
