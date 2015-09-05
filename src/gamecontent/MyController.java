/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.Delay;
import game.gameobject.*;
import sprites.Animation;

/**
 * @author przemek
 */
public class MyController extends PlayerController {

    public static final byte UP = 4, DOWN = 5, LEFT = 6, RIGHT = 7, ATTACK = 8, RUN = 9, LIGHT = 10, ZOOM = 11, NEXT = 12, PREVIOUS = 13, BLOCK = 14, DODGE = 15, REVERSE = 16, SNEAK = 17,
            ACTION_9 = 18, ACTION_8 = 19, ACTION_7 = 20, ACTION_6 = 21, ACTION_5 = 22, ACTION_4 = 23, ACTION_3 = 24, ACTION_2 = 25, ACTION_1 = 26;
    public static final byte MENU_ACTIONS = 4, ACTIONS_COUNT = 27, ATTACK_COUNT = 5;
    public static final int[] ON_OFF_ACTIONS = {0, 1, 2, 3, LIGHT, ZOOM, NEXT, PREVIOUS};

    public static final byte ATTACK_SLASH = 0, ATTACK_THRUST = 1, ATTACK_UPPER_SLASH = 2, ATTACK_WEAK_PUNCH = 3, ATTACK_STRONG_PUNCH = 4;
    private final int[] attackFrames;
    private final Delay sideDelay;
    private int direction, lagDuration, attackType, sideDirection;
    private boolean running, diagonal, inputLag;
    private Animation playerAnimation;
    private MyGUI gui;

    public MyController(Entity inControl, MyGUI playersGUI) {
        super(inControl);
        gui = playersGUI;
        inputs = new AnyInput[ACTIONS_COUNT];
        actions = new Action[ACTIONS_COUNT];
        states = new byte[ACTIONS_COUNT];
        sideDelay = new Delay(25);
        attackType = 0;
        attackFrames = new int[]{22, 27, 31, 38, 40};
    }

    @Override
    public void initialize() {
        for (int i = 0; i < ACTIONS_COUNT; i++) {
            actions[i] = isOnOff(i) ? new ActionOnOff(inputs[i]) : new ActionHold(inputs[i]);
        }
    }

