/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision.interactive;

import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Player;

import java.awt.geom.Line2D;

/**
 * @author przemek
 */
public abstract class InteractiveCollision {

    protected Point position = new Point();

    protected static int circleToCircleDistance(int xA, int yA, int xB, int yB, int radiusA, int radiusB) {
        return (radiusA + radiusB) - Methods.pointDistance(xA, (int) (yA * Methods.SQRT_ROOT_OF_2), xB, (int) (yB * Methods.SQRT_ROOT_OF_2));
    }

    protected static int lineToCircleDistance(int xC, int yC, int radius, Point start, Point end, int length) {
        double d = Line2D.ptSegDist(start.getX(), (int) (start.getY() * Methods.SQRT_ROOT_OF_2), end.getX(), (end.getY() * Methods.SQRT_ROOT_OF_2), xC, (int) (yC * Methods.SQRT_ROOT_OF_2));
        if (d <= radius) {
            int e = Methods.pointDistance(xC, (int) (yC * Methods.SQRT_ROOT_OF_2), end.getX(), (int) (end.getY() * Methods.SQRT_ROOT_OF_2));
            if (d < 1) {
                int pixelsIn = radius + e;
                return pixelsIn > length ? length : pixelsIn;
            } else {
                double d2 = d * d;
                int x = (int) Math.sqrt(e * e - d2);
                int y = (int) Math.sqrt(radius * radius - d2);
                if (x < y) {
                    return y > length ? length : y;
                }
                int pixelsIn = x + y;
                return pixelsIn > length ? length : pixelsIn;
            }
        }
        return -1;
    }

    public abstract void updatePosition(GameObject owner);

    public abstract int collide(GameObject object);

    public abstract int collide(Player player);
}
