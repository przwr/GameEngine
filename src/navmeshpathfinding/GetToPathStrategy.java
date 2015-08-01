/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import game.gameobject.Entity;

import static navmeshpathfinding.PathData.OBSTACLE_BETWEEN;

/**
 * @author przemek
 */
public class GetToPathStrategy implements PathStrategy {

    @Override
    public void findPath(Entity requester, PathData data, int xDest, int yDest) {
        data.update(requester, xDest, yDest);
        if (!data.flags.get(OBSTACLE_BETWEEN) && data.x == xDest && data.y == yDest) {
            data.xSpeed = data.ySpeed = 0;
            requester.brake(2);
        } else {
            PathStrategyCore.followPath(requester, data, xDest, yDest);
        }
    }
}
