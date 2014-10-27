/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.myGame;

import collision.Rectangle;
import game.gameobject.Mob;
import game.gameobject.GameObject;
import game.place.Place;
import java.util.ArrayList;

/**
 *
 * @author przemek
 */
public class MyMob extends Mob {

    public MyMob(int x, int y, int startX, int startY, int width, int height, int sx, int sy, int speed, int range, String name, Place place, boolean solid) {
        super(x, y, startX, startY, width, height, sx, sy, speed, range, name, place, solid);
        init("rabbit", name, x, y, sx, sy, place);
    }

    @Override
    public void update(ArrayList<GameObject> players) {
        if (prey != null) {
            //chase(prey);
        } else {
            look(players);
        }
    }

}
