/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.inputs;

import game.gameobject.AnyInput;

/**
 * @author przemek
 */
public class InputNull extends AnyInput {

    public InputNull() {
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
