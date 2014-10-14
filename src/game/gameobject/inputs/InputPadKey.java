/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

import game.gameobject.AnyInput;
import org.lwjgl.input.Controller;

/**
 *
 * @author przemek
 */
public class InputPadKey extends AnyInput {

    public InputPadKey(Controller[] controllers, int padNr, int key) {
        this.key = key;
        this.controllers = controllers;
        this.padNr = padNr;
        this.type = 2;
        label = "padbutton " + key;
    }

    @Override
    public boolean isPut() {
        return controllers[padNr].isButtonPressed(key);
    }

    @Override
    public String getLabel() {
        return label;
    }
}
