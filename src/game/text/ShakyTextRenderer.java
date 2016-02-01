/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

import engine.utilities.RandomGenerator;
import org.newdawn.slick.Color;

/**
 * @author Wojtek
 */
class ShakyTextRenderer extends TextRenderer {

    private final RandomGenerator random;

    ShakyTextRenderer(String text, TextEvent previous, int lineNum, Color color, FontHandler font, TextController tc) {
        super(text, previous, lineNum, color, font, tc);
        random = RandomGenerator.create(length + lineNum * 3);
    }

    @Override
    void innerEvent(int index, int lineNum) {
        setX();
        if (isVisible(index)) {
            int e = Math.min(index - start, length);
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
