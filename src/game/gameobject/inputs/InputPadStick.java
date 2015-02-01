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
    private final int axisNumber;
    private final boolean plus;

    public InputPadStick(Controller[] controllers, int padNumber, int axisNumber, boolean plus) {
        this.controllers = controllers;
        this.padNumber = padNumber;
        this.axisNumber = axisNumber;
        this.plus = plus;
        label = "JOY " + padNumber + ": AXIS " + axisNumber + (plus ? "+" : "-");
    }

    @Override
    public boolean isPut() {
        if (padNumber < controllers.length && controllers[padNumber] != null) {
            if (plus) {
                return controllers[padNumber].getAxisValue(axisNumber) > 0.1f;
            } else {
                return controllers[padNumber].getAxisValue(axisNumber) < -0.1f;
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
        return AnyInput.CONTROLLER_STICK + " " + padNumber + " " + axisNumber + " " + (plus ? 1 : 0);
    }
}
