/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import engine.Animation;
import game.place.AbstractMenu;
import game.place.AbstractPlace;
import game.place.cameras.Camera;
import net.AbstractGameOnline;

/**
 *
 * @author przemek
 */
public abstract class AbstractPlayer extends AbstractEntity {

    public AbstractMenu menu;
    protected Animation anim;
    public AbstractControler ctrl;
    protected Camera cam;
    protected AbstractGameOnline online;
    public boolean isFirst;
    public byte id;

    public AbstractPlayer(String name) {
        this.name = name;
    }

    public abstract void init(int startX, int startY, int width, int height, AbstractPlace place, int x, int y);

    public abstract void init(int startX, int startY, int width, int height, AbstractPlace place);

    public abstract void update(AbstractPlace place);

    public abstract void sendUpdate(AbstractPlace place);

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

    public void addMenu(AbstractMenu menu) {
        this.menu = menu;
    }

    public Animation getAnim() {
        return anim;
    }

    public void setPlaceToNull() {
        place = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AbstractPlayer getCollided(int magX, int magY) {
        return null;
    }

    public void setToLastNotCollided() {
        for (int i = online.pastNr - 1; i >= 0; i--) {
            if (!collision.ifCollideSolid(online.past[i].x, online.past[i].y, place)) {
                if (!collision.ifCollideSolid(online.past[i].x, getY(), place)) {
                    setPosition(online.past[i].x, getY());
                } else if (!collision.ifCollideSolid(getX(), online.past[i].y, place)) {
                    setPosition(getX(), online.past[i].y);
                } else {
                    setPosition(online.past[i].x, online.past[i].y);
                }
                upDepth();
                cam.update();
                return;
            }
        }
        for (int i = online.past.length - 1; i >= online.pastNr; i--) {
            if (!collision.ifCollideSolid(online.past[i].x, online.past[i].y, place)) {
                if (!collision.ifCollideSolid(online.past[i].x, getY(), place)) {
                    setPosition(online.past[i].x, getY());
                } else if (!collision.ifCollideSolid(getX(), online.past[i].y, place)) {
                    setPosition(getX(), online.past[i].y);
                } else {
                    setPosition(online.past[i].x, online.past[i].y);
                }
                upDepth();
                cam.update();
                return;
            }
        }
    }
}
