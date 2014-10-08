/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.myGame;

import game.gameobject.Player;
import game.gameobject.Controler;
import game.gameobject.Entity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 *
 * @author przemek
 */
public class MyKeyboard extends Controler {

    private boolean isPressed[] = new boolean[256];

    public MyKeyboard(Entity inControl) {
        super(inControl);
    }

    @Override
    protected void getInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            inControl.canMove(0, -1);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            inControl.canMove(0, 1);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            inControl.canMove(-1, 0);
            ((Player) inControl).getAnim().setFlip(0);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            inControl.canMove(1, 0);
            ((Player) inControl).getAnim().setFlip(1);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            ((Player) inControl).getPlace().shakeCam(((Player) inControl).getCam());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            inControl.setSpeed(16);
        } else {
            inControl.setSpeed(8);
        }

        {
            int key = (int) Keyboard.KEY_F;
            if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
                if (!isPressed[key]) {
                    ((Player) inControl).setEmits(!((Player) inControl).isEmits());
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
        if (Mouse.isButtonDown(0)) {
            ((Player) inControl).getPlace().shakeCam(((Player) inControl).getCam());
        }
    }

    @Override
    protected boolean isMenuOn() {
        int key = (int) Keyboard.KEY_ESCAPE;
        if (Keyboard.isKeyDown(key)) {
            if (!isPressed[key]) {
                isPressed[key] = true;
                return isPressed[key];
            }
        } else {
            isPressed[key] = false;
        }
        return false;
    }

    @Override
    protected void getMenuInput() {
        {
            int key = (int) Keyboard.KEY_UP;
            if (Keyboard.isKeyDown(key)) {

                if (!isPressed[key]) {
                    ((Player) inControl).menu.setChoosen(-1);
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
        {
            int key = (int) Keyboard.KEY_DOWN;
            if (Keyboard.isKeyDown(key)) {

                if (!isPressed[key]) {
                    ((Player) inControl).menu.setChoosen(1);
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
        {
            int key = (int) Keyboard.KEY_RETURN;
            if (Keyboard.isKeyDown(key)) {
                if (!isPressed[key]) {
                    ((Player) inControl).menu.choice();
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
        {
            int key = (int) Keyboard.KEY_ESCAPE;
            if (Keyboard.isKeyDown(key)) {
                if (!isPressed[key]) {
                    ((Player) inControl).menu.back();
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
    }
}
