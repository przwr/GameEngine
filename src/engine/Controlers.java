/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.gameobject.AnyInput;
import game.gameobject.inputs.*;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 *
 * @author przemek
 */
public class Controlers {

    public static Controller[] controllers = new Controller[Controllers.getControllerCount()];

    public static Controller[] init() {
        int j = 0;
        for (int i = 0; i < Controllers.getControllerCount(); i++) {
            if (Controllers.getController(i).getAxisCount() > 1 && Controllers.getController(i).getButtonCount() > 8) {
                controllers[j++] = Controllers.getController(i);
            }
        }
        return controllers;
    }

    public static AnyInput mapInput(int noiseAx[], AnyInput in) {
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            return new InputExitMapping();
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
            return new InputNull();
        }
        if (in != null && in.isPut()) {
            return null;
        } else {
            for (int k = 0; k < Keyboard.KEYBOARD_SIZE; k++) {
                if (Keyboard.isCreated() && Keyboard.isKeyDown(k) && k != Keyboard.KEY_ESCAPE && k != Keyboard.KEY_RETURN && k != Keyboard.KEY_INSERT && k != Keyboard.KEY_END && k != Keyboard.KEY_DELETE) {
                    return new InputKeyBoard(k);
                }
            }
            for (int m = 0; m < Mouse.getButtonCount(); m++) {
                if (Mouse.isCreated() && Mouse.isButtonDown(m)) {
                    return new InputMouse(m);
                }
            }
            for (int c = 0; c < controllers.length; c++) {
                if (controllers[c] != null) {
                    for (int b = 0; b < controllers[c].getButtonCount(); b++) {
                        if (controllers[c].isButtonPressed(b)) {
                            return new InputPadKey(controllers, c, b);
                        }
                    }
                    if (controllers[c].getPovX() > 0.1f) {
                        return new InputPadDPad(controllers, c, true, true);
                    }
                    if (controllers[c].getPovX() < -0.1f) {
                        return new InputPadDPad(controllers, c, true, false);
                    }
                    if (controllers[c].getPovY() > 0.1f) {
                        return new InputPadDPad(controllers, c, false, true);
                    }
                    if (controllers[c].getPovY() < -0.1f) {
                        return new InputPadDPad(controllers, c, false, false);
                    }
                    for (int a = 0; a < controllers[c].getAxisCount(); a++) {
                        if (a != noiseAx[c] && a != noiseAx[Controllers.getControllerCount() + c]) {
                            if (controllers[c].getAxisValue(a) > 0.1f) {
                                return new InputPadStick(controllers, c, a, true);
                            }
                            if (controllers[c].getAxisValue(a) < -0.1f) {
                                return new InputPadStick(controllers, c, a, false);
                            }
                        }
                    }
                }
            }
            return null;
        }
    }
}
