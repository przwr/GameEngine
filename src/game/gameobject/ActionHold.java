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
public class ActionHold extends Action {

    public ActionHold(AnyInput in) {
        super(in);
    }

    @Override
    public void act() {
        if (in != null && in.isPut()) {
            on = true;
        }
    }
}
