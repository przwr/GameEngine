/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

/**
 *
 * @author Wojtek
 */
public class QuestionMaker extends PropertyChanger {
    private final String[] answers, jumps;
    
    protected QuestionMaker(int start, String[] answers, String[] jumps, TextController tc) {
        super(start, 0, 0, tc);
        this.answers = answers;
        this.jumps = jumps;
    }

    @Override
    void event(int i, int lineNum) {
        if (i >= start && !done) {
            text.setQuestion(answers, jumps);
            done = true;
            text.setIndex(start);
        }
    }

}
