/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Methods;
import game.gameobject.Mob;
import game.place.Place;

/**
 *
 * @author przemek
 */
public class Rabbit extends Mob {

    public Rabbit(int x, int y, int width, int height, double speed, int range, String name, Place place, boolean solid, short ID) {
        super(x, y, speed, range, name, place, "rabbit", solid);
        setCollision(Rectangle.create(width, height, OpticProperties.NO_SHADOW, this));
        this.mobID = ID;
    }

    @Override
    public void update() {
        if (prey != null && ((MyPlayer) prey).isInGame()) {
            chase(prey);
            if (Methods.pointDistance(getX(), getY(), prey.getX(), prey.getY()) > range * 1.5 || prey.getMap() != map) {
                prey = null;
            }
        } else {
            look(place.players);
            brake(2);
        }
        moveIfPossible((int) (xEnvironmentalSpeed + xSpeed), (int) (yEnvironmentalSpeed + ySpeed));
        brakeOthers();
    }
}
