/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

/**
 * @author Wojtek
 */
class QuestionMaker extends PropertyChanger {
    private final String[] answers, jumps;

    QuestionMaker(int start, String[] answers, String[] jumps, TextController tc) {
        super(start, 0, 0, tc);
        this.answers = answers;
        this.jumps = jumps;
    }

    @Override
    void event(int i, int lineNum) {
        if (i >= start && !done) {
            controller.setQuestion(answers, jumps);
            done = true;
            controller.setIndex(start);
        }
    }

}
