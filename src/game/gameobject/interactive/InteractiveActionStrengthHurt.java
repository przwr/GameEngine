/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive;

import game.gameobject.GameObject;
import net.jodk.lang.FastMath;

/**
 * @author przemek
 */
public class InteractiveActionStrengthHurt implements InteractiveAction {

    @Override
    public void act(GameObject object, Interactive activator, InteractiveResponse response) {
        recalculateData(activator, response);
        object.getStats().decreaseHealth(response);
    }

    protected void recalculateData(Interactive activator, InteractiveResponse response) {
        response.setPixels((1 + (response.getPixels() / (response.getMaxPixels() * 5f) + (float) FastMath.random() / 10f)) * activator.getModifier() *
                activator.getOwner().getStats().getStrength() * activator.getWeaponModifier());
    }
}
