package game.gameobject.interactive.activator;

import game.gameobject.GameObject;

/**
 * Created by przemek on 04.08.15.
 */
public class InteractiveActivatorAlways implements InteractiveActivator {


    @Override
    public boolean checkActivation(GameObject owner) {
        return true;
    }

    @Override
    public void setActivated(boolean active) {}
}
