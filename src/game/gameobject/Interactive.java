/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.InteractiveAction;
import collision.InteractiveActionHurt;
import collision.InteractiveCollision;

import java.util.List;

/**
 * @author przemek
 */
public class Interactive {

    private static final InteractiveAction HURT = new InteractiveActionHurt();

    private final GameObject owner;
    private final InteractiveCollision collision;
    private final InteractiveAction action = HURT;
    private final boolean COLLIDES_WITH_SELF = false;
    private final boolean COLLIDES_WITH_PLAYERS = true;
    private final boolean COLLIDES_WITH_MOBS = true;
    private boolean active;

    public Interactive(GameObject owner, InteractiveCollision collision) {
        this.owner = owner;
        this.collision = collision;
    }

    public void checkCollision(GameObject[] players, List<Mob> mobs) {
        if (COLLIDES_WITH_MOBS) {
            mobs.stream().filter((mob) -> ((COLLIDES_WITH_SELF || mob != owner))).forEach((mob) -> {
                if (collision.isCollide(mob) > 0) {
                    action.act(mob);
                }
            });
        }
        if (COLLIDES_WITH_PLAYERS) {
            for (GameObject player : players) {
                if ((COLLIDES_WITH_SELF || player != owner)) {
                    if (collision.isCollide((Player) player) > 0) {
                        action.act(player);
                    }
                }
            }
        }
    }

    public void updateCollision() {
        collision.updatePosition(owner.getCollision().getXEnd() + 32, owner.getCollision().getYEnd() - 10);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
