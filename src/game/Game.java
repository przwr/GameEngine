/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.gameobject.entities.Player;
import game.logic.maploader.MapLoaderModule;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.menu.Menu;
import game.place.Place;
import game.text.fonts.FontType;
import net.GameOnline;
import org.lwjgl.input.Keyboard;

/**
 * @author przemek
 */
public abstract class Game {

    public static final byte OFFLINE = 0, ONLINE = 1;
    private final String title;
    public FontType font;
    public int mode;
    public GameOnline online;
    public boolean started;
    public boolean exitFlag;
    public Player[] players;
    protected boolean running;
    protected boolean pauseFlag;
    protected Place place;
    protected Player menuPlayer;
    protected Menu menu;
    protected PathFindingModule pathFinding;
    protected MapLoaderModule mapLoader;
    protected Thread pathThread;
    protected Thread mapThread;
    private boolean pause;

    protected Game(String title) {
        this.title = title;
    }

    public abstract void getInput();

    public abstract void startGame();

    public abstract void endGame();

    public abstract void resumeGame();

    public abstract void runClient();

    public abstract void runServer();

    public abstract void showLoading(int progress);

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

    public String getPlayerCoordinates() {
        Player p = (Player) place.players[0];
        return "[" + p.getX() + ", " + p.getY() + "] : "
                + "{" + p.getX() / Place.tileSize + ", " + p.getY() / Place.tileSize + "}";
    }

    public String getSimplePlayerCoordinates() {
        Player p = (Player) place.players[0];
        return p.getX() + ", " + p.getY();
    }

    protected void pause() {
        if (Keyboard.isKeyDown(Keyboard.KEY_PAUSE)) {
            if (!pause) {
                pauseFlag = true;
                pause = true;
            }
        } else {
            pause = false;
        }
    }

    protected void resume() {
        if (Keyboard.isKeyDown(Keyboard.KEY_PAUSE)) {
            if (!pause) {
                pauseFlag = false;
                pause = true;
            }
        } else {
            pause = false;
        }
    }

    public MapLoaderModule getMapLoader() {
        return mapLoader;
    }

    /**
     * @return the mapThread
     */
    public Thread getMapThread() {
        return mapThread;
    }
}
