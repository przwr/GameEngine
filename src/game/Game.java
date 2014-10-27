/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.myGame.MyMenu;
import game.myGame.MyPlace;
import game.myGame.MyPlayer;
import game.place.Place;
import java.io.File;
import engine.Sound;
import game.gameobject.GameObject;
import org.lwjgl.input.Controller;

/**
 *
 * @author przemek
 */
public class Game {

    private final Settings settings;
    private final MyPlayer menuPl;
    private final MyPlayer[] players = new MyPlayer[4];
    private final Place menu;
    private Place place;
    private final String title;
    private boolean runFlag;
    public boolean exitFlag;

    public Game(String title, Settings settings, Controller[] controllers) {
        this.settings = settings;
        this.title = title;

        players[0] = new MyPlayer(true);
        players[1] = new MyPlayer(false);
        players[2] = new MyPlayer(false);
        players[3] = new MyPlayer(false);

        settings.Up(players[1].ctrl.getActionsCount(), players, controllers);
        IO.ReadFile(new File("res/input.ini"), settings, false);
        menu = new MyMenu(this, 2048, 2048, 64, settings);
        menuPl = new MyPlayer(true);
        menu.addCamera(menuPl, 2, 2, 0);
        menu.players = new GameObject[1];
        menu.players[0] = menuPl;
        menuPl.addCamera(menu.cams[0]);
        menuPl.addMenu((MyMenu) menu);
    }

    public void getInput() {
        if (runFlag) {
            for (MyPlayer pl : players) {
                if (pl.getPlace() != null) {
                    if (pl.isMenuOn()) {
                        runFlag = false;
                        soundPause();
                    }
                    pl.getInput();
                }
            }
        } else {
            if (place == null) {
                menuPl.getMenuInput();
            } else {
                for (MyPlayer pl : players) {
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
        place = new MyPlace(this, 4096, 4096, 64, settings);
        place.players = new GameObject[nrPl];
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
            place.addCamerasFor2(players[0], players[1]);
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
            place.addCameraFor3(players[0], players[1], players[2]);
        } else if (nrPl == 4) {
            players[0].init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
            players[1].init(4, 4, 56, 56, 64, 64, "Player 2", place, 512, 1024);
            players[2].init(4, 4, 56, 56, 64, 64, "Player 3", place, 1024, 512);
            players[3].init(4, 4, 56, 56, 64, 64, "Player 4", place, 1024, 1024);
            place.addCamera(players[0], 4, 4, 0);
            place.addCamera(players[1], 4, 4, 1);
            place.addCamera(players[2], 4, 4, 2);
            place.addCamera(players[3], 4, 4, 3);
            place.addCameraFor4(players[0], players[1], players[2], players[3]);
        }
        for (int i = 0; i < nrPl; i++) {
            place.players[i] = players[i];
            players[i].addCamera(place.cams[i]);
            players[i].addMenu((MyMenu) menu);
        }
        place.makeShadows();
        runFlag = true;
    }

    public void endGame() {
        runFlag = false;
        place = null;
        settings.sounds = null;
    }

    public void resume() {
        if (place != null) {
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
