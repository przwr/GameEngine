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

    protected String label;
    protected boolean pressed;
    protected int type; // 0 - Keyboard;  1 - Mouse; 2 - Controller_Key; 3 - Controller_Dpad; 4 - Controller_Stick;
    protected int key;
    protected int padNr;

    public abstract boolean isPut();

    public abstract String getLabel();

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean bl) {
        pressed = bl;
    }

    public int getType() {
        return type;
    }

    public int getPadNr() {
        return padNr;
    }

    public int getKey() {
        return key;
    }

    public static AnyInput createInput(int type, int[] table, Settings settings) {
        if (type == 0) {
            return new InputKeyBoard(table[0]);
        } else if (type == 1) {
            return new InputMouse(table[0]);
        } else if (type == 2) {
            return new InputPadKey(settings.controllers, table[0], table[1]);
        } else if (type == 3) {
            return new InputPadDPad(settings.controllers, table[0], (table[1] == 1), (table[2] == 1));
        } else if (type == 4) {
            return new InputPadStick(settings.controllers, table[0], table[1], (table[2] == 1));
        }
        return null;
    }
}
