package game.gameobject.temporalmodifiers;

import game.gameobject.entities.Entity;

/**
 * Created by przemek on 02.02.16.
 */
public class LockChanger extends TemporalChanger {

    private Entity entity;

    public LockChanger(int time) {
        super();
        this.time = time;
    }

    @Override
    public boolean isOver() {
        boolean over = left <= 0;
        if (over) {
            if (entity != null) {
                entity.setUnableToMove(false);
            }
        }
        return over;
    }

    @Override
    public void modifyEffect(Entity entity) {
        this.entity = entity;
    }
}
