/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.*;

import java.util.List;

/**
 * @author przemek
 */
public class Interactive {

    public static final InteractiveAction HURT = new InteractiveActionHurt();
    public static final InteractiveActivator ALWAYS = new InteractiveActivatorAlways();

    private final GameObject owner;
    private final InteractiveCollision collision;
    private final InteractiveAction action;
    private final InteractiveActivator activator;
    private final boolean COLLIDES_WITH_SELF = false;
    private boolean active;

    public Interactive(GameObject owner, InteractiveActivator activator, InteractiveCollision collision, InteractiveAction action) {
        this.owner = owner;
        this.activator = activator;
        this.collision = collision;
        this.action = action;

    }

    public void checkCollision(GameObject[] players, List<Mob> mobs) {
        boolean COLLIDES_WITH_MOBS = true;
        if (COLLIDES_WITH_MOBS) {
            mobs.stream().filter((mob) -> ((COLLIDES_WITH_SELF || mob != owner))).forEach((mob) -> {
                int pixelsIn = collision.collide(mob);
                if (pixelsIn > 0) {
                    action.act(mob, owner, pixelsIn);
                }
            });
        }
        boolean COLLIDES_WITH_PLAYERS = true;
        if (COLLIDES_WITH_PLAYERS) {
            for (GameObject player : players) {
                if ((COLLIDES_WITH_SELF || player != owner)) {
                    int pixelsIn = collision.collide((Player) player);
                    if (pixelsIn > 0) {
                        action.act(player, owner, pixelsIn);
                    }
                }
            }
        }
    }

    public void update() {
        active = activator.checkActivation(owner);
        if (active)
            collision.updatePosition(owner);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
