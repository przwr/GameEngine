/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.myGame;

import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.ActionWhileClicked;
import game.gameobject.AnyInput;
import game.gameobject.Controler;
import game.gameobject.Entity;

/**
 *
 * @author przemek
 */
public class MyController extends Controler {

    public static final int UP = 0;
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int SHAKE = 4;
    public static final int RUN = 5;
    public static final int LIGHT = 6;

    public final AnyInput[] inputs = new AnyInput[36];
    public final Action[] actions = new Action[36]; // 4 pierwsze to menu
    private int[] states = new int[10];

    public MyController(Entity inControl) {
        super(inControl);
    }

    public void init() {
        actions[0] = new ActionOnOff(inputs[0], inControl) {
            @Override
            public void Act() {
                ((MyPlayer) inControl).menu.setChoosen(-1);
            }
        };
        actions[1] = new ActionOnOff(inputs[1], inControl) {
            @Override
            public void Act() {
                ((MyPlayer) inControl).menu.setChoosen(1);
            }
        };
        actions[2] = new ActionOnOff(inputs[2], inControl) {
            @Override
            public void Act() {
                ((MyPlayer) inControl).menu.choice();
            }
        };
        actions[3] = new ActionOnOff(inputs[3], inControl) {
            @Override
            public void Act() {
                ((MyPlayer) inControl).menu.back();
            }
        };
        for (int i = 0; i < 7; i++) {
            actions[i + 4] = new ActionWhileClicked(inputs[i + 4], inControl, states, i);
        }
    }

    public boolean isPressed(int i) {
        return states[i] > 0;
    }

    public boolean isClicked(int i) {
        return states[i] == 2;
    }

    public boolean isReleased(int i) {
        return states[i] == -1;
    }

    public boolean isMoving() {
        return states[0] > 0 || states[1] > 0 || states[2] > 0 || states[3] > 0;
    }

    @Override
    public void getInput() {
        for (int i = 4; i < 11; i++) {
            actions[i].Do();
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
    }

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
