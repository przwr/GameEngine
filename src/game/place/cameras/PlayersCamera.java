/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import game.gameobject.GameObject;
import game.place.Map;
import game.place.Place;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class PlayersCamera extends Camera {

    private initializeCam[] inits;

    public PlayersCamera(GameObject go, int ssX, int ssY, final int num) {
        super(go);
        inits = new initializeCam[3];
        inits[0] = () -> {
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
        };
        initsRest(place, num);
        initialize(ssX, ssY);
    }

    public PlayersCamera(GameObject go, GameObject go2) {
        super(go);
        gos.add(go2);
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    public PlayersCamera(GameObject go, GameObject go2, GameObject go3) {
        super(go);
        gos.add(go2);
        gos.add(go3);
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    public PlayersCamera(GameObject go, GameObject go2, GameObject go3, GameObject go4) {
        super(go);
        gos.add(go2);
        gos.add(go3);
        gos.add(go4);
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    private void initialize(int ssX, int ssY) {
        yUp = yDown = xLeft = xRight = 0;
        if (place.settings.nrPlayers > 1) {
            inits[place.settings.nrPlayers - 2].initialize();
        }
        widthHalf = Display.getWidth() / ssX;
        heightHalf = Display.getHeight() / ssY;
        update();
    }

    public void reInitialize(int ssX, int ssY) {
        yUp = yDown = xLeft = xRight = 0;
        if (place.settings.nrPlayers > 1) {
            inits[place.settings.nrPlayers - 2].initialize();
        }
        widthHalf = Display.getWidth() / ssX;
        heightHalf = Display.getHeight() / ssY;
        update();
    }

    private void initsRest(final Place place, final int num) {
        inits[1] = () -> {
            if (place.settings.hSplitScreen) {
                if (num == 0) {
                    yDown = 2;
                } else if (num == 1) {
                    xRight = yUp = 2;
                } else {
                    xLeft = yUp = 2;
                }
            } else {
                if (num == 0) {
                    xRight = 2;
                } else if (num == 1) {
                    xLeft = yDown = 2;
                } else {
                    xLeft = yUp = 2;
                }
            }
        };
        inits[2] = () -> {
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
        };
    }

    private interface initializeCam {

        void initialize();
    }
}
