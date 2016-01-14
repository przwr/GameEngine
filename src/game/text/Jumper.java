/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.text;

/**
 * @author Wojtek
 */
class Jumper extends PropertyChanger {
    private final String jumpLocation;

    public Jumper(int start, String jumpLocation, TextController tc) {
        super(start, 0, 0, tc);
        this.jumpLocation = jumpLocation;
    }

    @Override
    void event(int i, int lineNum) {
        if (i >= start && !done) {
            controller.setJumpLocation(jumpLocation);
            done = true;
            controller.setIndex(start);
        }
    }
}
