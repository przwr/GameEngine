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
public class InputPadDPad extends AnyInput {

    private final Controller[] controllers;
    private final boolean isX;
    private final boolean isPlus;

    public InputPadDPad(Controller[] controllers, int padNumber, boolean isX, boolean isPlus) {
        this.controllers = controllers;
        this.padNumber = padNumber;
        this.isX = isX;
        this.isPlus = isPlus;
        label = "JOY " + padNumber + ": D-PAD " + (isX ? "X" : "Y") + (isPlus ? "+" : "-");
    }

    @Override
    public boolean isPut() {
        if (padNumber < controllers.length) {
            if (isX) {
                if (isPlus) {
                    return controllers[padNumber].getPovX() > 0.1f;
                } else {
                    return controllers[padNumber].getPovX() < -0.1f;
                }
            } else {
                if (isPlus) {
                    return controllers[padNumber].getPovY() > 0.1f;
                } else {
                    return controllers[padNumber].getPovY() < -0.1f;
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
        return AnyInput.CONTROLLER_DPAD + " " + padNumber + " " + (isX ? 1 : 0) + " " + (isPlus ? 1 : 0);
    }
}
