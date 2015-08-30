package collision.interactive;

import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Player;

/**
 * Created by przemek on 29.08.15.
 */
public class LineInteractiveCollision extends InteractiveCollision {

    private int length, shift;
    private Point end = new Point();

    public LineInteractiveCollision(int length, int shift) {
        this.length = length;
        this.shift = shift;
    }

    @Override
    public void updatePosition(GameObject owner) {
        int x = owner.getX();
        int y = owner.getY();
        switch (owner.getDirection8Way()) {
            case 0:
                x += (owner.getCollision().getWidth() / 2 + shift);
                position.set(x, y);
                x += length;
                break;
            case 2:
                y -= (Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2 + shift);
                position.set(x, y);
                y -= length;
                break;
            case 4:
                x -= (owner.getCollision().getWidth() / 2 + shift);
                position.set(x, y);
                x -= length;
                break;
            case 6:
                y += (Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2 + shift);
                position.set(x, y);
                y += length;
                break;
            case 1:
                x += Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2 + shift;
                y -= (owner.getCollision().getWidth() / 4 + shift / 2);
                position.set(x, y);
                x += length;
                y += length / 2;
                break;
            case 3:
                x -= Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2 + shift;
                y -= (owner.getCollision().getWidth() / 4 + shift / 2);
                position.set(x, y);
                x -= length;
                y -= length / 2;
                break;
            case 5:
                x -= Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2 + shift;
                y += (owner.getCollision().getWidth() / 4 + shift / 2);
                position.set(x, y);
                x -= length;
                y += length / 2;
                break;
            case 7:
                x += Methods.ONE_BY_SQRT_ROOT_OF_2 * owner.getCollision().getWidth() / 2 + shift;
                y += (owner.getCollision().getWidth() / 4 + shift / 2);
                position.set(x, y);
                x += length;
                y += length / 2;
                break;
        }
        end.set(x, y);
    }

    @Override
    public int collide(GameObject object) {
        if (object != null && object.getCollision() != null) {
            return lineToCircleDistance(object.getX(), object.getY(), object.getCollisionWidth() / 2, position, end, length);

        }
        return -1;
    }

    @Override
    public int collide(Player player) {
        if (player != null && player.isInGame()) {
            return lineToCircleDistance(player.getX(), player.getY(), player.getCollisionWidth() / 2, position, end, length);
        }
        return -1;
    }

}
