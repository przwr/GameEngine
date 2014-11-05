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
public class FourPlayersCamera extends Camera {

    private final GameObject go2;
    private final GameObject go3;
    private final GameObject go4;

    public FourPlayersCamera(Place place, GameObject go, GameObject go2, GameObject go3, GameObject go4) {
        this.place = place;
        this.go = go;
        this.go2 = go2;
        this.go3 = go3;
        this.go4 = go4;
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        xOffset = Methods.Interval(-place.width + 2 * Dwidth, Dwidth - ((getGo().getX() + go2.getX() + go3.getX() + go4.getX()) / 4), 0);
        yOffset = Methods.Interval(-place.height + 2 * Dheight, Dheight - ((getGo().getY() + go2.getY() + go3.getY() + go4.getY()) / 4), 0);
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.restart();
    }

    @Override
    public synchronized void update() {
        xOffset = Methods.Interval(-place.width + 2 * getDwidth(), getDwidth() - ((getGo().getX() + go2.getX() + go3.getX() + go4.getX()) / 4), 0);
        yOffset = Methods.Interval(-place.height + 2 * getDheight(), getDheight() - ((getGo().getY() + go2.getY() + go3.getY() + go4.getY()) / 4), 0);
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
