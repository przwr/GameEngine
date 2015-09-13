/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.gameobject.inputs;

import static game.gameobject.inputs.PlayerController.*;

/**
 * @author przemek
 */
public class Action {

    public AnyInput input;
    boolean on;
    byte state;

    public Action(AnyInput in) {
        this.input = in;
    }

    public void updateActiveState() {
        if (input != null && input.isPut()) {
            on = true;
        }
        if (on) {
            on = false;
            if (state == KEY_NO_INPUT) {
                state = KEY_CLICKED;
            } else {
                state = KEY_PRESSED;
            }
        } else {
            if (state == KEY_PRESSED) {
                state = KEY_RELEASED;
            } else {
                state = KEY_NO_INPUT;
            }
        }
    }

    public void updatePassiveState() {
        if (state == KEY_CLICKED) {
            state = KEY_PRESSED;
        } else if (state == KEY_RELEASED) {
            state = KEY_NO_INPUT;
        }
    }

    public boolean isKeyReleased() {
        return state == KEY_RELEASED;
    }

    public boolean isKeyClicked() {
        return state == KEY_CLICKED;
    }

    public boolean isKeyPressed() {
        return state > KEY_NO_INPUT;
    }

    public int getState() {
        return state;
    }
    
    public void setInput(AnyInput input) {
        this.input = input;
    }
}
