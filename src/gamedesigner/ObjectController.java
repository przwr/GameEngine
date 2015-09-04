/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import game.gameobject.*;

/**
 * @author przemek
 */
public class ObjectController extends PlayerController {

    public ObjectController(Entity inControl) {
        super(inControl);
        inputs = new AnyInput[36];
        actions = new Action[36]; // 4 pierwsze to menu  
        states = new byte[13];
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
    }

    @Override
    public boolean isMenuOn() {
        if (actions[3].input != null) {
            if (actions[3].input.isPut()) {
                if (actions[3].input.isNotPressed()) {
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
            ((Player) inControl).getMenu().setChosen(-1);
        } else if (actions[1].isOn()) {
            ((Player) inControl).getMenu().setChosen(1);
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
