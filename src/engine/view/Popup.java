/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.view;

import engine.Main;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.Settings;
import game.place.Place;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Popup {

    private static final String[] messages = new String[100];
    public static TextPiece text, title;
    private static int WIDTH_HALF, HEIGHT_HALF;
    private final int smallFont = 22;
    private final int bigFont = 28;
    private final int middleOk;
    private final int border = 3;
    private int messagesPointer = -1;
    private int width;
    private int height;
    private int space;
    private int shift;

    public Popup() {
        text = new TextPiece("[ENTER]", smallFont, TextMaster.getFont("Lato-Regular"), Display.getWidth(), true);
        text.setColor(0, 0, 0);
        title = new TextPiece(Main.getTitle(), bigFont, TextMaster.getFont("Lato-Regular"), Display.getWidth(), true);
        title.setColor(0, 0, 0);
        middleOk = text.getWidth();
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
        int current;
        shift = (int) (Settings.nativeScale * smallFont / 0.75f);
        space = (int) (Settings.nativeScale * bigFont / 0.75f);
        title.setText(Main.getTitle());
        int biggest = title.getWidth() + (int) (Settings.nativeScale * Place.tileHalf);
        for (String line : lines) {
            current = text.getTextWidth(line, text.getFontSize()) + (int) (Settings.nativeScale * Place.tileHalf);
            if (current > biggest) {
                biggest = current;
            }
        }
        width = Methods.interval(WIDTH_HALF >> 2, biggest + shift, (WIDTH_HALF << 1) - (border << 1));
        height = Methods.interval(0, space + shift + shift * (lines.length + 1) + 2 * border, (HEIGHT_HALF << 1) - (border << 1));
        renderBackground();
        TextMaster.startRenderText();
        for (int i = 0; i < lines.length; i++) {
            text.setText(lines[i]);
            TextMaster.render(text, 0, HEIGHT_HALF - height / 2 + space + shift / 2 + shift * i + border);
        }
        TextMaster.endRenderText();
        glDisable(GL_BLEND);
        renderButtonArea();
        renderTitleAndButtonBackground();
        renderBorders();
        glEnable(GL_BLEND);
        TextMaster.startRenderText();
        text.setText("[ENTER]");
        TextMaster.render(text, 0, HEIGHT_HALF + height / 2 - (int) (Settings.nativeScale * smallFont / 0.75f));
        TextMaster.render(title, 0, HEIGHT_HALF - height / 2);
        TextMaster.endRenderText();
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
            Drawer.regularShader.resetTransformationMatrix();
            Drawer.regularShader.setUseTexture(false);
            Drawer.streamVBO.renderTriangleStream(data);
            Drawer.regularShader.setUseTexture(true);
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
            Drawer.regularShader.resetTransformationMatrix();
            Drawer.regularShader.setUseTexture(false);
            Drawer.streamVBO.renderTriangleStream(data);
            Drawer.regularShader.setUseTexture(true);
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
            Drawer.regularShader.resetTransformationMatrix();
            Drawer.regularShader.setUseTexture(false);
            Drawer.streamVBO.renderTriangleStream(data);
            Drawer.regularShader.setUseTexture(true);
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
            Drawer.regularShader.resetTransformationMatrix();
            Drawer.regularShader.setUseTexture(false);
            Drawer.streamVBO.renderTriangleStream(data);
            Drawer.regularShader.setUseTexture(true);
        } else {
            Drawer.setShaders();
        }
    }

    public int getId() {
        return messagesPointer;
    }
}
