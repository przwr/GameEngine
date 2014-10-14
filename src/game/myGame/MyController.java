/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.myGame;

import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.ActionSingleClick;
import game.gameobject.ActionWhileClicked;
import game.gameobject.AnyInput;
import game.gameobject.Controler;
import game.gameobject.Entity;
import game.gameobject.Player;

/**
 *
 * @author przemek
 */
public class MyController extends Controler {

    public final AnyInput[] inputs = new AnyInput[36];
    public final Action[] actions = new Action[36]; // 4 pierwsze to menu

    public MyController(Entity inControl) {
        super(inControl);
    }

    public void init() {
        actions[0] = new ActionOnOff(inputs[0], inControl) {
            @Override
            public void Act() {
                ((Player) inControl).menu.setChoosen(-1);
            }
        };
        actions[1] = new ActionOnOff(inputs[1], inControl) {
            @Override
            public void Act() {
                ((Player) inControl).menu.setChoosen(1);
            }
        };
        actions[2] = new ActionOnOff(inputs[2], inControl) {
            @Override
            public void Act() {
                ((Player) inControl).menu.choice();
            }
        };
        actions[3] = new ActionOnOff(inputs[3], inControl) {
            @Override
            public void Act() {
                ((Player) inControl).menu.back();
            }
        };
        actions[4] = new ActionSingleClick(inputs[4], inControl) {
            @Override
            public void Act() {
                inControl.canMove(0, -1);
            }
        };
        actions[5] = new ActionSingleClick(inputs[5], inControl) {
            @Override
            public void Act() {
                inControl.canMove(0, 1);
            }
        };
        actions[6] = new ActionSingleClick(inputs[6], inControl) {
            @Override
            public void Act() {
                inControl.canMove(-1, 0);
                ((Player) inControl).getAnim().setFlip(0);
            }
        };
        actions[7] = new ActionSingleClick(inputs[7], inControl) {
            @Override
            public void Act() {
                inControl.canMove(1, 0);
                ((Player) inControl).getAnim().setFlip(1);
            }
        };
        actions[8] = new ActionSingleClick(inputs[8], inControl) {
            @Override
            public void Act() {
                ((Player) inControl).getPlace().shakeCam(((Player) inControl).getCam());
            }
        };
        actions[9] = new ActionWhileClicked(inputs[9], inControl) {
            @Override
            public void Act() {
                inControl.setSpeed(16);
            }

            @Override
            public void noAct() {
                inControl.setSpeed(8);
            }
        };
        actions[10] = new ActionOnOff(inputs[10], inControl) {
            @Override
            public void Act() {
                ((Player) inControl).setEmits(!((Player) inControl).isEmits());
            }
        };
    }

    @Override
    public void getInput() {
        for (int i = 4; i < 11; i++) {
            actions[i].Do();
        }
    }

    @Override
    public boolean isMenuOn() {
        // 3 - ESCAPE
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
