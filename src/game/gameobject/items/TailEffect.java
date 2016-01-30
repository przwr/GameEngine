/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.items;

/**
 *
 * @author Wojtek
 */
public class TailEffect {

    private final Joint[] tail;
    private final int length;

    public TailEffect(int length) {
        this.tail = new Joint[length];
        this.length = length;
    }

    public void updatePoint(int x, int y, int height, int direction) {
        Joint j = new Joint(x, y, height, direction);
        Joint tmp;
        for (int i = 0; i < length; i++) {
            if (tail[i] != null) {
                
            }
        }
    }

    private class Joint {

        int x, y, height, direction;

        public Joint(int x, int y, int height, int direction) {
            this.x = x;
            this.y = y;
            this.height = height;
            this.direction = direction;
        }
    }
}
