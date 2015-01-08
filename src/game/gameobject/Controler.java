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

    public Action[] actions;
    public AnyInput[] inputs;
    protected final Entity inControl;
    protected boolean[] states;
    protected boolean[] statesSample;

    public Controler(Entity inControl) {
        this.inControl = inControl;
    }

    public abstract void getInput();

    public abstract boolean isMenuOn();

    public abstract void getMenuInput();

    public abstract int getActionsCount();

    public abstract void init();
}
