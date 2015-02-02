/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

/**
 *
 * @author przemek
 */
import game.place.Place;
import game.place.cameras.Camera;

public abstract class GUIObject {

    protected Camera camera;
    protected String name;
    protected Place place;
    protected boolean visible;

    protected GUIObject(String name, Place place) {
        this.name = name;
        this.place = place;
        visible = true;
    }

    public abstract void render(int xEffect, int yEffect);
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
    
    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
