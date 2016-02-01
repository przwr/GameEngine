/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive.action;

import game.gameobject.GameObject;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.InteractiveResponse;

/**
 * @author przemek
 */
public abstract class InteractiveAction {

    public void act(GameObject object, Interactive activator, InteractiveResponse response) {
        act(object, activator, response, null);
    }
    
    abstract public void act(GameObject object, Interactive activator, InteractiveResponse response, Object modifier);
}
