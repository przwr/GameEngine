/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text.effects;

/**
 * @author Wojtek
 */
class CheckExpressionMaker extends PropertyChanger {
    private final String[] jumps;
    private final String expression;

    CheckExpressionMaker(TextEvent previous, String expression, String[] jumps, TextController tc) {
        super(previous, 0, 0, tc);
        this.expression = expression;
        this.jumps = jumps;
    }

    @Override
    void innerEvent(int i, int lineNum, int xPosition, int yPosition) {
        if (i >= start && !done) {
            controller.setCheckingExpression(expression, jumps);
            done = true;
            controller.setIndex(start);
        }
    }
}
