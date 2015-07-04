package navmeshpathfinding;

import java.util.ArrayList;
import java.util.List;

import engine.Methods;
import game.gameobject.GameObject;

public class PathFindingModule implements Runnable {

    public static final PathStrategy GET_CLOSE = new GetClosePathStrategy();
    public static boolean run;
    private List<GameObject> requestedPaths = new ArrayList<>();

    @Override
    public void run() {
        run = true;
        while (run) {
            try {
                findAndReturnRequestedPaths();
            } catch (Exception exception) {
                Methods.swallowLogAndPrint(exception);
            }
        }
    }

    private void findAndReturnRequestedPaths() {

    }

    private void returnPath() {

    }

    public void requestPath(GameObject reqester) {
        requestedPaths.add(reqester);
    }

}
