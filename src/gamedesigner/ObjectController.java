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

    public static final byte MENU_UP = 0, MENU_DOWN = 1, MENU_ACTION = 2, MENU_BACK = 3, LEFT = 6, RIGHT = 7;
    private final int MENU_ACTIONS_COUNT = 11;

    public ObjectController(Entity inControl) {
        super(inControl);
        inputs = new AnyInput[MENU_ACTIONS_COUNT];
        actions = new Action[MENU_ACTIONS_COUNT]; // 4 pierwsze to menu
    }

    @Override
    public void initialize() {
        for (int i = 0; i < MENU_ACTIONS_COUNT; i++) {
            actions[i] = new Action(inputs[i]);
        }
    }

    @Override
    public void getInput() {
    }

    @Override
    public boolean isMenuOn() {
        actions[MENU_BACK].updateActiveState();
        return actions[MENU_BACK].isKeyClicked();
    }

    @Override
    public void getMenuInput() {
        for (int i = 0; i < MENU_ACTIONS_COUNT; i++) {
            actions[i].updateActiveState();
        }
        actions[RIGHT].updateActiveState();
        actions[LEFT].updateActiveState();
        if (actions[MENU_UP].isKeyClicked()) {
            ((Player) inControl).getMenu().setChosen(-1);
        } else if (actions[MENU_DOWN].isKeyClicked()) {
            ((Player) inControl).getMenu().setChosen(1);
        }
        if (actions[MENU_ACTION].isKeyClicked()) {
            ((Player) inControl).getMenu().choice(0);
        } else if (actions[RIGHT].isKeyClicked()) {
            ((Player) inControl).getMenu().choice(1);
        } else if (actions[LEFT].isKeyClicked()) {
            ((Player) inControl).getMenu().choice(2);
        } else if (actions[MENU_BACK].isKeyClicked()) {
            ((Player) inControl).getMenu().back();
        }
    }

    @Override
    public int getActionsCount() {
        return MENU_ACTIONS_COUNT;
    }
}
