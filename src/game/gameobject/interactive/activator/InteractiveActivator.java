package game.gameobject.interactive.activator;

import game.gameobject.GameObject;

/**
 * Created by przemek on 03.08.15.
 */
public interface InteractiveActivator {

    void setActivated(boolean active);
    
    boolean checkActivation(GameObject owner);

}
