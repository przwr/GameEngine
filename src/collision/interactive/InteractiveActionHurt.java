/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision.interactive;

import game.gameobject.GameObject;

/**
 * @author przemek
 */
public class InteractiveActionHurt implements InteractiveAction {

    @Override
    public void act(GameObject object, GameObject activator, int pixelsIn) {
        object.getStats().decreaseHealth((pixelsIn * activator.getStats().getStrength()) / 10);
        System.out.println(object.getName() + ": Aałła.. Zostało mi " + (100 * object.getStats().getHealth() / object.getStats().getMaxHealth()) + " % życia!");
    }
}
