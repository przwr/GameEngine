/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

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
public class MyController extends Controler {

    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, JUMP = 4, RUN = 5, LIGHT = 6;
    public static final int FIRST_NO_MENU_ACTION = 4, ACTIONS_COUNT = 10;

    public MyController(Entity inControl) {
        super(inControl);
        inputs = new AnyInput[36];
        actions = new Action[36];
        states = new boolean[7];
        statesSample = new boolean[7];
    }

    @Override
    public void initialize() {
        actions[0] = new ActionOnOff(inputs[0]);
        actions[1] = new ActionOnOff(inputs[1]);
        actions[2] = new ActionOnOff(inputs[2]);
        actions[3] = new ActionOnOff(inputs[3]);
        for (byte i = FIRST_NO_MENU_ACTION; i < ACTIONS_COUNT; i++) {
            actions[i] = new ActionHold(inputs[i]);
        }
        actions[ACTIONS_COUNT] = new ActionOnOff(inputs[ACTIONS_COUNT]);
    }

    @Override
    public void getInput() {
        for (int i = 4; i < 11; i++) {
            actions[i].act();
            states[i - 4] = actions[i].isOn();
        }
        if (states[UP]) {
            inControl.addSpeed(0, -4);
        } else if (states[DOWN]) {
            inControl.addSpeed(0, 4);
        } else {
            inControl.brake(1);
        }
        if (states[LEFT]) {
            inControl.addSpeed(-4, 0);
        } else if (states[RIGHT]) {
            inControl.addSpeed(4, 0);
        } else {
            inControl.brake(0);
        }
        if (states[JUMP]) {
            inControl.setJumping(true);
            inControl.setHop(true);
        }
        if (states[RUN]) {
            inControl.setMaxSpeed(2);
        } else {
            inControl.setMaxSpeed(8);
        }
        if (states[LIGHT]) {
            inControl.setEmits(!inControl.isEmits());
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
            ((Player) inControl).menu.setChoosen(-1);
        } else if (actions[1].isOn()) {
            ((Player) inControl).menu.setChoosen(1);
        }
        if (actions[2].isOn()) {
            ((Player) inControl).menu.choice();
        } else if (actions[3].isOn()) {
            ((Player) inControl).menu.back();
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
