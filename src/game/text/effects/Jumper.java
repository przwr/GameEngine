/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text.effects;

/**
 * @author Wojtek
 */
class Jumper extends PropertyChanger {
    private final String jumpLocation;

    public Jumper(TextEvent previous, String jumpLocation, TextController tc) {
        super(previous, 0, 0, tc);
        this.jumpLocation = jumpLocation;
    }

    @Override
    void innerEvent(int i, int lineNum, int xPosition, int yPosition) {
        if (i >= start && !done) {
            controller.setJumpLocation(jumpLocation);
            done = true;
            controller.setIndex(start);
        }
    }
}
