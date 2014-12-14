/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import game.gameobject.GameObject;
import game.place.AbstractPlace;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class PlayersCamera extends Camera {

    private initCam[] inits;

    public PlayersCamera(final AbstractPlace place, GameObject go, int ssX, int ssY, final int num) {
        super(place, go);
        inits = new initCam[3];
        inits[0] = new initCam() {
            @Override
            public void initCam() {
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
            }
        };
        initsRest(place, num);
        init(ssX, ssY);
    }

    public PlayersCamera(AbstractPlace place, GameObject go, GameObject go2) {
        super(place, go);
        gos.add(go2);
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    public PlayersCamera(AbstractPlace place, GameObject go, GameObject go2, GameObject go3) {
        super(place, go);
        gos.add(go2);
        gos.add(go3);
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    public PlayersCamera(AbstractPlace place, GameObject go, GameObject go2, GameObject go3, GameObject go4) {
        super(place, go);
        gos.add(go2);
        gos.add(go3);
        gos.add(go4);
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    private void init(int ssX, int ssY) {
        yUp = yDown = xLeft = xRight = 0;
        if (place.settings.nrPlayers > 1) {
            inits[place.settings.nrPlayers - 2].initCam();
        }
        Dwidth = Display.getWidth() / ssX;
        Dheight = Display.getHeight() / ssY;
        update();
    }

    public void reInit(int ssX, int ssY) {
        yUp = yDown = xLeft = xRight = 0;
        if (place.settings.nrPlayers > 1) {
            inits[place.settings.nrPlayers - 2].initCam();
        }
        Dwidth = Display.getWidth() / ssX;
        Dheight = Display.getHeight() / ssY;
        update();
    }

    private void initsRest(final AbstractPlace place, final int num) {
        inits[1] = new initCam() {
            @Override
            public void initCam() {
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
            }
        };
        inits[2] = new initCam() {
            @Override
            public void initCam() {
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
        };
    }

    private interface initCam {

        void initCam();
    }
}
