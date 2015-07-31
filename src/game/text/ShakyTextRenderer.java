/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import engine.RandomGenerator;
import org.newdawn.slick.Color;

/**
 * @author Wojtek
 */
class ShakyTextRenderer extends TextRenderer {

    private final RandomGenerator random;

    ShakyTextRenderer(String text, int start, int startX, int lineNum, Color color, FontHandler font, TextController tc) {
        super(text, start, startX, lineNum, color, font, tc);
        random = RandomGenerator.create(start + startX * 3 + lineNum * 5);
    }

    @Override
    void event(int index, int lineNum) {
        if (isVisible(index)) {
            int e = Math.min(index - start + 1, end);
            String tmp;
            int xd = 0;
            for (int i = 1; i <= e; i++) {
                tmp = text.substring(i - 1, i);
                font.drawLine(tmp, x + xd + random.random(2) - 1, y + random.random(2) - 1,
                        changeColor(color, lineNum));
                xd += font.getWidth(tmp);
            }
        }
    }
}
