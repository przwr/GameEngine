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
public class PropertyChanger extends TextEvent {

    protected static final int PROP_SPEED = 0;
    protected static final int PROP_SPEAKER = 1;
    protected static final int PROP_FLUSH = 2;
    protected static final int PROP_PORTRAIT = 3;
    protected static final int PROP_EXPRESSION = 4;
    
    protected final int type;
    private final float quatity;
    protected boolean done;
    protected TextController text;

    protected PropertyChanger(int start, int type, float quatity, TextController tc) {
        super(start, 0);
        this.type = type;
        this.quatity = Math.min(quatity, 10);
        text = tc;
    }

    @Override
    void event(int i, int lineNum) {
        if (i >= start && !done) {
            switch (type) {
                case PROP_SPEED:
                    text.setSpeed((float) quatity);
                    break;
                case PROP_FLUSH:
                    text.flushText();
                    break;
                case PROP_SPEAKER:
                    text.setSpeaker((int) quatity);
                    break;
                case PROP_PORTRAIT:
                    text.setPortrait((int) quatity);
                    break;
                case PROP_EXPRESSION:
                    text.setExpression((int) quatity);
                    break;
            }
            done = true;
            text.setIndex(start);
        }
    }

}
