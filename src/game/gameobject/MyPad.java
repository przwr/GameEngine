/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import org.lwjgl.input.Controllers;

/**
 *
 * @author przemek
 */
public class MyPad extends Controler {

    private final int padNr;
    private boolean isPressed[] = new boolean[24];

    public MyPad(Entity inControl, int padNr) {
        super(inControl);
        this.padNr = padNr;
    }

    //if (Controllers.getControllerCount() > 0) !!!                <-----
    @Override
    protected void getInput() {

        if (Controllers.getController(2 * padNr).getXAxisValue() != 0.0) {
            int xPad = Controllers.getController(2 * padNr).getXAxisValue() > 0.1 ? 1 : Controllers.getController(2 * padNr).getXAxisValue() < -0.1 ? -1 : 0;
            if (xPad == 1) {
                inControl.canMove(xPad, 0);
                ((Player) inControl).anim.setFlip(1);
            } else if (xPad == -1) {
                inControl.canMove(xPad, 0);
                ((Player) inControl).anim.setFlip(0);
            }
        }
        if (Controllers.getController(2 * padNr).getYAxisValue() != 0.0) {
            int yPad = Controllers.getController(2 * padNr).getYAxisValue() > 0.1 ? 1 : Controllers.getController(2 * padNr).getYAxisValue() < -0.1 ? -1 : 0;
            if (yPad != 0) {
                inControl.canMove(0, yPad);
            }
        }
        if (Controllers.getController(2 * padNr + 1).isButtonPressed(1)) {
            inControl.speed = 16;
        } else {
            inControl.speed = 8;
        }
        if (Controllers.getController(2 * padNr + 1).isButtonPressed(8)) {
            if (!((Player) inControl).pressed_Light) {
                ((Player) inControl).emits = !((Player) inControl).emits;
                ((Player) inControl).pressed_Light = true;
            }
        } else {
            ((Player) inControl).pressed_Light = false;
        }
        if (Controllers.getController(2 * padNr + 1).isButtonPressed(5)) {
            ((Player) inControl).place.shakeCam(((Player) inControl).getCam());
        }
    }

    @Override
    protected boolean isMenuOn() {
        int key = 9;
        if (Controllers.getController(2 * padNr + 1).isButtonPressed(key)) {
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
            int key = 16; //left Stick up
            if (Controllers.getController(2 * padNr + 1).getYAxisValue() < 0.0) {
                if (!isPressed[key]) {
                    ((Player) inControl).menu.setChoosen(-1);
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
        {
            int key = 17; //left Stick down
            if (Controllers.getController(2 * padNr + 1).getYAxisValue() > 0.0) {
                if (!isPressed[key]) {
                    ((Player) inControl).menu.setChoosen(1);
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
        {
            int key = 12; //pov down
            if (Controllers.getController(2 * padNr + 1).getPovY() < 0.0) {
                if (!isPressed[key]) {
                    ((Player) inControl).menu.setChoosen(-1);
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
        {
            int key = 13; //pov up
            if (Controllers.getController(2 * padNr + 1).getPovY() > 0.0) {
                if (!isPressed[key]) {
                    ((Player) inControl).menu.setChoosen(1);
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
        {
            int key = 2;
            if (Controllers.getController(2 * padNr + 1).isButtonPressed(key)) {
                if (!isPressed[key]) {
                    ((Player) inControl).menu.choice();
                    isPressed[key] = true;
                }
            } else {
                isPressed[key] = false;
            }
        }
    }
}
