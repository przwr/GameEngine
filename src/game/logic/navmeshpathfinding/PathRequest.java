/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.navmeshpathfinding;

import game.gameobject.entities.Entity;

/**
 * @author przemek
 */
public class PathRequest {

    Entity requester;
    int xDest;
    int yDest;

    public PathRequest() {
    }

    public PathRequest(Entity requester, int xDest, int yDest) {
        this.requester = requester;
        this.xDest = xDest;
        this.yDest = yDest;
    }
}
