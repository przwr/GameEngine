/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive.action;

import game.gameobject.entities.Entity;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.InteractiveResponse;
import game.gameobject.items.Weapon;
import net.jodk.lang.FastMath;

/**
 * @author przemek
 */
public class InteractiveActionBowHurt extends InteractiveAction {

    @Override
    public void act(Entity object, Interactive activator, InteractiveResponse response, Object modifier) {
        recalculateData(activator, response);
        object.getStats().decreaseHealth(response);
    }

    protected void recalculateData(Interactive activator, InteractiveResponse response) {
        float strengthModifier = activator.getOwner().getStats().getStrength();
        Weapon weapon = activator.getOwnersWeapon();

        response.setPixels((1 + (response.getPixels() / (response.getMaxPixels() * 5f) + (float) FastMath.random() / 10f)) * activator.getStrenght() *
                (strengthModifier + weapon.getModifier()));
        response.setKnockBack(activator.getKnockback() + weapon.getKnockback());
    }
}
