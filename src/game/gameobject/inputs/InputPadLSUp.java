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
public class InputPadLSUp extends AnyInput {

    public InputPadLSUp(Controller[] controllers, int padNr) {
        this.controllers = controllers;
        this.padNr = padNr;
        this.type = 7;
        label = "stick y-";
    }

    @Override
    public boolean isPut() {
        return controllers[padNr].getYAxisValue() < -0.1f;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
