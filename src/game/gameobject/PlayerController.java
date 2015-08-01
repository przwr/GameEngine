/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

/**
 * @author przemek
 */
public abstract class PlayerController {

    public static final byte KEY_PRESSED = 1;
    private static final byte KEY_NO_INPUT = 0;
    private static final byte KEY_CLICKED = 2;
    private static final byte KEY_RELEASED = -1;
    protected final Entity inControl;
    public Action[] actions;
    public AnyInput[] inputs;
    protected byte[] states;

    protected PlayerController(Entity inControl) {
        this.inControl = inControl;
    }

    public void showInput() {
        for (byte b : states)
            System.out.print(b + " ");
        System.out.println("");
    }

    public boolean isKeyReleased(int key) {
        return states[key] == KEY_RELEASED;
    }

    public boolean isKeyClicked(int key) {
        return states[key] == KEY_CLICKED;
    }

    public boolean isKeyPressed(int key) {
        return states[key] > KEY_NO_INPUT;
    }

    public abstract void getInput();

    public abstract boolean isMenuOn();

    public abstract void getMenuInput();

    public abstract int getActionsCount();

    public abstract void initialize();
}
