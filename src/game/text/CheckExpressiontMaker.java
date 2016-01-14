/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

/**
 * @author Wojtek
 */
class CheckExpressiontMaker extends PropertyChanger {
    private final String[] jumps;
    private final String expression;

    CheckExpressiontMaker(int start, String expression, String[] jumps, TextController tc) {
        super(start, 0, 0, tc);
        this.expression = expression;
        this.jumps = jumps;
    }

    @Override
    void event(int i, int lineNum) {
        if (i >= start && !done) {
            text.setCheckingExpression(expression, jumps);
            done = true;
            text.setIndex(start);
        }
    }

}
