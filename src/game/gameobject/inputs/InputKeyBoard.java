/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

import game.gameobject.AnyInput;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author przemek
 */
public class InputKeyBoard extends AnyInput {

    public InputKeyBoard(int key) {
        this.key = key;
        this.type = 0;
        label = Keyboard.getKeyName(key).toLowerCase();
    }

    @Override
    public boolean isPut() {
        if (Keyboard.isCreated()) {
            return Keyboard.isKeyDown(key);
        }
        return false;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return type + " " + key;
    }
}
