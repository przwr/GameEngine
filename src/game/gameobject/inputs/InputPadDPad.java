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

    public InputPadDPad(Controller[] controllers, int padNr, boolean isX, boolean isPlus) {
        this.controllers = controllers;
        this.padNr = padNr;
        this.isX = isX;
        this.isPlus = isPlus;
        this.type = 3;
        label = "JOY " + padNr + ": D-PAD " + (isX ? "X" : "Y") + (isPlus ? "+" : "-");
    }

    @Override
    public boolean isPut() {
        if (padNr < controllers.length) {
            if (isX) {
                if (isPlus) {
                    return controllers[padNr].getPovX() > 0.1f;
                } else {
                    return controllers[padNr].getPovX() < -0.1f;
                }
            } else {
                if (isPlus) {
                    return controllers[padNr].getPovY() > 0.1f;
                } else {
                    return controllers[padNr].getPovY() < -0.1f;
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
        return type + " " + padNr + " " + (isX ? 1 : 0) + " " + (isPlus ? 1 : 0);
    }
}
