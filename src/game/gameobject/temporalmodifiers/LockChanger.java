package game.gameobject.temporalmodifiers;

import game.gameobject.entities.Entity;

/**
 * Created by przemek on 02.02.16.
 */
public class LockChanger extends TemporalChanger {

    private Entity entity;

    public LockChanger(int time, Entity entity) {
        super();
        this.entity = entity;
        this.time = time;
    }

    @Override
    public void onStop() {
        if (entity != null) {
            entity.setAbleToMove(true);
            entity.setVisible(true);
        }
    }

    @Override
    public void modifyEffect(Entity entity) {
        if (getPercentDone() > 0.5) {
            entity.setVisible(true);
        }
    }
}
