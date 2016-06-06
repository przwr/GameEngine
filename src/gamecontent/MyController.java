/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Delay;
import engine.utilities.Methods;
import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.gameobject.inputs.Action;
import game.gameobject.inputs.AnyInput;
import game.gameobject.inputs.PlayerController;
import game.gameobject.stats.PlayerStats;
import game.gameobject.temporalmodifiers.Charger;
import game.gameobject.temporalmodifiers.SpeedChanger;
import game.place.Place;
import sounds.Sound;
import sprites.ClothedAppearance;

import java.util.ArrayList;

import static game.gameobject.entities.Entity.*;

/**
 * @author przemek
 */
public class MyController extends PlayerController {

    public static final byte INPUT_MENU_UP = 0, INPUT_MENU_DOWN = 1, INPUT_MENU_ACTION = 2, INPUT_MENU_BACK = 3, INPUT_MENU_LEFT = 4, INPUT_MENU_RIGHT = 5,
            INPUT_UP = 6, INPUT_DOWN = 7, INPUT_LEFT = 8, INPUT_RIGHT = 9, INPUT_ACTION = 10, INPUT_ATTACK = 11, INPUT_SECOND_ATTACK = 12, INPUT_BLOCK = 13,
            INPUT_RUN = 14, INPUT_REVERSE = 15, INPUT_DODGE = 16, INPUT_CHANGE_WEAPON = 17, INPUT_SNEAK = 18, INPUT_CHANGE_SET = 19,
            INPUT_SLOT_UP = 20, INPUT_SLOT_RIGHT = 21, INPUT_SLOT_DOWN = 22, INPUT_SLOT_LEFT = 23,
            INPUT_HANDY_MENU = 24, INPUT_ACTION_1 = 25, INPUT_ACTION_2 = 26, INPUT_ACTION_3 = 27, INPUT_ACTION_4 = 28;
    public static final byte MENU_ACTIONS_COUNT = 6, ACTIONS_COUNT = 29, ATTACK_COUNT = 5;
    public static final byte CAMSPEED_NORMAL = 3, CAMSPEED_SCOPING = 40;

    public static final byte ATTACK_SLASH = 0, ATTACK_THRUST = 1, ATTACK_UPPER_SLASH = 2,
            ATTACK_WEAK_PUNCH = 3, ATTACK_STRONG_PUNCH = 4, ATTACK_NORMAL_ARROW_SHOT = 5;
    private final Delay lagDelay;
    private final Delay sideDelay;
    private final Delay jumpDelay;
    private final Delay chargingDelay, attackDelay, preAttackDelay, afterAttackDelay;
    private final SpeedChanger jumpMaker;
    private final SpeedChanger attackMovement;
    private final Charger chargeValue;
    private final boolean[] blockedInputs;
    private final byte[] changedButtonState;
    float tmp = 3;
    Sound lastSound = null;
    private int tempDirection, sideDirection;
    private byte firstAttackType, secondAttackType, chargingType, lastAttackButton, lastAttackType;
    private boolean running, sneaking, diagonal, inputLag, charging, attacking, attacked, scoping;
    private ClothedAppearance animation;
    private PlayerStats stats;
    private MyGUI gui;
    private int jumpLag;
    private long lastEnergyUp = 0;

