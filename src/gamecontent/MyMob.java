/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.Methods;
import game.gameobject.Mob;
import game.place.Place;

/**
 *
 * @author przemek
 */
public class MyMob extends Mob {

    public MyMob(int x, int y, int xStart, int yStart, int width, int height, int speed, int range, String name, Place place, boolean solid, short ID) {
        super(x, y, xStart, yStart, width, height, speed, range, name, place, "rabbit", solid);
        this.ID = ID;
    }

    @Override
    public void update(Place place) {
        if (prey != null && ((MyPlayer) prey).getPlace() != null) {
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
