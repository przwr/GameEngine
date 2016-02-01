/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

/**
 * @author Wojtek
 */
class ExternalTextMaker extends PropertyChanger {
    private final String name;
    private TextRenderer text;
    private final TextController.TextRow textrow;

    public ExternalTextMaker(TextEvent previous, String event, TextController tc, TextController.TextRow tr) {
        super(previous, 0, 0, tc);
        this.name = event;
        textrow = tr;
    }

    public void addTextRenderer(TextRenderer text) {
        this.text = text;
    }
    
    @Override
    void innerEvent(int i, int lineNum) {
        if (i >= start && !done) {
            //controller.alterText(textrow, text, controller.getWriter(name).write(), start);
            done = true;
            controller.setIndex(start);
        }
    }
}
