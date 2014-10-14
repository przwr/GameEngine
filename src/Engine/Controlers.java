/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

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

    public static void getJoyInput() {
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
//
        for (int i = 0; i < controllers.length; i++) {
            for (int j = 0; j < controllers[i].getAxisCount(); j++) {
                if (controllers[i].getAxisValue(j) != 0.0f) {
                    System.out.println("Kontroler " + i + " Oś: " + controllers[i].getAxisName(j) + " " + j + " Wartość: " + controllers[i].getAxisValue(j) + " Liczba Osi: " + controllers[i].getAxisCount());
                }
            }
        }
//
    }

    public static AnyInput mapInput() {
        if(Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            return new InputExitMapping();
        }
        for (int k = 0; k < Keyboard.KEYBOARD_SIZE; k++) {
            if (Keyboard.isCreated() && Keyboard.isKeyDown(k) && k != Keyboard.KEY_ESCAPE && k != Keyboard.KEY_RETURN) {
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
                    if (!controllers[c].getAxisName(a).equals("slider") && controllers[c].getAxisValue(a) > 0.1f) {
                        return new InputPadStick(controllers, c, a, true);
                    }
                    if (!controllers[c].getAxisName(a).equals("slider") && controllers[c].getAxisValue(a) < -0.1f) {
                        return new InputPadStick(controllers, c, a, false);
                    }
                }
            }
        }
        return null;
    }

}
