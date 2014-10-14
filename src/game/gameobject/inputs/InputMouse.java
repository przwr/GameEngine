/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

import game.gameobject.AnyInput;
import org.lwjgl.input.Mouse;

/**
 *
 * @author przemek
 */
public class InputMouse extends AnyInput {

    public InputMouse(int key) {
        this.key = key;
        this.type = 1;
        label = "mouse_" + key + "_button";
    }

    @Override
    public boolean isPut() {
        if (Mouse.isCreated()) {
            return Mouse.isButtonDown(key);
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
