/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame;

import engine.Methods;
import game.gameobject.Mob;
import game.place.Place;

/**
 *
 * @author przemek
 */
public class MyMob extends Mob {

    public MyMob(int x, int y, int startX, int startY, int width, int height, int sx, int sy, int speed, int range, String name, Place place, boolean solid, double SCALE) {
        super(x, y, startX, startY, width, height, sx, sy, speed, range, name, place, solid, SCALE);
    }

    @Override
    public void update(Place place) {
        if (prey != null && ((MyPlayer) prey).getPlace() != null) {
            chase(prey);
            if (Methods.PointDistance(getX(), getY(), prey.getX(), prey.getY()) > range * 1.5) {
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
