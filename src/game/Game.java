/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import net.GameOnline;
import game.gameobject.Player;
import game.place.Menu;
import game.place.Place;
import game.place.SplitScreen;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author przemek
 */
public abstract class Game {

    public final Settings settings;
    public GameOnline online;
    protected final String title;
    protected Player menuPl;
    protected Menu menu;
    public Place place;
    public int mode;
    public boolean started, runFlag, pauseFlag, exitFlag, pause;
    public Player[] players;

    public Game(String title, Settings settings) {
        this.settings = settings;
        this.title = title;
        SplitScreen.init();
    }

    public abstract void getInput();

    public abstract void startGame();

    public abstract void endGame();

    public abstract void resumeGame();

    public abstract void runClient();

    public abstract void runServer();

    public abstract void update();

    public abstract void render();

    public void exit() {
        exitFlag = true;
        this.endGame();
    }

    public String getTitle() {
        return title;
    }

    public Menu getMenu() {
        return menu;
    }

    public Place getPlace() {
        return place;
    }

    public void pause() {
        if (Keyboard.isKeyDown(Keyboard.KEY_PAUSE)) {
            if (!pause) {
                pauseFlag = true;
                pause = true;
            }
        } else {
            pause = false;
        }
    }

    public void resume() {
        if (Keyboard.isKeyDown(Keyboard.KEY_PAUSE)) {
            if (!pause) {
                pauseFlag = false;
                pause = true;
            }
        } else {
            pause = false;
        }
    }
}
