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
public class InputPadStick extends AnyInput {

    private final Controller[] controllers;
    private final int axisNr;
    private final boolean isPlus;

    public InputPadStick(Controller[] controllers, int padNr, int axisNr, boolean isPlus) {
        this.controllers = controllers;
        this.padNr = padNr;
        this.axisNr = axisNr;
        this.isPlus = isPlus;
        this.type = 4;
        label = "pad_" + padNr + "_ax_" + axisNr + (isPlus ? "+" : "-");
    }

    @Override
    public boolean isPut() {
        if (controllers[padNr] != null/* && padNr < controllers.length*/) {
            if (isPlus) {
                return controllers[padNr].getAxisValue(axisNr) > 0.1f;
            } else {
                return controllers[padNr].getAxisValue(axisNr) < -0.1f;
            }
        }
        return false;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return type + " " + padNr + " " + axisNr + " " + (isPlus ? 1 : 0);
    }
}
