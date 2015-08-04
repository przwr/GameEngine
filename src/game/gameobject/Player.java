/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import engine.Drawer;
import game.place.Map;
import game.place.Menu;
import game.place.Place;
import game.place.cameras.Camera;
import net.GameOnline;

import java.util.ArrayList;

/**
 * @author przemek
 */
public abstract class Player extends Entity {

    private final ArrayList<GUIObject> guiList = new ArrayList<>();
    public byte playerID;
    public PlayerController playerController;
    protected Camera camera;
    protected GameOnline online;
    protected boolean first, stopped;
    private Menu menu;

    protected Player(String name) {
        this.name = name;
    }

    public abstract void initializeSetPosition(int width, int height, Place place, int x, int y);

    public abstract void initialize(int width, int height, Place place);

    public abstract void update();

    public abstract void sendUpdate();

    public abstract void renderClothed(int frame);

    public void addGui(GUIObject gui) {
        guiList.add(gui);
        sortGUI();
        gui.setPlayer(this);
    }

    public void removeGui(GUIObject gui) {
        guiList.remove(gui);
        guiList.trimToSize();
        sortGUI();
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

    private void sortGUI() {
        int i, j, newValue;
        GUIObject object;
        for (i = 1; i < guiList.size(); i++) {
            object = guiList.get(i);
            newValue = object.getPriority();
            j = i;
            while (j > 0 && guiList.get(j - 1).getPriority() > newValue) {
                guiList.set(j, guiList.get(j - 1));
                j--;
            }
            guiList.set(j, object);
        }
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
        return playerController.isMenuOn();
    }

    public boolean isNotFirst() {
        return !first;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public PlayerController getController() {
        return playerController;
    }

    public void getInput() {
        playerController.getInput();
    }

    public void getMenuInput() {
        playerController.getMenuInput();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setNotInGame() {
        this.place = null;
    }

}
