/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.myGame.MyMenu;
import game.myGame.MyPlace;
import game.gameobject.Player;
import game.place.Place;
import java.io.File;
import openGLEngine.Sound;
import org.lwjgl.input.Controller;

/**
 *
 * @author przemek
 */
public class Game {

    private final Settings settings;
    private final Player menuPl;
//    private Player players[0];
//    private Player players[1];
//    private Player players[2];
//    private Player players[3];
    private final Player[] players = new Player[4];
    private final Place menu;
    private Place place;
    private final String title;
    private boolean runFlag;
    public boolean exitFlag;

    public Game(String title, Settings settings, Controller[] controllers) {
        this.settings = settings;
        this.title = title;

        players[0] = new Player(true);
        players[1] = new Player(false);
        players[2] = new Player(false);
        players[3] = new Player(false);

        settings.Up(players[1].ctrl.getActionsCount(), players, controllers);
        IO.ReadFile(new File("res/input.ini"), settings, false);
        menu = new MyMenu(this, 2048, 2048, 64, settings);
        menuPl = new Player(true);
        menu.addCamera(menuPl, 2, 2, 0);
        menu.addPlayer(menuPl);
        menuPl.addCamera(menu.cams[0]);
        menuPl.addMenu((MyMenu) menu);
    }

    public void getInput() {
        if (runFlag) {
            for (Player pl : players) {
                if (pl.isMenuOn()) {
                    runFlag = false;
                    soundPause();
                }
                pl.getInput();
            }
        } else {
            if (place == null) {
                menuPl.getMenuInput();
            } else {
                for (Player pl : players) {
                    if (pl.menu != null) {
                        if (pl.isMenuOn()) {
                            ((MyMenu) menu).back();
                        }
                        pl.getMenuInput();
                    }
                }
            }
        }
    }

    public void startGame(int nrPl) {
        place = new MyPlace(this, 2048, 2048, 64, settings);
        if (nrPl == 1) {
            players[0].init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
            place.addCamera(players[0], 2, 2, 0); // 2 i 2 to tryb SS
        } else if (nrPl == 2) {
            players[0].init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
            players[1].init(4, 4, 56, 56, 64, 64, "Player 2", place, 512, 1024);
            if (settings.hSplitScreen) {
                place.addCamera(players[0], 2, 4, 0);
                place.addCamera(players[1], 2, 4, 1);
            } else {
                place.addCamera(players[0], 4, 2, 0);
                place.addCamera(players[1], 4, 2, 1);
            }
        } else if (nrPl == 3) {
            players[0].init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
            players[1].init(4, 4, 56, 56, 64, 64, "Player 2", place, 512, 1024);
            players[2].init(4, 4, 56, 56, 64, 64, "Player 3", place, 1024, 512);
            if (settings.hSplitScreen) {
                place.addCamera(players[0], 2, 4, 0);
            } else {
                place.addCamera(players[0], 4, 2, 0);
            }
            place.addCamera(players[1], 4, 4, 1);
            place.addCamera(players[2], 4, 4, 2);
        } else if (nrPl == 4) {
            players[0].init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
            players[1].init(4, 4, 56, 56, 64, 64, "Player 2", place, 512, 1024);
            players[2].init(4, 4, 56, 56, 64, 64, "Player 3", place, 1024, 512);
            players[3].init(4, 4, 56, 56, 64, 64, "Player 4", place, 1024, 1024);
            place.addCamera(players[0], 4, 4, 0);
            place.addCamera(players[1], 4, 4, 1);
            place.addCamera(players[2], 4, 4, 2);
            place.addCamera(players[3], 4, 4, 3);
        }
        for (int i = 0; i < nrPl; i++) {
            place.addPlayer(players[i]);
            players[i].addCamera(place.cams[i]);
            players[i].addMenu((MyMenu) menu);
        }
        runFlag = true;
    }

    public void endGame() {
        runFlag = false;
        place = null;
        settings.sounds = null;
    }

    public void resume() {
        if (players[0] != null) {
            soundResume();
            runFlag = true;
        }
    }

    public void exit() {
        exitFlag = true;
    }

    public void update() {
        if (runFlag) {
            place.update();
        } else {
            menu.update();
        }
    }

    public void render() {
        if (runFlag) {
            place.render();
        } else {
            menu.render();
        }
    }

    public String getTitle() {
        return title;
    }

    public Place getPlace() {
        return place;
    }

    private void soundPause() {
        if (settings.sounds != null) {
            for (Sound s : settings.sounds.getSoundsList()) {
                if (s.isPlaying()) {
                    if (s.isPaused()) {
                        s.setNotPlaying(true);
                    } else {
                        s.fade(0.01, true);
                    }
                } else {
                    s.setNotPlaying(true);
                }
            }
        }
    }

    private void soundResume() {
        if (settings.sounds != null) {
            for (Sound s : settings.sounds.getSoundsList()) {
                if (s.werePaused()) {
                    s.setNotPlaying(false);
                } else {
                    s.resume();
                    s.smoothStart(0.5);
                }
            }
        }
    }
}
