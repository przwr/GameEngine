/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

import game.Settings;

/**
 * @author przemek
 */
public abstract class AnyInput {

    public final static int ERROR2 = -2, ERROR = -1;
    protected final static int KEYBOARD = 0, MOUSE = 1, CONTROLLER_KEY = 2, CONTROLLER_DPAD = 3, CONTROLLER_STICK = 4;
    protected String label;
    protected int key;
    protected int pad;
    private boolean pressed;

    public static AnyInput createInput(int type, int[] table) {
        if (type == KEYBOARD) {
            return new InputKeyBoard(table[0]);
        } else if (type == CONTROLLER_KEY) {
            return new InputPadKey(Settings.controllers, table[0], table[1]);
        } else if (type == CONTROLLER_DPAD) {
            return new InputPadDPad(Settings.controllers, table[0], isTrue(table[1]), isTrue(table[2]));
        } else if (type == CONTROLLER_STICK) {
            return new InputPadStick(Settings.controllers, table[0], table[1], isTrue(table[2]));
        } else if (type == MOUSE) {
            return new InputMouse(table[0]);
        }
        return null;
    }

    private static boolean isTrue(int value) {
        return value == 1;
    }

    public abstract boolean isPut();

    public abstract String getLabel();

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isNotPressed() {
        return !pressed;
    }

    public int getPadNumber() {
        return pad;
    }

    public int getKey() {
        return key;
    }
}
