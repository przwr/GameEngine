/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.place.Place;
import game.gameobject.Player;
import game.place.Menu;
import org.lwjgl.input.Controller;

/**
 *
 * @author przemek
 */
public abstract class Game {

    protected final Settings settings;
    protected Player menuPl;
    protected Menu menu;
    protected Place place;
    protected final String title;
    public boolean runFlag;
    public boolean exitFlag;
    public Player[] players;

    public Game(String title, Settings settings, Controller[] controllers) {
        this.settings = settings;
        this.title = title;
    }

    public abstract void getInput();

    public abstract void startGame();

    public abstract void endGame();

    public abstract void resumeGame();

    public abstract void update();

    public abstract void render();

    public void exit() {
        exitFlag = true;
    }

    public String getTitle() {
        return title;
    }

    public Place getPlace() {
        return place;
    }
}
