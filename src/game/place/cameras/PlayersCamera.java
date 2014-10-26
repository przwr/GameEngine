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
public class PlayersCamera extends Camera {

    public PlayersCamera(Place place, GameObject go, int ssX, int ssY, int num) {
        this.place = place;
        this.go = go;
        if (place.settings.nrPlayers > 1) {
            if (place.settings.nrPlayers == 2) {
                if (place.settings.hSplitScreen) {
                    if (num == 0) {
                        yDown = 2;
                    } else {
                        yUp = 2;
                    }
                } else {
                    if (num == 0) {
                        xRight = 2;
                    } else {
                        xLeft = 2;
                    }
                }
            } else if (place.settings.nrPlayers == 3) {
                if (place.settings.hSplitScreen) {
                    if (num == 0) {
                        yDown = 2;
                    } else if (num == 1) {
                        xRight = 2;
                        yUp = 2;
                    } else {
                        xLeft = 2;
                        yUp = 2;

                    }
                } else {
                    if (num == 0) {
                        xRight = 2;
                    } else if (num == 1) {
                        xLeft = 2;
                        yDown = 2;
                    } else {
                        xLeft = 2;
                        yUp = 2;
                    }
                }
            } else if (place.settings.nrPlayers == 4) {
                if (place.settings.hSplitScreen) {
                    if (num == 0) {
                        xRight = 2;
                        yDown = 2;
                    } else if (num == 1) {
                        xLeft = 2;
                        yDown = 2;
                    } else if (num == 2) {
                        xRight = 2;
                        yUp = 2;
                    } else {
                        xLeft = 2;
                        yUp = 2;
                    }
                }
            }
        }
        Dwidth = Display.getWidth() / ssX;
        Dheight = Display.getHeight() / ssY;
        xOffset = Methods.Interval(-place.width + 2 * Dwidth - xRight, Dwidth - go.getMidX(), xLeft);
        yOffset = Methods.Interval(-place.height + 2 * Dheight - yDown, Dheight - go.getMidY(), yUp);
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.restart();
    }

    @Override
    public synchronized void update() {
        xOffset = Methods.Interval(-place.width + 2 * Dwidth - xRight, Dwidth - getGo().getMidX(), xLeft);
        yOffset = Methods.Interval(-place.height + 2 * Dheight - yDown, Dheight - getGo().getMidY(), yUp);
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

    public void reInit(int num) {
        if (place.settings.nrPlayers == 2) {
            if (place.settings.hSplitScreen) {
                if (num == 0) {
                    yDown = 2;
                    xLeft = xRight = yUp = 0;
                } else {
                    yUp = 2;
                    xLeft = xRight = yDown = 0;
                }
            } else {
                if (num == 0) {
                    xRight = 2;
                    xLeft = yUp = yDown = 0;
                } else {
                    xLeft = 2;
                    xRight = yUp = yDown = 0;

                }
            }
        }
    }

}
