package collision.interactive;

import game.gameobject.GameObject;

import java.util.Arrays;

/**
 * Created by przemek on 03.08.15.
 */
public class InteractiveActivatorFrames implements InteractiveActivator {

    private final int[] activeFrames;

    public InteractiveActivatorFrames(int[] activeFrames) {
        this.activeFrames = activeFrames;
        Arrays.sort(activeFrames);
    }


    @Override
    public boolean checkActivation(GameObject owner) {
        return isActiveFrame(owner.getAppearance().getCurrentFrameIndex());
    }

    private boolean isActiveFrame(int key) {
        return Arrays.binarySearch(activeFrames, key) >= 0;
    }
}
