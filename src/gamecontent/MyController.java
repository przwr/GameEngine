/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import game.gameobject.*;
import sprites.Animation;

/**
 *
 * @author przemek
 */
public class MyController extends Controler {

    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, JUMP = 4, RUN = 5, LIGHT = 6, ZOOM = 7;
    public static final int FIRST_NO_MENU_ACTION = 4, ACTIONS_COUNT = 11;

    private int direction;
    private boolean running, diagonal;
    private Animation playerAnimation;

    public MyController(Entity inControl) {
        super(inControl);
        inputs = new AnyInput[36];
        actions = new Action[36];
        states = new byte[8];
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
            if (actions[i].isOn()) {
                if (states[i - 4] == 0) {
                    states[i - 4] = 2;
                } else {
                    states[i - 4] = 1;
                }
            } else {
                if (states[i - 4] == 1) {
                    states[i - 4] = 3;
                } else {
                    states[i - 4] = 0;
                }
            }
        }
        //ANIMACJA//
        direction = inControl.getDirection();
        running = !isKeyPressed(RUN);

        playerAnimation = inControl.getAnimation();

        playerAnimation.setAnimate(true);
        diagonal = true;

        if (inControl.isAbleToMove()) {
            if (isKeyPressed(JUMP)) {
                playerAnimation.setStopAtEnd(true);
                playerAnimation.animateIntervalInDirection(direction / 45, 21, 25);
                inControl.brake(2);
            } else {
                playerAnimation.setStopAtEnd(false);
                if (isKeyPressed(UP)) {
                    if (isKeyPressed(LEFT)) {
                        animateMoving(135);
                        inControl.addSpeed(-4, -4);
                    } else if (isKeyPressed(RIGHT)) {
                        animateMoving(45);
                        inControl.addSpeed(4, -4);
                    } else {
                        animateMoving(90);
                        inControl.addSpeed(0, -4);
                    }
                } else if (isKeyPressed(DOWN)) {
                    if (isKeyPressed(LEFT)) {
                        animateMoving(225);
                        inControl.addSpeed(-4, 4);
                    } else if (isKeyPressed(RIGHT)) {
                        animateMoving(315);
                        inControl.addSpeed(4, 4);
                    } else {
                        animateMoving(270);
                        inControl.addSpeed(0, 4);
                    }
                } else {
                    if (isKeyPressed(RIGHT)) {
                        animateMoving(0);
                        inControl.addSpeed(4, 0);
                    } else if (isKeyPressed(LEFT)) {
                        animateMoving(180);
                        inControl.addSpeed(-4, 0);
                    } else {
                        playerAnimation.animateSingleInDirection(direction / 45, 0);
                    }
                }
                if (!isKeyPressed(UP) && !isKeyPressed(DOWN)) {
                    diagonal = false;
                    inControl.brake(1);
                }
                if (!isKeyPressed(LEFT) && !isKeyPressed(RIGHT)) {
                    diagonal = false;
                    inControl.brake(0);
                }
            }

            /*if (isKeyPressed(JUMP)) {
             inControl.setJumping(true);
             inControl.setHop(true);
             }*/
            if (isKeyPressed(RUN)) {
                inControl.setMaxSpeed(diagonal ? 1.5 : 2);
            } else {
                inControl.setMaxSpeed(diagonal ? 6 : 8);
            }
            if (isKeyPressed(LIGHT)) {
                inControl.setEmits(!inControl.isEmits());
            }
            if (isKeyPressed(ZOOM)) {
                if (inControl instanceof Player) {
                    ((Player) inControl).getCamera().switchZoom();
                }
            }
        } else {
            playerAnimation.animateSingleInDirection(direction / 45, 0);
        }
        if (!isKeyPressed(JUMP)) {
            if (running) {
                playerAnimation.setFPS((int) (inControl.getSpeed() * 4));
            } else {
                playerAnimation.setFPS((int) (inControl.getSpeed() * 5));
            }
        } else {
            playerAnimation.setFPS(30);
        }
    }

    private void animateMoving(int direction) {
        if (running) {
            playerAnimation.animateIntervalInDirection(direction / 45, 7, 18);
        } else {
            playerAnimation.animateIntervalInDirection(direction / 45, 1, 6);
        }
        inControl.setDirection(direction);
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
