/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

import org.lwjgl.input.Controller;

/**
 * @author przemek
 */
public class InputPadStick extends AnyInput {

    private final Controller[] controllers;
    private final int axisCount;
    private final boolean plus;

    public InputPadStick(Controller[] controllers, int padNumber, int axisNumber, boolean plus) {
        this.controllers = controllers;
        this.pad = padNumber;
        this.axisCount = axisNumber;
        this.plus = plus;
        label = "JOY " + padNumber + ": AXIS " + axisNumber + (plus ? "+" : "-");
    }

    @Override
    public boolean isPut() {
        if (pad < controllers.length && controllers[pad] != null) {
            if (plus) {
                return controllers[pad].getAxisValue(axisCount) > 0.1f;
            } else {
                return controllers[pad].getAxisValue(axisCount) < -0.1f;
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
        return AnyInput.CONTROLLER_STICK + " " + pad + " " + axisCount + " " + (plus ? 1 : 0);
    }
}
