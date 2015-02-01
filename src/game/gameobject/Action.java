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
public abstract class Action {

    protected boolean on;

    public AnyInput input;

    public Action(AnyInput in) {
        this.input = in;
    }

    public boolean isOn() {
        if (on) {
            on = false;
            return true;
        }
        return false;
    }

    public abstract void act();
}
