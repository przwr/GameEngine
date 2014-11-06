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
public class ActionWhileClicked extends Action {

    private final int[] states;
    private final int i;

    public ActionWhileClicked(AnyInput in, Entity inControl, int[] states, int i) {
        super(in, inControl);
        this.states = states;
        this.i = i;
    }

    @Override
    public void Do() {
        if (in != null && in.isPut()) {
            if (states[i] <= 0) {
                states[i] = 2;
            } else {
                states[i] = 1;
            }
        } else {
            if (states[i] >= 0) {
                states[i] = -1;
            } else {
                states[i] = 0;
            }
        }
        /*if (in != null && in.isPut()) {
         Act();
         } else {
         noAct();
         }*/
    }

    @Override
    public void Act() {
    }
;/*
    
 public abstract void noAct();*/


}
