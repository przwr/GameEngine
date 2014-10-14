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
    public Player player1;
    public Player player2;
    public Player player3;
    public Player player4;
    private final Place menu;
    private Place place;
    private final String title;
    private boolean runFlag;
    public boolean exitFlag;

    public Game(String title, Settings settings, Controller[] controllers) {
        this.settings = settings;
        this.title = title;
        player1 = new Player(controllers, 0);
        player2 = new Player(controllers, 1);
        player3 = new Player(controllers, 1);
        player4 = new Player(controllers, 1);
        settings.Up(player1.ctrl.getActionsCount(), player1, player2, player3, player4, controllers);
        IO.ReadFileInput(new File("res/input.ini"), settings);
        menu = new MyMenu(this, 2048, 2048, 64, settings);
        menuPl = new Player(0, 0, 0, 0, 0, 0, "", menu, 0, 0, controllers, 0);
        menu.addCamera1(menuPl, 2, 2);
        menu.addPlayer(menuPl);
        menuPl.addCamera(menu.cam1);
        menuPl.addMenu((MyMenu) menu);
    }

    public void getInput() {
        if (runFlag) {
            if (player1.isMenuOn()) {
                runFlag = false;
                soundPause();
            }
            player1.getInput();
            if (player2.isMenuOn()) {
                runFlag = false;
                soundPause();
            }
            player2.getInput();
            if (player3.isMenuOn()) {
                runFlag = false;
                soundPause();
            }
            player3.getInput();
            if (player4.isMenuOn()) {
                runFlag = false;
                soundPause();
            }
            player4.getInput();

        } else {
            if (place == null) {
                menuPl.getMenuInput();
            } else {
                if (player1.isMenuOn()) {
                    ((MyMenu) menu).back();
                }
                player1.getMenuInput();
                if (player2.menu != null) {
                    if (player2.isMenuOn()) {
                        ((MyMenu) menu).back();
                    }
                    player2.getMenuInput();
                }
                if (player3.menu != null) {
                    if (player3.isMenuOn()) {
                        ((MyMenu) menu).back();
                    }
                    player3.getMenuInput();
                }
                if (player4.menu != null) {
                    if (player4.isMenuOn()) {
                        ((MyMenu) menu).back();
                    }
                    player4.getMenuInput();
                }
            }
        }
    }

    public void startGame(int nrPl) {
        place = new MyPlace(this, 2048, 2048, 64, settings);
        if (nrPl == 1) {
            player1.init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
            place.addCamera1(player1, 2, 2); // 2 i 2 to tryb SS
            place.addPlayer(player1);
            player1.addCamera(place.cam1);
            player1.addMenu((MyMenu) menu);
        } else if (nrPl == 2) {
            if (settings.hSplitScreen) {
                player1.init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
                place.addCamera1(player1, 2, 4);
                place.addPlayer(player1);
                player1.addCamera(place.cam1);
                player1.addMenu((MyMenu) menu);
                player2.init(4, 4, 56, 56, 64, 64, "Player 2", place, 512, 1024);
                place.addCamera2(player2, 2, 4);
                place.addPlayer(player2);
                player2.addCamera(place.cam2);
                player2.addMenu((MyMenu) menu);
            } else {
                player1.init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
                place.addCamera1(player1, 4, 2);
                place.addPlayer(player1);
                player1.addCamera(place.cam1);
                player1.addMenu((MyMenu) menu);
                player2.init(4, 4, 56, 56, 64, 64, "Player 2", place, 512, 1024);
                place.addCamera2(player2, 4, 2);
                place.addPlayer(player2);
                player2.addCamera(place.cam2);
                player2.addMenu((MyMenu) menu);
            }
        } else if (nrPl == 3) {
            if (settings.hSplitScreen) {
                player1.init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
                place.addCamera1(player1, 2, 4);
                place.addPlayer(player1);
                player1.addCamera(place.cam1);
                player1.addMenu((MyMenu) menu);
                player2.init(4, 4, 56, 56, 64, 64, "Player 2", place, 512, 1024);
                place.addCamera2(player2, 4, 4);
                place.addPlayer(player2);
                player2.addCamera(place.cam2);
                player2.addMenu((MyMenu) menu);
                player3.init(4, 4, 56, 56, 64, 64, "Player 3", place, 1024, 512);
                place.addCamera3(player3, 4, 4);
                place.addPlayer(player3);
                player3.addCamera(place.cam3);
                player3.addMenu((MyMenu) menu);
            } else {
                player1.init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
                place.addCamera1(player1, 4, 2);
                place.addPlayer(player1);
                player1.addCamera(place.cam1);
                player1.addMenu((MyMenu) menu);
                player2.init(4, 4, 56, 56, 64, 64, "Player 2", place, 512, 1024);
                place.addCamera2(player2, 4, 4);
                place.addPlayer(player2);
                player2.addCamera(place.cam2);
                player2.addMenu((MyMenu) menu);
                player3.init(4, 4, 56, 56, 64, 64, "Player 3", place, 1024, 512);
                place.addCamera3(player3, 4, 4);
                place.addPlayer(player3);
                player3.addCamera(place.cam3);
                player3.addMenu((MyMenu) menu);
            }
        } else if (nrPl == 4) {
            player1.init(4, 4, 56, 56, 64, 64, "Player 1", place, 256, 256);
            place.addCamera1(player1, 4, 4);
            place.addPlayer(player1);
            player1.addCamera(place.cam1);
            player1.addMenu((MyMenu) menu);
            player2.init(4, 4, 56, 56, 64, 64, "Player 2", place, 512, 1024);
            place.addCamera2(player2, 4, 4);
            place.addPlayer(player2);
            player2.addCamera(place.cam2);
            player2.addMenu((MyMenu) menu);
            player3.init(4, 4, 56, 56, 64, 64, "Player 3", place, 1024, 512);
            place.addCamera3(player3, 4, 4);
            place.addPlayer(player3);
            player3.addCamera(place.cam3);
            player3.addMenu((MyMenu) menu);
            player4.init(4, 4, 56, 56, 64, 64, "Player 4", place, 1024, 1024);
            place.addCamera4(player4, 4, 4);
            place.addPlayer(player4);
            player4.addCamera(place.cam4);
            player4.addMenu((MyMenu) menu);
        }
        runFlag = true;
    }

    public void endGame() {
        runFlag = false;
        place = null;
        settings.sounds = null;
    }

    public void resume() {
        if (player1 != null) {
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
