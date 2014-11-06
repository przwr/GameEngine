/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

/**
 *
 * @author przemek
 */
public abstract class Controler {

    protected final Entity inControl;

    public AnyInput[] inputs;
    public Action[] actions;
    public int[] states;

    public Controler(Entity inControl) {
        this.inControl = inControl;
    }

    public abstract void getInput();

    public abstract boolean isMenuOn();

    public abstract void getMenuInput();

    public abstract int getActionsCount();

    public abstract void init();

    public abstract boolean isPressed(int i);

    public abstract boolean isClicked(int i);

    public abstract boolean isReleased(int i);

    public abstract boolean isMoving();
}
