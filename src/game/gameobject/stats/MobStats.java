package game.gameobject.stats;

import game.gameobject.entities.Agro;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.InteractiveResponse;

/**
 * Created by przemek on 10.08.15.
 */
public class MobStats extends Stats {

    private float agroModifier = 0.5f;

    public MobStats(Mob owner) {
        super(owner);
    }

    @Override
    public void hurtReaction(InteractiveResponse response) {
        super.hurtReaction(response);
        Mob own = (Mob) owner;
        Agro agro = own.getAgresor(response.getAttacker());
        if (agro != null) {
            agro.addValue(hurt);
        } else {
            agro = new Agro(response.getAttacker(), hurt);
            own.getAgro().add(agro);
        }
        own.updateAgro(agro, Math.round(hurt * agroModifier));
    }
}
