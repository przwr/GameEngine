/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Methods;
import game.gameobject.GameObject;
import game.gameobject.Player;

/**
 *
 * @author przemek
 */
public class CircleInteractiveCollision extends InteractiveCollision {

    int radius;

    public CircleInteractiveCollision(int radius) {
        this.radius = radius;
    }

    private static boolean circleToCircleCollision(int xA, int yA, int xB, int yB, int radiusA, int radiusB) {
        return Methods.pointDistance(xA, yA, xB, yB) <= (radiusA + radiusB);
    }

    @Override
    public void updatePosition(int x, int y) {
        position.set(x, y);
    }

    @Override
    public int isCollide(GameObject object) {
        if (object != null && object.getCollision() != null) {
            if (circleToCircleCollision(position.getX(), position.getY(), object.getX(), object.getY(), radius, object.getCollisionWidth() / 2)) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int isCollide(Player player) {
        if (player != null && player.isInGame()) {
            if (circleToCircleCollision(position.getX(), position.getY(), player.getX(), player.getY(), radius, player.getCollisionWidth() / 2)) {
                return 1;
            }
        }
        return 0;
    }

}
