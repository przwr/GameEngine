/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import game.gameobject.GameObject;
import game.place.Place;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class TwoPlayersCamera extends Camera {

    private final GameObject go2;

    public TwoPlayersCamera(Place place, GameObject go, GameObject go2) {
        super(place, go);
        this.go2 = go2;
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    @Override
    public synchronized void shake() {
        if (shakeDelay.isOver()) {
            if (shakeUp) {
                xEffect += shakeAmp;
                yEffect += shakeAmp / 2;
                shakeUp = false;
            } else {
                xEffect -= shakeAmp;
                yEffect -= shakeAmp / 2;
                shakeUp = true;
            }
            shakeDelay.restart();
        }
    }

    @Override
    public int getMidX() {
        return (getGo().getX() + go2.getX()) / 2;
    }

    @Override
    public int getMidY() {
        return (getGo().getY() + go2.getY()) / 2;
    }
}
