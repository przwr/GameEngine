/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.utilities;

import engine.matrices.MatrixMath;
import game.ScreenPlace;
import game.text.FontHandler;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import sprites.Appearance;
import sprites.fbo.FrameBufferObject;
import sprites.shaders.RegularShader;
import sprites.shaders.ShadowShader;
import sprites.shaders.StaticShader;
import sprites.vbo.VertexBufferObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class Drawer {

    private static final Texture font = loadFontTexture();
    public static VertexBufferObject streamVBO;
    public static VertexBufferObject grassVBO;
    public static VertexBufferObject screenVBO;
    public static ArrayList<Float> streamData = new ArrayList<>(60);
    public static StaticShader staticShader;
    public static RegularShader regularShader;
    public static ShadowShader shadowShader;
    public static int displayWidth, displayHeight;
    private static float xCurrent, yCurrent;
    private static Color currentColor = Color.white;

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
        glColor4f(currentColor.r, currentColor.g, currentColor.b, 1.0f);
//        spriteShader.start();
        regularShader.loadColourModifier(new Vector4f(currentColor.r, currentColor.g, currentColor.b, 1.0f));
//        spriteShader.stop();
    }

    public static void setColorAlpha(float alpha) {
        glColor4f(currentColor.r, currentColor.g, currentColor.b, alpha);
//        spriteShader.start();
        regularShader.loadColourModifier(new Vector4f(currentColor.r, currentColor.g, currentColor.b, alpha));
//        spriteShader.stop();
    }

    public static void setColorStatic(Color color) {
        glColor4f(color.r, color.g, color.b, color.a);
//        spriteShader.start();
        regularShader.loadColourModifier(new Vector4f(color.r, color.g, color.b, color.a));
//        spriteShader.stop();
    }

    public static void setColorBlended(Color color) {
        glColor4f(color.r * currentColor.r, color.g * currentColor.g, color.b * currentColor.b, color.a);
//        spriteShader.start();
        regularShader.loadColourModifier(new Vector4f(color.r * currentColor.r, color.g * currentColor.g, color.b * currentColor.b, color.a));
//        spriteShader.stop();
    }

    public static void setColorStatic(float r, float g, float b, float a) {
        glColor4f(r, g, b, a);
//        spriteShader.start();
        regularShader.loadColourModifier(new Vector4f(r, g, b, a));
//        spriteShader.stop();
    }

    public static void setColorBlended(float r, float g, float b, float a) {
        glColor4f(r * currentColor.r, g * currentColor.g, b * currentColor.b, a);
//        spriteShader.start();
        regularShader.loadColourModifier(new Vector4f(r * currentColor.r, g * currentColor.g, b * currentColor.b, a));
//        spriteShader.stop();
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

    public static void setCentralPoint() {  //Miejsce do którego można wrócić
        xCurrent = 0;
        yCurrent = 0;
    }

    public static void translate(float x, float y) {
        xCurrent += x;
        yCurrent += y;
        glTranslatef((int) x, (int) y, 0f);
    }

    public static void returnToCentralPoint() {
        glTranslatef(-xCurrent, -yCurrent, 0f);
        setCentralPoint();
    }

    public static void drawRectangleInShade(int xStart, int yStart, int width, int height, float color) {
        setColorStatic(color, color, color, 1);
        if (width < 0) {
            width = -width;
            xStart -= width;
        }
        float[] data = {
                xStart, yStart,
                xStart, yStart + height,
                xStart + width, yStart,
                xStart + width, yStart + height,
        };
//        shadowShader.start();
        shadowShader.loadTextureShift(0, 0);
        shadowShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        shadowShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        shadowShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(data);
        shadowShader.setUseTexture(true);
        setColorStatic(1, 1, 1, 1);
    }

    public static void drawRectangleInBlack(int xStart, int yStart, int width, int height) {
        setColorStatic(0, 0, 0, 1);
        if (width < 0) {
            width = -width;
            xStart -= width;
        }
        float[] data = {
                xStart, yStart,
                xStart, yStart + height,
                xStart + width, yStart,
                xStart + width, yStart + height,
        };
//        shadowShader.start();
        shadowShader.loadTextureShift(0, 0);
        shadowShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        shadowShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        shadowShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(data);
        shadowShader.setUseTexture(true);
        setColorStatic(1, 1, 1, 1);
    }

    public static void drawTextureTriangle(int xA, int yA, int xB, int yB, int xC, int yC) {
        float[] data = {
                xA, yA,
                xB, yB,
                0, 0,
                xC, yC,
        };
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        streamVBO.updateVerticesStream(data);
        streamVBO.renderTextured(0, 3);
//        spriteShader.stop();
    }

    public static void drawTriangle(int xA, int yA, int xB, int yB, int xC, int yC) {
        float[] data = {
                xA, yA,
                xB, yB,
                xC, yC,
        };
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStream(data);
        regularShader.setUseTexture(true);
//        spriteShader.stop();
    }

    public static void drawRectangle(int xStart, int yStart, int width, int height) {
        float[] data = {
                xStart, yStart,
                xStart, yStart + height,
                xStart + width, yStart,
                xStart + width, yStart + height,
        };
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(data);
        regularShader.setUseTexture(true);
//        spriteShader.stop();
    }

    public static void drawRectangleBorder(int xStart, int yStart, int width, int height) {
        float[] data = {
                xStart, yStart,
                xStart, yStart + height,
                xStart + width, yStart + height,
                xStart + width, yStart,
        };
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        regularShader.setUseTexture(false);
        streamVBO.renderLineLoopStream(data);
        regularShader.setUseTexture(true);
//        spriteShader.stop();
    }

    public static void drawTextureQuad(int xA, int yA, int xB, int yB, int xC, int yC, int xD, int yD) {
        float[] data = {
                xD, yD,
                xA, yA,
                xB, yB,
                xC, yC,
        };
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        streamVBO.updateVerticesStream(data);
        streamVBO.renderTextured(0, 6);
//        spriteShader.stop();
    }

    public static void drawCircle(int xStart, int yStart, int radius, int precision) {
        drawEllipse(xStart, yStart, radius, radius, precision);
    }

    public static void drawEllipse(int xStart, int yStart, int xRadius, int yRadius, int precision) {
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleFanStream(getEllipseVertices(xStart, yStart, xRadius, yRadius, precision));
        regularShader.setUseTexture(true);
//        spriteShader.stop();


    }

    public static float[] getCircleVertices(int xStart, int yStart, int radius, int precision) {
        return getEllipseVertices(xStart, yStart, radius, radius, precision);
    }

    public static float[] getEllipseVertices(int xStart, int yStart, int xRadius, int yRadius, int precision) {
        int step = 360 / precision;
        if (step < 1) {
            step = 1;
        }
        float[] vertices = new float[(360 / step) * 2 + 4 + (360 % step == 0 ? 0 : 2)];
        int j = 1;
        vertices[0] = xStart + xRadius;
        vertices[1] = yStart;
        for (int i = 360; i > 0; i -= step) {
            vertices[j * 2] = xStart + (float) Methods.xRadius(i, xRadius);
            vertices[j * 2 + 1] = yStart + (float) Methods.yRadius(i, yRadius);
            j++;
        }
        vertices[j * 2] = xStart;
        vertices[j * 2 + 1] = yStart;
        return vertices;
    }

    public static void drawCircleSector(int xStart, int yStart, int radius, int startAngle, int endAngle, int precision) {
        drawEllipseSector(xStart, yStart, radius, radius, startAngle, endAngle, precision);
    }

    public static void drawEllipseSector(int xStart, int yStart, int xRadius, int yRadius, int startAngle, int endAngle, int precision) {
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleFanStream(getEllipseSectorVertices(xStart, yStart, xRadius, yRadius, startAngle, endAngle, precision));
        regularShader.setUseTexture(true);
//        spriteShader.stop();
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
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(getEllipseBowVertices(xStart, yStart, xRadius, yRadius, width, startAngle, endAngle, precision));
        regularShader.setUseTexture(true);
//        spriteShader.stop();
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
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(getRingVertices(xStart, yStart, radius, width, precision));
        regularShader.setUseTexture(true);
//        spriteShader.stop();
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
        float[] data = {
                xStart + xWidth, yStart + yWidth,
                xStart + xDelta + xWidth, yStart + yDelta + yWidth,
                xStart - xWidth, yStart - yWidth,
                xStart + xDelta - xWidth, yStart + yDelta - yWidth,
        };
//        spriteShader.start();
        regularShader.loadTextureShift(0, 0);
        regularShader.loadSizeModifier(Appearance.ZERO_VECTOR);
        regularShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
        regularShader.setUseTexture(false);
        streamVBO.renderTriangleStripStream(data);
        regularShader.setUseTexture(true);
//        spriteShader.stop();
    }

    public static void drawLine(int xStart, int yStart, int xDelta, int yDelta) {
        drawLineWidth(xStart, yStart, xDelta, yDelta, 1);
    }

    public static void drawShapeInShade(Appearance appearance, float color) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadow(color);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }


    public static void drawShapeTopInShade(FrameBufferObject appearance, float color) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowTop(color);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }


    public static void drawShapeBottomInShade(FrameBufferObject appearance, float color) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottom(color);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawShapePartInShade(Appearance appearance, float color, int partXStart, int partXEnd) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowPart(partXStart, partXEnd, color);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawShapeBottomPartInShade(FrameBufferObject appearance, float color, int partXStart, int partXEnd) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottomPart(partXStart, partXEnd, color);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawShapeInBlack(Appearance appearance) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadow(0);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawShapeTopInBlack(FrameBufferObject appearance) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowTop(0);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawShapeBottomInBlack(FrameBufferObject appearance) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottom(0);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawShapePartInBlack(Appearance appearance, int partXStart, int partXEnd) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowPart(partXStart, partXEnd, 0);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void drawShapeBottomPartInBlack(FrameBufferObject appearance, int partXStart, int partXEnd) {
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        appearance.renderShadowBottomPart(partXStart, partXEnd, 0);
        glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void renderStringCentered(String message, double x, double y, FontHandler font, Color color) {
        bindFontTexture();
        font.drawLine(message, (float) (x - font.getWidth(message) / 2),
                (float) (y - (4 * font.getHeight()) / 3), color);
    }

    public static void renderString(String message, double x, double y, FontHandler font, Color color) {
        bindFontTexture();
        font.drawLine(message, (int) x, (int) y, color);
    }

    public static void setShaders() {
        staticShader = new StaticShader();
        regularShader = new RegularShader();
        shadowShader = new ShadowShader();
        shadowShader.start();
        shadowShader.setUseTexture(true);
        regularShader.start();
        regularShader.setUseTexture(true);
//        spriteShader.stop();
//        shadowShader.stop();
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
        float[] positions = {0, 0, 0, 1, 1, 0,};
        float[] colors = {1, 1, 1, 1, 1, 1, 1, 1, 1};
        grassVBO = VertexBufferObject.createColored(positions, colors);
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
        if (staticShader != null) {
            staticShader.cleanUp();
            staticShader = null;
        }
        if (staticShader != null) {
            regularShader.cleanUp();
            regularShader = null;
        }
        if (shadowShader != null) {
            shadowShader.cleanUp();
            shadowShader = null;
        }
        if (streamVBO != null) {
            streamVBO.clear();
            streamVBO = null;
            streamData.clear();
        }
        if (grassVBO != null) {
            grassVBO.clear();
            grassVBO = null;
        }
        if (screenVBO != null) {
            screenVBO.clear();
            screenVBO = null;
        }
    }
}
