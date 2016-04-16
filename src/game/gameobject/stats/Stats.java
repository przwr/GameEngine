package game.gameobject.stats;

import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.interactive.InteractiveResponse;
import game.place.Place;
import gamecontent.effects.DamageNumber;
import net.jodk.lang.FastMath;

import static game.gameobject.interactive.InteractiveResponse.*;
import sounds.Sound;
import sounds.Sound3D;

/**
 * Created by przemek on 10.08.15.
 */
public class Stats {

    protected Entity owner;
    protected int health = 100;
    protected int maxHealth = 100;
    protected int strength = 1;
    protected int defence = 1;
    protected int weight = 1;
    protected int hurt = 0;
    protected float sideDefenceModifier = 1;
    protected float backDefenceModifier = 1;
    protected float protection = 1;
    protected float protectionSideModifier = 1;
    protected float protectionBackModifier = 1;
    protected boolean protectionState;
    protected boolean unhurtableState;  //stan kiedy nie ma reakcji na ból
    
    Sound3D deadSound;

    public void initializeSounds() {
        if (deadSound == null) {
            deadSound = Settings.sounds.get3DSoundEffect("ded.ogg", owner);
            deadSound.setSoundRanges(0.8f, 1.1f);
            deadSound.setRandomized(0.1f);
        }
    }

    public Stats(Entity owner) {
        this.owner = owner;
        initializeSounds();
    }

    public void decreaseHealth(InteractiveResponse response) {
        if (health > 0 && owner.getKnockBack().isOver() && !isInvicibleState()) {
            hurt = 0;
            switch (response.getDirection()) {
                case FRONT:
                    hurt = Math.round(response.getPixels() / (defence * (protectionState ? protection : 1)));
                    break;
                case BACK:
                    hurt = Math.round(response.getPixels() / (defence * (protectionState ? protection * protectionBackModifier :
                            backDefenceModifier)));
                    break;
                case SIDE:
                    hurt = Math.round(response.getPixels() / (defence * (protectionState ? protection * protectionSideModifier :
                            sideDefenceModifier)));
                    break;
            }
            if (protectionState) {
                reactionWhileProtect(response);
            }
            health -= hurt;
            if (health < 0) {
                health = 0;
            }
            System.out.println(owner.getName() + " dostał za " + hurt + " Życie: " + health + "/" + maxHealth);
            DamageNumber damage = new DamageNumber(hurt, maxHealth, owner.getX(), owner.getY(),
                    Place.tileSize, owner.getMap().place);
            owner.getMap().addObject(damage);
            if (health == 0) {
                died(response.getAttacker());
            } else if (hurt != 0) {
                hurtReaction(response);
                //response.getAttacker().updateCausedDamage(owner, hurt);
            }
        }
    }

    public void reactionWhileProtect(InteractiveResponse response) {
    }

    public void died(GameObject attacker) {
        deadSound.play();
        owner.delete();
        System.out.println(owner.getName() + " zginał.");
    }

    public void hurtReaction(InteractiveResponse response) {
        double hurtPower = response.getKnockBack() * FastMath.logQuick(20 * ((float) (100 - weight) / 100) + 1);
        owner.getHurt((int) hurtPower, hurtPower / 6, response.getAttacker());
        response.getAttacker().reactToAttack(response.getAttackType(), owner, hurt);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setStartHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public boolean isProtectionState() {
        return protectionState;
    }

    public void setProtectionState(boolean protectionState) {
        this.protectionState = protectionState;
    }

    public boolean isUnhurtableState() {
        return unhurtableState;
    }

    public void setUnhurtableState(boolean unhurtableState) {
        this.unhurtableState = unhurtableState;
    }

    public boolean isInvicibleState() {
        return owner.isInvicibleState();
    }

    public float getSideDefenceModifier() {
        return sideDefenceModifier;
    }

    public void setSideDefenceModifier(float sideDefenceModifier) {
        this.sideDefenceModifier = sideDefenceModifier;
    }

    public float getBackDefenceModifier() {
        return backDefenceModifier;
    }

    public void setBackDefenceModifier(float backDefenceModifier) {
        this.backDefenceModifier = backDefenceModifier;
    }

    public float getProtection() {
        return protection;
    }

    public void setProtection(float protection) {
        this.protection = protection;
    }

    public float getProtectionSideModifier() {
        return protectionSideModifier;
    }

    public void setProtectionSideModifier(float protectionSideModifier) {
        this.protectionSideModifier = protectionSideModifier;
    }

    public float getProtectionBackModifier() {
        return protectionBackModifier;
    }

    public void setProtectionBackModifier(float protectionBackModifier) {
        this.protectionBackModifier = protectionBackModifier;
    }
}
