/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import sprites.Animation;
import game.place.Map;
import game.place.Menu;
import game.place.Place;
import game.place.cameras.Camera;
import net.GameOnline;

/**
 *
 * @author przemek
 */
public abstract class Player extends Entity {

    public Menu menu;
    protected Animation animation;
    public Controler controler;
    protected Camera cam;
    protected GameOnline online;
    protected boolean first;
    public byte id;

    public Player(String name) {
        this.name = name;
    }

    public abstract void initialize(int startX, int startY, int width, int height, Place place, int x, int y);

    public abstract void initialize(int startX, int startY, int width, int height, Place place);

    public abstract void update();

    public abstract void sendUpdate(Place place);

    public void setCamera(Camera cam) {
        this.cam = cam;
    }

    public void getInput() {
        controler.getInput();
    }

    public boolean isMenuOn() {
        return controler.isMenuOn();
    }

    public void getMenuInput() {
        controler.getMenuInput();
    }

    public Camera getCam() {
        return cam;
    }

    @Override
    public void changeMap(Map newMap) {
        super.changeMap(newMap);
        if (cam != null) {
            cam.setMap(newMap);
        }
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void setPlaceToNull() {
        place = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Player getCollided(int magX, int magY) {
        return null;
    }

    public void setToLastNotCollided() {
        for (int i = online.pastNr - 1; i >= 0; i--) {
            if (!collision.isCollideSolid(online.past[i].x, online.past[i].y, map)) {
                if (!collision.isCollideSolid(online.past[i].x, getY(), map)) {
                    setPosition(online.past[i].x, getY());
                } else if (!collision.isCollideSolid(getX(), online.past[i].y, map)) {
                    setPosition(getX(), online.past[i].y);
                } else {
                    setPosition(online.past[i].x, online.past[i].y);
                }
                cam.update();
                return;
            }
        }
        for (int i = online.past.length - 1; i >= online.pastNr; i--) {
            if (!collision.isCollideSolid(online.past[i].x, online.past[i].y, map)) {
                if (!collision.isCollideSolid(online.past[i].x, getY(), map)) {
                    setPosition(online.past[i].x, getY());
                } else if (!collision.isCollideSolid(getX(), online.past[i].y, map)) {
                    setPosition(getX(), online.past[i].y);
                } else {
                    setPosition(online.past[i].x, online.past[i].y);
                }
                cam.update();
                return;
            }
        }
    }

    public boolean isFirst() {
        return first;
    }
}
