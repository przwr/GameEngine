/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive.action;

import game.gameobject.GameObject;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.InteractiveResponse;
import net.jodk.lang.FastMath;

/**
 * @author przemek
 */
public class InteractiveActionBowHurt implements InteractiveAction {

    @Override
    public void act(GameObject object, Interactive activator, InteractiveResponse response) {
        recalculateData(activator, response);
        object.getStats().decreaseHealth(response);
    }

    protected void recalculateData(Interactive activator, InteractiveResponse response) {
        float strengthModifier = activator.getOwner().getStats().getStrength() / 2f;
        float weaponModifier = activator.getWeaponModifier();
        strengthModifier = strengthModifier > weaponModifier ? weaponModifier : strengthModifier;

        response.setPixels((1 + (response.getPixels() / (response.getMaxPixels() * 5f) + (float) FastMath.random() / 10f)) * activator.getModifier() *
                (strengthModifier + weaponModifier));
    }
}