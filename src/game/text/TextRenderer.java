/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public class TextRenderer extends TextEvent {

    protected final int x, y, end;
    protected final String text;
    protected final float height;
    protected final FontHandler font;
    protected final TextController control;
    protected final Color color;

    TextRenderer(String text, int start, int startX, int lineNum, Color color, FontHandler font, TextController tc) {
        super(start, lineNum);
        this.text = text;
        this.font = font;
        x = startX;
        height = (float) (font.getHeight() * 1.2);
        y = (int) (font.getHeight() * 1.2 * lineNum);
        end = text.length();
        control = tc;
        this.color = color;
    }

    int getWidth() {
        return font.getWidth(text);
    }

    boolean isVisible(int index, int lineNum) {
        return index >= start;
    }

    Color changeColor(Color base, int lineNum) {
        base.a = (lineNum == control.getCurrentRow() && control.isFlushing() ? Math.max(0f, 1f - 3 * control.getChange()) : 1f);
        return base;
    }

    @Override
    void event(int index, int lineNum) {
        if (isVisible(index, lineNum)) {
            int i = index - start + 1;
            if (i < end) {
                font.drawLine(text.substring(0, i), x, y, changeColor(color, lineNum));
            } else {
                font.drawLine(text, x, y, changeColor(color, lineNum));
            }
        }
    }
}
