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

    protected boolean isOn;

    public AnyInput in;

    public Action(AnyInput in) {
        this.in = in;
    }

    public boolean isOn() {
        if (isOn) {
            isOn = !isOn;
            return !isOn;
        }
        return isOn;
    }

    public boolean isOnVal() {
        return isOn;
    }

    public abstract void Do();
}
