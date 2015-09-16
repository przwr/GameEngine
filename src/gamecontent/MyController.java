/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Delay;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.inputs.Action;
import game.gameobject.inputs.AnyInput;
import game.gameobject.inputs.PlayerController;
import game.gameobject.temporalmodifiers.SpeedChanger;
import sprites.Animation;

/**
 * @author przemek
 */
public class MyController extends PlayerController {

    public static final byte MENU_UP = 0, MENU_DOWN = 1, MENU_ACTION = 2, MENU_BACK = 3, MENU_LEFT = 4,
            MENU_RIGHT = 5, UP = 6, DOWN = 7, LEFT = 8, RIGHT = 9, ATTACK = 10, RUN = 11, LIGHT = 12,
            ZOOM = 13, NEXT = 14, PREVIOUS = 15, BLOCK = 16, DODGE = 17, REVERSE = 18, SNEAK = 19,
            ACTION = 20, ACTION_8 = 21, ACTION_7 = 22, ACTION_6 = 23, ACTION_5 = 24, ACTION_4 = 25,
            ACTION_3 = 26, ACTION_2 = 27, ACTION_1 = 28;
    public static final byte MENU_ACTIONS_COUNT = 6, ACTIONS_COUNT = 29, ATTACK_COUNT = 5;

    public static final byte ATTACK_SLASH = 0, ATTACK_THRUST = 1, ATTACK_UPPER_SLASH = 2,
            ATTACK_WEAK_PUNCH = 3, ATTACK_STRONG_PUNCH = 4;
    private final int[] attackFrames;
    private final Delay sideDelay;
    private final Delay jumpDelay;
    private final SpeedChanger jumpMaker;
    private final SpeedChanger attackMovement;
    private int tempDirection, lagDuration, attackType, sideDirection;
    private boolean running, diagonal, inputLag;
    private Animation playerAnimation;
    private MyGUI gui;
    private int jumpDirection, jumpLag;

    private boolean[] blockedInputs;

    public MyController(Entity inControl, MyGUI playersGUI) {
        super(inControl);
        gui = playersGUI;
        inputs = new AnyInput[ACTIONS_COUNT];
        actions = new Action[ACTIONS_COUNT];
        blockedInputs = new boolean[ACTIONS_COUNT];
        sideDelay = new Delay(25);
        jumpDelay = new Delay(400);
        attackType = 0;
        attackFrames = new int[]{22, 27, 31, 38, 40};
        jumpMaker = new SpeedChanger(8);
        jumpMaker.setType(SpeedChanger.DECREASING);
        attackMovement = new SpeedChanger(4);
        attackMovement.setType(SpeedChanger.DECREASING);
    }

    @Override
    public void initialize() {
        for (int i = 0; i < ACTIONS_COUNT; i++) {
            actions[i] = new Action(inputs[i]);
        }
    }

