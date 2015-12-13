package game.gameobject.stats;

import game.gameobject.GameObject;
import game.gameobject.entities.Agro;
import game.gameobject.entities.Mob;
import game.gameobject.interactive.InteractiveResponse;
import game.gameobject.items.Arrow;

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
        GameObject attacker = response.getAttacker();
        //TODO Projectile, nie Arrow
        if (attacker instanceof Arrow) {
            attacker = ((Arrow) attacker).getOwner();
        }
        Agro agro = own.getAgresor(attacker);
        if (agro != null) {
            agro.addHurtsOwner(hurt);
        } else {
            agro = new Agro(attacker, hurt);
            own.getAgro().add(agro);
        }
        own.updateAgro(agro, Math.round(-hurt * agroModifier));
    }
}
