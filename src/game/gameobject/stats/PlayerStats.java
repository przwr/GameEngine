package game.gameobject.stats;

import collision.OpticProperties;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.gameobject.interactive.InteractiveResponse;
import game.gameobject.temporalmodifiers.DeathChanger;
import game.gameobject.temporalmodifiers.TemporalChanger;
import game.place.Place;
import gamecontent.MyPlayer;
import gamecontent.effects.DamageNumber;

import static game.gameobject.interactive.InteractiveResponse.*;
import gamecontent.MyController;

/**
 * Created by przemek on 10.08.15.
 */
public class PlayerStats extends Stats {

    private float energy = 100;
    private float maxEnergy = 100;
    private Player player;

    public PlayerStats(Player owner) {
        super(owner);
        this.player = owner;
        strength = 20;
        sideDefenceModifier = 0.8f;
        backDefenceModifier = 0.5f;
        protection = 10;
        protectionSideModifier = 0.8f;
        protectionBackModifier = 0.1f;
    }

    @Override
    public void decreaseHealth(InteractiveResponse response) {
        if (health > 0 && owner.getKnockBack().isOver() && !isInvicibleState()) {
            hurt = 0;
            switch (response.getDirection()) {
                case FRONT:
                    hurt = Math.round(response.getPixels() / (defence * (protectionState ? protection : 1)));
                    break;
                case BACK:
                    hurt = Math.round(response.getPixels() / (defence * (protectionState ? protection * protectionBackModifier
                            : backDefenceModifier)));
                    break;
                case SIDE:
                    hurt = Math.round(response.getPixels() / (defence * (protectionState ? protection * protectionSideModifier
                            : sideDefenceModifier)));
                    break;
            }
            if (protectionState) {
                reactionWhileProtect(response);
            }
            ((MyPlayer) player).getGUI().activateLifeHistory(health);
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
                response.getAttacker().updateCausedDamage(owner, hurt);
            }
        }
    }

    @Override
    public void died(GameObject attacker) {
//        player.getCollision().setCollide(false);
        ((MyController) player.getController()).stopAttack();
        player.getCollision().setHitable(false);
        setUnhurtableState(true);
        player.setUnableToMove(true);
        player.getCollision().setOpticProperties(OpticProperties.NO_SHADOW);
        player.setColorAlpha(0.5f);
        TemporalChanger death = new DeathChanger(100, player);
        player.addChanger(death);
        player.knockBack(20, 8, attacker);
        death.start();
//        ((MyPlayer) player).getGUI().deactivate();
        System.out.println(player.getName() + " zginał.");
    }

    @Override
    public void hurtReaction(InteractiveResponse response) {
        super.hurtReaction(response);
//        if (health <= 900) {
//            System.out.println("____________R.I.P.___________");
//        }
//        ((MyPlayer) player).getGUI().activateLifeIndicator();
    }

    @Override
    public void reactionWhileProtect(InteractiveResponse response) {
        int normalHurt = 0;
        switch (response.getDirection()) {
            case FRONT:
                normalHurt = Math.round(response.getPixels() / defence);
                break;
            case BACK:
                normalHurt = Math.round(response.getPixels() / (defence * backDefenceModifier));
                break;
            case SIDE:
                normalHurt = Math.round(response.getPixels() / (defence * sideDefenceModifier));
                break;
        }
        float energyToLife = 3f;
        decreaseEnergy(energyToLife * normalHurt);
        if (energy < energyToLife * normalHurt) {
            float percent = energy / (energyToLife * normalHurt);
            hurt = Math.round((percent * hurt + (1 - percent) * normalHurt));
            if (hurt > normalHurt) {
                hurt = normalHurt;
            }
        }
    }

    public void decreaseEnergy(float amount) {
        if (amount > maxEnergy * 0.01f) {
            ((MyPlayer) player).getGUI().activateEnergyHistory(energy);
        }
        energy -= amount;
        if (energy <= 0) {
            energy = 0;
        }
    }

    public void increaseEnergy(float amount) {
        if (energy < maxEnergy) {
            energy += amount;
            if (energy >= maxEnergy) {
                energy = maxEnergy;
//                ((MyPlayer) player).getGUI().activateEnergyIndicator();
            }
        }
    }

    public float getEnergy() {
        return energy;
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }
}
