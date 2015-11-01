/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive;

import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.items.Arrow;
import game.gameobject.items.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author przemek
 */
public class Interactive {

    public static final InteractiveAction STRENGTH_HURT = new InteractiveActionStrengthHurt();
    public static final InteractiveAction BOW_HURT = new InteractiveActionBowHurt();
    public static final InteractiveActivator ALWAYS = new InteractiveActivatorAlways();

    private final GameObject owner;
    private final InteractiveCollision collision;
    private final InteractiveAction action;
    private final InteractiveActivator activator;
    private boolean COLLIDES_WITH_SELF = false, COLLIDES_WITH_MOBS = true, COLLIDES_WITH_PLAYERS = true, COLLIDES_WITH_FRIENDS = false;
    private float modifier;
    private byte weaponType = -1;
    private byte attackType = -1;
    private boolean active, activated;
    private ArrayList<GameObject> exceptions;


    private Interactive(GameObject owner, InteractiveActivator activator, InteractiveCollision collision, InteractiveAction action, byte weaponType, byte
            attackType, float modifier) {
        this.owner = owner;
        this.activator = activator;
        this.collision = collision;
        this.action = action;
        this.weaponType = weaponType;
        this.attackType = attackType;
        this.modifier = modifier;
    }

    public static Interactive create(GameObject owner, InteractiveActivator activator, InteractiveCollision collision, InteractiveAction action,
                                     byte weaponType, byte attackType, float modifier) {
        return new Interactive(owner, activator, collision, action, weaponType, attackType, modifier);
    }

    public static Interactive createNotWeapon(GameObject owner, InteractiveActivator activator, InteractiveCollision collision, InteractiveAction action, byte
            attackType, float modifier) {
        return new Interactive(owner, activator, collision, action, (byte) -1, attackType, modifier);
    }

    public static Interactive createSpawner(GameObject owner, InteractiveActivator activator, InteractiveAction action, byte weaponType, byte attackType) {
        return new Interactive(owner, activator, null, action, weaponType, attackType, 0);
    }

    public void addException(GameObject exception) {
        if (exceptions == null) {
            exceptions = new ArrayList<>();
        }
        exceptions.add(exception);
    }

    public void clearExceptions() {
        if (exceptions != null) {
            exceptions.clear();
        }
    }

    public void actIfActivated(GameObject[] players, List<Mob> mobs) {
        activated = false;
        if (collisionActivates()) {
            if (COLLIDES_WITH_MOBS) {
                mobs.stream().filter((mob) -> (!isException(mob) && (COLLIDES_WITH_SELF || mob != owner) && (COLLIDES_WITH_FRIENDS
                        || mob.getClass().getName() != owner.getClass().getName()))).forEach((mob) -> {
                    InteractiveResponse response = collision.collide(owner, mob, attackType);
                    if (response.getPixels() > 0) {
                        activated = true;
                        action.act(mob, this, response);
                    }
                });
            }
            if (COLLIDES_WITH_PLAYERS) {
                for (GameObject player : players) {
                    if (((Player) player).isInGame() && !isException(player) && (COLLIDES_WITH_SELF || (player != owner))) {
                        InteractiveResponse response = collision.collide(owner, (Player) player, attackType);
                        if (response.getPixels() > 0) {
                            activated = true;
                            action.act(player, this, response);
                        }
                    }
                }
            }
        } else {
            activated = true;
            action.act(owner, this, InteractiveResponse.NO_RESPONSE);
        }
    }

    private boolean isException(GameObject object) {
        return exceptions != null && exceptions.stream().anyMatch((go) -> (object == go));
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

    public InteractiveActivator getActivator() {
        return activator;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActivated() {
        return activated;
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

    public boolean collisionActivates() {
        return collision != null;
    }

    public GameObject getOwner() {
        return owner;
    }

    public float getWeaponModifier() {
        if (owner instanceof Player) {
            Weapon weapon = ((Player) owner).getWeapon();
            if (weapon != null) {
                if (weapon.getType() == weaponType) {
                    return weapon.getModifier();
                }
            }
        }
        if (owner instanceof Arrow) {
            GameObject ow = ((Arrow) owner).getOwner();
            if (ow instanceof Player) {
                Weapon weapon = ((Player) ow).getWeapon();
                if (weapon != null) {
                    if (weapon.getType() == weaponType) {
                        return weapon.getModifier();
                    }
                }
            }
        }
        return 1;
    }
}
