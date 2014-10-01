/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import openGLEngine.Delay;
import game.gameobject.GameObject;
import game.place.Place;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class CameraFor1 extends Camera {

    public CameraFor1(Place place, GameObject go, int xStart, int yStart) {
        this.place = place;
        this.go = go;
        if (go.getX() - xStart > 0) {
            xOffset = 0;
            if (xStart < 0) {
                go.setX(0);
            } else {
                go.setX(xStart);
            }
        } else if (go.getX() - xStart < -(place.getWidth() - Display.getWidth())) {
            xOffset = -(place.getWidth() - Display.getWidth());
            if (xStart + go.getSX() + go.getWidth() > Display.getWidth()) {
                go.setX(Display.getWidth() - (go.getSX() + go.getWidth()));
            } else {
                go.setX(xStart);
            }
        } else {
            xOffset = go.getX() - xStart;
        }
        if (go.getY() - yStart > 0) {
            yOffset = 0;
            if (yStart < 0) {
                go.setY(0);
            } else {
                go.setY(yStart);
            }
        } else if (go.getY() - yStart < -(place.getHeight() - Display.getHeight())) {
            yOffset = -(place.getHeight() - Display.getHeight());
            if (yStart + go.getSY() + go.getHeight() > Display.getHeight()) {
                go.setY(Display.getHeight() - (go.getSY() + go.getHeight()));
            } else {
                go.setY(yStart);
            }
        } else {
            yOffset = go.getY() - yStart;
        }
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.restart();
    }

    @Override
    public synchronized void move(int xPos, int yPos) {
        int Dwidth = Display.getWidth() / 2;
        int Dheight = Display.getHeight() / 2;
        if (xOffset - xPos > 0 || xOffset - xPos < -(place.getWidth() - Display.getWidth())) {
            if (!((go.getBegOfX() + xPos < 0) || go.getEndOfX() + xPos > Display.getWidth())) {
                go.addX(xPos);
            }
        } else if (go.getMidX() != Dwidth) {
            go.addX(xPos);
        } else {
            setXOff(xOffset - xPos);
        }

        if (yOffset - yPos > 0 || yOffset - yPos < -(place.getHeight() - Display.getHeight())) {
            if (!(go.getBegOfY() + yPos < 0 || go.getEndOfY() + yPos > Display.getHeight())) {
                go.addY(yPos);
            }
        } else if (go.getMidY() != Dheight) {
            go.addY(yPos);
        } else {
            setYOff(yOffset - yPos);
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
