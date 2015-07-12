/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Methods;
import game.gameobject.Entity;
import static navmeshpathfinding.PathData.OBSTACLE_BEETWEEN;

/**
 *
 * @author przemek
 */
public class GetClosePathStrategy implements PathStrategy {

    private boolean justStopped = false;

    @Override
    public void findPath(Entity requester, PathData data, int xDest, int yDest) {
        data.update(requester, xDest, yDest);
        if (justStopped) {
            if (data.flags.get(OBSTACLE_BEETWEEN) || Methods.pointDistance(data.x, data.y, xDest, yDest) >= data.scope * 1.2) {
                PathStrategyCore.followPath(requester, data, xDest, yDest);
                justStopped = false;
            }
        } else if (!justStopped && (data.flags.get(OBSTACLE_BEETWEEN) || Methods.pointDistance(data.x, data.y, xDest, yDest) >= data.scope)) {
            PathStrategyCore.followPath(requester, data, xDest, yDest);
        } else {
            data.xSpeed = data.ySpeed = 0;
            requester.brake(2);
            justStopped = true;
        }
    }
}
