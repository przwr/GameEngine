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
public class FourPlayersCamera extends Camera {

    private final GameObject go2;
    private final GameObject go3;
    private final GameObject go4;

    public FourPlayersCamera(Place place, GameObject go, GameObject go2, GameObject go3, GameObject go4) {
        super(place, go);
        this.go2 = go2;
        this.go3 = go3;
        this.go4 = go4;
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();
    }

    @Override
    public final int getMidX() {
        return ((getGo().getMidX() + go2.getMidX() + go3.getMidX() + go4.getMidX()) / 4);
    }

    @Override
    public final int getMidY() {
        return ((getGo().getMidY() + go2.getMidY() + go3.getMidY() + go4.getMidY()) / 4);
    }
}
