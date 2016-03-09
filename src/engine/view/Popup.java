/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.view;

import engine.Main;
import engine.utilities.Methods;
import game.Settings;
import game.text.FontHandler;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

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
        glBegin(GL_TRIANGLES);
        renderButtonArea();
        renderTitleAndButtonBackground();
        renderBorders();
        glEnd();
        glEnable(GL_BLEND);
        renderLine(smallFont, WIDTH_HALF, HEIGHT_HALF + height / 2, "[ENTER]", Color.black);
        renderLine(bigFont, WIDTH_HALF, HEIGHT_HALF - height / 2 + space, Main.getTitle(), Color.black);
        glEnable(GL_TEXTURE_2D);
    }

    private void renderBackground() {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_TRIANGLES);
        glColor3f(1f, 1f, 1f);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 - space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space);

        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space);
        glEnd();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void renderButtonArea() {
        glColor3f(1f, 1f, 1f);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 - space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2);

        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space);
    }

    private void renderTitleAndButtonBackground() {
        glColor3f(0.8f, 0.8f, 0.8f);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2);

        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space);

        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2);

        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2 - shift);
    }

    private void renderBorders() {
        glColor3f(0.5f, 0.5f, 0.5f);
        renderOuterBorders();
        renderInnerBorders();
    }

    private void renderOuterBorders() {
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 - border);

        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 - border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 - border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2);


        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border);

        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2);

        glVertex2f(WIDTH_HALF - width / 2 - border, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 - border);

        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 - border);
        glVertex2f(WIDTH_HALF - width / 2 - border, HEIGHT_HALF - height / 2 - border);
        glVertex2f(WIDTH_HALF - width / 2 - border, HEIGHT_HALF + height / 2 + border);

        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF + width / 2 + border, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF + width / 2 + border, HEIGHT_HALF - height / 2 - border);

        glVertex2f(WIDTH_HALF + width / 2 + border, HEIGHT_HALF - height / 2 - border);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 - border);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border);
    }

    private void renderInnerBorders() {
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space + border);

        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space);

        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2);

        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift);

        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift - border);
        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift - border);
        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift);

        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift - border);

        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2);

        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift);
    }

    private void renderLine(FontHandler font, int x, int y, String message, Color color) {
        font.drawLine(message, x - font.getWidth(message) / 2, y - font.getHeight(), color);
    }

    public int getId() {
        return messagesPointer;
    }
}
