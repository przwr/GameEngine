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

    public AnyInput in;
    public Entity inControl;

    public Action(AnyInput in, Entity inControl) {
        this.in = in;
        this.inControl = inControl;
    }

    public abstract void Do();

    public abstract void Act();
}
