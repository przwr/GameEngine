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

    final int x;
    final int y;
    int end;
    String text;
    String unaltered;
    final FontHandler font;
    final TextController control;
    final Color color;
    private final float height;

    TextRenderer(String text, int start, int startX, int lineNum, Color color, FontHandler font, TextController tc, boolean isAltered) {
        super(start, lineNum);
        this.text = text;
        if (isAltered) {
            unaltered = text;
        }
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

    boolean isVisible(int index) {
        return index > start;
    }

    void alterText(int index, String added) {
        int i = index - start + 1;
        text = unaltered.substring(0, i) + added + unaltered.substring(i);
        end = text.length();
    }
    
    Color changeColor(Color base, int lineNum) {
        base.a = (lineNum == control.getCurrentRow() && control.isFlushing() ? Math.max(0f, 1f - 3 * control.getChange()) : 1f);
        return base;
    }

    @Override
    void event(int index, int lineNum) {
        if (isVisible(index)) {
            int i = index - start + 1;
            if (i < end) {
                font.drawLine(text.substring(0, i), x, y, changeColor(color, lineNum));
            } else {
                font.drawLine(text, x, y, changeColor(color, lineNum));
            }
        }
    }
}
