/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

/**
 * @author przemek
 */
public class ActionOnOff extends Action {

    public ActionOnOff(AnyInput input) {
        super(input);
    }

    @Override
    public void act() {
        if (input != null) {
            if (input.isPut()) {
                if (input.isNotPressed()) {
                    input.setPressed(true);
                    on = true;
                }
            } else {
                input.setPressed(false);
            }
        }
    }
}
