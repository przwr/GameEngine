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
class ShakyTextRenderer extends TextRenderer {

    private final RandomGenerator random;

    ShakyTextRenderer(String text, TextEvent previous, int lineNum, Color color, TextController tc) {
        super(text, previous, lineNum, color, tc);
        random = RandomGenerator.create(length + lineNum * 3);
    }

    @Override
    void innerEvent(int index, int lineNum, int xPosition, int yPosition) {
        setX();
        if (isVisible(index)) {
            int e = Math.min(index - start, text.replace(" ", "").length());
            line.setText(text);
            line.setColor(changeColor(color, lineNum));
            TextMaster.startRenderText();
            for (int i = 0; i < e; i++) {
                TextMaster.renderCharacters(line, xPosition + x + random.random(2) - 1,
                        yPosition + y + random.random(2) - 1, i, 1);
            }
            TextMaster.endRenderText();
        }
    }
}
