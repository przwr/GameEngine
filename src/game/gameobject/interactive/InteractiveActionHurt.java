/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive;

import game.gameobject.GameObject;

/**
 * @author przemek
 */
public class InteractiveActionHurt implements InteractiveAction {

    @Override
    public void act(GameObject object, Interactive activator, InteractiveResponse response) {
        activator.recalculateData(response);
        object.getStats().decreaseHealth(response);
    }
}
