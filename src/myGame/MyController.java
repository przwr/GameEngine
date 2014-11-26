/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame;

import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.ActionHold;
import game.gameobject.AnyInput;
import game.gameobject.Controler;
import game.gameobject.Entity;
import game.gameobject.Player;
import net.packets.PacketInput;

/**
 *
 * @author przemek
 */
public class MyController extends Controler {

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int JUMP = 4;
    public static final int RUN = 5;
    public static final int LIGHT = 6;

    public MyController(Entity inControl) {
        super(inControl);
        inputs = new AnyInput[36];
        actions = new Action[36]; // 4 pierwsze to menu  
        states = new boolean[7];
        statesSample = new boolean[7];
    }

    @Override
    public void init() {
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
            actions[i].Do();
            states[i - 4] = actions[i].isOn();
        }
        System.arraycopy(states, 0, statesSample, 0, statesSample.length);
        if (inControl.getPlace().game.online.client != null) {
            inControl.getPlace().game.online.client.sendInput(new PacketInput(((Player) inControl).id, statesSample));
        }
        if (states[UP]) {
            inControl.addSpeed(0, -4, true);
        } else if (states[DOWN]) {
            inControl.addSpeed(0, 4, true);
        } else {
            inControl.brake(1);
        }
        if (states[LEFT]) {
            inControl.addSpeed(-4, 0, true);
        } else if (states[RIGHT]) {
            inControl.addSpeed(4, 0, true);
        } else {
            inControl.brake(0);
        }
        if (states[JUMP]) {
            inControl.setisJumping(true);
        }
        if (states[RUN]) {
            inControl.setMaxSpeed(16);
        } else {
            inControl.setMaxSpeed(8);
        }
        if (states[LIGHT]) {
            inControl.setEmits(!inControl.isEmits());
        }
    }

    @Override
    public synchronized void setInput(boolean[] states) {
        System.arraycopy(states, 0, this.states, 0, this.states.length);
        if (states[UP]) {
            inControl.addSpeed(0, -4, true);
        } else if (states[DOWN]) {
            inControl.addSpeed(0, 4, true);
        } else {
            inControl.brake(1);
        }
        if (states[LEFT]) {
            inControl.addSpeed(-4, 0, true);
        } else if (states[RIGHT]) {
            inControl.addSpeed(4, 0, true);
        } else {
            inControl.brake(0);
        }
        if (states[JUMP]) {
            inControl.setisJumping(true);
        }
        if (states[RUN]) {
            inControl.setMaxSpeed(16);
        } else {
            inControl.setMaxSpeed(8);
        }
        if (states[LIGHT]) {
            inControl.setEmits(!inControl.isEmits());
        }
    }

    @Override
    public boolean isMenuOn() {
        if (actions[3].in != null) {
            if (actions[3].in.isPut()) {
                if (!actions[3].in.isPressed()) {
                    actions[3].in.setPressed(true);
                    return true;
                }
            } else {
                actions[3].in.setPressed(false);
            }
        }
        return false;
    }

    @Override
    public void getMenuInput() {
        for (int i = 0; i < 4; i++) {
            actions[i].Do();
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
