/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text.effects;

/**
 * @author Wojtek
 */
class EventMaker extends PropertyChanger {
    private final String event;

    public EventMaker(TextEvent previous, String event, TextController tc) {
        super(previous, 0, 0, tc);
        this.event = event;
    }

    @Override
    void innerEvent(int i, int lineNum, int xPosition, int yPosition) {
        if (i >= start && !done) {
            controller.triggerEvent(event);
            done = true;
            controller.setIndex(start);
        }
    }
}
