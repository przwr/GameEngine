/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive;

import engine.utilities.Methods;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;

import static game.gameobject.GameObject.*;

/**
 * @author przemek
 */
public class CircleInteractiveCollision extends InteractiveCollision {

    private int radius;

    public CircleInteractiveCollision(int fromBottom, int height, int shift, int radius) {
        super(fromBottom, height, shift);
        this.radius = radius;
    }

    @Override
    public void updatePosition(GameObject owner) {
        int x = owner.getX();
        int y = owner.getY();
        switch (owner.getDirection8Way()) {
            case RIGHT:
                x += (owner.getCollision().getWidth() / 2 + shift);
                break;
            case UP:
                y -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                break;
            case LEFT:
                x -= (owner.getCollision().getWidth() / 2 + shift);
                break;
            case DOWN:
                y += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                break;
            case UP_RIGHT:
                x += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                y -= owner.getCollision().getWidth() / 4 + shift / 2;
                break;
            case UP_LEFT:
                x -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                y -= owner.getCollision().getWidth() / 4 + shift / 2;
                break;
            case DOWN_LEFT:
                x -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                y += owner.getCollision().getWidth() / 4 + shift / 2;
                break;
            case DOWN_RIGHT:
                x += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                y += owner.getCollision().getWidth() / 4 + shift / 2;
                break;
        }
        position.set(x, y);
    }

    @Override
    public InteractiveResponse collide(GameObject owner, GameObject object) {
        if (object != null && object.getCollision() != null) {
            int objectBottom = (int) object.getAboveGroundHeight();
            int objectTop = objectBottom + object.getAppearance().getActualHeight();
            int bottom = (int) owner.getAboveGroundHeight() + fromBottom;
            int top = bottom + height;
            if (objectTop > bottom && objectBottom < top) {
                int pixelsIn = circleToCircleDistance(position.getX(), position.getY(), object.getX(), object.getY(), radius, object.getCollisionWidth() / 2);
                if (pixelsIn > 0) {
                    response.setResponse(pixelsIn, (byte) (calculateInteractionDirection(object.getDirection8Way(),
                            object.getCollision(), owner.getX(), owner.getY())), owner);
                    return response;
                }
            }
        }
        return InteractiveResponse.NO_RESPONSE;
    }

    @Override
    public InteractiveResponse collide(GameObject owner, Player player) {
        if (player != null && player.isInGame()) {
            int playerBottom = (int) player.getAboveGroundHeight();
            int playerTop = playerBottom + player.getAppearance().getActualHeight();
            int bottom = (int) owner.getAboveGroundHeight() + fromBottom;
            int top = bottom + height;
            if (playerTop > bottom && playerBottom < top) {
                int pixelsIn = circleToCircleDistance(position.getX(), position.getY(), player.getX(), player.getY(), radius, player.getCollisionWidth() / 2);
                if (pixelsIn > 0) {
                    response.setResponse(pixelsIn, (byte) (calculateInteractionDirection(player.getDirection8Way(),
                            player.getCollision(), owner.getX(), owner.getY())), owner);
                    return response;
                }
            }
        }
        return InteractiveResponse.NO_RESPONSE;
    }

}
