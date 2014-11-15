/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import engine.Animation;
import game.place.Menu;
import game.place.Place;
import game.place.cameras.Camera;

/**
 *
 * @author przemek
 */
public abstract class Player extends Entity {

    public Menu menu;
    protected Animation anim;   
    public Controler ctrl;
    protected Camera cam;
    public boolean isFirst;

    public Player(String name) {
        this.name = name;
    }

    protected abstract void initControler(boolean isFirst);

    public abstract void init(int startX, int startY, int width, int height, int sw, int sh, Place place, int x, int y, double SCALE);

    public void addCamera(Camera cam) {
        this.cam = cam;
    }

    public void getInput() {
        ctrl.getInput();
    }

    public boolean isMenuOn() {
        return ctrl.isMenuOn();
    }

    public void getMenuInput() {
        ctrl.getMenuInput();
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public Camera getCam() {
        return cam;
    }

    public void addMenu(Menu menu) {
        this.menu = menu;
    }

    public Animation getAnim() {
        return anim;
    }

    public void setPlaceToNull() {
        place = null;
    }
}
