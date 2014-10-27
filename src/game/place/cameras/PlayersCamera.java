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
        if (place.isResize) {
            Dwidth = (Display.getWidth()) / ssX;
            Dheight = Display.getHeight() / ssY;
        } else {
            Dwidth = Display.getWidth() / ssX;
            Dheight = Display.getHeight() / ssY;
        }
        xOffset = Dwidth - go.getMidX();
        yOffset = Dheight - go.getMidY();
        if (go.getMidX() <= Dwidth - xLeft) {
            xOffset = xLeft;
        }
        if (go.getMidX() >= place.width - Dwidth + xRight) {
            xOffset = -place.width + 2 * Dwidth - xRight;
        }
        if (go.getMidY() <= Dheight - yUp) {
            yOffset = yUp;
        }
        if (go.getMidY() >= place.height - Dheight + yDown) {
            yOffset = -place.height + 2 * Dheight - yDown;
        }
        delaylenght = 50;
        shakeDelay = new Delay(delaylenght);
        shakeDelay.restart();
    }

    @Override
    public synchronized void move(double xPos, double yPos) {
        xOffset = Methods.Interval(-place.width + 2 * Dwidth - xRight, xOffset - xPos, xLeft);
        yOffset = Methods.Interval(-place.height + 2 * Dheight - yDown, yOffset - yPos, yUp);
    }

    @Override
    public synchronized void setPosition(int xPos, int yPos) {
        xOffset = Methods.Interval(-place.width + 2 * Dwidth - xRight, xPos, xLeft);
        yOffset = Methods.Interval(-place.height + 2 * Dheight - yDown, yPos, yUp);
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
