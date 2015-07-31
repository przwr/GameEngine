/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Player;

/**
 *
 * @author przemek
 */
public abstract class InteractiveCollision {

    protected Point position = new Point();

    protected int direction;

    public abstract void updatePosition(int x, int y);

    public abstract int isCollide(GameObject object);

    public abstract int isCollide(Player player);
}
