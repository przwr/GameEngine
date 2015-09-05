package game.gameobject;

/**
 * Created by przemek on 10.08.15.
 */
public class PlayerStats extends Stats {

    public PlayerStats(Player owner) {
        super(owner);
        sideDefenceModifier = 0.8f;
        backDefenceModifier = 0.5f;
        protection = 10;
        protectionSideModifier = 0.8f;
        protectionBackModifier = 0.1f;
    }

    public void died() {
        health = maxHealth / 2;
        owner.setPosition(128, 128);
        System.out.println(owner.getName() + " zginał.");
    }

}
