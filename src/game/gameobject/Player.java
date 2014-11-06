/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.place.cameras.Camera;
import game.place.Place;
import engine.Animation;
import game.myGame.MyController;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public abstract class Player extends Entity {

    public Menu menu;
    protected Animation anim;
    protected boolean animate;
    public MyController ctrl;
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

    public Place getPlace() {
        return place;
    }

    public void setPlaceToNull() {
        place = null;
    }
}
