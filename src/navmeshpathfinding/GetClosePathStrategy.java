/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import engine.Methods;
import game.gameobject.Entity;

/**
 *
 * @author przemek
 */
public class GetClosePathStrategy implements PathStrategy {

    @Override
    public void findPath(Entity requester, PathData data, int xDest, int yDest) {
        data.update(requester, xDest, yDest);
        if (!data.obstacleBeetween && Methods.pointDistance(data.x, data.y, xDest, yDest) < data.scope) {
            data.xSpeed = data.ySpeed = 0;
            requester.brake(2);
        } else {
            PathStrategyCore.followPath(requester, data, xDest, yDest);
        }
    }
}
