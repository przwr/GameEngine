/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

import game.gameobject.AnyInput;
import org.lwjgl.input.Controller;

/**
 * @author przemek
 */
public class InputPadDPad extends AnyInput {

    private final Controller[] controllers;
    private final boolean xAxis;
    private final boolean positive;

    public InputPadDPad(Controller[] controllers, int padNumber, boolean xAxis, boolean positive) {
        this.controllers = controllers;
        this.pad = padNumber;
        this.xAxis = xAxis;
        this.positive = positive;
        label = "JOY " + padNumber + ": D-PAD " + (xAxis ? "X" : "Y") + (positive ? "+" : "-");
    }

    @Override
    public boolean isPut() {
        if (pad < controllers.length) {
            if (xAxis) {
                if (positive) {
                    return controllers[pad].getPovX() > 0.1f;
                } else {
                    return controllers[pad].getPovX() < -0.1f;
                }
            } else {
                if (positive) {
                    return controllers[pad].getPovY() > 0.1f;
                } else {
                    return controllers[pad].getPovY() < -0.1f;
                }
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
        return AnyInput.CONTROLLER_DPAD + " " + pad + " " + (xAxis ? 1 : 0) + " " + (positive ? 1 : 0);
    }
}
