package game.gameobject.interactive;

import game.gameobject.GameObject;

/**
 * Created by przemek on 03.08.15.
 */
public interface InteractiveActivator {

    public void setActivated(boolean active);
    
    boolean checkActivation(GameObject owner);

}
