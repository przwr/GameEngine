package navmeshpathfinding;

import engine.Methods;
import game.gameobject.Entity;
import static navmeshpathfinding.PathData.PATH_REQUESTED;

public class PathFindingModule implements Runnable {

    public static final PathStrategy GET_CLOSE = new GetClosePathStrategy();
    public static boolean run;
    private static PathRequestContener requestedPaths = new PathRequestContener(16);
    protected static int actualPath = 0;

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
        PathRequest request = requestedPaths.get(actualPath);
        if (request.requester != null) {
            returnPath(request);
            requestedPaths.remove(actualPath);
        }
        incrementPath();
    }

    private void incrementPath() {
        actualPath++;
        if (actualPath > requestedPaths.size()) {
            actualPath = 0;
        }
    }

    private synchronized static void returnPath(PathRequest request) {
        PathStrategyCore.setPath(request.requester, request.requester.getPathData(), request.xDest, request.yDest);
        request.requester.getPathData().flags.clear(PATH_REQUESTED);
    }

    public synchronized static void requestPath(Entity requester, int xDest, int yDest) {
        requester.getPathData().flags.set(PATH_REQUESTED);
        requestedPaths.add(requester, xDest, yDest);
    }

    public static void stop() {
        run = false;
    }
}
