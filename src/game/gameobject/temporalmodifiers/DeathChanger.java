package game.gameobject.temporalmodifiers;

import collision.OpticProperties;
import engine.utilities.Methods;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.place.cameras.Camera;


/**
 * Created by przemek on 02.02.16.
 */
public class DeathChanger extends TemporalChanger {

    private Entity entity;

    public DeathChanger(int time, Entity entity) {
        super();
        this.entity = entity;
        this.time = time;
    }

    @Override
    public void onStop() {
        if (entity != null) {
            entity.getStats().setHealth(entity.getStats().getMaxHealth() / 2);
            entity.setPosition(entity.getSpawnPosition().getX(), entity.getSpawnPosition().getY());
            if (entity instanceof Player) {
                Camera camera = ((Player) entity).getCamera();
                if (camera != null) {
                    camera.updateStatic();
                }
            }
            entity.getCollision().setCollide(true);
            entity.getCollision().setHitable(true);
            entity.getStats().setUnhurtableState(false);
            entity.getCollision().setOpticProperties(OpticProperties.NO_SHADOW);
            entity.setColorAlpha(1f);
            entity.setAbleToMove(true);
        }
    }

    @Override
    public void modifyEffect(Entity entity) {
        entity.setColorAlpha(Math.max(0.5f - (float) getPercentDone(), 0));
    }
}
