/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import Engine.Delay;
import game.gameobject.GameObject;
import game.place.Place;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class PlayersCamera extends Camera {

    public PlayersCamera(Place place, GameObject go, int ssX, int ssY) {
        this.place = place;
        this.go = go;
        Dwidth = Display.getWidth() / ssX;
        Dheight = Display.getHeight() / ssY;
        xOffset = Dwidth - go.getMidX();
        yOffset = Dheight - go.getMidY();
        if (go.getMidX() <= Dwidth) {
            xOffset = 0;
        }
        if (go.getMidX() >= place.width - Dwidth) {
            xOffset = -place.width + 2 * Dwidth;
        }
        if (go.getMidY() <= Dheight) {
            yOffset = 0;
        }
        if (go.getMidY() >= place.height - Dheight) {
            yOffset = -place.height + 2 * Dheight;
        }
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.restart();
    }

    @Override
    public synchronized void move(int xPos, int yPos) {
        xOffset -= xPos;
        yOffset -= yPos;
        if (go.getMidX() <= Dwidth) {
            xOffset = 0;
        }
        if (go.getMidX() >= place.width - Dwidth) {
            xOffset = -place.width + 2 * Dwidth;
        }
        if (go.getMidY() <= Dheight) {
            yOffset = 0;
        }
        if (go.getMidY() >= place.height - Dheight) {
            yOffset = -place.height + 2 * Dheight;
        }
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
