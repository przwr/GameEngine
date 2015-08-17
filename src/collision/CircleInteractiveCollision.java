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
 * @author przemek
 */
public class CircleInteractiveCollision extends InteractiveCollision {

    private int radius;

    public CircleInteractiveCollision(int radius) {
        this.radius = radius;
    }

    @Override
    public void updatePosition(GameObject owner) {
        int x = owner.getX();
        int y = owner.getY();
        switch (owner.getDirection8Way()) {
            case 0:
                x += owner.getCollision().getWidth() / 2;
                break;
            case 2:
                y -= Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2;
                break;
            case 4:
                x -= owner.getCollision().getWidth() / 2;
                break;
            case 6:
                y += Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2;
                break;
            case 1:
                x += Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2);
                y -= owner.getCollision().getWidth() / 4;
                break;
            case 3:
                x -= Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2;
                y -= owner.getCollision().getWidth() / 4;
                break;
            case 5:
                x -= Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2;
                y += owner.getCollision().getWidth() / 4;
                break;
            case 7:
                x += Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2;
                y += owner.getCollision().getWidth() / 4;
                break;
        }
        position.set(x, y);
    }

    @Override
    public int collide(GameObject object) {
        if (object != null && object.getCollision() != null) {
            return circleToCircleDistance(position.getX(), position.getY(), object.getX(), object.getY(), radius, object.getCollisionWidth() / 2);
        }
        return -1;
    }

    @Override
    public int collide(Player player) {
        if (player != null && player.isInGame()) {
            return circleToCircleDistance(position.getX(), position.getY(), player.getX(), player.getY(), radius, player.getCollisionWidth() / 2);
        }
        return -1;
    }

}
