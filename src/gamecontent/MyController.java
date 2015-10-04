/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Delay;
import engine.utilities.Methods;
import game.Settings;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.inputs.Action;
import game.gameobject.inputs.AnyInput;
import game.gameobject.inputs.PlayerController;
import game.gameobject.items.Arrow;
import game.gameobject.stats.PlayerStats;
import game.gameobject.temporalmodifiers.SpeedChanger;
import game.place.Place;
import sprites.Animation;

/**
 * @author przemek
 */
public class MyController extends PlayerController {

    public static final byte INPUT_MENU_UP = 0, INPUT_MENU_DOWN = 1, INPUT_MENU_ACTION = 2, INPUT_MENU_BACK = 3, INPUT_MENU_LEFT = 4, INPUT_MENU_RIGHT = 5,
            INPUT_UP = 6, INPUT_DOWN = 7, INPUT_LEFT = 8, INPUT_RIGHT = 9, INPUT_ACTION = 10, INPUT_ATTACK = 11, INPUT_SECOND_ATTACK = 12, INPUT_BLOCK = 13,
            INPUT_RUN = 14, INPUT_REVERSE = 15, INPUT_DODGE = 16, INPUT_CHANGE_WEAPON = 17, INPUT_SNEAK = 18, INPUT_CHANGE_SET = 19,
            INPUT_SLOT_UP = 20, INPUT_SLOT_RIGHT = 21, INPUT_SLOT_DOWN = 22, INPUT_SLOT_LEFT = 23,
            INPUT_HANDY_MENU = 24, INPUT_ACTION_1 = 25, INPUT_ACTION_2 = 26, INPUT_LIGHT = 27, INPUT_ZOOM = 28;
    public static final byte MENU_ACTIONS_COUNT = 6, ACTIONS_COUNT = 29, ATTACK_COUNT = 5;

    public static final byte ATTACK_SLASH = 0, ATTACK_THRUST = 1, ATTACK_UPPER_SLASH = 2,
            ATTACK_WEAK_PUNCH = 3, ATTACK_STRONG_PUNCH = 4, ATTACK_NORMAL_ARROW_SHOT = 5;
    private final int[] attackFrames;
    private final Delay lagDelay;
    private final Delay sideDelay;
    private final Delay jumpDelay;
    private final Delay chargingDelay;
    private final SpeedChanger jumpMaker;
    private final SpeedChanger attackMovement;
    private final boolean[] blockedInputs;
    private int tempDirection, lagDuration, sideDirection;
    private byte firstAttackType, secondAttackType, chargingType, lastAttackButton;
    private boolean running, diagonal, inputLag, charging;
    private Animation playerAnimation;
    private PlayerStats stats;
    private MyGUI gui;
    private int jumpDirection, jumpLag;

