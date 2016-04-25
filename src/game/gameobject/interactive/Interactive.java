/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.interactive;

import collision.Block;
import collision.Rectangle;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.gameobject.entities.Player;
import game.gameobject.interactive.action.InteractiveAction;
import game.gameobject.interactive.action.InteractiveActionBowHurt;
import game.gameobject.interactive.action.InteractiveActionPush;
import game.gameobject.interactive.action.InteractiveActionStrengthHurt;
import game.gameobject.interactive.activator.InteractiveActivator;
import game.gameobject.interactive.activator.InteractiveActivatorAlways;
import game.gameobject.interactive.collision.InteractiveCollision;
import game.gameobject.interactive.collision.LineInteractiveCollision;
import game.gameobject.items.Arrow;
import game.gameobject.items.Weapon;
import game.place.map.Area;

import java.util.ArrayList;
import java.util.List;

/**
 * @author przemek
 */
public class Interactive {

    public static final InteractiveAction PUSH = new InteractiveActionPush();
    public static final InteractiveAction STRENGTH_HURT = new InteractiveActionStrengthHurt();
    public static final InteractiveAction BOW_HURT = new InteractiveActionBowHurt();
    public static final InteractiveActivator ALWAYS = new InteractiveActivatorAlways();
    private final static int PLAYERS = 0, ENVIRONMENT = 1, MOBS = 2, FRIENDS = 3, SELF = 4;
    private final GameObject owner;
    private final InteractiveCollision collision;
    private final InteractiveAction action;
    private final InteractiveActivator activator;
    private final Rectangle environmentCollision;
    private boolean collidesSelf = false;
    private boolean collidesMobs = true;
    private boolean collidesPlayers = true;
    private boolean collidesFriends = false;
    private boolean collidesWithEnvironment = true;
    private float strenght, knockback;
    private byte weaponType = -1;
    private byte attackType = -1;
    private boolean active, activated, halfEnvironmentalCollision;
    private ArrayList<GameObject> exceptions;
    private Object actionModifier;

    private Interactive(GameObject owner, InteractiveActivator activator, InteractiveCollision collision, InteractiveAction action, byte weaponType, byte
            attackType, float strenght, float knockback) {
        this.owner = owner;
        this.activator = activator;
        this.collision = collision;
        this.action = action;
        this.weaponType = weaponType;
        this.attackType = attackType;
        this.strenght = strenght;
        this.knockback = knockback;
        this.environmentCollision = Rectangle.createTileRectangle(0, 0);
        this.environmentCollision.setOwner(owner);
    }

    public static Interactive create(GameObject owner, InteractiveActivator activator, InteractiveCollision collision, InteractiveAction action,
                                     byte weaponType, byte attackType, float strenght, float knockback, boolean... collides) {
        Interactive interactive = new Interactive(owner, activator, collision, action, weaponType, attackType, strenght, knockback);
        setCollides(collides, interactive);
        return interactive;
    }

    public static Interactive createNotWeapon(GameObject owner, InteractiveActivator activator, InteractiveCollision collision, InteractiveAction action,
                                              byte attackType, float strenght, float knockback, boolean... collides) {
        Interactive interactive = new Interactive(owner, activator, collision, action, (byte) -1, attackType, strenght, knockback);
        setCollides(collides, interactive);
        return interactive;
    }

    public static Interactive createSpawner(GameObject owner, InteractiveActivator activator, InteractiveAction action, byte weaponType, byte attackType,
                                            boolean... collides) {
        Interactive interactive = new Interactive(owner, activator, null, action, weaponType, attackType, 0, 0);
        setCollides(collides, interactive);

        return interactive;
    }

