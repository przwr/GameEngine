/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.entities;

import engine.utilities.Drawer;
import game.gameobject.GUIObject;
import game.gameobject.GameObject;
import game.gameobject.inputs.PlayerController;
import game.gameobject.items.Item;
import game.gameobject.items.Weapon;
import game.menu.Menu;
import game.place.Place;
import game.place.cameras.Camera;
import game.place.map.Map;
import game.text.effects.TextController;
import net.GameOnline;

import java.util.ArrayList;

/**
 * @author przemek
 */
public abstract class Player extends Entity {

    private final ArrayList<GUIObject> guiList = new ArrayList<>();
    public byte playerID;
    protected PlayerController playerController;
    protected TextController textControl;
    protected Camera camera;
    protected boolean first, stopped;
    protected boolean visibleShadow = true;
    protected Weapon activeWeapon;
    protected Map empty;
    protected GameOnline online;
    protected Menu menu;
    protected boolean usesHandyMenu, gearOn = true, backpackOn = true;
    protected Item[] loot;
    protected int menuKey = -1;
    protected String lootName;

    protected Player(String name) {
        super();
        this.name = name;
    }

    public abstract void initializeSetPosition(int width, int height, Place place, int x, int y);

    public abstract void initialize(int width, int height, Place place);

    public abstract void sendUpdate();

    public ArrayList<GameObject> getInteractingObjects() {
        return null;
    }

    public void addGui(GUIObject gui) {
        if (!guiList.isEmpty()) {
            for (GUIObject g : guiList) {
                if (g.getClass() == gui.getClass()) {
                    return;
                }
            }
        }
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

    public void renderGUI(int xEffect, int yEffect) {
        Drawer.refreshForRegularDrawing();
        guiList.stream().forEach((go) -> {
            if (go.isVisible()) {
                go.render();
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

    public void setShadowVisibility(boolean shadow) {
        visibleShadow = shadow;
    }

    @Override
    public void changeMap(Map map, int x, int y) {
        super.changeMap(map, x, y);
        if (camera != null) {
            camera.setMap(map);
        }
    }

    @Override
    public Player getCollided(double xMagnitude, double yMagnitude) {
        return null;
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
            return true;
        }
        return false;
    }

    /*public boolean isPlayerTalkingToMe(Player player) {
        return player.getController().getAction(MyController.INPUT_ACTION).isKeyClicked()
                && !player.getTextController().isStarted() && Methods.pointDistanceSimple(getX(), getY(),
                        player.getX(), player.getY()) <= Place.tileSize * 1.5 + Math.max(appearance.getActualWidth(), appearance.getActualHeight()) / 2
                && Math.abs(Methods.angleDifference(player.getDirection(), (int) Methods.pointAngleCounterClockwise(player.getX(), player.getY(), x, y))) <= 80;
    }*/

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

    public Weapon getWeapon() {
        return activeWeapon;
    }

    public TextController getTextController() {
        return textControl;
    }

    public void setUsesHandyMenu(boolean usesHandyMenu) {
        this.usesHandyMenu = usesHandyMenu;
    }

    public boolean usesHandyMenu() {
        return usesHandyMenu;
    }

    public boolean isGearOn() {
        return gearOn;
    }

    public void setGearOn(boolean gearOn) {
        this.gearOn = gearOn;
    }

    public boolean isEquipmentOn() {
        return backpackOn || loot != null;
    }

    public void setBackpackOn(boolean backpackOn) {
        this.backpackOn = backpackOn;
    }

    public int getMenuKey() {
        return menuKey;
    }

    public void setMenuKey(int menuKey) {
        this.menuKey = menuKey;
    }

    public void turnOffLoot() {
        this.loot = null;
    }

    @Override
    public int getXBackpackSize() {
        return loot != null ? loot.length / 3 : xBackpackSize;
    }

    @Override
    public int getYBackpackSize() {
        return loot != null ? 3 : yBackpackSize;
    }

    @Override
    public Item[] getItems() {
        return loot != null ? loot : items;
    }

    public boolean isLoot() {
        return loot != null;
    }

    public void setLoot(Item[] loot, String lootName) {
        this.loot = loot;
        this.lootName = lootName;
    }

    public Item getActiveWeapon() {
        if (activeWeapon != null)
            return activeWeapon;
        return Item.EMPTY;
    }

    public String getLootName() {
        return lootName;
    }
}
