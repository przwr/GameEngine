/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmeshpathfinding;

import game.gameobject.Entity;

/**
 *
 * @author przemek
 */
public class PathRequest {

    protected Entity requester;
    protected int xDest, yDest;

    public PathRequest(){        
    }
    
    public PathRequest(Entity requester, int xDest, int yDest) {
        this.requester = requester;
        this.xDest = xDest;
        this.yDest = yDest;
    }
}
