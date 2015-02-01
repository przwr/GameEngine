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

    public InputPadKey(Controller[] controllers, int padNumber, int key) {
        this.key = key;
        this.controllers = controllers;
        this.padNumber = padNumber;
        label = "JOY " + padNumber + ": BUTTON " + key;
    }

    @Override
    public boolean isPut() {
        if (padNumber < controllers.length) {
            return controllers[padNumber].isButtonPressed(key);
        }
        return false;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return AnyInput.CONTROLLER_KEY + " " + padNumber + " " + key;
    }
}