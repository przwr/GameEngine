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
class NervousTextRenderer extends TextRenderer {

    private final RandomGenerator random;

    NervousTextRenderer(String text, TextEvent previous, int lineNum, Color color, FontHandler font, TextController tc) {
        super(text, previous, lineNum, color, font, tc);
        random = RandomGenerator.create(length + lineNum * 3);
    }

    private int getRandom() {
        return (random.chance(5) ? random.random(4) : 2) - 2;
    }

    @Override
    void innerEvent(int index, int lineNum) {
        setX();
        if (isVisible(index)) {
            int e = Math.min(index - start, length);
            String tmp;
            int xd = 0;
            int chosen = random.random(e);
            for (int i = 1; i <= e; i++) {
                tmp = text.substring(i - 1, i);
                if (i == chosen) {
                    font.drawLine(tmp, x + xd + getRandom(), y + getRandom(),
                            changeColor(color, lineNum));
                } else {
                    font.drawLine(tmp, x + xd, y, changeColor(color, lineNum));
                }
                xd += font.getWidth(tmp);
            }
        }
    }
}
