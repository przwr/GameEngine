/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.gameobject.menu.MyMenu;
import game.gameobject.MyPlace;
import game.gameobject.Player;
import game.place.Place;
import org.lwjgl.openal.AL;

/**
 *
 * @author przemek
 */
public class Game {

    private final Settings settings;
    private final Player menuPl;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private final Place menu;
    private Place place;
    private final String title;
    private boolean runFlag;
    public boolean exitFlag;
    public boolean fullScreen;

    public Game(String title, Settings settings) {
        this.settings = settings;
        this.title = title;
        menu = new MyMenu(this, 2048, 2048, 64, settings);
        menuPl = new Player(0, 0, 0, 0, 0, 0, "", menu, 2, 2, 0);
        menu.addCamera1For1(menuPl, 128, 128);
        menu.addPlayer(menuPl);
        menuPl.addCamera(menu.cam1);
        menuPl.addMenu((MyMenu) menu);
    }

    public void getInput() {
        if (runFlag) {
            if (player1.isMenuOn()) {
                runFlag = false;
            }
            player1.getInput();
            if (player2 != null && runFlag == true) {
                if (player2.isMenuOn()) {
                    runFlag = false;
                }
                player2.getInput();
            }
            if (player3 != null && runFlag == true) {
                if (player3.isMenuOn()) {
                    runFlag = false;
                }
                player3.getInput();
            }
            if (player4 != null && runFlag == true) {
                if (player4.isMenuOn()) {
                    runFlag = false;
                }
                player4.getInput();
            }
        } else {
            if (player1 == null) {
                menuPl.getMenuInput();
            } else {
                if (player1.isMenuOn()) {
                    ((MyMenu) menu).back();
                }
                player1.getMenuInput();
                if (player2 != null) {
                    if (player2.isMenuOn()) {
                        ((MyMenu) menu).back();
                    }
                    player2.getMenuInput();

                }
                if (player3 != null) {
                    if (player3.isMenuOn()) {
                        ((MyMenu) menu).back();
                    }
                    player3.getMenuInput();
                }
                if (player4 != null) {
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
            player1 = new Player(4, 4, 56, 56, 64, 64, "Player 1", getPlace(), 2, 2, 0); // 2 i 2 to tryb SS - nie zmieniać tego!
            getPlace().addCamera1For1(player1, 256, 256);
            getPlace().addPlayer(player1);
            player1.addCamera(getPlace().cam1);
            player1.addMenu((MyMenu) menu);
        } else if (nrPl == 2) {
            if (settings.hSplitScreen) {
                player1 = new Player(4, 4, 56, 56, 64, 64, "Player 1", getPlace(), 2, 4, 0);
                getPlace().addCamera1For2H(player1, 256, 256);
                getPlace().addPlayer(player1);
                player1.addCamera(getPlace().cam1);
                player1.addMenu((MyMenu) menu);
                player2 = new Player(4, 4, 56, 56, 64, 64, "Player 2", getPlace(), 2, 4, 1);
                getPlace().addCamera2For2H(player2, 1024, 512);
                getPlace().addPlayer(player2);
                player2.addCamera(getPlace().cam2);
                player2.addMenu((MyMenu) menu);
            } else {
                player1 = new Player(4, 4, 56, 56, 64, 64, "Player 1", getPlace(), 4, 2, 0);
                getPlace().addCamera1For2V(player1, 256, 256);
                getPlace().addPlayer(player1);
                player1.addCamera(getPlace().cam1);
                player1.addMenu((MyMenu) menu);
                player2 = new Player(4, 4, 56, 56, 64, 64, "Player 2", getPlace(), 4, 2, 1);
                getPlace().addCamera2For2V(player2, 1024, 512);
                getPlace().addPlayer(player2);
                player2.addCamera(getPlace().cam2);
                player2.addMenu((MyMenu) menu);
            }
        } else if (nrPl == 3) {
            if (settings.hSplitScreen) {
                player1 = new Player(4, 4, 56, 56, 64, 64, "Player 1", getPlace(), 2, 4, 0);
                getPlace().addCamera1For2H(player1, 256, 256);
                getPlace().addPlayer(player1);
                player1.addCamera(getPlace().cam1);
                player1.addMenu((MyMenu) menu);
                player2 = new Player(4, 4, 56, 56, 64, 64, "Player 2", getPlace(), 4, 4, 1);
                getPlace().addCamera2For4(player2, 1024, 512);
                getPlace().addPlayer(player2);
                player2.addCamera(getPlace().cam2);
                player2.addMenu((MyMenu) menu);
                player3 = new Player(4, 4, 56, 56, 64, 64, "Player 3", getPlace(), 4, 4, 1);
                getPlace().addCamera3For4(player3, 512, 1024);
                getPlace().addPlayer(player3);
                player3.addCamera(getPlace().cam3);
                player3.addMenu((MyMenu) menu);
            } else {
                player1 = new Player(4, 4, 56, 56, 64, 64, "Player 1", getPlace(), 4, 2, 0);
                getPlace().addCamera1For2V(player1, 256, 256);
                getPlace().addPlayer(player1);
                player1.addCamera(getPlace().cam1);
                player1.addMenu((MyMenu) menu);
                player2 = new Player(4, 4, 56, 56, 64, 64, "Player 2", getPlace(), 4, 4, 1);
                getPlace().addCamera2For4(player2, 1024, 512);
                getPlace().addPlayer(player2);
                player2.addCamera(getPlace().cam2);
                player2.addMenu((MyMenu) menu);
                player3 = new Player(4, 4, 56, 56, 64, 64, "Player 3", getPlace(), 4, 4, 1);
                getPlace().addCamera3For4(player3, 512, 1024);
                getPlace().addPlayer(player3);
                player3.addCamera(getPlace().cam3);
                player3.addMenu((MyMenu) menu);
            }
        } else if (nrPl == 4) {
            player1 = new Player(4, 4, 56, 56, 64, 64, "Player 1", getPlace(), 4, 4, 0);
            getPlace().addCamera1For4(player1, 256, 256);
            getPlace().addPlayer(player1);
            player1.addCamera(getPlace().cam1);
            player1.addMenu((MyMenu) menu);
            player2 = new Player(4, 4, 56, 56, 64, 64, "Player 2", getPlace(), 4, 4, 1);
            getPlace().addCamera2For4(player2, 256, 512);
            getPlace().addPlayer(player2);
            player2.addCamera(getPlace().cam2);
            player2.addMenu((MyMenu) menu);
            player3 = new Player(4, 4, 56, 56, 64, 64, "Player 3", getPlace(), 4, 4, 1);
            getPlace().addCamera3For4(player3, 512, 1024);
            getPlace().addPlayer(player3);
            player3.addCamera(getPlace().cam3);
            player3.addMenu((MyMenu) menu);
            player4 = new Player(4, 4, 56, 56, 64, 64, "Player 4", getPlace(), 4, 4, 1);
            getPlace().addCamera4For4(player4, 1024, 256);
            getPlace().addPlayer(player4);
            player4.addCamera(getPlace().cam4);
            player4.addMenu((MyMenu) menu);
        }
        runFlag = true;
    }

    public void endGame() {
        runFlag = false;
        place = null;
        player1 = player2 = player3 = player4 = null;
    }

    public void resume() {
        if (player1 != null) {
            runFlag = true;
        }
    }

    public void exit() {
        AL.destroy();
        exitFlag = true;
    }

    public void update() {
        if (runFlag) {
            getPlace().update();
        } else {
            menu.update();
        }
    }

    public void render() {
        if (runFlag) {
            getPlace().render();
        } else {
            menu.render();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setDesktopFullScreen() {
        fullScreen = true;
    }

    public Place getPlace() {
        return place;
    }
}
