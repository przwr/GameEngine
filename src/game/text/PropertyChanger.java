/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

/**
 * @author Wojtek
 */
class PropertyChanger extends TextEvent {

    static final int PROP_SPEED = 0;
    static final int PROP_SPEAKER = 1;
    static final int PROP_FLUSH = 2;
    static final int PROP_PORTRAIT = 3;
    static final int PROP_EXPRESSION = 4;
    final TextController text;
    private final int type;
    private final float quality;
    boolean done;

    PropertyChanger(int start, int type, float quality, TextController tc) {
        super(start, 0);
        this.type = type;
        this.quality = Math.min(quality, 10);
        text = tc;
    }

    @Override
    void event(int i, int lineNum) {
        if (i >= start && !done) {
            switch (type) {
                case PROP_SPEED:
                    text.setSpeed(quality);
                    break;
                case PROP_FLUSH:
                    text.flushText();
                    break;
                case PROP_SPEAKER:
                    text.setSpeaker((int) quality);
                    break;
                case PROP_PORTRAIT:
                    text.setPortrait((int) quality);
                    break;
                case PROP_EXPRESSION:
                    text.setExpression((int) quality);
                    break;
            }
            done = true;
            text.setIndex(start);
        }
    }

}
