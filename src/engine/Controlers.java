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
public final class Controlers {

    private static Controller[] controllers;

    public static Controller[] init() {
        Controller[] tempControllers = new Controller[Controllers.getControllerCount()];
        int j = 0;
        for (int i = 0; i < Controllers.getControllerCount(); i++) {
            if (Controllers.getController(i).getAxisCount() > 1 && Controllers.getController(i).getButtonCount() > 8) {
                tempControllers[j++] = Controllers.getController(i);
            }
        }
        controllers = new Controller[j];
        System.arraycopy(tempControllers, 0, controllers, 0, j);
        return controllers;
    }

    public static AnyInput mapInput(int noiseAxes[], int maxAxesNumber, AnyInput input) {
        if (Keyboard.isCreated()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                return new InputExitMapping();
            } else if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
                return new InputNull();
            }
        }
        if (input != null && input.isPut()) {
            return null;
        } else {
            return checkInputs(noiseAxes, maxAxesNumber);
        }
    }

    private static AnyInput checkInputs(int noiseAxes[], int maxAxesNumber) {
        for (int key = 0; key < Keyboard.KEYBOARD_SIZE; key++) {
            if (Keyboard.isCreated() && Keyboard.isKeyDown(key) && isNotSpecialKey(key)) {
                return new InputKeyBoard(key);
            }
        }
        for (int mouseButton = 0; mouseButton < Mouse.getButtonCount(); mouseButton++) {
            if (Mouse.isCreated() && Mouse.isButtonDown(mouseButton)) {
                return new InputMouse(mouseButton);
            }
        }
        return checkControllers(noiseAxes, maxAxesNumber);
    }

    private static boolean isNotSpecialKey(int key) {
        return key != Keyboard.KEY_ESCAPE && key != Keyboard.KEY_RETURN
                && key != Keyboard.KEY_INSERT && key != Keyboard.KEY_END && key != Keyboard.KEY_DELETE;
    }

    private static AnyInput checkControllers(int noiseAxes[], int maxAxesNumber) {
        for (int controler = 0; controler < controllers.length; controler++) {
            if (controllers[controler] != null) {
                for (int button = 0; button < controllers[controler].getButtonCount(); button++) {
                    if (controllers[controler].isButtonPressed(button)) {
                        return new InputPadKey(controllers, controler, button);
                    }
                }
                AnyInput dPad = checkDPad(controler);
                if (dPad != null) {
                    return dPad;
                } else {
                    return checkAxes(controler, noiseAxes, maxAxesNumber);
                }
            }
        }
        return null;
    }

    private static AnyInput checkDPad(int controler) {
        if (controllers[controler].getPovX() > 0.1f) {
            return new InputPadDPad(controllers, controler, true, true);
        } else if (controllers[controler].getPovX() < -0.1f) {
            return new InputPadDPad(controllers, controler, true, false);
        } else if (controllers[controler].getPovY() > 0.1f) {
            return new InputPadDPad(controllers, controler, false, true);
        } else if (controllers[controler].getPovY() < -0.1f) {
            return new InputPadDPad(controllers, controler, false, false);
        }
        return null;
    }

    private static AnyInput checkAxes(int controler, int noiseAxes[], int maxAxesNumber) {
        boolean noisy = false;
        for (int i = 0; i < controllers[controler].getAxisCount(); i++) {
            for (int j = 0; j < controllers[controler].getAxisCount(); j++) {
                if (i == noiseAxes[controler * maxAxesNumber + j]) {
                    noisy = true;
                }
            }
            if (noisy) {
                noisy = false;
            } else if (controllers[controler].getAxisValue(i) > 0.1f) {
                return new InputPadStick(controllers, controler, i, true);
            } else if (controllers[controler].getAxisValue(i) < -0.1f) {
                return new InputPadStick(controllers, controler, i, false);
            }
        }
        return null;
    }

    public static Controller[] getControllers() {
        return controllers;
    }
}
