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
 *
 * @author przemek
 */
public class Interactive {

    public static InteractiveAction HURT = new InteractiveActionHurt();

    private GameObject owner;
    private InteractiveCollision collision;
    private InteractiveAction action = HURT;
    private boolean active;
    private boolean COLLIDES_WITH_SELF = false, COLLIDES_WITH_PLAYERS = true, COLLIDES_WITH_MOBS = true;

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
