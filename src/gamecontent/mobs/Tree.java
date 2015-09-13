/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import game.gameobject.entities.Mob;
import game.place.Place;

/**
 * @author przemek
 */
public class Tree extends Mob {

    public Tree(int x, int y, int width, int height, double speed, int range, String name, Place place, boolean solid, short ID) {
        super(x, y, speed, range, name, place, "bigTree", solid, ID);
        setCollision(Rectangle.create(width, height, OpticProperties.FULL_SHADOW, this));
        setSimpleLighting(false);
        collision.setSmall(true);
    }

    @Override
    public void update() {
    }
}
