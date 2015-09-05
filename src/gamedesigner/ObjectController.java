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

    private final int ACTIONS_COUNT = 11;

    public ObjectController(Entity inControl) {
        super(inControl);
        inputs = new AnyInput[ACTIONS_COUNT];
        actions = new Action[ACTIONS_COUNT]; // 4 pierwsze to menu
    }

    @Override
    public void initialize() {
        for (int i = 0; i < ACTIONS_COUNT; i++) {
            actions[i] = new Action(inputs[i]);
        }
    }

    @Override
    public void getInput() {
    }

    @Override
    public boolean isMenuOn() {
        actions[3].updateActiveState();
        return actions[3].isKeyClicked();
    }

    @Override
    public void getMenuInput() {
        for (int i = 0; i < 4; i++) {
            actions[i].updateActiveState();
        }
        if (actions[0].isKeyClicked()) {
            ((Player) inControl).getMenu().setChosen(-1);
        } else if (actions[1].isKeyClicked()) {
            ((Player) inControl).getMenu().setChosen(1);
        }
        if (actions[2].isKeyClicked()) {
            ((Player) inControl).getMenu().choice();
        } else if (actions[3].isKeyClicked()) {
            ((Player) inControl).getMenu().back();
        }
    }

    @Override
    public int getActionsCount() {
        return ACTIONS_COUNT;
    }
}
