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

    public MyMob(int x, int y, int startX, int startY, int width, int height, int speed, int range, String name, Place place, boolean solid, short id) {
        super(x, y, startX, startY, width, height, speed, range, name, place, solid);
        this.id = id;
    }

    @Override
    public void update(Place place) {
        if (prey != null && ((MyPlayer) prey).getPlace() != null) {
            chase(prey);
            if (Methods.PointDistance(getX(), getY(), prey.getX(), prey.getY()) > range * 1.5 || prey.getMap() != map) {
                prey = null;
            }
        } else {
            look(place.players);
            brake(2);
        }
        canMove((int) (hspeed + myHspeed), (int) (vspeed + myVspeed));
        brakeOthers();
    }
}
