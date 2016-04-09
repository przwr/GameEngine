/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text.effects;

/**
 * @author Wojtek
 */
class QuestionMaker extends PropertyChanger {
    private final String[] answers, jumps;

    QuestionMaker(TextEvent previous, String[] answers, String[] jumps, TextController tc) {
        super(previous, 0, 0, tc);
        this.answers = answers;
        this.jumps = jumps;
    }

    @Override
    void innerEvent(int i, int lineNum, int xPosition, int yPosition) {
        if (i >= start && !done) {
            controller.setQuestion(answers, jumps);
            done = true;
            controller.setIndex(start);
        }
    }

}
