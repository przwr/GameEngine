/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import game.gameobject.GameObject;
import game.Settings;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class PlayersCamera extends Camera {

    private initializeCamera[] inits;

    public PlayersCamera(GameObject firstOwner, int xSplit, int ySplit, int ownersCount) {
        super(firstOwner);
        inits = new initializeCamera[3];
        inits[0] = () -> {
            if (Settings.horizontalSplitScreen) {
                if (ownersCount == 0) {
                    yDown = 2;
                } else {
                    yUp = 2;
                }
            } else {
                if (ownersCount == 0) {
                    xRight = 2;
                } else {
                    xLeft = 2;
                }
            }
        };
        initsRest(ownersCount);
        initialize(xSplit, ySplit);
    }

    public PlayersCamera(GameObject firstOwner, GameObject secondOwner) {
        super(firstOwner);
        owners.add(secondOwner);
        widthHalf = Display.getWidth() / 2;
        heightHalf = Display.getHeight() / 2;
        update();
    }

    public PlayersCamera(GameObject firstOwner, GameObject secondOwner, GameObject thirdOwner) {
        super(firstOwner);
        owners.add(secondOwner);
        owners.add(thirdOwner);
        widthHalf = Display.getWidth() / 2;
        heightHalf = Display.getHeight() / 2;
        update();
    }

    public PlayersCamera(GameObject firstOwner, GameObject secondOwner, GameObject thirdOwner, GameObject fourthOwner) {
        super(firstOwner);
        owners.add(secondOwner);
        owners.add(thirdOwner);
        owners.add(fourthOwner);
        widthHalf = Display.getWidth() / 2;
        heightHalf = Display.getHeight() / 2;
        update();
    }

    private void initialize(int ssX, int ssY) {
        yUp = yDown = xLeft = xRight = 0;
        if (Settings.playersCount > 1) {
            inits[Settings.playersCount - 2].initialize();
        }
        widthHalf = Display.getWidth() / ssX;
        heightHalf = Display.getHeight() / ssY;
        update();
    }

    public void reInitialize(int ssX, int ssY) {
        yUp = yDown = xLeft = xRight = 0;
        if (Settings.playersCount > 1) {
            inits[Settings.playersCount - 2].initialize();
        }
        widthHalf = Display.getWidth() / ssX;
        heightHalf = Display.getHeight() / ssY;
        update();
    }

    private void initsRest(final int ownersCount) {
        inits[1] = () -> {
            if (Settings.horizontalSplitScreen) {
                if (ownersCount == 0) {
                    yDown = 2;
                } else if (ownersCount == 1) {
                    xRight = yUp = 2;
                } else {
                    xLeft = yUp = 2;
                }
            } else {
                if (ownersCount == 0) {
                    xRight = 2;
                } else if (ownersCount == 1) {
                    xLeft = yDown = 2;
                } else {
                    xLeft = yUp = 2;
                }
            }
        };
        inits[2] = () -> {
            if (Settings.horizontalSplitScreen) {
                if (ownersCount == 0) {
                    xRight = 2;
                    yDown = 2;
                } else if (ownersCount == 1) {
                    xLeft = 2;
                    yDown = 2;
                } else if (ownersCount == 2) {
                    xRight = 2;
                    yUp = 2;
                } else {
                    xLeft = 2;
                    yUp = 2;
                }
            }
        };
    }

    private interface initializeCamera {

        void initialize();
    }
}
