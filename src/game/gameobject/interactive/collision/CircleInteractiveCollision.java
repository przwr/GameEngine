/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive.collision;

import collision.Rectangle;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.gameobject.interactive.InteractiveResponse;
import org.newdawn.slick.Color;

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
                x += (owner.getCollision().getWidthHalf() + shift);
                break;
            case UP:
                y -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidthHalf() + shift));
                break;
            case LEFT:
                x -= (owner.getCollision().getWidthHalf() + shift);
                break;
            case DOWN:
                y += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidthHalf() + shift));
                break;
            case UP_RIGHT:
                x += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidthHalf() + shift));
                y -= owner.getCollision().getWidth() / 4 + shift / 2;
                break;
            case UP_LEFT:
                x -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidthHalf() + shift));
                y -= owner.getCollision().getWidth() / 4 + shift / 2;
                break;
            case DOWN_LEFT:
                x -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidthHalf() + shift));
                y += owner.getCollision().getWidth() / 4 + shift / 2;
                break;
            case DOWN_RIGHT:
                x += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidthHalf() + shift));
                y += owner.getCollision().getWidth() / 4 + shift / 2;
                break;
        }
        position.set(x, y);
    }

    @Override
    protected InteractiveResponse collideImplementation(GameObject owner, GameObject object, byte attackType) {
        if (object != null && object.getCollision() != null) {
            int objectBottom = (int) object.getFloatHeight();
            int objectTop = objectBottom + object.getActualHeight();
            int bottom = (int) owner.getFloatHeight() + fromBottom;
            int top = bottom + height;
            if (objectTop > bottom && objectBottom < top) {
                int pixelsIn = circleToCircleDistance(position.getX(), position.getY(), object.getX(), object.getY(), radius, object.getCollisionWidth() / 2);
                if (pixelsIn > 0) {
                    response.setResponse(pixelsIn, shift + radius, (byte) (calculateInteractionDirection(object
                            .getDirection8Way(), object.getCollision(), owner.getX(), owner.getY())), attackType, owner);
                    return response;
                }
            }
        }
        return InteractiveResponse.NO_RESPONSE;
    }

    @Override
    protected InteractiveResponse collideImplementation(GameObject owner, Player player, byte attackType) {
        if (player != null && player.isInGame()) {
            int playerBottom = (int) player.getFloatHeight();
            int playerTop = playerBottom + player.getActualHeight();
            int bottom = (int) owner.getFloatHeight() + fromBottom;
            int top = bottom + height;
            if (playerTop > bottom && playerBottom < top) {
                int pixelsIn = circleToCircleDistance(position.getX(), position.getY(), player.getX(), player.getY(), radius, player.getCollisionWidth() / 2);
                if (pixelsIn > 0) {
                    response.setResponse(pixelsIn, shift + radius, (byte) (calculateInteractionDirection(player
                            .getDirection8Way(), player.getCollision(), owner.getX(), owner.getY())), attackType, owner);
                    return response;
                }
            }
        }
        return InteractiveResponse.NO_RESPONSE;
    }

    @Override
    public void render(GameObject owner) {
        Drawer.setColorStatic(new Color(0.9f, 0.1f, 0.1f));
        Drawer.drawEllipse(position.getX(), position.getY(), radius, Methods.roundDouble(radius * Methods.ONE_BY_SQRT_ROOT_OF_2), 16);
        Drawer.refreshColor();
    }


    public void setEnvironmentCollision(Rectangle environmentCollision, GameObject owner, boolean half) {
        int r = half ? radius / 2 : radius;
        int sqrt2Radius = Methods.roundDouble(Methods.ONE_BY_SQRT_ROOT_OF_2 * r);
        environmentCollision.setXStart(position.getX() - r);
        environmentCollision.setYStart(position.getY() - sqrt2Radius);
        environmentCollision.setWidth(2 * r);
        environmentCollision.setHeight(sqrt2Radius * 2);
    }

}
