/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import engine.Drawer;
import sprites.Animation;
import game.place.Map;
import game.place.Menu;
import game.place.Place;
import game.place.cameras.Camera;
import java.util.ArrayList;
import net.GameOnline;

/**
 *
 * @author przemek
 */
public abstract class Player extends Entity {

    public byte playerID;
    protected Menu menu;
    public Controler controler;
    protected Camera camera;
    protected GameOnline online;
    protected boolean first;

    protected ArrayList<GUIObject> guiList = new ArrayList<>();

    public abstract void initializeSetPosition(int xStart, int yStart, int width, int height, Place place, int x, int y);

    public abstract void initialize(int xStart, int yStart, int width, int height, Place place);

    public abstract void update();

    public abstract void sendUpdate();

    public Player(String name) {
        this.name = name;
    }

    public void addGui(GUIObject gui) {
        guiList.add(gui);
        gui.setPlayer(this);
    }

    public void removeGui(GUIObject gui) {
        guiList.remove(gui);
        gui.setPlayer(null);
    }

    public void renderGUI() {
        Drawer.refreshForRegularDrawing();
        guiList.stream().forEach((go) -> {
            if (go.isVisible()) {
                go.render(0, 0);
            }
        });
    }

    @Override
    public void changeMap(Map map) {
        super.changeMap(map);
        if (camera != null) {
            camera.setMap(map);
        }
    }

    @Override
    public Player getCollided(int xMagnitude, int yMagnitude) {
        return null;
    }

    @Override
    protected void move(int xPosition, int yPosition) {
        setPosition(x + xPosition, y + yPosition);
    }

    @Override
    public void setPosition(double xPosition, double yPosition) {
        super.setPosition(xPosition, yPosition);
        if (camera != null) {
            camera.update();
        }
    }

    public void setToLastNotCollided() {
        for (int i = online.pastPositionsNumber - 1; i >= 0; i--) {
            if (setToLastNotCollidedToEnd(i)) {
                return;
            }
        }
        for (int i = online.pastPositions.length - 1; i >= online.pastPositionsNumber; i--) {
            if (setToLastNotCollidedFromStart(i)) {
                return;
            }
        }
    }

    private boolean setToLastNotCollidedToEnd(int i) {
        if (!collision.isCollideSolid(online.pastPositions[i].getX(), online.pastPositions[i].getY(), map)) {
            if (!collision.isCollideSolid(online.pastPositions[i].getX(), getY(), map)) {
                setPosition(online.pastPositions[i].getX(), getY());
            } else if (!collision.isCollideSolid(getX(), online.pastPositions[i].getY(), map)) {
                setPosition(getX(), online.pastPositions[i].getY());
            } else {
                setPosition(online.pastPositions[i].getX(), online.pastPositions[i].getY());
            }
            camera.update();
            return true;
        }
        return false;
    }

    private boolean setToLastNotCollidedFromStart(int i) {
        if (!collision.isCollideSolid(online.pastPositions[i].getX(), online.pastPositions[i].getY(), map)) {
            if (!collision.isCollideSolid(online.pastPositions[i].getX(), getY(), map)) {
                setPosition(online.pastPositions[i].getX(), getY());
            } else if (!collision.isCollideSolid(getX(), online.pastPositions[i].getY(), map)) {
                setPosition(getX(), online.pastPositions[i].getY());
            } else {
                setPosition(online.pastPositions[i].getX(), online.pastPositions[i].getY());
            }
            camera.update();
            return true;
        }
        return false;
    }

    public boolean isInGame() {
        return place != null;
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
        return camera;
    }

    @Override
    public Animation getAnimation() {
        return animation;
    }

    public void setNotInGame() {
        this.place = null;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

}
