/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.interactive.*;

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
    private boolean COLLIDES_WITH_SELF = false, COLLIDES_WITH_MOBS = true, COLLIDES_WITH_PLAYERS = true;
    private float modifier;
    private boolean active;

    public Interactive(GameObject owner, InteractiveActivator activator, InteractiveCollision collision, InteractiveAction action, float modifier) {
        this.owner = owner;
        this.activator = activator;
        this.collision = collision;
        this.action = action;
        this.modifier = modifier;
    }

    public void checkCollision(GameObject[] players, List<Mob> mobs) {
        if (COLLIDES_WITH_MOBS) {
            mobs.stream().filter((mob) -> ((COLLIDES_WITH_SELF || mob != owner))).forEach((mob) -> {
                InteractiveResponse response = collision.collide(owner, mob);
                if (response.getData() > 0) {
                    action.act(mob, this, response);
                }
            });
        }
        if (COLLIDES_WITH_PLAYERS) {
            for (GameObject player : players) {
                if ((COLLIDES_WITH_SELF || player != owner)) {
                    InteractiveResponse response = collision.collide(owner, (Player) player);
                    if (response.getData() > 0) {
                        action.act(player, this, response);
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

    //TODO stworzyć Weapon, które ma właściwości jego użycia, jak przeliczenie danych, wygląd, czy używane statystyki

    public void recalculateData(InteractiveResponse response) {
        response.setData(response.getData() * modifier * owner.getStats().getStrength());
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public float getModifier() {
        return modifier;
    }

    public void setModifier(float modifier) {
        this.modifier = modifier;
    }
}
