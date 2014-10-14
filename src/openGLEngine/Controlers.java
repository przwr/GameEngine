/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openGLEngine;

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
            if (Controllers.getController(i).getAxisCount() > 1 && Controllers.getController(i).getButtonCount() > 10) {
                controllers[j++] = Controllers.getController(i);
            }
        }
        return controllers;
    }

//    public static void getJoyInput() {
//        for (int k = 0;
//                k < Keyboard.KEYBOARD_SIZE;
//                k++) {
//            if (Keyboard.isCreated() && Keyboard.isKeyDown(k)) {
//                System.out.println("Naciśnięto przycisk: " + Keyboard.getKeyName(k) + " " + k);
//            }
//        }
//        for (int i = 0; i < Controllers.getControllerCount(); i++) {
//            for (int j = 0; j < Controllers.getController(i).getButtonCount(); j++) {
//                if (Controllers.getController(i).isButtonPressed(j)) {
//                    System.out.println("Naciśnięto przycisk: " + Controllers.getController(i).getButtonName(j) + " z kontrolera: " + i);
//                }
//            }
//            if (Controllers.getController(i).getPovX() != 0.0f) {
//                System.out.println("Naciśnięto przycisk: PovX z kontrolera: " + i);
//            }
//            if (Controllers.getController(i).getPovY() != 0.0f) {
//                System.out.println("Naciśnięto przycisk: PovY z kontrolera: " + i);
//            }
//            if (Controllers.getController(i).getXAxisValue() != 0.0f) {
//                System.out.println("Naciśnięto przycisk: Left X z kontrolera: " + i);
//            }
//            if (Controllers.getController(i).getYAxisValue() != 0.0f) {
//                System.out.println("Naciśnięto przycisk: Left Y z kontrolera: " + i);
//            }
//            if (Controllers.getController(i).getRYAxisValue() != 0.0f) {
//                System.out.println("Naciśnięto przycisk: Right Y z kontrolera: " + i);
//            }
//            if (Controllers.getController(i).getRXAxisValue() != 0.0f) {
//                System.out.println("Naciśnięto przycisk: Right X z kontrolera: " + i);
//            }
//            if (Controllers.getController(i).getRZAxisValue() != 0.0f) {
//                System.out.println("Gałka: RZ  z kontrolera: " + i);
//            }
//            if (Controllers.getController(i).getZAxisValue() != 0.0f) {
//                System.out.println("Gałka: Z  z kontrolera: " + i);
//            }
//        }
//    }

    public static AnyInput mapInput() {
        for (int k = 0; k < Keyboard.KEYBOARD_SIZE; k++) {
            if (Keyboard.isCreated() && k != Keyboard.KEY_ESCAPE && k != Keyboard.KEY_RETURN && Keyboard.isKeyDown(k)) {
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
                    return new InputPadPovRight(controllers, c);
                }
                if (controllers[c].getPovX() < -0.1f) {
                    return new InputPadPovLeft(controllers, c);
                }
                if (controllers[c].getPovY() > 0.1f) {
                    return new InputPadPovDown(controllers, c);
                }
                if (controllers[c].getPovY() < -0.1f) {
                    return new InputPadPovUp(controllers, c);
                }
                if (controllers[c].getXAxisValue() > 0.1f) {
                    return new InputPadLSRight(controllers, c);
                }
                if (controllers[c].getXAxisValue() < -0.1f) {
                    return new InputPadLSLeft(controllers, c);
                }
                if (controllers[c].getYAxisValue() > 0.1f) {
                    return new InputPadLSDown(controllers, c);
                }
                if (controllers[c].getYAxisValue() < -0.1f) {
                    return new InputPadLSUp(controllers, c);
                }
                if (controllers[c].getRXAxisValue() > 0.1f) {
                    return new InputPadRSRight(controllers, c);
                }
                if (controllers[c].getRXAxisValue() < -0.1f) {
                    return new InputPadRSLeft(controllers, c);
                }
                if (controllers[c].getRYAxisValue() > 0.1f) {
                    return new InputPadRSDown(controllers, c);
                }
                if (controllers[c].getRYAxisValue() < -0.1f) {
                    return new InputPadRSUp(controllers, c);
                }
                if (controllers[c].getRZAxisValue() > 0.1f) {
                    return new InputPadRZRight(controllers, c);
                }
                if (controllers[c].getRZAxisValue() < -0.1f) {
                    return new InputPadRZLeft(controllers, c);
                }
                if (controllers[c].getZAxisValue() > 0.1f) {
                    return new InputPadRZDown(controllers, c);
                }
                if (controllers[c].getZAxisValue() < -0.1f) {
                    return new InputPadRZUp(controllers, c);
                }
            }
        }

        return null;
    }

}
