/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

/**
 * @author przemek
 */

import game.gameobject.entities.Player;
import game.gameobject.inputs.PlayerController;
import game.place.Place;
import game.place.cameras.Camera;

public abstract class GUIObject {

    protected Player player;
    protected PlayerController playerController;
    protected Place place;
    protected boolean visible;
    protected int priority;
    private String name;

    protected GUIObject(String name, Place place) {
        this.name = name;
        this.place = place;
        visible = true;
    }

    public abstract void render();

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        playerController = player.getController();
    }

    protected Camera getCamera() {
        return player.getCamera();
    }

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getRelativePlayersX() {
        return player.getX() - player.getCamera().getXStart();
    }

    public int getRelativePlayersY() {
        return player.getY() - player.getCamera().getYStart();
    }
}
