/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.gameobject.GameObject;
import game.place.Place;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author przemek
 */
public class Physics {

    public static GameObject checkCollision(GameObject go1, GameObject go2) {
        return checkCollision(new Rectangle(go2.getSX() + go2.getX(), go2.getSY() + go2.getY(), go2.getWidth(), go2.getHeight()), go2, 0, 0);
    }

    public static GameObject checkCollision(Rectangle r1, GameObject go2, int magX, int magY) {
        Rectangle r2 = new Rectangle(go2.getSX() + go2.getX() + magX, go2.getSY() + go2.getY() + magY, go2.getWidth(), go2.getHeight());
        if (r1.intersects(r2)) {
            return go2;
        }
        return null;
    }    


    public static int dist(float x1, float y1, float x2, float y2) {
        double x = x2 - x1;
        double y = y2 - y1;

        return (int) Math.sqrt((x * x) + (y * y));
    }

    public static GameObject sphereCollide(int x, int y, int radius, ArrayList<GameObject> objects, Place place) {
        for (GameObject go : objects) {
            if (dist(go.getX() + go.getSX() + go.getWidth() / 2, go.getY() + go.getSY() + go.getHeight() / 2, x, y) < radius) {
                return go;
            }
        }
        return null;
    }

    public static GameObject sphereCollideWPl(int x, int y, int radius, ArrayList<GameObject> objects, Place place) {

        for (GameObject go : objects) {
            if (dist(go.getX() + go.getSX() + go.getWidth() / 2, go.getY() + go.getSY() + go.getHeight() / 2, x, y) < radius) {
                return go;
            }
        }
        return null;
    }
}
