package game.gameobject;

/**
 * Created by przemek on 10.08.15.
 */
public class PlayerStats extends Stats {

    public PlayerStats(Player owner) {
        super(owner);
        this.health = 1000;
    }
}
