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
public class MusicTextRenderer extends TextRenderer {

    MusicTextRenderer(String text, int start, int startX, int lineNum, Color color, FontHandler font, TextController tc) {
        super(text, start, startX, lineNum, color, font, tc);
    }

    @Override
    void event(int index, int lineNum) {
        if (isVisible(index, lineNum)) {
            int e = Math.min(index - start + 1, end);
            String tmp;
            int xd = 0, dt = control.getTime();
            for (int i = 1; i <= e; i++) {
                tmp = text.substring(i - 1, i);
                font.drawLine(tmp, x + xd, (int) (y + 5 * Math.sin((float) dt / 30 * Math.PI)),
                        changeColor(color, lineNum));
                xd += font.getWidth(tmp);
                dt += 4;
            }
        }
    }
}
