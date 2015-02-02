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

    public byte ID;
    protected Menu menu;
    protected Animation animation;
    public Controler controler;
    protected Camera camera;
    protected GameOnline online;
    protected boolean first;

    public abstract void initialize(int xStart, int yStart, int width, int height, Place place, int x, int y);

    public abstract void initialize(int xStart, int yStart, int width, int height, Place place);

    public abstract void update();

    public abstract void sendUpdate(Place place);

    public Player(String name) {
        this.name = name;
    }

    @Override
    public void changeMap(Map newMap) {
        super.changeMap(newMap);
        if (camera != null) {
            camera.setMap(newMap);
        }
    }

    @Override
    public Player getCollided(int xMagnitude, int yMagnitude) {
        return null;
    }

    public void setToLastNotCollided() {
        for (int i = online.pastPositionsNumber - 1; i >= 0; i--) {
            if (!collision.isCollideSolid(online.pastPositions[i].getX(), online.pastPositions[i].getY(), map)) {
                if (!collision.isCollideSolid(online.pastPositions[i].getX(), getY(), map)) {
                    setPosition(online.pastPositions[i].getX(), getY());
                } else if (!collision.isCollideSolid(getX(), online.pastPositions[i].getY(), map)) {
                    setPosition(getX(), online.pastPositions[i].getY());
                } else {
                    setPosition(online.pastPositions[i].getX(), online.pastPositions[i].getY());
                }
                camera.update();
                return;
            }
        }
        for (int i = online.pastPositions.length - 1; i >= online.pastPositionsNumber; i--) {
            if (!collision.isCollideSolid(online.pastPositions[i].getX(), online.pastPositions[i].getY(), map)) {
                if (!collision.isCollideSolid(online.pastPositions[i].getX(), getY(), map)) {
                    setPosition(online.pastPositions[i].getX(), getY());
                } else if (!collision.isCollideSolid(getX(), online.pastPositions[i].getY(), map)) {
                    setPosition(getX(), online.pastPositions[i].getY());
                } else {
                    setPosition(online.pastPositions[i].getX(), online.pastPositions[i].getY());
                }
                camera.update();
                return;
            }
        }
    }

    public boolean isMenuOn() {
        return controler.isMenuOn();
    }

    public boolean isFirst() {
        return first;
    }

    public Menu getMenu() {
        return menu;
    }

    public void getInput() {
        controler.getInput();
    }

    public void getMenuInput() {
        controler.getMenuInput();
    }

    public Camera getCamera() {
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

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setPlaceToNull() {
        place = null;
    }
}
