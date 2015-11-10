/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import game.Settings;
import game.gameobject.GameObject;
import gamecontent.MyPlayer;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class PlayersCamera extends Camera {

    private static final initializer[] inits = new initializer[3];

    {
        inits[0] = (int ownersCount) -> {
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
        inits[1] = (int ownersCount) -> {
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
        inits[2] = (int ownersCount) -> {
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

    public PlayersCamera(GameObject firstOwner, int xSplit, int ySplit, int ownersCount) {
        super(firstOwner);
        initialize(xSplit, ySplit, ownersCount);
    }

    public PlayersCamera(GameObject firstOwner, GameObject secondOwner) {
        super(firstOwner);
        owners.add(secondOwner);
        widthHalf = Display.getWidth() / 2;
        heightHalf = Display.getHeight() / 2;
        setScale(0, 0, 0);
        updateStatic();
    }

    public PlayersCamera(GameObject firstOwner, GameObject secondOwner, GameObject thirdOwner) {
        super(firstOwner);
        owners.add(secondOwner);
        owners.add(thirdOwner);
        widthHalf = Display.getWidth() / 2;
        heightHalf = Display.getHeight() / 2;
        setScale(0, 0, 0);
        updateStatic();
    }

    public PlayersCamera(GameObject firstOwner, GameObject secondOwner, GameObject thirdOwner, GameObject fourthOwner) {
        super(firstOwner);
        owners.add(secondOwner);
        owners.add(thirdOwner);
        owners.add(fourthOwner);
        widthHalf = Display.getWidth() / 2;
        heightHalf = Display.getHeight() / 2;
        setScale(0, 0, 0);
        updateStatic();
    }

    private void initialize(int ssX, int ssY, int ownersCount) {
        this.ownersCount = ownersCount;
        yUp = yDown = xLeft = xRight = 0;
        if (Settings.playersCount > 1) {
            inits[Settings.playersCount - 2].initialize(ownersCount);
        }
        widthHalf = Display.getWidth() / ssX;
        heightHalf = Display.getHeight() / ssY;
        setScale(ssX, ssY, ownersCount);
        updateStatic();
    }

    public void reInitialize(int ssX, int ssY, int ownersCount) {
        this.ownersCount = ownersCount;
        yUp = yDown = xLeft = xRight = 0;
        if (Settings.playersCount > 1) {
            inits[Settings.playersCount - 2].initialize(ownersCount);
        }
        widthHalf = Display.getWidth() / ssX;
        heightHalf = Display.getHeight() / ssY;
        setScale(ssX, ssY, ownersCount);
        updateStatic();
    }

    @Override
    public void preRenderGUI() {
        for (GameObject owner : owners) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            if (owner instanceof MyPlayer)
                ((MyPlayer) owner).preRenderGroundGUI();
        }
    }

    private interface initializer {

        void initialize(int ownersCount);
    }
}
