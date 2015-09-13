/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

/**
 * @author przemek
 */
public class InputExitMapping extends AnyInput {

    public InputExitMapping() {
    }

    @Override
    public boolean isPut() {
        return false;
    }

    @Override
    public String getLabel() {
        return "";
    }

}
