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

/**
 *
 * @author przemek
 */
public class Game {

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

    public Game(String title) {
        this.title = title;
        menu = new MyMenu(2048, 2048, 64, this);
        menuPl = new Player(0, 0, 0, 0, 0, 0, "", menu, 0);
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
                    runFlag = true;
                }
                player1.getMenuInput();
                if (player2 != null) {
                    if (player2.isMenuOn()) {
                        runFlag = true;
                    }
                    player2.getMenuInput();

                }
                if (player3 != null) {
                    if (player3.isMenuOn()) {
                        runFlag = true;
                    }
                    player3.getMenuInput();
                }
                if (player4 != null) {
                    if (player4.isMenuOn()) {
                        runFlag = true;
                    }
                    player4.getMenuInput();
                }
            }
        }

    }

    public void startGame() {
        place = new MyPlace(2048, 2048, 64);
        player1 = new Player(4, 4, 56, 56, 64, 64, "Player 1", place, 0);
        place.addCamera1(player1, 256, 256);
        place.addPlayer(player1);
        player1.addCamera(place.cam1);
        player1.addMenu((MyMenu) menu);
        player2 = new Player(4, 4, 56, 56, 64, 64, "Player 2", place, 1);
        place.addCamera2(player2, 256, 512);
        place.addPlayer(player2);
        player2.addCamera(place.cam2);
        player2.addMenu((MyMenu) menu);
        runFlag = true;
    }

    public void resume() {
        if (player1 != null) {
            runFlag = true;
        }
    }
    
    public void exit(){
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
}
