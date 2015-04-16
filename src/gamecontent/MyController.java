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

    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, JUMP = 4, RUN = 5, LIGHT = 6, ZOOM = 7;
    public static final int FIRST_NO_MENU_ACTION = 4, ACTIONS_COUNT = 11;

    private int direction;
    private boolean running, diagonal;

    public MyController(Entity inControl) {
        super(inControl);
        inputs = new AnyInput[36];
        actions = new Action[36];
        states = new boolean[8];
    }

    @Override
    public void initialize() {
        actions[0] = new ActionOnOff(inputs[0]);
        actions[1] = new ActionOnOff(inputs[1]);
        actions[2] = new ActionOnOff(inputs[2]);
        actions[3] = new ActionOnOff(inputs[3]);
        for (byte i = FIRST_NO_MENU_ACTION; i < ACTIONS_COUNT - 1; i++) {
            actions[i] = new ActionHold(inputs[i]);
        }
        actions[ACTIONS_COUNT - 1] = new ActionOnOff(inputs[ACTIONS_COUNT - 1]);
        actions[ACTIONS_COUNT] = new ActionOnOff(inputs[ACTIONS_COUNT]);
    }

    @Override
    public void getInput() {
        for (int i = 4; i <= ACTIONS_COUNT; i++) {
            actions[i].act();
            states[i - 4] = actions[i].isOn();
        }
        diagonal = true;

        if (states[UP]) {
            inControl.addSpeed(0, -4);
        } else if (states[DOWN]) {
            inControl.addSpeed(0, 4);
        } else {
            diagonal = false;
            inControl.brake(1);
        }
        if (states[LEFT]) {
            inControl.addSpeed(-4, 0);
        } else if (states[RIGHT]) {
            inControl.addSpeed(4, 0);
        } else {
            diagonal = false;
            inControl.brake(0);
        }

        //ANIMACJA//
        direction = inControl.getDirection();
        running = !states[RUN];

        inControl.getAnimation().setAnimate(true);

        if (states[UP]) {
            if (states[LEFT]) {
                if (running) {
                    inControl.getAnimation().animateInterval(64, 75);
                } else {
                    inControl.getAnimation().animateInterval(58, 63);
                }
                inControl.setDirection(135);
            } else if (states[RIGHT]) {
                if (running) {
                    inControl.getAnimation().animateInterval(102, 113);
                } else {
                    inControl.getAnimation().animateInterval(96, 101);
                }
                inControl.setDirection(45);
            } else {
                if (running) {
                    inControl.getAnimation().animateInterval(83, 94);
                } else {
                    inControl.getAnimation().animateInterval(77, 82);
                }
                inControl.setDirection(90);
            }
        } else if (states[DOWN]) {
            if (states[LEFT]) {
                if (running) {
                    inControl.getAnimation().animateInterval(26, 37);
                } else {
                    inControl.getAnimation().animateInterval(20, 25);
                }
                inControl.setDirection(225);
            } else if (states[RIGHT]) {
                if (running) {
                    inControl.getAnimation().animateInterval(140, 151);
                } else {
                    inControl.getAnimation().animateInterval(134, 139);
                }
                inControl.setDirection(315);
            } else {
                if (running) {
                    inControl.getAnimation().animateInterval(7, 18);
                } else {
                    inControl.getAnimation().animateInterval(1, 6);
                }
                inControl.setDirection(270);
            }
        } else if (states[RIGHT]) {
            if (running) {
                inControl.getAnimation().animateInterval(121, 132);
            } else {
                inControl.getAnimation().animateInterval(115, 120);
            }
            inControl.setDirection(0);
        } else if (states[LEFT]) {
            if (running) {
                inControl.getAnimation().animateInterval(45, 56);
            } else {
                inControl.getAnimation().animateInterval(39, 44);
            }
            inControl.setDirection(180);
        } else {
            inControl.getAnimation().animateSingle((270 - direction) / 45 * 19);
        }

        if (states[JUMP]) {
            inControl.setJumping(true);
            inControl.setHop(true);
        }
        if (states[RUN]) {
            inControl.setMaxSpeed(diagonal ? 1.5 : 2);
        } else {
            inControl.setMaxSpeed(diagonal ? 6 : 8);
        }
        inControl.getAnimation().setFPS((int) (inControl.getSpeed() * 4));

        if (states[LIGHT]) {
            inControl.setEmits(!inControl.isEmits());
        }
        if (states[ZOOM]) {
            if (inControl instanceof Player) {
                ((Player) inControl).getCamera().switchZoom();
            }
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