    @Override
    public void getInput() {
        if (gui != null) {
            updateActionsIfNoLag();
            //ANIMACJA//
            tempDirection = inControl.getDirection8Way();
            running = !actions[RUN].isKeyPressed();

            playerAnimation = (Animation) inControl.getAppearance();
            playerAnimation.setAnimate(true);
            diagonal = true;

            if (inControl.isAbleToMove()) {
                //System.out.println(getAllInput(ATTACK));
                if (jumpLag == 0) {
                    if (actions[ATTACK].isKeyPressed()) {
                        updateAttack();
                    } else {
                        updateMovement();
                    }
                } else {
                    inControl.brake(2);
                }
                updateDodgeJump();
                updateRest();
            } else {
                playerAnimation.animateSingleInDirection(tempDirection, 0);
                inControl.brake(2);
            }
            if (!actions[ATTACK].isKeyPressed() && jumpLag == 0) {
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

    void setInputBlocked(boolean blocked, int... inputs) {
        for (int i : inputs) {
            blockedInputs[i] = blocked;
        }
    }

    void setAllInputUnblocked() {
        for (int i = 0; i < blockedInputs.length; i++) {
            blockedInputs[i] = false;
        }
    }

    private void updateActionsIfNoLag() {
        if (inputLag) {
            lagDuration--;
            if (lagDuration < 0) {
                inputLag = false;
            }
            for (int i = MENU_ACTIONS_COUNT; i < ACTIONS_COUNT; i++) {
                if (blockedInputs[i]) {
                    actions[i].updateBlockedState();
                } else {
                    actions[i].updatePassiveState();
                }
            }
        } else {
            for (int i = MENU_ACTIONS_COUNT; i < ACTIONS_COUNT; i++) {
                if (blockedInputs[i]) {
                    actions[i].updateBlockedState();
                } else {
                    actions[i].updateActiveState();
                }
            }
        }
    }

    private void updateAttack() {
        if (actions[ATTACK].isKeyClicked()) {
            setInputLag(15);
            attackMovement.setSpeedInDirection(tempDirection * 45, 10);
            attackMovement.start();
            inControl.addChanger(attackMovement);
            switch (attackType) {
                case ATTACK_SLASH:
                    playerAnimation.animateIntervalInDirectionOnce(tempDirection, 21, 25);
                    break;
                case ATTACK_THRUST:
                    playerAnimation.animateIntervalInDirectionOnce(tempDirection, 26, 28);
                    break;
                case ATTACK_UPPER_SLASH:
                    playerAnimation.animateIntervalInDirectionOnce(tempDirection, 29, 36);
                    break;
                case ATTACK_WEAK_PUNCH:
                    playerAnimation.animateIntervalInDirectionOnce(tempDirection, 37, 39);
                    break;
                case ATTACK_STRONG_PUNCH:
                    playerAnimation.animateIntervalInDirectionOnce(tempDirection, 40, 41);
                    break;
            }
        }
        inControl.brakeWithModifier(2, 2);
    }

    private void updateMovement() {
        if (actions[UP].isKeyPressed()) {
            if (actions[LEFT].isKeyPressed()) {
                animateMoving(3);
                inControl.addSpeed(-4, -4);
            } else if (actions[RIGHT].isKeyPressed()) {
                animateMoving(1);
                inControl.addSpeed(4, -4);
            } else {
                if (actions[LEFT].isKeyReleased()) {
                    sideDirection = 3;
                    sideDelay.start();
                }
                if (actions[RIGHT].isKeyReleased()) {
                    sideDirection = 1;
                    sideDelay.start();
                }
                animateMoving(2);
                inControl.addSpeed(0, -4);
            }
        } else if (actions[DOWN].isKeyPressed()) {
            if (actions[LEFT].isKeyPressed()) {
                animateMoving(5);
                inControl.addSpeed(-4, 4);
            } else if (actions[RIGHT].isKeyPressed()) {
                animateMoving(7);
                inControl.addSpeed(4, 4);
            } else {
                if (actions[LEFT].isKeyReleased()) {
                    sideDirection = 5;
                    sideDelay.start();
                }
                if (actions[RIGHT].isKeyReleased()) {
                    sideDirection = 7;
                    sideDelay.start();
                }
                animateMoving(6);
                inControl.addSpeed(0, 4);
            }
        } else {
            if (actions[RIGHT].isKeyPressed()) {
                if (actions[UP].isKeyReleased()) {
                    sideDirection = 1;
                    sideDelay.start();
                }
                if (actions[DOWN].isKeyReleased()) {
                    sideDirection = 7;
                    sideDelay.start();
                }
                animateMoving(0);
                inControl.addSpeed(4, 0);
            } else if (actions[LEFT].isKeyPressed()) {
                if (actions[UP].isKeyReleased()) {
                    sideDirection = 3;
                    sideDelay.start();
                }
                if (actions[DOWN].isKeyReleased()) {
                    sideDirection = 5;
                    sideDelay.start();
                }
                animateMoving(4);
                inControl.addSpeed(-4, 0);
            } else {
                if (!sideDelay.isActive()) {
                    playerAnimation.animateSingleInDirection(tempDirection, 0);
                } else {
                    playerAnimation.animateSingleInDirection(sideDirection, 0);
                }
            }
        }
        if (!actions[UP].isKeyPressed() && !actions[DOWN].isKeyPressed()) {
            diagonal = false;
            inControl.brake(1);
        }
        if (!actions[LEFT].isKeyPressed() && !actions[RIGHT].isKeyPressed()) {
            diagonal = false;
            inControl.brake(0);
        }
        if (sideDelay.isOver()) {
            if (!actions[UP].isKeyPressed() && !actions[DOWN].isKeyPressed()
                    && !actions[LEFT].isKeyPressed() && !actions[RIGHT].isKeyPressed()) {
                inControl.setDirection(sideDirection * 45);
            }
            sideDelay.stop();
        }
    }

    private void updateDodgeJump() {
        if (jumpMaker.isOver()) {
            if (jumpLag == 0) {
                int jumpSpeed = 40;
                if (!actions[DODGE].isKeyPressed()) {
                    checkDoubleClickDodge(jumpSpeed);
                } else {
                    checkOneButtonDodge(jumpSpeed);
                }
            } else {
                jumpLag--;
                playerAnimation.animateSingleInDirection(tempDirection, 45);
                if (jumpLag == 0) {
                    setInputBlocked(false, ATTACK);
                }
            }
        } else {
            playerAnimation.animateSingleInDirection(tempDirection, 43);
        }
        if (jumpDelay.isActive() && jumpDelay.isOver()) {
            jumpDelay.stop();
        }
    }

    private void checkDoubleClickDodge(int jumpSpeed) {
        if (actions[UP].isKeyClicked()) {
            if (actions[LEFT].isKeyPressed()) {
                prepareDodgeJump(-jumpSpeed, -jumpSpeed, 135);
            } else if (actions[RIGHT].isKeyPressed()) {
                prepareDodgeJump(jumpSpeed, -jumpSpeed, 45);
            } else {
                prepareDodgeJump(0, -jumpSpeed, 90);
            }
        }
        if (actions[DOWN].isKeyClicked()) {
            if (actions[LEFT].isKeyPressed()) {
                prepareDodgeJump(-jumpSpeed, jumpSpeed, 225);
            } else if (actions[RIGHT].isKeyPressed()) {
                prepareDodgeJump(jumpSpeed, jumpSpeed, 315);
            } else {
                prepareDodgeJump(0, jumpSpeed, 270);
            }
        }
        if (actions[LEFT].isKeyClicked()) {
            if (actions[UP].isKeyPressed()) {
                prepareDodgeJump(-jumpSpeed, -jumpSpeed, 135);
            } else if (actions[DOWN].isKeyPressed()) {
                prepareDodgeJump(-jumpSpeed, jumpSpeed, 225);
            } else {
                prepareDodgeJump(-jumpSpeed, 0, 180);
            }
        }
        if (actions[RIGHT].isKeyClicked()) {
            if (actions[UP].isKeyPressed()) {
                prepareDodgeJump(jumpSpeed, -jumpSpeed, 45);
            } else if (actions[DOWN].isKeyPressed()) {
                prepareDodgeJump(jumpSpeed, jumpSpeed, 315);
            } else {
                prepareDodgeJump(jumpSpeed, 0, 0);
            }
        }
    }

    private void checkOneButtonDodge(int jumpSpeed) {
        if (actions[UP].isKeyPressed()) {
            if (actions[LEFT].isKeyPressed()) {
                dodgeJump(-jumpSpeed, -jumpSpeed);
            } else if (actions[RIGHT].isKeyPressed()) {
                dodgeJump(jumpSpeed, -jumpSpeed);
            } else {
                dodgeJump(0, -jumpSpeed);
            }
        } else if (actions[DOWN].isKeyPressed()) {
            if (actions[LEFT].isKeyPressed()) {
                dodgeJump(-jumpSpeed, jumpSpeed);
            } else if (actions[RIGHT].isKeyPressed()) {
                dodgeJump(jumpSpeed, jumpSpeed);
            } else {
                dodgeJump(0, jumpSpeed);
            }
        } else if (actions[LEFT].isKeyPressed()) {
            dodgeJump(-jumpSpeed, 0);
        } else if (actions[RIGHT].isKeyPressed()) {
            dodgeJump(jumpSpeed, 0);
        }
    }

    private void prepareDodgeJump(int xSpeed, int ySpeed, int direction) {
        if (jumpDelay.isActive()) {
            if (!jumpDelay.isOver() && jumpDirection == direction) {
                dodgeJump(xSpeed, ySpeed);
            }
        } else {
            jumpDirection = direction;
            jumpDelay.start();
        }
    }

    private void dodgeJump(int xSpeed, int ySpeed) {
        if (xSpeed == 0 || ySpeed == 0) {
            jumpMaker.setSpeed(xSpeed, ySpeed);
        } else {
            jumpMaker.setSpeed((int) (xSpeed * 0.7), (int) (ySpeed * 0.7));
        }
        jumpMaker.start();
        inControl.addChanger(jumpMaker);
        jumpDelay.stop();
        setInputBlocked(true, ATTACK);
        jumpLag = jumpMaker.getTotalTime() / 2;
        setInputLag(jumpLag);
    }

    private void updateRest() {
        if (actions[NEXT].isKeyClicked()) {
            attackType += 1;
            if (attackType > ATTACK_COUNT - 1) {
                attackType = 0;
            }
            gui.changeAttackIcon(attackType);
        } else if (actions[PREVIOUS].isKeyClicked()) {
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
        if (actions[LIGHT].isKeyClicked()) {
            inControl.setEmits(!inControl.isEmits());
        }
        if (actions[ZOOM].isKeyClicked()) {
            if (inControl instanceof Player) {
                ((Player) inControl).getCamera().switchZoom();
            }
        }
    }

    private void animateMoving(int direction) {
        if (running) {
            playerAnimation.animateIntervalInDirection(direction, 7, 18);
        } else {
            playerAnimation.animateIntervalInDirection(direction, 1, 6);
        }
        inControl.setDirection(direction * 45);
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
        } else if (actions[MENU_RIGHT].isKeyClicked() || actions[RIGHT].isKeyClicked()) {
            ((Player) inControl).getMenu().choice(1);
        } else if (actions[MENU_LEFT].isKeyClicked() || actions[LEFT].isKeyClicked()) {
            ((Player) inControl).getMenu().choice(2);
        } else if (actions[MENU_BACK].isKeyClicked()) {
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

    private void setInputLag(int time) {
        if (!inputLag) {
            inputLag = true;
            lagDuration = time;
        }
    }

    public void setPlayersGUI(MyGUI gui) {
        this.gui = gui;
    }
}
