/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.Settings;
import game.gameobject.inputs.InputKeyBoard;
import game.gameobject.inputs.InputMouse;
import game.gameobject.inputs.InputPadDPad;
import game.gameobject.inputs.InputPadKey;
import game.gameobject.inputs.InputPadStick;

/**
 *
 * @author przemek
 */
public abstract class AnyInput {

    public final static int ERROR2 = -2, ERROR = -1, KEYBOARD = 0, MOUSE = 1, CONTROLLER_KEY = 2, CONTROLLER_DPAD = 3, CONTROLLER_STICK = 4;
    protected String label;
    protected boolean pressed;
    protected int key;
    protected int padNumber;

    public abstract boolean isPut();

    public abstract String getLabel();

    public static AnyInput createInput(int type, int[] table, Settings settings) {
        if (type == KEYBOARD) {
            return new InputKeyBoard(table[0]);
        } else if (type == CONTROLLER_KEY) {
            return new InputPadKey(settings.controllers, table[0], table[1]);
        } else if (type == CONTROLLER_DPAD) {
            return new InputPadDPad(settings.controllers, table[0], isTrue(table[1]), isTrue(table[2]));
        } else if (type == CONTROLLER_STICK) {
            return new InputPadStick(settings.controllers, table[0], table[1], isTrue(table[2]));
        } else if (type == MOUSE) {
            return new InputMouse(table[0]);
        }
        return null;
    }

    private static boolean isTrue(int value) {
        return value == 1;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isPressed() {
        return pressed;
    }

    public int getPadNumber() {
        return padNumber;
    }

    public int getKey() {
        return key;
    }
}
