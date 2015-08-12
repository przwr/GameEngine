package game.gameobject;

/**
 * Created by przemek on 10.08.15.
 */
public abstract class Stats {

    protected GameObject owner;
    protected int health = 100;
    protected int maxHealth = 100;
    protected int strength = 2;


    public Stats(GameObject owner) {
        this.owner = owner;
    }

    public void decreaseHealth(int value) {
        health -= value;
        if (health <= 0) {
            owner.delete();
            System.out.println(owner.getName() + " zginał.");
        }
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
}
