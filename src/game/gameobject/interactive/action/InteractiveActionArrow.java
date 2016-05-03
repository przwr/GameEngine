/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive.action;

import game.gameobject.entities.Entity;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.InteractiveResponse;
import game.gameobject.items.Arrow;
import game.place.Place;

/**
 * @author przemek
 */
public class InteractiveActionArrow extends InteractiveAction {

    @Override
    public void act(Entity entity, Interactive activator, InteractiveResponse response, Object modifier) {
        Arrow arrow = new Arrow(modifier != null ? (double) modifier : 80, entity.getDirection(), Place.tileSize, entity);
        arrow.setPositionWithoutAreaUpdate(entity.getX(), entity.getY());
        entity.getMap().addObject(arrow);
    }
}
