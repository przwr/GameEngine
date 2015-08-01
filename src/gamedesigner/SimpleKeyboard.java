/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import org.lwjgl.input.Keyboard;

/**
 * @author Wojtek
 */
class SimpleKeyboard {
    private boolean pressed, prevClick;

    public void keyboardStart() {
        pressed = false;
    }

    public void keyboardEnd() {
        if (!pressed) {
            prevClick = false;
        }
    }

    public boolean key(int k) {
        return Keyboard.isKeyDown(k);
    }

    public boolean keyPressed(int k) {
        if (key(k)) {
            pressed = true;
            if (!prevClick) {
                prevClick = true;
                return true;
            }
        }
        return false;
    }
}
