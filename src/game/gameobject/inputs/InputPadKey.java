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

    private final Controller[] controllers;

    public InputPadKey(Controller[] controllers, int padNr, int key) {
        this.key = key;
        this.controllers = controllers;
        this.padNr = padNr;
        this.type = 2;
        label = "JOY " + padNr + ": BUTTON " + key;
    }

    @Override
    public boolean isPut() {
        if (padNr < controllers.length) {
            return controllers[padNr].isButtonPressed(key);
        }
        return false;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return type + " " + padNr + " " + key;
    }
}
