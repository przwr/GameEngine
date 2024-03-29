/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

import org.lwjgl.input.Mouse;

/**
 * @author przemek
 */
public class InputMouse extends AnyInput {

    public InputMouse(int key) {
        this.key = key;
        label = "MOUSE: " + Mouse.getButtonName(key).toUpperCase();
    }

    @Override
    public boolean isPut() {
        return Mouse.isCreated() && Mouse.isButtonDown(key);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return AnyInput.MOUSE + " " + key;
    }
}
