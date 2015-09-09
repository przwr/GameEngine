package game.gameobject;


import collision.interactive.InteractiveResponse;

import static collision.interactive.InteractiveResponse.*;

/**
 * Created by przemek on 10.08.15.
 */
public abstract class Stats {

    protected GameObject owner;
    protected int health = 100;
    protected int maxHealth = 100;
    protected int strength = 2;
    protected int defence = 2;
    protected float sideDefenceModifier = 10;
    protected float backDefenceModifier = 4;
    protected float protection = 40;
    protected float protectionSideModifier = 4;
    protected float protectionBackModifier = 1;
    protected boolean protectionState;


    public Stats(GameObject owner) {
        this.owner = owner;
    }

    public void decreaseHealth(InteractiveResponse response) {
        if (health > 0) {
            int hurt = 0;
            switch (response.getDirection()) {
                case FRONT:
                    hurt = Math.round(response.getData() / (defence * (protectionState ? protection : 1)));
                    break;
                case BACK:
                    hurt = Math.round(response.getData() / (defence * (protectionState ? protection * protectionBackModifier : backDefenceModifier)));
                    break;
                case SIDE:
                    hurt = Math.round(response.getData() / (defence * (protectionState ? protection * protectionSideModifier : sideDefenceModifier)));
                    break;
            }
            health -= hurt;
            if (health < 0) {
                health = 0;
            }
            System.out.println(owner.getName() + " dostał za " + hurt + " Życie: " + health + "/" + maxHealth);
            if (health == 0) {
                died();
            }

        }
    }

    public void died() {
        owner.delete();
        System.out.println(owner.getName() + " zginał.");
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
}
