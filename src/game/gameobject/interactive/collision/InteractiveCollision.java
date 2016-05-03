/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive.collision;

import collision.Figure;
import collision.Rectangle;
import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.interactive.InteractiveResponse;
import net.jodk.lang.FastMath;

import java.awt.geom.Line2D;

import static game.gameobject.entities.Entity.*;

/**
 * @author przemek
 */
public abstract class InteractiveCollision {

    private static int[] directions = {InteractiveResponse.FRONT, InteractiveResponse.FRONT, InteractiveResponse.SIDE, InteractiveResponse.BACK,
            InteractiveResponse.BACK, InteractiveResponse.BACK, InteractiveResponse.SIDE, InteractiveResponse.FRONT};
    protected Point position = new Point();
    protected int fromBottom, height, shift;
    protected InteractiveResponse response = new InteractiveResponse();

    public InteractiveCollision(int fromBottom, int height, int shift) {
        this.fromBottom = fromBottom;
        this.height = height;
        this.shift = shift;
    }

    protected static int circleToCircleDistance(int xA, int yA, int xB, int yB, int radiusA, int radiusB) {
        return (radiusA + radiusB) - Methods.pointDistance(xA, (int) (yA * Methods.SQRT_ROOT_OF_2), xB, (int) (yB * Methods.SQRT_ROOT_OF_2));
    }

    protected static int lineToCircleDistance(int xC, int yC, int radius, Point start, Point end, int length) {
        double d = Line2D.ptSegDist(start.getX(), (int) (start.getY() * Methods.SQRT_ROOT_OF_2), end.getX(), (end.getY() * Methods.SQRT_ROOT_OF_2), xC, (int)
                (yC * Methods.SQRT_ROOT_OF_2));
        if (d <= radius) {
            int e = Methods.pointDistance(xC, (int) (yC * Methods.SQRT_ROOT_OF_2), end.getX(), (int) (end.getY() * Methods.SQRT_ROOT_OF_2));
            if (d < 1) {
                int pixelsIn = radius + e;
                return pixelsIn > length ? length : pixelsIn;
            } else {
                double d2 = d * d;
                int x = (int) FastMath.sqrt(e * e - d2);
                int y = (int) FastMath.sqrt(radius * radius - d2);
                if (x < y) {
                    return y > length ? length : y;
                }
                int pixelsIn = x + y;
                return pixelsIn > length ? length : pixelsIn;
            }
        }
        return -1;
    }

    public abstract void updatePosition(Entity owner);

    protected abstract InteractiveResponse collideImplementation(Entity owner, Entity object, byte attackType);

    protected abstract InteractiveResponse collideImplementation(Entity owner, Player player, byte attackType);

    public abstract void setEnvironmentCollision(Rectangle environmentCollision, Entity owner, boolean half);

    public InteractiveResponse collide(Entity owner, Entity object, byte attackType) {
        if (owner.getCollision().isCollide() && object.getCollision().isHitable()) {
            return collideImplementation(owner, object, attackType);
        } else {
            return InteractiveResponse.NO_RESPONSE;
        }
    }

    public InteractiveResponse collide(Entity owner, Player player, byte attackType) {
        if (owner.getCollision().isCollide() && player.getCollision().isHitable()) {
            return collideImplementation(owner, player, attackType);
        } else {
            return InteractiveResponse.NO_RESPONSE;
        }
    }

    protected int calculateInteractionDirection(int objectDirection, Figure collision, int x, int y) {
        int xS = collision.getX();
        int xE = collision.getXEnd();
        int yS = collision.getY();
        int yE = collision.getYEnd();
        int ownerDirection;
        if (x > xE) {
            if (y > yE) {
                ownerDirection = DOWN_RIGHT;
            } else if (y > yS) {
                ownerDirection = RIGHT;
            } else {
                ownerDirection = UP_RIGHT;
            }
        } else if (x > xS) {
            if (y > yE) {
                ownerDirection = DOWN;
            } else if (y > yS) {   // point inside
                ownerDirection = objectDirection;
            } else {
                ownerDirection = UP;
            }
        } else {
            if (y > yE) {
                ownerDirection = DOWN_LEFT;
            } else if (y > yS) {
                ownerDirection = LEFT;
            } else {
                ownerDirection = UP_LEFT;
            }
        }
        return directions[Math.abs(objectDirection - ownerDirection)];
    }

    public abstract void render(Entity owner);

    public Point getPosition() {
        return position;
    }
}
