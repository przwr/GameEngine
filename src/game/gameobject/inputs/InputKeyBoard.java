/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

import org.lwjgl.input.Keyboard;

/**
 * @author przemek
 */
public class InputKeyBoard extends AnyInput {

    public InputKeyBoard(int key) {
        this.key = key;
        label = Keyboard.getKeyName(key).toUpperCase();
    }

    @Override
    public boolean isPut() {
        return Keyboard.isCreated() && Keyboard.isKeyDown(key);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return AnyInput.KEYBOARD + " " + key;
    }
}
