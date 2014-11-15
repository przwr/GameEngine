/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.cameras;

import game.gameobject.GameObject;
import game.place.Place;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class PlayersCamera extends Camera {

    public PlayersCamera(Place place, GameObject go, int ssX, int ssY, int num) {
        super(place, go);
        init(ssX, ssY, num);
    }

    public PlayersCamera(Place place, GameObject go, GameObject go2) {
        super(place, go);
        gos.add(go2);
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    public PlayersCamera(Place place, GameObject go, GameObject go2, GameObject go3) {
        super(place, go);
        gos.add(go2);
        gos.add(go3);
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    public PlayersCamera(Place place, GameObject go, GameObject go2, GameObject go3, GameObject go4) {
        super(place, go);
        gos.add(go2);
        gos.add(go3);
        gos.add(go4);
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    public void init(int ssX, int ssY, int num) {
        yUp = yDown = xLeft = xRight = 0;
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
        update();
    }
}
