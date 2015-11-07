/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive.action;

import game.gameobject.GameObject;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.InteractiveResponse;
import game.gameobject.items.Arrow;
import game.place.Place;

/**
 * @author przemek
 */
public class InteractiveActionArrow implements InteractiveAction {

    @Override
    public void act(GameObject object, Interactive activator, InteractiveResponse response) {
        Arrow arrow = new Arrow(80, object.getDirection(), Place.tileSize, object);
        arrow.setPositionWithoutAreaUpdate(object.getX(), object.getY());
        object.getMap().addObject(arrow);
    }
}
