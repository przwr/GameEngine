/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text.effects;

import engine.utilities.Methods;
import game.Settings;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

/**
 * @author Wojtek
 */
public class TextRenderer extends TextEvent {

    public static TextPiece line;
    final int y;
    final TextController control;
    final Color color;
    int x;
    String text;
    private String alterer;

    TextRenderer(String text, TextEvent previous, int lineNum, Color color, TextController tc) {
        super(previous, text.length());
        this.text = text;
        control = tc;
        if (line == null) {
            line = new TextPiece("", (int) (36 / Settings.nativeScale), TextMaster.getFont("Lato-Regular"), Display.getWidth(), false);
            line.setColor(0, 0, 0);
        }
        y = Methods.roundDouble(line.getFontSize() * Settings.nativeScale * 1.5f * lineNum);
        this.color = color;
    }

    public void setAlterer(String alterer) {
        this.alterer = alterer;
    }

    @Override
    int getX(int y) {
        if (this.y == y) {
            if (previous == null) {
                return line.getTextWidth(text, line.getFontSize());
            } else {
                return previous.getX(y) + line.getTextWidth(text, line.getFontSize());
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

    boolean isVisible(int index) {
        return index >= start;
    }

    Color changeColor(Color base, int lineNum) {
        base.a = (lineNum == control.getCurrentRow() && control.isFlushing() ? Math.max(0f, 1f - 3 * control.getChange()) : 1f);
        return base;
    }

    @Override
    void innerEvent(int index, int lineNum, int xPosition, int yPosition) {
        setX();
        if (isVisible(index)) {
            if (text.isEmpty()) {
                text = control.getWriter(alterer).write();
                length = text.length();
            }
            line.setColor(changeColor(color, lineNum));
            line.setText(text);
            int i = index - start;
            if (i < line.getTextString().replace(" ", "").length()) {
                TextMaster.renderFirstCharactersOnce(line, xPosition + x, yPosition + y, i);
            } else {
                TextMaster.renderOnce(line, xPosition + x, yPosition + y);
            }
        }
    }
}