    public MyController(Player inControl, MyGUI playersGUI) {
        super(inControl);
        gui = playersGUI;
        inputs = new AnyInput[ACTIONS_COUNT];
        actions = new Action[ACTIONS_COUNT];
        blockedInputs = new boolean[ACTIONS_COUNT];
        attackDelay = Delay.createInMilliseconds(5, true);
        preAttackDelay = Delay.createInMilliseconds(40, true);
        afterAttackDelay = Delay.createInMilliseconds(160, true);
        lagDelay = Delay.createInMilliseconds(250, true);
        sideDelay = Delay.createInMilliseconds(25, true);
        jumpDelay = Delay.createInMilliseconds(400, true);
        chargingDelay = Delay.createInMilliseconds(300, true);
        chargingDelay.terminate();
        jumpMaker = new SpeedChanger(8);
        jumpMaker.setType(SpeedChanger.DECREASING);
        attackMovement = new SpeedChanger(4);
        attackMovement.setType(SpeedChanger.DECREASING);
        chargeValue = new Charger();
        lastAttackButton = INPUT_ATTACK;
        changedButtonState = new byte[2];
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
            animation = (ClothedAppearance) inControl.getAppearance();
            stats = (PlayerStats) inControl.getStats();
            animation.setAnimate(true);
            diagonal = true;
            tempDirection = inControl.getDirection8Way();
            if ((running && stats.getEnergy() > 0) || stats.getEnergy() >= 30) {
                running = actions[INPUT_RUN].isKeyPressed();
            } else {
                running = false;
            }
            if (running) {
                sneaking = false;
            } else {
                if (actions[INPUT_SNEAK].isKeyClicked()) {
                    sneaking = !sneaking;
                }
            }
            updateEnergy();
            handyMenu();
            if (!inControl.isHurt() || stats.isProtectionState()) {
                stats.setProtectionState(false);
                if (inControl.isAbleToMove()) {
                    if (!charging && chargingDelay.isOver()) {
                        if (jumpLag == 0) {
                            updateAttackTypes();
                            if (attacking
                                    || (firstAttackType >= 0 && actions[INPUT_ATTACK].isKeyPressed())
                                    || (secondAttackType >= 0 && actions[INPUT_SECOND_ATTACK].isKeyPressed())) {
                                updateAttack();
                            } else if (actions[INPUT_BLOCK].isKeyPressed()) {
                                updateBlock();
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
                    animation.animateSingleInDirection(tempDirection, animation.IDLE, 0);
                    inControl.brake(2);
                }
            } else {
                updateGettingHurt();
            }
            if (attacking) {
                if (!charging) {
                    animation.setFPS(60);
                } else {
                    animation.getLowerBody().setFPS((int) (inControl.getSpeed() * 3));
                }
            } else {
                if (!running) {
                    animation.setFPS((int) (inControl.getSpeed() * 3));
                } else {
                    animation.setFPS((int) (inControl.getSpeed() * 3.5));
                }
            }
        }
    }


    private void updateEnergy() {
        float energyGain = 0.015f;
        if (lastEnergyUp > 0) {
            long current = Place.getDayCycle().getCurrentTimeInMiliSeconds();
            long difference = current - lastEnergyUp;
            if (running && inControl.getSpeed() > 0 && inControl.isAbleToMove()) {
                float decrease = 0.7f + energyGain * difference;
                if (decrease < 0) {
                    decrease = 0;
                }
                stats.decreaseEnergy(decrease);
            } else if (!stats.isProtectionState() && !scoping) {
                stats.increaseEnergy(energyGain * difference);
            }
            lastEnergyUp = current;
        } else {
            lastEnergyUp = Place.getDayCycle().getCurrentTimeInMiliSeconds();
        }
    }

    private void handyMenu() {
        if (actions[INPUT_HANDY_MENU].isKeyClicked()) {
            if (inControl.usesHandyMenu()) {
                inControl.setUsesHandyMenu(false);
                inControl.setAbleToMove(true);
                inControl.turnOffLoot();
            } else {
                inControl.setUsesHandyMenu(true);
                ((MyPlayer) inControl).getGUI().resetHelpDelay();
                inControl.setAbleToMove(false);
                inControl.setBackpackOn(true);
                inControl.setGearOn(true);
                inControl.turnOffLoot();
            }
        }
        if (inControl.usesHandyMenu()) {
            if (actions[INPUT_DOWN].isKeyClicked()) {
                inControl.setMenuKey(MyGUI.DOWN);
            } else if (actions[INPUT_UP].isKeyClicked()) {
                inControl.setMenuKey(MyGUI.UP);
            } else if (actions[INPUT_RIGHT].isKeyClicked()) {
                inControl.setMenuKey(MyGUI.RIGHT);
            } else if (actions[INPUT_LEFT].isKeyClicked()) {
                inControl.setMenuKey(MyGUI.LEFT);
            } else if (actions[INPUT_ACTION].isKeyClicked()) {
                inControl.setMenuKey(MyGUI.USE);
            }
        }
    }

    private void updateBlock() {
        if (actions[INPUT_BLOCK].isKeyClicked()) {
            //PERFEKCYJNY BLOK (pierwsza klatka obrony)
        }
        animation.getUpperBody().animateSingleInDirection(tempDirection, animation.SHIELD);
        stats.setProtectionState(true);
        updateChargingMovement();
        updateDirectionWithSlotInput();
        //RESZTA BLOKOWANIA
    }

    public void stopAttack() {
        charging = false;
        if (scoping) {
            inControl.getCamera().clearLookingPoint();
            scoping = false;
        }
        attacking = false;
    }

    private void updateGettingHurt() {
        inControl.setDirection8way(Methods.pointAngle8Directions(inControl.getKnockBack().getXSpeed(),
                inControl.getKnockBack().getYSpeed(), 0, 0));
        stopAttack();
        actions[lastAttackButton].setInterrupted();
        animation.animateSingleInDirection(inControl.getDirection8Way(), animation.IDLE, 6);
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
            updateInputNuances();
        }
    }

    private void updateInputAxisChanges(byte inputA, byte inputB, int type) {
        if (actions[inputA].isKeyPressed() && actions[inputB].isKeyPressed()) {
            if (changedButtonState[type] == 0) {
                if (actions[inputA].isKeyClicked()) {
                    changedButtonState[type] = 1;
                } else if (actions[inputB].isKeyClicked()) {
                    changedButtonState[type] = 2;
                }
            } else {
                if (changedButtonState[type] == 1) {
                    actions[inputB].setState(KEY_NO_INPUT);
                } else {
                    actions[inputA].setState(KEY_NO_INPUT);
                }
            }
        } else {
            changedButtonState[type] = 0;
        }
    }

    private void updateInputNuances() {
        updateInputAxisChanges(INPUT_LEFT, INPUT_RIGHT, 0);
        updateInputAxisChanges(INPUT_UP, INPUT_DOWN, 1);
    }

    private void updateAttackTypes() {
        firstAttackType = ((MyPlayer) inControl).getFirstAttackType();
        secondAttackType = ((MyPlayer) inControl).getSecondAttackType();
    }

    private void updateAttack() {
        if (!attacking) {
            attacked = false;
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
        } else if (attacked) {
            if (preAttackDelay.isWorking()) {
                int tmpDir = tempDirection;
                if (!actions[INPUT_BLOCK].isKeyPressed()) {
                    updateDirection();
                }
                if (tmpDir != tempDirection) {
                    animation.changeDirection(tempDirection);
                }
            }
            if (preAttackDelay.isOver()) {
                attackDelay.start();
                preAttackDelay.stop();
            }
            if (attackDelay.isWorking()) {
                inControl.getAttackActivator(lastAttackType).setActivated(true);
            }
            if (attackDelay.isOver()) {
                afterAttackDelay.start();
                attackDelay.stop();
            }
            if (afterAttackDelay.isOver()) {
                actions[lastAttackButton].setInterrupted();
                stopAttack();
                afterAttackDelay.stop();
            }
            inControl.brakeWithModifier(2, 2);
        }
    }

    private void startAttack(byte attack) {
        switch (attack) {
            case ATTACK_SLASH:
                if (stats.getEnergy() >= 14) {
                    animation.animateIntervalInDirectionOnce(tempDirection, animation.SWORD, 2, 6);
                    stats.decreaseEnergy(14);
                    attacked = true;
                } else {
                    ((MyPlayer) inControl).getGUI().activateLowEnergy(20);
                }
                break;
            case ATTACK_THRUST:
                if (stats.getEnergy() >= 19) {
                    animation.animateIntervalInDirectionOnce(tempDirection, animation.SWORD, 7, 9);
                    stats.decreaseEnergy(19);
                    attacked = true;
                } else {
                    ((MyPlayer) inControl).getGUI().activateLowEnergy(24);
                }
                break;
            case ATTACK_UPPER_SLASH:
                if (stats.getEnergy() >= 15) {
                    animation.animateIntervalInDirectionOnce(tempDirection, animation.SWORD, 10, 17);
                    stats.decreaseEnergy(15);
                    attacked = true;
                } else {
                    ((MyPlayer) inControl).getGUI().activateLowEnergy(20);
                }
                break;
            case ATTACK_WEAK_PUNCH:
                if (stats.getEnergy() >= 5) {
                    animation.animateIntervalInDirectionOnce(tempDirection, animation.FISTS, 0, 2);
                    stats.decreaseEnergy(5);
                    attacked = true;

                } else {
                    ((MyPlayer) inControl).getGUI().activateLowEnergy(5);
                }
                break;
            case ATTACK_STRONG_PUNCH:
                if (stats.getEnergy() >= 7) {
                    animation.animateIntervalInDirectionOnce(tempDirection, animation.FISTS, 3, 4);
                    stats.decreaseEnergy(7);
                    attacked = true;
                } else {
                    ((MyPlayer) inControl).getGUI().activateLowEnergy(7);
                }
                break;
            case ATTACK_NORMAL_ARROW_SHOT:
                if (stats.getEnergy() >= 15) {
                    animation.animateIntervalInDirectionOnce(tempDirection, animation.BOW, 0, 2);
                    animation.getUpperBody().setFPS(3);
                    chargeValue.setType(60, 100, Charger.INCREASING, 4);
                    chargeValue.start();
                    inControl.addChanger(chargeValue);
                    attackMovement.stop();
                    stopInputLag();
                    charging = true;
                    chargingType = ATTACK_NORMAL_ARROW_SHOT;
                    attacked = true;
                    scoping = true;
                } else {
                    ((MyPlayer) inControl).getGUI().activateLowEnergy(15);
                }
                break;
        }
        if (scoping) {
            inControl.getCamera().setLookingPoint(
                    (int) Methods.xRadius8Directions(tempDirection, 5 * Place.tileSize),
                    -(int) Methods.yRadius8Directions(tempDirection, 5 * Place.tileSize));
        }
        if (attacked) {
            preAttackDelay.setFrameLengthInMilliseconds(1);
            attackDelay.setFrameLengthInMilliseconds(40);
            afterAttackDelay.setFrameLengthInMilliseconds(160);
            lastAttackType = attack;
            attackMovement.setSpeedInDirection(tempDirection * 45, 10);
            attackMovement.start();
            attacking = true;
            inControl.addChanger(attackMovement);
            preAttackDelay.start();
        }
    }

    private void updateCharging() {
        if (charging && actions[lastAttackButton].isKeyReleased()) {
            switch (chargingType) {
                case ATTACK_NORMAL_ARROW_SHOT:
                    if (stats.getEnergy() >= 15) {
                        animation.animateSingleInDirection(tempDirection, animation.BOW, 3);
                        inControl.getAttackActivator(ATTACK_NORMAL_ARROW_SHOT, chargeValue.getChargedValue() + 20).setActivated(true);
                        stats.decreaseEnergy(15);
                    } else {
                        ((MyPlayer) inControl).getGUI().activateLowEnergy(15);
                    }
                    break;
            }
            chargingDelay.start();
            stopAttack();
        }
        if (chargingDelay.isOver()) {
            updateChargingMovement();
            if (updateDirectionWithSlotInput()) {
                animation.getUpperBody().changeDirection(tempDirection);
                inControl.getCamera().setLookingPoint(
                        (int) Methods.xRadius8Directions(tempDirection, 5 * Place.tileSize),
                        -(int) Methods.yRadius8Directions(tempDirection, 5 * Place.tileSize));
            }
        } else {
            if (!charging) {
                actions[lastAttackButton].setInterrupted();
            }
            inControl.brake(2);
        }
    }

    private void updateChargingMovement() {
        running = false;
        if (actions[INPUT_UP].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                inControl.addSpeed(-4, -4);
                animation.getLowerBody().animateIntervalInDirection(tempDirection, 1, 6);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.addSpeed(4, -4);
                animation.getLowerBody().animateIntervalInDirection(tempDirection, 1, 6);
            } else {
                inControl.addSpeed(0, -4);
                animation.getLowerBody().animateIntervalInDirection(tempDirection, 1, 6);
            }
        } else if (actions[INPUT_DOWN].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                inControl.addSpeed(-4, 4);
                animation.getLowerBody().animateIntervalInDirection(tempDirection, 1, 6);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.addSpeed(4, 4);
                animation.getLowerBody().animateIntervalInDirection(tempDirection, 1, 6);
            } else {
                inControl.addSpeed(0, 4);
                animation.getLowerBody().animateIntervalInDirection(tempDirection, 1, 6);
            }
        } else {
            if (actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.addSpeed(4, 0);
                animation.getLowerBody().animateIntervalInDirection(tempDirection, 1, 6);
            } else if (actions[INPUT_LEFT].isKeyPressed()) {
                inControl.addSpeed(-4, 0);
                animation.getLowerBody().animateIntervalInDirection(tempDirection, 1, 6);
            } else {
                animation.getLowerBody().animateSingleInDirection(tempDirection, animation.BOW + 2);
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

    private boolean updateDirectionWithSlotInput() {
        if (actions[INPUT_SLOT_UP].isKeyPressed()) {
            if (actions[INPUT_SLOT_LEFT].isKeyPressed()) {
                inControl.setDirection8way(3);
            } else if (actions[INPUT_SLOT_RIGHT].isKeyPressed()) {
                inControl.setDirection8way(1);
            } else {
                if (actions[INPUT_SLOT_LEFT].isKeyReleased()) {
                    sideDirection = 3;
                    sideDelay.start();
                }
                if (actions[INPUT_SLOT_RIGHT].isKeyReleased()) {
                    sideDirection = 1;
                    sideDelay.start();
                }
                inControl.setDirection8way(2);
            }
        } else if (actions[INPUT_SLOT_DOWN].isKeyPressed()) {
            if (actions[INPUT_SLOT_LEFT].isKeyPressed()) {
                inControl.setDirection8way(5);
            } else if (actions[INPUT_SLOT_RIGHT].isKeyPressed()) {
                inControl.setDirection8way(7);
            } else {
                if (actions[INPUT_SLOT_LEFT].isKeyReleased()) {
                    sideDirection = 5;
                    sideDelay.start();
                }
                if (actions[INPUT_SLOT_RIGHT].isKeyReleased()) {
                    sideDirection = 7;
                    sideDelay.start();
                }
                inControl.setDirection8way(6);
            }
        } else {
            if (actions[INPUT_SLOT_RIGHT].isKeyPressed()) {
                if (actions[INPUT_SLOT_UP].isKeyReleased()) {
                    sideDirection = 1;
                    sideDelay.start();
                }
                if (actions[INPUT_SLOT_DOWN].isKeyReleased()) {
                    sideDirection = 7;
                    sideDelay.start();
                }
                inControl.setDirection8way(0);
            } else if (actions[INPUT_SLOT_LEFT].isKeyPressed()) {
                if (actions[INPUT_SLOT_UP].isKeyReleased()) {
                    sideDirection = 3;
                    sideDelay.start();
                }
                if (actions[INPUT_SLOT_DOWN].isKeyReleased()) {
                    sideDirection = 5;
                    sideDelay.start();
                }
                inControl.setDirection8way(4);
            }
        }
        if (sideDelay.isActive()) {
            inControl.setDirection8way(sideDirection);
        }
        if (sideDelay.isOver()) {
            if (!actions[INPUT_SLOT_UP].isKeyPressed() && !actions[INPUT_SLOT_DOWN].isKeyPressed()
                    && !actions[INPUT_SLOT_LEFT].isKeyPressed() && !actions[INPUT_SLOT_RIGHT].isKeyPressed()) {
                inControl.setDirection8way(sideDirection);
            }
            sideDelay.stop();
        }
        if (tempDirection != inControl.getDirection8Way()) {
            tempDirection = inControl.getDirection8Way();
            return true;
        }
        return false;
    }

    private void updateDirection() {
        if (actions[INPUT_UP].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                inControl.setDirection8way(3);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.setDirection8way(1);
            } else {
                inControl.setDirection8way(2);
            }
        } else if (actions[INPUT_DOWN].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                inControl.setDirection8way(5);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.setDirection8way(7);
            } else {
                inControl.setDirection8way(6);
            }
        } else {
            if (actions[INPUT_RIGHT].isKeyPressed()) {
                inControl.setDirection8way(0);
            } else if (actions[INPUT_LEFT].isKeyPressed()) {
                inControl.setDirection8way(4);
            }
        }
        tempDirection = inControl.getDirection8Way();
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
            if (!sneaking) {
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
            if (!sneaking) {
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
                if (!sneaking) {
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
                if (!sneaking) {
                    inControl.setMakeNoise(true);
                }
            } else {
                if (!sideDelay.isActive()) {
                    inControl.setMakeNoise(false);
                    animation.animateSingleInDirection(tempDirection, animation.IDLE, 0);
                } else {
                    animation.animateSingleInDirection(sideDirection, animation.IDLE, 0);
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
                if (actions[INPUT_DODGE].isKeyPressed()) {
                    if (stats.getEnergy() >= 25) {
                        checkOneButtonDodge(jumpSpeed);
                    } else {
                        ((MyPlayer) inControl).getGUI().activateLowEnergy(24);
                    }
                }
                /* else {
                 checkDoubleClickDodge(jumpSpeed);
                 }*/
            } else {
                jumpLag--;
                animation.animateSingleInDirection(tempDirection, animation.ACROBATICS, 3);
                if (jumpLag == 0) {
                    setInputBlocked(false, INPUT_ATTACK, INPUT_SECOND_ATTACK);
                }
                inControl.setMakeNoise(true);
            }
        } else {
            animation.animateSingleInDirection(tempDirection, animation.ACROBATICS, 1);
        }
        if (jumpDelay.isActive() && jumpDelay.isOver()) {
            jumpDelay.stop();
        }
    }

    private void checkOneButtonDodge(int jumpSpeed) {
        int scaledValue = Methods.roundDouble(jumpSpeed * Methods.ONE_BY_SQRT_ROOT_OF_2);
        if (actions[INPUT_UP].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                dodgeJump(-scaledValue, -scaledValue);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                dodgeJump(scaledValue, -scaledValue);
            } else {
                dodgeJump(0, -jumpSpeed);
            }
        } else if (actions[INPUT_DOWN].isKeyPressed()) {
            if (actions[INPUT_LEFT].isKeyPressed()) {
                dodgeJump(-scaledValue, scaledValue);
            } else if (actions[INPUT_RIGHT].isKeyPressed()) {
                dodgeJump(scaledValue, scaledValue);
            } else {
                dodgeJump(0, jumpSpeed);
            }
        } else if (actions[INPUT_LEFT].isKeyPressed()) {
            dodgeJump(-jumpSpeed, 0);
        } else if (actions[INPUT_RIGHT].isKeyPressed()) {
            dodgeJump(jumpSpeed, 0);
        } else {
            switch (inControl.getDirection8Way()) {
                case RIGHT:
                    dodgeJump(jumpSpeed, 0);
                    break;
                case UP:
                    dodgeJump(0, -jumpSpeed);
                    break;
                case LEFT:
                    dodgeJump(-jumpSpeed, 0);
                    break;
                case DOWN:
                    dodgeJump(0, jumpSpeed);
                    break;
                case UP_RIGHT:
                    dodgeJump(scaledValue, -scaledValue);
                    break;
                case UP_LEFT:
                    dodgeJump(-scaledValue, -scaledValue);
                    break;
                case DOWN_LEFT:
                    dodgeJump(-scaledValue, scaledValue);
                    break;
                case DOWN_RIGHT:
                    dodgeJump(scaledValue, scaledValue);
                    break;
            }
        }
    }

    private void dodgeJump(int xSpeed, int ySpeed) {
        if (xSpeed == 0 || ySpeed == 0) {
            jumpMaker.setSpeed(xSpeed, ySpeed);
        } else {
            jumpMaker.setSpeed((int) (xSpeed * 0.7), (int) (ySpeed * 0.7));
        }
        stats.decreaseEnergy(25);
        jumpMaker.start();
        inControl.addChanger(jumpMaker);
        jumpDelay.stop();
        setInputBlocked(true, INPUT_ATTACK, INPUT_SECOND_ATTACK);
        inControl.setUpForce(jumpMaker.getTotalTime() / 4);
        jumpLag = jumpMaker.getTotalTime() / 2;
        setInputLag(jumpLag);
        animation.setFPS(60);
    }

    private void updateRest() {
//        if (actions[INPUT_CHANGE_WEAPON].isKeyPressed()) {
//            inControl.setFloatHeight(inControl.getFloatHeight() - 1);
//        }
//        if (actions[INPUT_ACTION_3].isKeyPressed()) {
//            inControl.setFloatHeight(inControl.getFloatHeight() + 1);
//        }
        if (actions[INPUT_CHANGE_WEAPON].isKeyClicked()) {
            //((MyPlayer) inControl).randomizeClothes();
            if (((MyPlayer) inControl).changeWeapon()) {
                updateAttackTypes();
            }
            gui.changeAttackIcon(firstAttackType, secondAttackType);
        } else if (actions[INPUT_CHANGE_SET].isKeyClicked()) {
            if (((MyPlayer) inControl).hideWeapon()) {
                updateAttackTypes();
            }
            gui.changeAttackIcon(firstAttackType, secondAttackType);
        }
        if (!charging && !actions[INPUT_BLOCK].isKeyPressed()) {
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
        }
        if (!running) {
            if (sneaking || charging || actions[INPUT_BLOCK].isKeyPressed()) {
                inControl.setMaxSpeed(getNumberSquared(3, diagonal));
            } else {
                inControl.setMaxSpeed(getNumberSquared(7, diagonal));
            }
        } else {
            inControl.setMaxSpeed(getNumberSquared(10, diagonal));
        }
        if (actions[INPUT_ACTION].isKeyClicked()) {
            ArrayList<GameObject> interacting = inControl.getInteractingObjects();
            if (interacting != null && !interacting.isEmpty()) {
                //TODO LISTA!!
                interacting.get(0).interact(inControl);
            }
        }
        if (actions[INPUT_ACTION_3].isKeyClicked()) {
            inControl.setEmits(!inControl.isEmits());
        }
        if (actions[INPUT_ACTION_4].isKeyClicked()) {
            if (inControl instanceof Player) {
                inControl.getCamera().switchZoom();
            }
        }
    }

    private double getNumberSquared(double num, boolean diagonal) {
        return diagonal ? num * Methods.ONE_BY_SQRT_ROOT_OF_2 : num;
    }

    private void animateMoving(int direction) {
        if (sneaking) {
            animation.animateIntervalInDirection(direction, animation.IDLE, 1, 6);
        } else {
            animation.animateIntervalInDirection(direction, animation.RUN, 0, 11);
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
        boolean firstPlayer = !inControl.isNotFirst();
        for (int i = 0; i < MENU_ACTIONS_COUNT + 5; i++) {
            if (firstPlayer || isNotEqualToFirstPlayerMenuAction(actions[i].input)) {
                actions[i].updateActiveState();
            }
        }
        if (actions[INPUT_MENU_UP].isKeyClicked() || actions[INPUT_UP].isKeyClicked()) {
            inControl.getMenu().setChosen(-1);
        } else if (actions[INPUT_MENU_DOWN].isKeyClicked() || actions[INPUT_DOWN].isKeyClicked()) {
            inControl.getMenu().setChosen(1);
        }
        if (actions[INPUT_MENU_ACTION].isKeyClicked() || actions[INPUT_ACTION].isKeyClicked()) {
            inControl.getMenu().choice(0);
        } else if (actions[INPUT_MENU_RIGHT].isKeyClicked() || actions[INPUT_RIGHT].isKeyClicked()) {
            inControl.getMenu().choice(1);
        } else if (actions[INPUT_MENU_LEFT].isKeyClicked() || actions[INPUT_LEFT].isKeyClicked()) {
            inControl.getMenu().choice(2);
        }
    }

    private boolean isNotEqualToFirstPlayerMenuAction(AnyInput input) {
        Action[] firstPlayerActions = Settings.players[0].getController().actions;
        for (int i = 0; i < MENU_ACTIONS_COUNT; i++) {
            if (firstPlayerActions[i].input != null && firstPlayerActions[i].input.equals(input)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getActionsCount() {
        return ACTIONS_COUNT;
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

    public boolean isRunning() {
        return running;
    }

    public boolean isScoping() {
        return scoping;
    }
}
