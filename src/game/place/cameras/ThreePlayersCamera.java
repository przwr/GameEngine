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
public class ThreePlayersCamera extends Camera {
    
    private final GameObject go2;
    private final GameObject go3;
    
    public ThreePlayersCamera(Place place, GameObject go, GameObject go2, GameObject go3) {
        super(place, go);
        this.go2 = go2;
        this.go3 = go3;
        Dwidth = Display.getWidth() / 2;
        Dheight = Display.getHeight() / 2;
        update();        
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
    
    @Override
    public final int getMidX() {
        return (getGo().getMidX() + go2.getMidX() + go3.getMidX()) / 3;
    }
    
    @Override
    public final int getMidY() {
        return (getGo().getMidY() + go2.getMidY() + go3.getMidY()) / 3;
    }
}
