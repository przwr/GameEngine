/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text.effects;

import engine.utilities.RandomGenerator;
import game.text.fonts.TextMaster;
import org.newdawn.slick.Color;

/**
 * @author Wojtek
 */
class NervousTextRenderer extends TextRenderer {

    private final RandomGenerator random;

    NervousTextRenderer(String text, TextEvent previous, int lineNum, Color color, TextController tc) {
        super(text, previous, lineNum, color, tc);
        random = RandomGenerator.create(length + lineNum * 3);
    }

    private int getRandom() {
        return (random.chance(5) ? random.random(4) : 2) - 2;
    }

    @Override
    void innerEvent(int index, int lineNum, int xPosition, int yPosition) {
        setX();
        if (isVisible(index)) {
            int e = Math.min(index - start, text.replace(" ", "").length());
            int chosen = random.random(e);
            line.setText(text);
            line.setColor(changeColor(color, lineNum));
            TextMaster.startRenderText();
            if (chosen != 0) {
                TextMaster.renderFirstCharacters(line, xPosition + x, yPosition + y, chosen);
            }
            TextMaster.renderCharacters(line, xPosition + x + getRandom(), yPosition + y + getRandom(), chosen, 1);
            TextMaster.renderCharacters(line, xPosition + x, yPosition + y, chosen + 1, e - chosen);
            TextMaster.endRenderText();
        }
    }
}
