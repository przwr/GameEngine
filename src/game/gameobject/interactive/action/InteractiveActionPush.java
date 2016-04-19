package game.gameobject.interactive.action;

import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.InteractiveResponse;
import net.jodk.lang.FastMath;

/**
 * Created by przemek on 11.11.15.
 */
public class InteractiveActionPush extends InteractiveAction {

    @Override
    public void act(GameObject object, Interactive activator, InteractiveResponse response, Object modifier) {
        if (object instanceof Entity && activator.getOwner() instanceof Entity) {
            Entity owner = (Entity) activator.getOwner();
            if (object.getFloatHeight() == 0) {
                double speed = FastMath.sqrt(owner.getKnockBack().getXSpeed() * owner.getKnockBack().getXSpeed() + owner.getKnockBack().getYSpeed() * owner
                        .getKnockBack().getYSpeed());
                double hurtPower = 0.5 * FastMath.logQuick(owner.getStats().getWeight() * speed * ((float) (100 - object.getStats().getWeight()) / 100) + 1);
                object.getHurt((int) hurtPower, hurtPower / 3, owner);
            }
        }
    }
}