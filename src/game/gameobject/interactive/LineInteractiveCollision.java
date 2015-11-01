package game.gameobject.interactive;

import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;

import static game.gameobject.GameObject.*;

/**
 * Created by przemek on 29.08.15.
 */
public class LineInteractiveCollision extends InteractiveCollision {

    private int length, width;
    private Point end = new Point();

    public LineInteractiveCollision(int fromBottom, int height, int shift, int length, int width) {
        super(fromBottom, height, shift);
        this.length = length;
        this.width = width;
    }

    @Override
    public void updatePosition(GameObject owner) {
        int x = owner.getX();
        int y = owner.getY();
        switch (owner.getDirection8Way()) {
            case RIGHT:
                x += (owner.getCollision().getWidth() / 2 + shift);
                position.set(x, y);
                x += length;
                break;
            case UP:
                y -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                position.set(x, y);
                y -= length;
                break;
            case LEFT:
                x -= (owner.getCollision().getWidth() / 2 + shift);
                position.set(x, y);
                x -= length;
                break;
            case DOWN:
                y += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                position.set(x, y);
                y += length;
                break;
            case UP_RIGHT:
                x += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                y -= (owner.getCollision().getWidth() / 4 + shift / 2);
                position.set(x, y);
                x += length;
                y += length / 2;
                break;
            case UP_LEFT:
                x -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                y -= (owner.getCollision().getWidth() / 4 + shift / 2);
                position.set(x, y);
                x -= length;
                y -= length / 2;
                break;
            case DOWN_LEFT:
                x -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                y += (owner.getCollision().getWidth() / 4 + shift / 2);
                position.set(x, y);
                x -= length;
                y += length / 2;
                break;
            case DOWN_RIGHT:
                x += (Methods.ONE_BY_SQRT_ROOT_OF_2 * (owner.getCollision().getWidth() / 2 + shift));
                y += (owner.getCollision().getWidth() / 4 + shift / 2);
                position.set(x, y);
                x += length;
                y += length / 2;
                break;
        }
        end.set(x, y);
    }

    @Override
    protected InteractiveResponse collideImplementation(GameObject owner, GameObject object, byte attackType) {
        if (object != null && object.getCollision() != null) {
            int objectBottom = (int) object.getFloatHeight();
            int objectTop = objectBottom + object.getActualHeight();
            int bottom = (int) owner.getFloatHeight() + fromBottom;
            int top = bottom + height;
            if (objectTop > bottom && objectBottom < top) {
                int pixelsIn = lineToCircleDistance(object.getX(), object.getY(), (object.getCollisionWidth() + width) / 2, position, end, length);
                if (pixelsIn > 0) {
                    response.setResponse(pixelsIn, length, (byte) (calculateInteractionDirection(object
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
                int pixelsIn = lineToCircleDistance(player.getX(), player.getY(), (player.getCollisionWidth() + width) / 2, position, end, length);
                if (pixelsIn > 0) {
                    response.setResponse(pixelsIn, length, (byte) (calculateInteractionDirection(player
                                    .getDirection8Way(),
                            player.getCollision(), owner.getX(), owner.getY())), attackType, owner);
                    return response;
                }
            }
        }
        return InteractiveResponse.NO_RESPONSE;
    }
}
