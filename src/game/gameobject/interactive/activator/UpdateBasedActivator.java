/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive.activator;

import game.gameobject.interactive.activator.InteractiveActivator;
import game.gameobject.GameObject;

/**
 *
 * @author Wojtek
 */
public class UpdateBasedActivator implements InteractiveActivator {
    boolean activated;
    
    @Override
    public void setActivated(boolean active) {
        this.activated = active;
    }
    
    @Override
    public boolean checkActivation(GameObject owner) {
        return activated;
    }
    
}
