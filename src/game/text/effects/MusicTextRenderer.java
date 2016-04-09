/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text.effects;

import engine.utilities.Methods;
import game.text.fonts.TextMaster;
import org.newdawn.slick.Color;

/**
 * @author Wojtek
 */
class MusicTextRenderer extends TextRenderer {

    MusicTextRenderer(String text, TextEvent previous, int lineNum, Color color, TextController tc) {
        super(text, previous, lineNum, color, tc);
    }

    @Override
    void innerEvent(int index, int lineNum, int xPosition, int yPosition) {
        setX();
        if (isVisible(index)) {
            int dt = control.getTime();
            int e = Math.min(index - start, text.replace(" ", "").length());
            line.setText(text);
            line.setColor(changeColor(color, lineNum));
            TextMaster.startRenderText();
            for (int i = 0; i < e; i++) {
                TextMaster.renderCharacters(line, xPosition + x,
                        yPosition + y + Methods.roundDouble(5 * Math.sin((float) dt / 30 * Math.PI)), i, 1);
                dt += 4;
            }
            TextMaster.endRenderText();
        }
    }
}
