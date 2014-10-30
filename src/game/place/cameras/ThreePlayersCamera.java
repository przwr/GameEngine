/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import engine.Delay;
import game.Methods;
import game.gameobject.GameObject;
import game.place.Place;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class ThreePlayersCamera extends Camera {

    private final GameObject go2;
    private final GameObject go3;

    public ThreePlayersCamera(Place place, GameObject go, GameObject go2, GameObject go3) {
        this.place = place;
        this.go = go;
        this.go2 = go2;
        this.go3 = go3;
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        xOffset = Methods.Interval(-place.width + 2 * Dwidth, Dwidth - ((getGo().getMidX() + go2.getMidX() + go3.getMidX()) / 3), 0);
        yOffset = Methods.Interval(-place.height + 2 * Dheight, Dheight - ((getGo().getMidY() + go2.getMidY() + go3.getMidY()) / 3), 0);
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.restart();
    }

    @Override
    public synchronized void update() {
        xOffset = Methods.Interval(-place.width + 2 * Dwidth, Dwidth - ((getGo().getMidX() + go2.getMidX() + go3.getMidX()) / 3), 0);
        yOffset = Methods.Interval(-place.height + 2 * Dheight, Dheight - ((getGo().getMidY() + go2.getMidY() + go3.getMidY()) / 3), 0);
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
}