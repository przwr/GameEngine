package game.logic.navmeshpathfinding;

import engine.utilities.ErrorHandler;
import game.gameobject.entities.Entity;

import static game.logic.navmeshpathfinding.PathData.PATH_REQUESTED;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class PathFindingModule implements Runnable {

    public static final PathStrategy GET_CLOSE = new GetClosePathStrategy();
    public static final PathStrategy WONDER_AROUND = new WonderAroundPathStrategy();
    public static final PathStrategy GET_TO = new GetToPathStrategy();
    private static final PathRequestContainer requestedPaths = new PathRequestContainer(512);
    private static boolean run;
    private static boolean cleaning;
    private static PathRequest request;
    private static int actualPath = 0;

    private synchronized static void returnPath(PathRequest request) {
        Entity requesterCopy = request.requester;
        if (requesterCopy != null) {
            PathStrategyCore.findPath(requesterCopy, requesterCopy.getPathData(), request.xDest, request.yDest);
        }
    }

    public synchronized static void requestPath(Entity requester, int xDest, int yDest) {
        if (requestedPaths.isSufficientCapacity()) {
            requestedPaths.add(requester, xDest, yDest);
            requester.getPathData().flags.set(PATH_REQUESTED);
        } else {
            cleaning = true;
        }
    }

    public static void stop() {
        requestedPaths.clear();
        run = false;
    }

    @Override
    public void run() {
        run = true;
        while (run) {
            try {
                findAndReturnRequestedPaths();
            } catch (Exception exception) {
                ErrorHandler.swallowLogAndPrint(exception);
            }
        }
    }

    private void findAndReturnRequestedPaths() {
        if (cleaning) {
            clean();
        } else {
            findPaths();
        }
    }

    private void clean() {
        for (int j = 0; j < requestedPaths.size() - 1; j++) {
            if (requestedPaths.get(j).requester == null) {
                int i = j + 1;
                for (; i < requestedPaths.size(); i++) {
                    request = requestedPaths.get(i);
                    if (request.requester != null) {
                        requestedPaths.set(j, request);
                        break;
                    }
                }
                if (i == requestedPaths.size()) {
                    requestedPaths.setSize(j);
                    break;
                }
            }
        }
        actualPath = 0;
        cleaning = false;
    }

    private void findPaths() {
        request = requestedPaths.get(actualPath);
        if (request.requester != null) {
            returnPath(request);
            request.requester = null;
        }
        incrementPath();
    }

    private void incrementPath() {
        actualPath++;
        if (actualPath >= requestedPaths.size()) {
            actualPath = 0;
        }
    }
}
