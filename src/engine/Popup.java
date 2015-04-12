/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.Settings;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class Popup {

    private static final int WIDTH_HALF = Display.getWidth() / 2, HEIGHT_HALF = Display.getHeight() / 2;
    private static final String[] messages = new String[100];
    private final FontBase fonts;
    private final int middleOk;
    private int messagesPointer = -1;

    private int width, height, space, shift, biggest, border = 3;
    private String lines[];

    public Popup(String font) {
        fonts = new FontBase(2);
        fonts.add(font, (int) (Settings.nativeScale * 22));
        fonts.add(font, (int) (Settings.nativeScale * 28));
        middleOk = fonts.getFont(0).getWidth("[ENTER]");
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

    public void renderMesagges() {
        for (int i = 0; i <= messagesPointer; i++) {
            renderMessage(i);
        }
    }

    public void renderMessage(int id) {
        lines = messages[id].split("\\r?\\n");
        shift = fonts.getFont(0).getHeight();
        space = fonts.getFont(1).getHeight();
        biggest = fonts.getFont(1).getWidth(Main.getTitle());
        for (String line : lines) {
            biggest = fonts.getFont(0).getWidth(line) > biggest ? fonts.getFont(0).getWidth(line) : biggest;
        }
        width = Methods.interval(WIDTH_HALF >> 2, biggest + shift, (WIDTH_HALF << 1) - (border << 1));
        height = Methods.interval(0, space + shift + (int) (shift * (lines.length + 1)) + 2 * border, (HEIGHT_HALF << 1) - (border << 1));
        renderBackground();
        for (int i = 0; i < lines.length; i++) {
            renderLine(0, WIDTH_HALF, HEIGHT_HALF - height / 2 + space + shift / 2 + shift * (i + 1) + border, lines[i], Color.black);
        }
        glDisable(GL_BLEND);
        glBegin(GL_QUADS);
        renderButtonArea();
        renderTitleAndButtonBackground();
        renderBorders();
        glEnd();
        glEnable(GL_BLEND);
        renderLine(0, WIDTH_HALF, HEIGHT_HALF + height / 2, "[ENTER]", Color.black);
        renderLine(1, WIDTH_HALF, HEIGHT_HALF - height / 2 + space, Main.getTitle(), Color.black);
        glEnable(GL_TEXTURE_2D);
    }

    private void renderBackground() {
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glColor3f(1f, 1f, 1f);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 - space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space);
        glEnd();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void renderButtonArea() {
        glColor3f(1f, 1f, 1f);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 - space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 - space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2);
    }

    private void renderTitleAndButtonBackground() {
        glColor3f(0.8f, 0.8f, 0.8f);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2);

        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2);
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
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 - border);

        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 + border);

        glVertex2f(WIDTH_HALF - width / 2 - border, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 - border);
        glVertex2f(WIDTH_HALF - width / 2 - border, HEIGHT_HALF - height / 2 - border);

        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF + width / 2 + border, HEIGHT_HALF + height / 2 + border);
        glVertex2f(WIDTH_HALF + width / 2 + border, HEIGHT_HALF - height / 2 - border);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 - border);
    }

    private void renderInnerBorders() {
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space);
        glVertex2f(WIDTH_HALF + width / 2, HEIGHT_HALF - height / 2 + space + border);
        glVertex2f(WIDTH_HALF - width / 2, HEIGHT_HALF - height / 2 + space + border);

        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF - middleOk, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2);

        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift - border);
        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift - border);
        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF - middleOk - border, HEIGHT_HALF + height / 2 - shift);

        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2 - shift);
        glVertex2f(WIDTH_HALF + middleOk, HEIGHT_HALF + height / 2);
        glVertex2f(WIDTH_HALF + middleOk + border, HEIGHT_HALF + height / 2);
    }

    private void renderLine(int font, int x, int y, String message, Color color) {
        fonts.getFont(font).drawLine(message, x - fonts.getFont(font).getWidth(message) / 2, y - fonts.getFont(font).getHeight(), color);
    }

    public int getId() {
        return messagesPointer;
    }
}
