/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import org.newdawn.slick.Color;

/**
 * @author Wojtek
 */
class TextRenderer extends TextEvent {

    int x;
    final int y;
    String text;
    final FontHandler font;
    final TextController control;
    final Color color;
    private final float height;

    private String alterer;

    TextRenderer(String text, TextEvent previous, int lineNum, Color color, FontHandler font, TextController tc) {
        super(previous, text.length());
        this.text = text;
        this.font = font;
        height = (float) (font.getHeight() * 1.2);
        y = (int) (font.getHeight() * 1.2 * lineNum);
        control = tc;
        this.color = color;
    }

    public void setAlterer(String alterer) {
        this.alterer = alterer;
    }

    @Override
    int getX(int y) {
        if (this.y == y) {
            if (previous == null) {
                return font.getWidth(text);
            } else {
                return previous.getX(y) + font.getWidth(text);
            }
        } else {
            return 0;
        }
    }

    void setX() {
        if (previous == null) {
            x = 0;
        } else {
            x = previous.getX(y);
        }
    }

    int getWidth() {
        return font.getWidth(text);
    }

    boolean isVisible(int index) {
        return index >= start;
    }

    Color changeColor(Color base, int lineNum) {
        base.a = (lineNum == control.getCurrentRow() && control.isFlushing() ? Math.max(0f, 1f - 3 * control.getChange()) : 1f);
        return base;
    }

    @Override
    void innerEvent(int index, int lineNum) {
        setX();
        if (isVisible(index)) {
            int i = index - start;
            if (text.isEmpty()) {
                text = control.getWriter(alterer).write();
                length = text.length();
            }
            if (i < text.length()) {
                font.drawLine(text.substring(0, i), x, y, changeColor(color, lineNum));
            } else {
                font.drawLine(text, x, y, changeColor(color, lineNum));
            }
        }
    }
}
