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

    public PlayersCamera(final Map map, GameObject owner, int ssX, int ssY, final int num) {
        super(map, owner);
        inits = new initializeCam[3];
        inits[0] = () -> {
            if (map.place.settings.hSplitScreen) {
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
        initsRest(map.place, num);
        initialize(ssX, ssY);
    }

    public PlayersCamera(Map map, GameObject firstOwner, GameObject secondOwner) {
        super(map, firstOwner);
        owners.add(secondOwner);
        width = Display.getWidth();
        height = Display.getHeight();
        widthHalf = width / 2;
        heightHalf = height / 2;
        update();
    }

    public PlayersCamera(Map map, GameObject firstOwenr, GameObject secondOwner, GameObject thirdOwner) {
        super(map, firstOwenr);
        owners.add(secondOwner);
        owners.add(thirdOwner);
        width = Display.getWidth();
        height = Display.getHeight();
        widthHalf = width / 2;
        heightHalf = height / 2;
        update();
    }

    public PlayersCamera(Map map, GameObject firstOwner, GameObject secondOwner, GameObject thirdOwner, GameObject fourthOwner) {
        super(map, firstOwner);
        owners.add(secondOwner);
        owners.add(thirdOwner);
        owners.add(fourthOwner);
        width = Display.getWidth();
        height = Display.getHeight();
        widthHalf = width / 2;
        heightHalf = height / 2;
        update();
    }

    private void initialize(int ssX, int ssY) {
        yUp = yDown = xLeft = xRight = 0;
        if (map.place.settings.nrPlayers > 1) {
            inits[map.place.settings.nrPlayers - 2].initialize();
        }
        widthHalf = Display.getWidth() / ssX;
        heightHalf = Display.getHeight() / ssY;
        update();
    }

    public void reInitialize(int ssX, int ssY) {
        yUp = yDown = xLeft = xRight = 0;
        if (map.place.settings.nrPlayers > 1) {
            inits[map.place.settings.nrPlayers - 2].initialize();
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
