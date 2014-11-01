/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.place.cameras.Camera;
import game.place.Place;
import engine.Time;

/**
 *
 * @author przemek
 */
public abstract class Entity extends GameObject {

    protected double speed;
    public double defaultSpeed;

    public void canMove(int magX, int magY) {
        /*if (magX != 0 && magY != 0) {
         canMove(magX, 0);
         canMove(0, magY);
         } else {
         int sp = (int) (this.getSpeed() * Time.getDelta());
         for (int i = 0; i < sp; i++) {
         if (!isColided(magX, magY)) {
         move(magX, magY);
         } else {
         break;
         }
         }
         }*/
        int sp = (int) (speed * Time.getDelta());
        for (int i = 0; i < sp; i++) {
            if (magY != 0 && !isColided(0, magY)) {
                move(0, magY);
            }
            if (magX != 0 && !isColided(magX, 0)) {
                move(magX, 0);
            }
        }
    }

    protected abstract boolean isColided(int magX, int magY);

    protected abstract void move(int xPos, int yPos);

    protected abstract void setPosition(int xPos, int yPos);

    protected abstract void renderName(Place place, Camera cam);

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
