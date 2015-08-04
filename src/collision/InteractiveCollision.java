/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Player;

/**
 * @author przemek
 */
public abstract class InteractiveCollision {

    protected Point position = new Point();

    protected static int circleToCircleDistance(int xA, int yA, int xB, int yB, int radiusA, int radiusB) {
        return (radiusA + radiusB) - Methods.pointDistance(xA, (int) (yA * Methods.SQRT_ROOT_OF_2), xB, (int) (yB * Methods.SQRT_ROOT_OF_2));
    }

    public abstract void updatePosition(GameObject owner);

    public abstract int collide(GameObject object);

    public abstract int collide(Player player);
}
