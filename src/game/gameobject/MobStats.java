package game.gameobject;

/**
 * Created by przemek on 10.08.15.
 */
public class MobStats extends Stats {


    public MobStats(Mob owner) {
        super(owner);
        this.health = 1000;
        this.maxHealth = 1000;
    }

}
