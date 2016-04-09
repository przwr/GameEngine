/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text.effects;

/**
 * @author Wojtek
 */
class PropertyChanger extends TextEvent {

    static final int PROP_SPEED = 0;
    static final int PROP_SPEAKER = 1;
    static final int PROP_FLUSH = 2;
    static final int PROP_PORTRAIT = 3;
    static final int PROP_EXPRESSION = 4;
    static final int PROP_END = 5;
    final TextController controller;
    private final int type;
    private final float quality;
    boolean done;

    PropertyChanger(TextEvent previous, int type, float quality, TextController tc) {
        super(previous, type == PROP_FLUSH ? 1 : 0);
        this.type = type;
        this.quality = Math.min(quality, 10);
        controller = tc;
    }

    @Override
    void innerEvent(int i, int lineNum, int xPosition, int yPosition) {
        if (i >= start && !done) {
            switch (type) {
                case PROP_SPEED:
                    controller.setSpeed(quality);
                    break;
                case PROP_FLUSH:
                    controller.flushText();
                    break;
                case PROP_SPEAKER:
                    controller.setSpeaker((int) quality);
                    break;
                case PROP_PORTRAIT:
                    controller.setPortrait((int) quality);
                    break;
                case PROP_EXPRESSION:
                    controller.setExpression((int) quality);
                    break;
                case PROP_END:
                    controller.terminateDialog();
                    break;
            }
            done = true;
            controller.setIndex(start);
        }
    }

}
