/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.place.Camera;
import game.place.Place;
import openGLEngine.Time;

/**
 *
 * @author przemek
 */
public abstract class Entity extends GameObject {

    protected int speed;
    protected String name;

    public void canMove(int magX, int magY) {
        if (magX != 0 && magY != 0) {
            canMove(magX, 0);
            canMove(0, magY);
        } else {
            for (int i = 0; i < this.speed; i++) {
                int xPos = (int) (magX * Time.getDelta());
                int yPos = (int) (magY * Time.getDelta());
                if (!isColided(magX, magY)) {
                    move(xPos, yPos);
                }
            }
        }
    }

    protected abstract boolean isColided(int magX, int magY);

    protected abstract void move(int xPos, int yPos);

    protected abstract void renderName(Place place, Camera cam);
}
