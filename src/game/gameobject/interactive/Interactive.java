/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive;

import engine.utilities.Methods;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import net.jodk.lang.FastMath;

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
    private byte weaponType = -1;
    private byte attackType = -1;
    private boolean active;

    // for Players and Weapons
    public Interactive(GameObject owner, InteractiveActivator activator,
            InteractiveCollision collision, InteractiveAction action,
            byte weaponType, byte attackType, float modifier) {
        this.owner = owner;
        this.activator = activator;
        this.collision = collision;
        this.action = action;
        this.weaponType = weaponType;
        this.attackType = attackType;
        this.modifier = modifier;
    }

    // for Mobs and NPCs
    public Interactive(GameObject owner, InteractiveActivator activator,
            InteractiveCollision collision, InteractiveAction action, byte attackType, float modifier) {
        this.owner = owner;
        this.activator = activator;
        this.collision = collision;
        this.action = action;
        this.modifier = modifier;
        this.attackType = attackType;
    }

    public void checkCollision(GameObject[] players, List<Mob> mobs) {
        if (collision != null) {
            if (COLLIDES_WITH_MOBS) {
                mobs.stream().filter((mob) -> ((COLLIDES_WITH_SELF || mob != owner))).forEach((mob) -> {
                    InteractiveResponse response = collision.collide(owner, mob, attackType);
                    if (response.getPixels() > 0) {
                        action.act(mob, this, response);
                    }
                });
            }
            if (COLLIDES_WITH_PLAYERS) {
                for (GameObject player : players) {
                    if ((COLLIDES_WITH_SELF || player != owner)) {
                        InteractiveResponse response = collision.collide(owner, (Player) player, attackType);
                        if (response.getPixels() > 0) {
                            action.act(player, this, response);
                        }
                    }
                }
            }
        } else {    //To obejście dla ataków nie-kolizyjnych jest strasznie chamskie... trzeba będzie coś wymyślić innego <(-_-<)
            action.act(owner, this, InteractiveResponse.NO_RESPONSE);
        }
    }

    public void update() {
        active = activator.checkActivation(owner);
        if (active) {
            if (collision != null) {
                collision.updatePosition(owner);
            }
            activator.setActivated(false);
        }
    }

    //TODO stworzyć Weapon, które ma właściwości jego użycia, jak przeliczenie danych, wygląd, czy używane statystyki
    public void recalculateData(InteractiveResponse response) {
        response.setPixels(Methods.roundDouble(FastMath.sqrt(response.getPixels()) * modifier * owner.getStats().getStrength()));
    }

    public InteractiveActivator getActivator() {
        return activator;
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

    public byte getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(byte weaponType) {
        this.weaponType = weaponType;
    }

    public byte getAttackType() {
        return attackType;
    }

    public void setAttackType(byte attackType) {
        this.attackType = attackType;
    }
}
