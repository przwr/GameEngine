/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class Popup {

    private static final int w2 = Display.getWidth() / 2, h2 = Display.getHeight() / 2;
    private static final String[] messages = new String[100];
    public int i = -1;
    private final FontBase fonts;

    public Popup(String font, float scale) {
        fonts = new FontBase(2);
        fonts.add(font, (int) (scale * 22));
        fonts.add(font, (int) (scale * 28));
    }

    public String popMessage() {
        if (i != -1) {
            i--;
            if (i < 0) {
                Main.pause = false;
                Main.ENTER = true;
            }
            return messages[i + 1];
        }
        return null;
    }

    public void addMessage(String msg) {
        messages[++i] = msg;
    }

    public void renderMesagges() {
        for (int j = 0; j <= i; j++) {
            RenderMessage(j);
        }
    }

    public void RenderMessage(int i) {
        String lines[] = messages[i].split("\\r?\\n");
        int l = 1;
        int shift = fonts.write(0).getHeight();
        int space = fonts.write(1).getHeight();
        int border = 3;
        int biggest = fonts.write(1).getWidth(Main.getTitle());
        int middleOk = fonts.write(0).getWidth("[ENTER]");
        for (String line : lines) {
            biggest = fonts.write(0).getWidth(line) > biggest ? fonts.write(0).getWidth(line) : biggest;
        }
        int wSize = Methods.Interval(w2 >> 2, biggest + shift, (w2 << 1) - (border << 1));
        int hSize = Methods.Interval(0, space + shift + (int) (shift * (lines.length + 1)) + 2 * border, (h2 << 1) - (border << 1));

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        glColor3f(1f, 1f, 1f);
        glVertex2f(w2 - wSize / 2, h2 + hSize / 2 - space);
        glVertex2f(w2 + wSize / 2, h2 + hSize / 2 - space);
        glVertex2f(w2 + wSize / 2, h2 - hSize / 2 + space);
        glVertex2f(w2 - wSize / 2, h2 - hSize / 2 + space);
        glEnd();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for (String line : lines) {
            renderLine(0, w2, h2 - hSize / 2 + space + shift / 2 + shift * l + border, line, Color.black);
            l++;
        }

        glDisable(GL_BLEND);
        glBegin(GL_QUADS);

        glColor3f(1f, 1f, 1f);

        glVertex2f(w2 - wSize / 2, h2 + hSize / 2 - space);
        glVertex2f(w2 + wSize / 2, h2 + hSize / 2 - space);
        glVertex2f(w2 + wSize / 2, h2 + hSize / 2);
        glVertex2f(w2 - wSize / 2, h2 + hSize / 2);

        glColor3f(0.8f, 0.8f, 0.8f);
        glVertex2f(w2 - wSize / 2, h2 - hSize / 2 + space);
        glVertex2f(w2 + wSize / 2, h2 - hSize / 2 + space);
        glVertex2f(w2 + wSize / 2, h2 - hSize / 2);
        glVertex2f(w2 - wSize / 2, h2 - hSize / 2);

        glVertex2f(w2 - middleOk, h2 + hSize / 2 - shift);
        glVertex2f(w2 + middleOk, h2 + hSize / 2 - shift);
        glVertex2f(w2 + middleOk, h2 + hSize / 2);
        glVertex2f(w2 - middleOk, h2 + hSize / 2);

        glColor3f(0.5f, 0.5f, 0.5f);
        glVertex2f(w2 - wSize / 2, h2 - hSize / 2);
        glVertex2f(w2 + wSize / 2, h2 - hSize / 2);
        glVertex2f(w2 + wSize / 2, h2 - hSize / 2 - border);
        glVertex2f(w2 - wSize / 2, h2 - hSize / 2 - border);

        glVertex2f(w2 - wSize / 2, h2 + hSize / 2);
        glVertex2f(w2 + wSize / 2, h2 + hSize / 2);
        glVertex2f(w2 + wSize / 2, h2 + hSize / 2 + border);
        glVertex2f(w2 - wSize / 2, h2 + hSize / 2 + border);

        glVertex2f(w2 - wSize / 2 - border, h2 + hSize / 2 + border);
        glVertex2f(w2 - wSize / 2, h2 + hSize / 2 + border);
        glVertex2f(w2 - wSize / 2, h2 - hSize / 2 - border);
        glVertex2f(w2 - wSize / 2 - border, h2 - hSize / 2 - border);

        glVertex2f(w2 + wSize / 2, h2 + hSize / 2 + border);
        glVertex2f(w2 + wSize / 2 + border, h2 + hSize / 2 + border);
        glVertex2f(w2 + wSize / 2 + border, h2 - hSize / 2 - border);
        glVertex2f(w2 + wSize / 2, h2 - hSize / 2 - border);

        glVertex2f(w2 - wSize / 2, h2 - hSize / 2 + space);
        glVertex2f(w2 + wSize / 2, h2 - hSize / 2 + space);
        glVertex2f(w2 + wSize / 2, h2 - hSize / 2 + space + border);
        glVertex2f(w2 - wSize / 2, h2 - hSize / 2 + space + border);

        glVertex2f(w2 - middleOk - border, h2 + hSize / 2 - shift);
        glVertex2f(w2 - middleOk, h2 + hSize / 2 - shift);
        glVertex2f(w2 - middleOk, h2 + hSize / 2);
        glVertex2f(w2 - middleOk - border, h2 + hSize / 2);

        glVertex2f(w2 - middleOk - border, h2 + hSize / 2 - shift - border);
        glVertex2f(w2 + middleOk + border, h2 + hSize / 2 - shift - border);
        glVertex2f(w2 + middleOk + border, h2 + hSize / 2 - shift);
        glVertex2f(w2 - middleOk - border, h2 + hSize / 2 - shift);

        glVertex2f(w2 + middleOk + border, h2 + hSize / 2 - shift);
        glVertex2f(w2 + middleOk, h2 + hSize / 2 - shift);
        glVertex2f(w2 + middleOk, h2 + hSize / 2);
        glVertex2f(w2 + middleOk + border, h2 + hSize / 2);

        glEnd();

        glEnable(GL_BLEND);
        renderLine(0, w2, h2 + hSize / 2, "[ENTER]", Color.black);
        renderLine(1, w2, h2 - hSize / 2 + space, Main.getTitle(), Color.black);

        glEnable(GL_TEXTURE_2D);
    }

    public void renderLine(int i, int x, int y, String ms, Color color) {
        fonts.write(i).drawString(x - fonts.write(i).getWidth(ms) / 2, y - fonts.write(i).getHeight(), ms, color);
    }
}
