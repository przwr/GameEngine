package game.gameobject.interactive.collision;

import collision.Rectangle;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.interactive.InteractiveResponse;
import org.newdawn.slick.Color;

import static game.gameobject.entities.Entity.*;

/**
 * @author przemek
 */
public class CurveInteractiveCollision extends InteractiveCollision {

    private final int radius, activationAngle;

    public CurveInteractiveCollision(int fromBottom, int height, int shift, int radius, int activationAngle) {
        super(fromBottom, height, shift);
        this.radius = radius;
        this.activationAngle = activationAngle;
    }

    @Override
    public void updatePosition(Entity owner) {
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
    protected InteractiveResponse collideImplementation(Entity owner, Entity entity, byte attackType) {
        if (entity != null && entity.getCollision() != null) {
            int objectBottom = (int) entity.getFloatHeight();
            int objectTop = objectBottom + entity.getActualHeight();
            int bottom = (int) owner.getFloatHeight() + fromBottom;
            int top = bottom + height;
            if (objectTop > bottom && objectBottom < top) {
                int pixelsIn = circleToCircleDistance(position.getX(), position.getY(), entity.getX(), entity.getY(), radius, entity.getCollisionWidth() / 2);
                if (pixelsIn > 0) {
                    int direction = owner.getDirection8Way();
                    double angle = Methods.pointAngleCounterClockwise(position.getX(), position.getY(), entity.getX(), entity.getY());
                    if (direction == 0) {
                        direction = 8;
                    }
                    if (angle == -0) {
                        angle = 360;
                    }
                    double angleDifference = direction * 45 - angle;
                    if (Math.abs(angleDifference) <= activationAngle || Math.abs(angleDifference - 360) <= activationAngle || Math.abs(angleDifference + 360)
                            <= activationAngle) {
                        response.setResponse(pixelsIn, shift + radius, (byte) (calculateInteractionDirection(entity.getDirection8Way(),
                                entity.getCollision(), owner.getX(), owner.getY())), attackType, owner);
                        return response;
                    }
                }
            }
        }
        return InteractiveResponse.NO_RESPONSE;
    }

    @Override
    protected InteractiveResponse collideImplementation(Entity owner, Player player, byte attackType) {
        if (player != null && player.isInGame()) {
            int playerBottom = (int) player.getFloatHeight();
            int playerTop = playerBottom + player.getActualHeight();
            int bottom = (int) owner.getFloatHeight() + fromBottom;
            int top = bottom + height;
            if (playerTop > bottom && playerBottom < top) {
                int pixelsIn = circleToCircleDistance(position.getX(), position.getY(), player.getX(), player.getY(), radius, player.getCollisionWidth() / 2);
                if (pixelsIn > 0) {
                    double angle = Methods.pointAngleCounterClockwise(position.getX(), position.getY(), player.getX(), player.getY());
                    int direction = owner.getDirection8Way();
                    if (direction == 0) {
                        direction = 8;
                    }
                    if (angle == -0) {
                        angle = 360;
                    }
                    double angleDifference = direction * 45 - angle;
                    if (Math.abs(angleDifference) <= activationAngle || Math.abs(angleDifference - 360) <= activationAngle || Math.abs(angleDifference + 360)
                            <= activationAngle) {
                        response.setResponse(pixelsIn, shift + radius, (byte) (calculateInteractionDirection(player.getDirection8Way(),
                                player.getCollision(), owner.getX(), owner.getY())), attackType, owner);
                        return response;
                    }
                }
            }
        }
        return InteractiveResponse.NO_RESPONSE;
    }

    @Override
    public void render(Entity owner) {
        Drawer.setColorStatic(new Color(0.9f, 0.1f, 0.1f));
        int angle = 360 - (owner.getDirection8Way() * 45);
        Drawer.drawEllipseSector(position.getX(), position.getY(), radius, Methods.roundDouble(radius * Methods.ONE_BY_SQRT_ROOT_OF_2), angle -
                activationAngle / 2, angle + activationAngle / 2, 32);
        Drawer.refreshColor();
    }


    public void setEnvironmentCollision(Rectangle environmentCollision, Entity owner, boolean half) {
        int r = half ? radius / 2 : radius;
        int sqrt2Radius = Methods.roundDouble(Methods.ONE_BY_SQRT_ROOT_OF_2 * r);
        switch (((Entity) environmentCollision.getOwner()).getDirection8Way()) {
            case RIGHT:
                environmentCollision.setXStart(position.getX());
                environmentCollision.setYStart(position.getY() - sqrt2Radius);
                environmentCollision.setWidth(r);
                environmentCollision.setHeight(sqrt2Radius * 2);
                break;
            case UP:
                environmentCollision.setXStart(position.getX() - r);
                environmentCollision.setYStart(position.getY() - sqrt2Radius);
                environmentCollision.setWidth(radius);
                environmentCollision.setHeight(sqrt2Radius);
                break;
            case LEFT:
                environmentCollision.setXStart(position.getX() - r);
                environmentCollision.setYStart(position.getY() - sqrt2Radius);
                environmentCollision.setWidth(r);
                environmentCollision.setHeight(sqrt2Radius * 2);
                break;
            case DOWN:
                environmentCollision.setXStart(position.getX() - r);
                environmentCollision.setYStart(position.getY());
                environmentCollision.setWidth(radius);
                environmentCollision.setHeight(sqrt2Radius);
                break;
            case UP_RIGHT:
                environmentCollision.setXStart(position.getX() - sqrt2Radius);
                environmentCollision.setYStart(position.getY() - sqrt2Radius);
                environmentCollision.setWidth(sqrt2Radius + r);
                environmentCollision.setHeight(sqrt2Radius + r / 2);
                break;
            case UP_LEFT:
                environmentCollision.setXStart(position.getX() - r);
                environmentCollision.setYStart(position.getY() - sqrt2Radius);
                environmentCollision.setWidth(sqrt2Radius + r);
                environmentCollision.setHeight(sqrt2Radius + r / 2);
                break;
            case DOWN_LEFT:
                environmentCollision.setXStart(position.getX() - r);
                environmentCollision.setYStart(position.getY() - r / 2);
                environmentCollision.setWidth(sqrt2Radius + r);
                environmentCollision.setHeight(sqrt2Radius + r / 2);
                break;
            case DOWN_RIGHT:
                environmentCollision.setXStart(position.getX() - sqrt2Radius);
                environmentCollision.setYStart(position.getY() - r / 2);
                environmentCollision.setWidth(sqrt2Radius + r);
                environmentCollision.setHeight(sqrt2Radius + r / 2);
                break;
        }

    }
}
