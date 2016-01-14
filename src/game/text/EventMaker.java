/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

/**
 * @author Wojtek
 */
class EventMaker extends PropertyChanger {
    private final String event;

    public EventMaker(int start, String event, TextController tc) {
        super(start, 0, 0, tc);
        this.event = event;
    }

    @Override
    void event(int i, int lineNum) {
        if (i >= start && !done) {
            text.triggerEvent(event);
            done = true;
            text.setIndex(start);
        }
    }
}
