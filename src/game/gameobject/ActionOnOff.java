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
public class ActionOnOff extends AbstractAction {

    public ActionOnOff(AbstractAnyInput in) {
        super(in);
    }

    @Override
    public void act() {
        if (in != null) {
            if (in.isPut()) {
                if (!in.isPressed()) {
                    in.setPressed(true);
                    on = true;
                }
            } else {
                in.setPressed(false);
            }
        }
    }
}
