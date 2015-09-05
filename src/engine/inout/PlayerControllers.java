/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.inout;

import game.gameobject.AnyInput;
import game.gameobject.inputs.*;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author przemek
 */
public final class PlayerControllers {

    private static Controller[] controllers;

    public static Controller[] initialize() {
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
        AnyInput input;
        for (int controller = 0; controller < controllers.length; controller++) {
            if (controllers[controller] != null) {
                for (int button = 0; button < controllers[controller].getButtonCount(); button++) {
                    if (controllers[controller].isButtonPressed(button)) {
                        return new InputPadKey(controllers, controller, button);
                    }
                }
                input = checkDPad(controller);
                if (input != null) {
                    return input;
                }
                input = checkAxes(controller, noiseAxes, maxAxesNumber);
                if (input != null) {
                    return input;
                }
            }
        }
        return null;
    }

    private static AnyInput checkDPad(int controller) {
        if (controllers[controller].getPovX() > 0.1f) {
            return new InputPadDPad(controllers, controller, true, true);
        } else if (controllers[controller].getPovX() < -0.1f) {
            return new InputPadDPad(controllers, controller, true, false);
        } else if (controllers[controller].getPovY() > 0.1f) {
            return new InputPadDPad(controllers, controller, false, true);
        } else if (controllers[controller].getPovY() < -0.1f) {
            return new InputPadDPad(controllers, controller, false, false);
        }
        return null;
    }

    private static AnyInput checkAxes(int controller, int noiseAxes[], int maxAxesNumber) {
        boolean noisy = false;
        for (int i = 0; i < controllers[controller].getAxisCount(); i++) {
            for (int j = 0; j < controllers[controller].getAxisCount(); j++) {
                if (i == noiseAxes[controller * maxAxesNumber + j]) {
                    noisy = true;
                }
            }
            if (noisy) {
                noisy = false;
            } else if (controllers[controller].getAxisValue(i) > 0.1f) {
                return new InputPadStick(controllers, controller, i, true);
            } else if (controllers[controller].getAxisValue(i) < -0.1f) {
                return new InputPadStick(controllers, controller, i, false);
            }
        }
        return null;
    }

    public static Controller[] getControllers() {
        return controllers;
    }
}
