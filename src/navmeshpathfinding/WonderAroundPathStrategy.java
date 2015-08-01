/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Methods;
import game.gameobject.Entity;

import static navmeshpathfinding.PathData.OBSTACLE_BETWEEN;

/**
 * @author przemek
 */
public class WonderAroundPathStrategy implements PathStrategy {

    @Override
    public void findPath(Entity requester, PathData data, int xDest, int yDest) {
        data.update(requester, xDest, yDest);
        if (data.flags.get(OBSTACLE_BETWEEN) || Methods.pointDistance(data.x, data.y, xDest, yDest) >= data.scope) {
            PathStrategyCore.followPath(requester, data, xDest, yDest);
        }
    }
}
