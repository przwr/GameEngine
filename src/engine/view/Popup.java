/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.view;

import engine.Main;
import engine.matrices.MatrixMath;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.Settings;
import game.text.FontHandler;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import sprites.Appearance;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Popup {

    private static final String[] messages = new String[100];
    private static int WIDTH_HALF, HEIGHT_HALF;
    private final FontHandler smallFont;
    private final FontHandler bigFont;
    private final int middleOk;
    private final int border = 3;
    private int messagesPointer = -1;
    private int width;
    private int height;
    private int space;
    private int shift;

    public Popup(String font) {
        smallFont = Settings.fonts.getFont(font, (int) (Settings.nativeScale * 22));
        bigFont = Settings.fonts.getFont(font, (int) (Settings.nativeScale * 28));
        middleOk = smallFont.getWidth("[ENTER]");
        WIDTH_HALF = Display.getWidth() / 2;
        HEIGHT_HALF = Display.getHeight() / 2;
    }

    public String popMessage() {
        if (messagesPointer != -1) {
            messagesPointer--;
            if (messagesPointer < 0) {
                Main.pause = false;
                Main.enter = true;
            }
            return messages[messagesPointer + 1];
        }
        return null;
    }

    public void addMessage(String message) {
        messages[++messagesPointer] = message;
    }

    public void renderMessages() {
        for (int i = 0; i <= messagesPointer; i++) {
            renderMessage(i);
        }
    }

    private void renderMessage(int id) {
        String[] lines = messages[id].split("\\r?\\n");
        shift = smallFont.getHeight();
        space = bigFont.getHeight();
        int biggest = bigFont.getWidth(Main.getTitle());
        for (String line : lines) {
            biggest = smallFont.getWidth(line) > biggest ? smallFont.getWidth(line) : biggest;
        }
        width = Methods.interval(WIDTH_HALF >> 2, biggest + shift, (WIDTH_HALF << 1) - (border << 1));
        height = Methods.interval(0, space + shift + shift * (lines.length + 1) + 2 * border, (HEIGHT_HALF << 1) - (border << 1));
        renderBackground();
        for (int i = 0; i < lines.length; i++) {
            renderLine(smallFont, WIDTH_HALF, HEIGHT_HALF - height / 2 + space + shift / 2 + shift * (i + 1) + border, lines[i], Color.black);
        }
        glDisable(GL_BLEND);
        renderButtonArea();
        renderTitleAndButtonBackground();
        renderBorders();
        glEnable(GL_BLEND);
        renderLine(smallFont, WIDTH_HALF, HEIGHT_HALF + height / 2, "[ENTER]", Color.black);
        renderLine(bigFont, WIDTH_HALF, HEIGHT_HALF - height / 2 + space, Main.getTitle(), Color.black);
    }

    private void renderBackground() {
        Drawer.setColorStatic(1f, 1f, 1f, 1f);
        float[] data = {
                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space,
                WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space,
                WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 - space,

                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space,
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space,
                WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space,
        };
        if (Drawer.streamVBO != null) {
            Drawer.spriteShader.start();
            Drawer.spriteShader.loadTextureShift(0, 0);
            Drawer.spriteShader.loadSizeModifier(Appearance.ZERO_VECTOR);
            Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
            Drawer.spriteShader.setUseTexture(false);
            Drawer.streamVBO.renderTriangleStream(data);
            Drawer.spriteShader.setUseTexture(true);
            Drawer.spriteShader.stop();
        } else {
            Drawer.setShaders();
        }
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void renderButtonArea() {
        Drawer.setColorStatic(1f, 1f, 1f, 1f);
        float[] data = {
                WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space,
                WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2,
                WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 - space,
                WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2,
                WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space,
                WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2,
        };
        if (Drawer.streamVBO != null) {
            Drawer.spriteShader.start();
            Drawer.spriteShader.loadTextureShift(0, 0);
            Drawer.spriteShader.loadSizeModifier(Appearance.ZERO_VECTOR);
            Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
            Drawer.spriteShader.setUseTexture(false);
            Drawer.streamVBO.renderTriangleStream(data);
            Drawer.spriteShader.setUseTexture(true);
            Drawer.spriteShader.stop();
        } else {
            Drawer.setShaders();
        }
    }

    private void renderTitleAndButtonBackground() {
        Drawer.setColorStatic(0.8f, 0.8f, 0.8f, 1f);
        float[] data = {
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space,
                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space,
                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2,
//
                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2,
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2,
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space,

                WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2 - shift,
                WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2,
                WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2 - shift,

                WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2,
                WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2 - shift,
                WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2,
        };
        if (Drawer.streamVBO != null) {
            Drawer.spriteShader.start();
            Drawer.spriteShader.loadTextureShift(0, 0);
            Drawer.spriteShader.loadSizeModifier(Appearance.ZERO_VECTOR);
            Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
            Drawer.spriteShader.setUseTexture(false);
            Drawer.streamVBO.renderTriangleStream(data);
            Drawer.spriteShader.setUseTexture(true);
            Drawer.spriteShader.stop();
        } else {
            Drawer.setShaders();
        }
    }

    private void renderBorders() {
        Drawer.setColorStatic(0.5f, 0.5f, 0.5f, 1f);
        float[] data = {
//                OUTER
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2,
                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2,
                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 - border,

                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 - border,
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 - border,
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2,

                WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2,
                WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border,
                WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2,

                WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border,
                WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2,
                WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 + border,

                WIDTH_HALF - width / 2 - border, HEIGHT_HALF + height / 2 + border,
                WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 + border,
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 - border,

                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 - border,
                WIDTH_HALF - width / 2 - border, HEIGHT_HALF - height / 2 - border,
                WIDTH_HALF - width / 2 - border, HEIGHT_HALF + height / 2 + border,

                WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border,
                WIDTH_HALF + width / 2 + border, HEIGHT_HALF + height / 2 + border,
                WIDTH_HALF + width / 2 + border, HEIGHT_HALF - height / 2 - border,

                WIDTH_HALF + width / 2 + border, HEIGHT_HALF - height / 2 - border,
                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 - border,
                WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border,

//                INNER
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space,
                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space + border,
                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space,

                WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space + border,
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space,
                WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space + border,

                WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift,
                WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2,
                WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2 - shift,

                WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2,
                WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift,
                WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2,

                WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift - border,
                WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift,
                WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift - border,

                WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift,
                WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift - border,
                WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift,

                WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift,
                WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2 - shift,
                WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2,

                WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2,
                WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2,
                WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift
        };
        if (Drawer.streamVBO != null) {
            Drawer.spriteShader.start();
            Drawer.spriteShader.loadTextureShift(0, 0);
            Drawer.spriteShader.loadSizeModifier(Appearance.ZERO_VECTOR);
            Drawer.spriteShader.loadTransformationMatrix(MatrixMath.STATIC_MATRIX);
            Drawer.spriteShader.setUseTexture(false);
            Drawer.streamVBO.renderTriangleStream(data);
            Drawer.spriteShader.setUseTexture(true);
            Drawer.spriteShader.stop();
        } else {
            Drawer.setShaders();
        }
    }

    private void renderLine(FontHandler font, int x, int y, String message, Color color) {
        font.drawLine(message, x - font.getWidth(message) / 2, y - font.getHeight(), color);
    }

    public int getId() {
        return messagesPointer;
    }
}
