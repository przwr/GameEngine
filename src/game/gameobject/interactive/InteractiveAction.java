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
public interface InteractiveAction {

    void act(GameObject object, Interactive activator, InteractiveResponse response);
}