    private boolean isOnOff(int action) {
        for (int i = 0; i < ON_OFF_ACTIONS.length; i++) {
            if (ON_OFF_ACTIONS[i] == action) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void getInput() {
        if (gui != null) {
            updateActionsIfNoLag();
            //ANIMACJA//
            direction = inControl.getDirection();
            running = !isKeyPressed(RUN);

            playerAnimation = (Animation) inControl.getAppearance();
            playerAnimation.setAnimate(true);
            diagonal = true;

            if (inControl.isAbleToMove()) {
                if (isKeyPressed(ATTACK)) {
                    updateAttack();
                } else {
                    updateMovement();
                }
                updateRest();
            } else {
                playerAnimation.animateSingleInDirection(direction / 45, 0);
            }
            if (!isKeyPressed(ATTACK)) {
                if (running) {
                    playerAnimation.setFPS((int) (inControl.getSpeed() * 4));
                } else {
                    playerAnimation.setFPS((int) (inControl.getSpeed() * 3));
                }
            } else {
                playerAnimation.setFPS(60);
            }
        }
    }

    private void updateActionsIfNoLag() {
        if (inputLag) {
            lagDuration--;
            if (lagDuration < 0) {
                inputLag = false;
            }
            for (int i = MENU_ACTIONS; i < ACTIONS_COUNT; i++) {
                if (states[i] == KEY_CLICKED) {
                    states[i] = KEY_PRESSED;
                } else if (states[i] == KEY_RELEASED) {
                    states[i] = KEY_NO_INPUT;
                }
            }
        } else {
            for (int i = MENU_ACTIONS; i < ACTIONS_COUNT; i++) {
                actions[i].act();
                updateAction(i);
            }
        }
    }

    private void updateAttack() {
        if (isKeyClicked(ATTACK)) {
            setInputLag(30);
        }
        playerAnimation.setStopAtEnd(true);
        switch (attackType) {
            case ATTACK_SLASH:
                playerAnimation.animateIntervalInDirection(direction / 45, 21, 25);
                break;
            case ATTACK_THRUST:
                playerAnimation.animateIntervalInDirection(direction / 45, 26, 28);
                break;
            case ATTACK_UPPER_SLASH:
                playerAnimation.animateIntervalInDirection(direction / 45, 29, 36);
                break;
            case ATTACK_WEAK_PUNCH:
                playerAnimation.animateIntervalInDirection(direction / 45, 37, 39);
                break;
            case ATTACK_STRONG_PUNCH:
                playerAnimation.animateIntervalInDirection(direction / 45, 40, 41);
                break;
        }
        inControl.brakeWithModifier(2, 2);
    }

    private void updateMovement() {
        playerAnimation.setStopAtEnd(false);
        if (isKeyPressed(UP)) {
            if (isKeyPressed(LEFT)) {
                animateMoving(135);
                inControl.addSpeed(-4, -4);
            } else if (isKeyPressed(RIGHT)) {
                animateMoving(45);
                inControl.addSpeed(4, -4);
            } else {
                if (isKeyReleased(LEFT)) {
                    sideDirection = 135;
                    sideDelay.start();
                }
                if (isKeyReleased(RIGHT)) {
                    sideDirection = 45;
                    sideDelay.start();
                }
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
                if (isKeyReleased(LEFT)) {
                    sideDirection = 225;
                    sideDelay.start();
                }
                if (isKeyReleased(RIGHT)) {
                    sideDirection = 315;
                    sideDelay.start();
                }
                animateMoving(270);
                inControl.addSpeed(0, 4);
            }
        } else {
            if (isKeyPressed(RIGHT)) {
                if (isKeyReleased(UP)) {
                    sideDirection = 45;
                    sideDelay.start();
                }
                if (isKeyReleased(DOWN)) {
                    sideDirection = 315;
                    sideDelay.start();
                }
                animateMoving(0);
                inControl.addSpeed(4, 0);
            } else if (isKeyPressed(LEFT)) {
                if (isKeyReleased(UP)) {
                    sideDirection = 135;
                    sideDelay.start();
                }
                if (isKeyReleased(DOWN)) {
                    sideDirection = 225;
                    sideDelay.start();
                }
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
        if (sideDelay.isActive() && sideDelay.isOver()) {
            if (!isKeyPressed(UP) && !isKeyPressed(DOWN)
                    && !isKeyPressed(LEFT) && !isKeyPressed(RIGHT)) {
                inControl.setDirection(sideDirection);
            }
            sideDelay.stop();
        }
    }

    private void updateRest() {
        if (isKeyClicked(NEXT)) {
            attackType += 1;
            if (attackType > ATTACK_COUNT - 1) {
                attackType = 0;
            }
            gui.changeAttackIcon(attackType);
        } else if (isKeyClicked(PREVIOUS)) {
            attackType -= 1;
            if (attackType < 0) {
                attackType = ATTACK_COUNT - 1;
            }
            gui.changeAttackIcon(attackType);
        }
        if (!running) {
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
    }

    private void setInputLag(int time) {
        if (!inputLag) {
            inputLag = true;
            lagDuration = time;
        }
    }

    private void updateAction(int action) {
        if (actions[action].isOn()) {
            if (states[action] == KEY_NO_INPUT) {

                states[action] = KEY_CLICKED;
            } else {
                states[action] = KEY_PRESSED;
            }
        } else {
            if (states[action] == KEY_PRESSED) {
                states[action] = KEY_RELEASED;
            } else {
                states[action] = KEY_NO_INPUT;
            }
        }
    }


    public void setPlayersGUI(MyGUI gui) {
        this.gui = gui;
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
        return ACTIONS_COUNT;
    }

    public int[] getAttackFrames() {
        return attackFrames;
    }

    public int getAttackType() {
        return attackType;
    }
}
