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
public class CircleWeaponCollision extends WeaponCollision {

    int radius;

    public CircleWeaponCollision(int radius) {
        this.radius = radius;
    }

    @Override
    public void updatePosition(int x, int y) {
        position.set(x, y);
    }

    @Override
    public int isCollide(GameObject object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int isCollide(Player player) {
        if (player.isInGame()) {
            if (circleToCircleCollision(position.getX(), position.getY(), player.getX(), player.getY(), radius, player.getCollisionWidth() / 3)) {
                System.out.println("Ałć " + System.nanoTime());
            }
        }
        return 0;
    }

    private static boolean circleToCircleCollision(int xA, int yA, int xB, int yB, int radiusA, int radiusB) {
        return Methods.pointDistance(xA, yA, xB, yB) <= (radiusA + radiusB);
    }

}
