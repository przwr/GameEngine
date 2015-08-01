/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import game.gameobject.GameObject;

/**
 *
 * @author przemek
 */
public class InteractiveActionHurt implements InteractiveAction {

    @Override
    public void act(GameObject object) {
        System.out.println(object.getName() + ": Ałaaa, no weeeź...");
    }

}