    private static void setCollides(boolean[] collides, Interactive interactive) {
        if (collides.length > 0) {
            for (int i = 0; i < collides.length; i++) {
                switch (i) {
                    case PLAYERS:
                        interactive.setCollidesPlayers(collides[i]);
                        break;
                    case ENVIRONMENT:
                        interactive.setCollidesWithEnvironment(collides[i]);
                        break;
                    case MOBS:
                        interactive.setCollidesMobs(collides[i]);
                        break;
                    case FRIENDS:
                        interactive.setCollidesFriends(collides[i]);
                        break;
                    case SELF:
                        interactive.setCollidesSelf(collides[i]);
                        break;
                }
            }
        }
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

    public void setActionModifier(Object modifier) {
        this.actionModifier = modifier;
    }

    public void actIfActivated(GameObject[] players, List<Mob> mobs) {
        activated = false;
        if (owner != null) {
            if (collisionActivates()) {
                if (collidesWithEnvironment) {
                    collision.setEnvironmentCollision(environmentCollision, owner, halfEnvironmentalCollision);
                    if (owner.getMap() != null) {
                        Area area = owner.getMap().getArea(owner.getArea());
                        for (Block block : area.getNearBlocks()) {
                            if (block.isSolid() && block.isCollide(0, 0, environmentCollision)) {
                                owner.getHurt(3, 1, block);
                                return;
                            }
                        }
                        for (GameObject object : area.getNearSolidObjects()) {
                            if (environmentCollision.checkCollision(0, 0, object)) {
                                owner.getHurt(3, 1, object);
                                return;
                            }
                        }
                    }
                }
                if (collidesMobs) {
                    for (Mob mob : mobs) {
                        if (!isException(mob) && (collidesSelf || mob != owner) && (collidesFriends
                                || mob.getClass().getName() != owner.getClass().getName())) {
                            InteractiveResponse response = collision.collide(owner, mob, attackType);
                            if (response.getPixels() > 0) {
                                activated = true;
                                action.act(mob, this, response, actionModifier);
                            }
                        }
                    }
                }
                if (collidesPlayers) {
                    for (GameObject player : players) {
                        if (((Player) player).isInGame() && !isException(player) && (collidesSelf || (player != owner))) {
                            InteractiveResponse response = collision.collide(owner, (Player) player, attackType);
                            if (response.getPixels() > 0) {
                                activated = true;
                                action.act(player, this, response, actionModifier);
                            }
                        }
                    }
                }
            } else {
                activated = true;
                action.act(owner, this, InteractiveResponse.NO_RESPONSE, actionModifier);
            }
            actionModifier = null;
        }
    }

    public boolean wouldCollide(GameObject object) {
        collision.updatePosition(owner);
        if (object instanceof Player) {
            if (collidesPlayers) {
                if (((Player) object).isInGame() && !isException(object) && (collidesSelf || (object != owner))) {
                    InteractiveResponse response = collision.collide(owner, (Player) object, attackType);
                    if (response.getPixels() > 0) {
                        return true;
                    }
                }
            }
        } else if (collidesMobs) {
            if (!isException(object) && (collidesSelf || object != owner) && (collidesFriends || object.getClass().getName() != owner.getClass().getName())) {
                InteractiveResponse response = collision.collide(owner, object, attackType);
                if (response.getPixels() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isException(GameObject object) {
        return exceptions != null && exceptions.stream().anyMatch((go) -> (object == go));
    }

    public void update() {
        active = activator.checkActivation(owner);
        if (active) {
            if (collision != null) {
                if (!(collision instanceof LineInteractiveCollision) || !halfEnvironmentalCollision || !collidesWithEnvironment) {
                    collision.updatePosition(owner);
                }
            }
            activator.setActivated(false);
        }
    }

    public void render() {
        if (active && collision != null) {
            collision.render(owner);
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

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public float getStrenght() {
        return strenght;
    }

    public void setStrenght(float strenght) {
        this.strenght = strenght;
    }

    public float getKnockback() {
        return knockback;
    }

    public void setKnockback(float knockback) {
        this.knockback = knockback;
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
        Weapon ret = getOwnersWeapon();
        return ret != null ? ret.getModifier() : 1;
    }

    public float getWeaponKnockback() {
        Weapon ret = getOwnersWeapon();
        return ret != null ? ret.getKnockback() : 1;
    }

    public Weapon getOwnersWeapon() {
        if (owner instanceof Player) {
            Weapon weapon = ((Player) owner).getWeapon();
            if (weapon != null) {
                if (weapon.getType() == weaponType) {
                    return weapon;
                }
            }
        }
        if (owner instanceof Arrow) {
            GameObject ow = ((Arrow) owner).getOwner();
            if (ow instanceof Player) {
                Weapon weapon = ((Player) ow).getWeapon();
                if (weapon != null) {
                    if (weapon.getType() == weaponType) {
                        return weapon;
                    }
                }
            }
        }
        return null;
    }

    public boolean isCollidesSelf() {
        return collidesSelf;
    }

    public void setCollidesSelf(boolean collidesSelf) {
        this.collidesSelf = collidesSelf;
    }

    public boolean isCollidesMobs() {
        return collidesMobs;
    }

    public void setCollidesMobs(boolean collidesMobs) {
        this.collidesMobs = collidesMobs;
    }

    public boolean isCollidesPlayers() {
        return collidesPlayers;
    }

    public void setCollidesPlayers(boolean collidesPlayers) {
        this.collidesPlayers = collidesPlayers;
    }

    public boolean isCollidesFriends() {
        return collidesFriends;
    }

    public void setCollidesFriends(boolean collidesFriends) {
        this.collidesFriends = collidesFriends;
    }


    public boolean isCollidesWithEnvironment() {
        return collidesWithEnvironment;
    }

    public void setCollidesWithEnvironment(boolean collidesWithEnvironment) {
        this.collidesWithEnvironment = collidesWithEnvironment;
    }

    public boolean isHalfEnvironmentalCollision() {
        return halfEnvironmentalCollision;
    }

    public void setHalfEnvironmentalCollision(boolean halfEnvironmentalCollision) {
        this.halfEnvironmentalCollision = halfEnvironmentalCollision;
    }
}
