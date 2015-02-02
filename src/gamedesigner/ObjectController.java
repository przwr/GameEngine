/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.ActionHold;
import game.gameobject.AnyInput;
import game.gameobject.Controler;
import game.gameobject.Entity;
import game.gameobject.Player;

/**
 *
 * @author przemek
 */
public class ObjectController extends Controler {

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int JUMP = 4;
    public static final int RUN = 5;
    public static final int LIGHT = 6;

    public ObjectController(Entity inControl) {
        super(inControl);
        inputs = new AnyInput[36];
        actions = new Action[36]; // 4 pierwsze to menu  
        states = new boolean[7];
        statesSample = new boolean[7];
    }

    @Override
    public void initialize() {
        actions[0] = new ActionOnOff(inputs[0]);
        actions[1] = new ActionOnOff(inputs[1]);
        actions[2] = new ActionOnOff(inputs[2]);
        actions[3] = new ActionOnOff(inputs[3]);
        for (byte i = 4; i < 10; i++) {
            actions[i] = new ActionHold(inputs[i]);
        }
        actions[10] = new ActionOnOff(inputs[10]);
    }

    @Override
    public void getInput() {
        for (int i = 4; i < 11; i++) {
            actions[i].act();
            states[i - 4] = actions[i].isOn();
        }
    }

    @Override
    public boolean isMenuOn() {
        if (actions[3].input != null) {
            if (actions[3].input.isPut()) {
                if (!actions[3].input.isPressed()) {
                    actions[3].input.setPressed(true);
                    return true;
                }
            } else {
                actions[3].input.setPressed(false);
            }
        }
        return false;
    }

    @Override
    public void getMenuInput() {
        for (int i = 0; i < 4; i++) {
            actions[i].act();
        }
        if (actions[0].isOn()) {
            ((Player) inControl).getMenu().setChoosen(-1);
        } else if (actions[1].isOn()) {
            ((Player) inControl).getMenu().setChoosen(1);
        }
        if (actions[2].isOn()) {
            ((Player) inControl).getMenu().choice();
        } else if (actions[3].isOn()) {
            ((Player) inControl).getMenu().back();
        }
    }

    @Override
    public int getActionsCount() {
        int nr = 0;
        for (Action a : actions) {
            if (a != null) {
                nr++;
            }
        }
        return nr;
    }
}
