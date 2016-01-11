package game.gameobject.stats;

import game.gameobject.entities.Entity;
import game.gameobject.interactive.InteractiveResponse;

/**
 * Created by przemek on 12.09.15.
 */
public class NPCStats extends Stats {


    public NPCStats(Entity owner) {
        super(owner);
    }

    public void decreaseHealth(InteractiveResponse response) {
        System.out.println(owner.getName() + ": Can't touch this!");
    }
}