    public MyController(Entity inControl, MyGUI playersGUI) {
        super(inControl);
        gui = playersGUI;
        inputs = new AnyInput[ACTIONS_COUNT];
        actions = new Action[ACTIONS_COUNT];
        blockedInputs = new boolean[ACTIONS_COUNT];
        lagDelay = Delay.createDelayInMiliseconds(250);
        sideDelay = Delay.createDelayInMiliseconds(25);
        jumpDelay = Delay.createDelayInMiliseconds(400);
        chargingDelay = Delay.createDelayInMiliseconds(300);
        chargingDelay.terminate();
        attackFrames = new int[]{22, 27, 31, 38, 40, 123};
        jumpMaker = new SpeedChanger(8);
        jumpMaker.setType(SpeedChanger.DECREASING);
        attackMovement = new SpeedChanger(4);
        attackMovement.setType(SpeedChanger.DECREASING);
        lastAttackButton = INPUT_ATTACK;
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
            running = !actions[INPUT_RUN].isKeyPressed();

            playerAnimation = (Animation) inControl.getAppearance();
            stats = (PlayerStats) inControl.getStats();
            playerAnimation.setAnimate(true);
            diagonal = true;
            if (!inControl.isHurt()) {
                if (inControl.isAbleToMove()) {
                    if (!charging && chargingDelay.isOver()) {
                        if (jumpLag == 0) {
                            updateAttackTypes();
                            if (actions[INPUT_ATTACK].isKeyPressed() || actions[INPUT_SECOND_ATTACK].isKeyPressed()) {
                                updateAttack();
                            } else {
                                updateMovement();
                            }
                        } else {
                            inControl.brake(2);
                        }
                        updateDodgeJump();
                    } else {
                        updateCharging();
                    }
                    updateRest();
                } else {
                    playerAnimation.animateSingleInDirection(tempDirection, 0);
                    inControl.brake(2);
                }
            } else {
                updateGettingHurt();
            }
            if ((!actions[INPUT_ATTACK].isKeyPressed() || firstAttackType < 0) && (!actions[INPUT_SECOND_ATTACK].isKeyPressed() || secondAttackType < 0) && jumpLag == 0) {
                if (running) {
                    playerAnimation.setFPS((int) (inControl.getSpeed() * 3.5));
                } else {
                    playerAnimation.setFPS((int) (inControl.getSpeed() * 3));
                }
            } else {
                playerAnimation.setFPS(60);
            }
        }
    }

    private void updateGettingHurt() {
        inControl.setDirection8way(Methods.pointAngle8Directions(inControl.getKnockback().getXSpeed(),
                inControl.getKnockback().getYSpeed(), 0, 0));
        charging = false;
        actions[lastAttackButton].setInterrupted();
        playerAnimation.animateSingleInDirection(inControl.getDirection8Way(), 6);
        inControl.brake(2);
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
            if (lagDelay.isOver()) {
                actions[lastAttackButton].setInterrupted();
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

    private void updateAttackTypes() {
        firstAttackType = ((MyPlayer) inControl).getFirstAttackType();
        secondAttackType = ((MyPlayer) inControl).getSecondAttackType();
    }

    private void updateAttack() {
        if (actions[INPUT_ATTACK].isKeyClicked()) {
            if (firstAttackType >= 0) {
                startAttack(firstAttackType);
                lastAttackButton = INPUT_ATTACK;
            }
        } else if (actions[INPUT_SECOND_ATTACK].isKeyClicked()) {
            if (secondAttackType >= 0) {
                startAttack(secondAttackType);
                lastAttackButton = INPUT_SECOND_ATTACK;
            }
        }
        inControl.brakeWithModifier(2, 2);
    }

    private void startAttack(byte attack) {
        setInputLag(220);
        attackMovement.setSpeedInDirection(tempDirection * 45, 10);
        attackMovement.start();
        inControl.addChanger(attackMovement);
        switch (attack) {
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
            case ATTACK_NORMAL_ARROW_SHOT:
                playerAnimation.animateIntervalInDirectionOnce(tempDirection, 46, 48);
                attackMovement.stop();
                stopInputLag();
                charging = true;
                chargingType = ATTACK_NORMAL_ARROW_SHOT;
                break;
        }
    }

    private void updateCharging() {
        if (actions[lastAttackButton].isKeyReleased()) {
            switch (chargingType) {
                case ATTACK_NORMAL_ARROW_SHOT:
                    playerAnimation.animateSingleInDirection(tempDirection, 49);
                    inControl.getAttackActivator(ATTACK_NORMAL_ARROW_SHOT).setActivated(true);
                    break;
            }
            chargingDelay.start();
            charging = false;
        }
        if (chargingDelay.isOver()) {
            updateChargingMovement();
        } else {
            inControl.brake(2);
        }
    }

    private void updateChargingMovement() {
        running = false;
        if (actions[INPUT_UP].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                inControl.addSpeed(-4, -4);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.addSpeed(4, -4);
            } else {
                inControl.addSpeed(0, -4);
            }
        } else if (actions[INPUT_DOWN].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                inControl.addSpeed(-4, 4);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.addSpeed(4, 4);
            } else {
                inControl.addSpeed(0, 4);
            }
        } else {
            if (actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.addSpeed(4, 0);
            } else if (actions[INPUT_LEFT].isKeyPressed()) {
                inControl.addSpeed(-4, 0);
            }
        }
        if (!actions[INPUT_UP].isKeyPressed() && !actions[INPUT_DOWN].isKeyPressed()) {
            diagonal = false;
            inControl.brake(1);
        }
        if (!actions[INPUT_LEFT].isKeyPressed() && !actions[INPUT_RIGHT].isKeyPressed()) {
            diagonal = false;
            inControl.brake(0);
        }
    }

    private void updateMovement() {
        if (actions[INPUT_UP].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                animateMoving(3);
                inControl.addSpeed(-4, -4);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                animateMoving(1);
                inControl.addSpeed(4, -4);
            } else {
                if (actions[INPUT_LEFT].isKeyReleased()) {
                    sideDirection = 3;
                    sideDelay.start();
                }
                if (actions[INPUT_RIGHT].isKeyReleased()) {
                    sideDirection = 1;
                    sideDelay.start();
                }
                animateMoving(2);
                inControl.addSpeed(0, -4);
            }
            if (running) {
                inControl.setMakeNoise(true);
            }
        } else if (actions[INPUT_DOWN].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                animateMoving(5);
                inControl.addSpeed(-4, 4);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                animateMoving(7);
                inControl.addSpeed(4, 4);
            } else {
                if (actions[INPUT_LEFT].isKeyReleased()) {
                    sideDirection = 5;
                    sideDelay.start();
                }
                if (actions[INPUT_RIGHT].isKeyReleased()) {
                    sideDirection = 7;
                    sideDelay.start();
                }
                animateMoving(6);
                inControl.addSpeed(0, 4);
            }
            if (running) {
                inControl.setMakeNoise(true);
            }
        } else {
            if (actions[INPUT_RIGHT].isKeyPressed()) {
                if (actions[INPUT_UP].isKeyReleased()) {
                    sideDirection = 1;
                    sideDelay.start();
                }
                if (actions[INPUT_DOWN].isKeyReleased()) {
                    sideDirection = 7;
                    sideDelay.start();
                }
                animateMoving(0);
                inControl.addSpeed(4, 0);
                if (running) {
                    inControl.setMakeNoise(true);
                }
            } else if (actions[INPUT_LEFT].isKeyPressed()) {
                if (actions[INPUT_UP].isKeyReleased()) {
                    sideDirection = 3;
                    sideDelay.start();
                }
                if (actions[INPUT_DOWN].isKeyReleased()) {
                    sideDirection = 5;
                    sideDelay.start();
                }
                animateMoving(4);
                inControl.addSpeed(-4, 0);
                if (running) {
                    inControl.setMakeNoise(true);
                }
            } else {
                if (!sideDelay.isActive()) {
                    inControl.setMakeNoise(false);
                    playerAnimation.animateSingleInDirection(tempDirection, 0);
                } else {
                    playerAnimation.animateSingleInDirection(sideDirection, 0);
                    inControl.setMakeNoise(false);
                }
            }
        }
        if (!actions[INPUT_UP].isKeyPressed() && !actions[INPUT_DOWN].isKeyPressed()) {
            diagonal = false;
            inControl.brake(1);
        }
        if (!actions[INPUT_LEFT].isKeyPressed() && !actions[INPUT_RIGHT].isKeyPressed()) {
            diagonal = false;
            inControl.brake(0);
        }
        if (sideDelay.isOver()) {
            if (!actions[INPUT_UP].isKeyPressed() && !actions[INPUT_DOWN].isKeyPressed()
                    && !actions[INPUT_LEFT].isKeyPressed() && !actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.setDirection(sideDirection * 45);
                inControl.setMakeNoise(false);
            }
            sideDelay.stop();
        }
    }

    private void updateDodgeJump() {
        if (jumpMaker.isOver()) {
            if (jumpLag == 0) {
                int jumpSpeed = 40;
                if (!actions[INPUT_DODGE].isKeyPressed()) {
                    checkDoubleClickDodge(jumpSpeed);
                } else {
                    checkOneButtonDodge(jumpSpeed);
                }
            } else {
                jumpLag--;
                playerAnimation.animateSingleInDirection(tempDirection, 45);
                if (jumpLag == 0) {
                    setInputBlocked(false, INPUT_ATTACK, INPUT_SECOND_ATTACK);
                }
                inControl.setMakeNoise(true);
            }
        } else {
            playerAnimation.animateSingleInDirection(tempDirection, 43);
        }
        if (jumpDelay.isActive() && jumpDelay.isOver()) {
            jumpDelay.stop();
        }
    }

    private void checkDoubleClickDodge(int jumpSpeed) {
        if (actions[INPUT_UP].isKeyClicked()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                prepareDodgeJump(-jumpSpeed, -jumpSpeed, 135);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                prepareDodgeJump(jumpSpeed, -jumpSpeed, 45);
            } else {
                prepareDodgeJump(0, -jumpSpeed, 90);
            }
        }
        if (actions[INPUT_DOWN].isKeyClicked()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                prepareDodgeJump(-jumpSpeed, jumpSpeed, 225);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                prepareDodgeJump(jumpSpeed, jumpSpeed, 315);
            } else {
                prepareDodgeJump(0, jumpSpeed, 270);
            }
        }
        if (actions[INPUT_LEFT].isKeyClicked()) {
            if (actions[INPUT_UP].isKeyPressed()) {
                prepareDodgeJump(-jumpSpeed, -jumpSpeed, 135);
            } else if (actions[INPUT_DOWN].isKeyPressed()) {
                prepareDodgeJump(-jumpSpeed, jumpSpeed, 225);
            } else {
                prepareDodgeJump(-jumpSpeed, 0, 180);
            }
        }
        if (actions[INPUT_RIGHT].isKeyClicked()) {
            if (actions[INPUT_UP].isKeyPressed()) {
                prepareDodgeJump(jumpSpeed, -jumpSpeed, 45);
            } else if (actions[INPUT_DOWN].isKeyPressed()) {
                prepareDodgeJump(jumpSpeed, jumpSpeed, 315);
            } else {
                prepareDodgeJump(jumpSpeed, 0, 0);
            }
        }
    }

    private void checkOneButtonDodge(int jumpSpeed) {
        if (actions[INPUT_UP].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                dodgeJump(-jumpSpeed, -jumpSpeed);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                dodgeJump(jumpSpeed, -jumpSpeed);
            } else {
                dodgeJump(0, -jumpSpeed);
            }
        } else if (actions[INPUT_DOWN].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                dodgeJump(-jumpSpeed, jumpSpeed);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                dodgeJump(jumpSpeed, jumpSpeed);
            } else {
                dodgeJump(0, jumpSpeed);
            }
        } else if (actions[INPUT_LEFT].isKeyPressed()) {
            dodgeJump(-jumpSpeed, 0);
        } else if (actions[INPUT_RIGHT].isKeyPressed()) {
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
        setInputBlocked(true, INPUT_ATTACK, INPUT_SECOND_ATTACK);
        inControl.setJumpForce(jumpMaker.getTotalTime() / 4);
        jumpLag = jumpMaker.getTotalTime() / 2;
        setInputLag(jumpLag);
    }

    private void updateRest() {
        if (actions[INPUT_CHANGE_WEAPON].isKeyClicked()) {
            if (((MyPlayer) inControl).changeWeapon()) {
                updateAttackTypes();
                gui.changeAttackIcon(firstAttackType, secondAttackType);
            }
        } else if (actions[INPUT_CHANGE_SET].isKeyClicked()) {
            if (((MyPlayer) inControl).hideWeapon()) {
                updateAttackTypes();

                gui.changeAttackIcon(firstAttackType, secondAttackType);
            }
        }
        if (actions[INPUT_SLOT_UP].isKeyClicked()) {
            ((MyPlayer) inControl).setActionPair(0);
            updateAttackTypes();
            gui.changeAttackIcon(firstAttackType, secondAttackType);
        } else if (actions[INPUT_SLOT_RIGHT].isKeyClicked()) {
            ((MyPlayer) inControl).setActionPair(1);
            updateAttackTypes();
            gui.changeAttackIcon(firstAttackType, secondAttackType);
        } else if (actions[INPUT_SLOT_DOWN].isKeyClicked()) {
            ((MyPlayer) inControl).setActionPair(2);
            updateAttackTypes();
            gui.changeAttackIcon(firstAttackType, secondAttackType);
        } else if (actions[INPUT_SLOT_LEFT].isKeyClicked()) {
            ((MyPlayer) inControl).setActionPair(3);
            updateAttackTypes();
            gui.changeAttackIcon(firstAttackType, secondAttackType);
        }
        if (!running) {
            inControl.setMaxSpeed(diagonal ? 1.5 : 2);
        } else {
            inControl.setMaxSpeed(diagonal ? 6 : 8);
        }
        if (actions[INPUT_LIGHT].isKeyClicked()) {
            inControl.setEmits(!inControl.isEmits());
        }
        if (actions[INPUT_ZOOM].isKeyClicked()) {
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
        actions[INPUT_MENU_BACK].updateActiveState();
        return actions[INPUT_MENU_BACK].isKeyClicked();
    }

    @Override
    public void getMenuInput() {
        boolean firstPlayer = !((MyPlayer) inControl).isNotFirst();
        for (int i = 0; i < MENU_ACTIONS_COUNT + 5; i++) {
            if (firstPlayer || isNotEqualToFirstPlayerMenuAction(actions[i].input)) {
                actions[i].updateActiveState();
            }
        }
        if (actions[INPUT_MENU_UP].isKeyClicked() || actions[INPUT_UP].isKeyClicked()) {
            ((Player) inControl).getMenu().setChosen(-1);
        } else if (actions[INPUT_MENU_DOWN].isKeyClicked() || actions[INPUT_DOWN].isKeyClicked()) {
            ((Player) inControl).getMenu().setChosen(1);
        }
        if (actions[INPUT_MENU_ACTION].isKeyClicked() || actions[INPUT_ACTION].isKeyClicked()) {
            ((Player) inControl).getMenu().choice(0);
        } else if (actions[INPUT_MENU_RIGHT].isKeyClicked() || actions[INPUT_RIGHT].isKeyClicked()) {
            ((Player) inControl).getMenu().choice(1);
        } else if (actions[INPUT_MENU_LEFT].isKeyClicked() || actions[INPUT_LEFT].isKeyClicked()) {
            ((Player) inControl).getMenu().choice(2);
        } else if (actions[INPUT_MENU_BACK].isKeyClicked()) {
            ((Player) inControl).getMenu().back();
        }
    }

    private boolean isNotEqualToFirstPlayerMenuAction(AnyInput input) {
        Action[] firstPlayerActions = Settings.players[0].getController().actions;
        for (int i = 0; i < MENU_ACTIONS_COUNT; i++) {
            if (firstPlayerActions[i].input.equals(input)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getActionsCount() {
        return ACTIONS_COUNT;
    }

    public int[] getAttackFrames() {
        return attackFrames;
    }

    private void setInputLag(int time) {
        if (!inputLag) {
            inputLag = true;
            lagDelay.startAt(time);
        }
    }

    private void stopInputLag() {
        inputLag = false;
    }

    public void setPlayersGUI(MyGUI gui) {
        this.gui = gui;
    }
}
