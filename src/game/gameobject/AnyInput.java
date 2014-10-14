/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.Settings;
import game.gameobject.inputs.*;
import org.lwjgl.input.Controller;

/**
 *
 * @author przemek
 */
public abstract class AnyInput {

    protected String label;
    protected boolean isPressed;
    protected Controller[] controllers;
    protected int type; // 0 - Keyboard;  1 - Mouse; 2 - Controller_Key; 3 - Controller_Pov_Up; 4 - Controller_Pov_Down; 5 - Controller_Pov_Left;
    // 6 - Controller_Pov_Right; 7 - Controller_LS_Up; 8 - Controller_LS_Down; 9 - Controller_LS_Left; 10 - Controller_LS_Right;
    // 11 - Controller_RS_Up; 12 - Controller_RS_Down; 13 - Controller_RS_Left; 14 - Controller_RS_Right;
    // 15 - Controller_RZ_Up; 16 - Controller_RZ_Down; 17 - Controller_RZ_Left; 18 - Controller_RZ_Right;
    protected int key;
    protected int padNr;

    public abstract boolean isPut();

    public abstract String getLabel();

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean bl) {
        isPressed = bl;
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

    public static AnyInput CreateInput(int type, int padNr, int key, Settings settings) {
        if (type == 0) {
            return new InputKeyBoard(key);
        } else if (type == 1) {
            return new InputMouse(key);

        } else if (type == 2) {
            return new InputPadKey(settings.controllers, padNr, key);
        } else if (type == 3) {
            return new InputPadPovUp(settings.controllers, padNr);
        } else if (type == 4) {
            return new InputPadPovDown(settings.controllers, padNr);
        } else if (type == 5) {
            return new InputPadPovLeft(settings.controllers, padNr);
        } else if (type == 6) {
            return new InputPadPovRight(settings.controllers, padNr);
        } else if (type == 7) {
            return new InputPadLSUp(settings.controllers, padNr);
        } else if (type == 8) {
            return new InputPadLSDown(settings.controllers, padNr);
        } else if (type == 9) {
            return new InputPadLSLeft(settings.controllers, padNr);
        } else if (type == 10) {
            return new InputPadLSRight(settings.controllers, padNr);
        } else if (type == 11) {
            return new InputPadRSUp(settings.controllers, padNr);
        } else if (type == 12) {
            return new InputPadRSDown(settings.controllers, padNr);
        } else if (type == 13) {
            return new InputPadRSLeft(settings.controllers, padNr);
        } else if (type == 14) {
            return new InputPadRSRight(settings.controllers, padNr);
        } else if (type == 15) {
            return new InputPadRZUp(settings.controllers, padNr);
        } else if (type == 16) {
            return new InputPadRZDown(settings.controllers, padNr);
        } else if (type == 17) {
            return new InputPadRZLeft(settings.controllers, padNr);
        } else if (type == 18) {
            return new InputPadRZRight(settings.controllers, padNr);
        }
        return null;
    }
}
