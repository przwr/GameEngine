package game.gameobject.temporalmodifiers;

import engine.view.SplitScreen;
import game.Settings;
import game.gameobject.entities.Entity;

/**
 * Created by przemek on 03.02.16.
 */
public class CameraJoiner extends TemporalChanger {


    public CameraJoiner(int time) {
        super();
        this.time = time;
    }

    @Override
    public void modifyEffect(Entity entity) {
        if (!Settings.joinSplitScreen) {
            if (SplitScreen.isClose(entity.getPlace())) {
                Settings.joinSplitScreen = true;
            }
        } else {
            stop();
        }
    }
}
