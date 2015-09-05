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
    public static final byte KEY_NO_INPUT = 0;
    public static final byte KEY_CLICKED = 2;
    public static final byte KEY_RELEASED = -1;
    protected final Entity inControl;
    public Action[] actions;
    public AnyInput[] inputs;

    protected PlayerController(Entity inControl) {
        this.inControl = inControl;
    }

    public Action getAction(int i) {
        return actions[i];
    }

    public abstract void getInput();

    public abstract boolean isMenuOn();

    public abstract void getMenuInput();

    public abstract int getActionsCount();

    public abstract void initialize();
}
