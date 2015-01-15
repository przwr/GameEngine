/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.editorgame;

import game.Game;
import game.IO;
import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.Player;
import java.io.File;
import org.lwjgl.input.Controller;

/**
 *
 * @author przemek
 */
public class EditorGame extends Game {

    private final getInput[] ins = new getInput[1];
    private final update[] ups = new update[1];
    private boolean PAUSE = true;

    public EditorGame(String title, Settings settings, Controller[] controllers) {
        super(title, settings);
        players = new Player[1];
        players[0] = new EditPlayer(true, "Player 1");
        settings.Up(4, players, controllers);
        IO.readFile(new File("res/input.ini"), settings, false);
        initMethods();
    }

    private void initMethods() {
        ins[0] = new getInput() {
            @Override
            public void getInput() {
            }
        };
        ups[0] = new update() {
            @Override
            public void update() {
                place.update();
            }
        };
    }

    @Override

    public void getInput() {
        ins[0].getInput();
    }

    @Override
    public void update() {
        ups[0].update();
    }

    @Override
    public void render() {
        place.render();
    }

    @Override
    public void startGame() {
        place = new EditorPlace(this, (int) (settings.SCALE * 32000), (int) (settings.SCALE * 32000), (int) (settings.SCALE * 64), settings);
        place.players = new GameObject[1];
        players[0].initialize(4, 4, 56, 56, place, 256, 256);
        players[0].setCamera(new EditCamera(place.maps.get(0), players[0], 2, 2, 0));
        System.arraycopy(players, 0, place.players, 0, 4);
        place.makeShadows();
        mode = 0;
        started = runFlag = true;
    }

    @Override
    public void endGame() {
        runFlag = started = false;
        place = null;
        settings.sounds = null;
        for (Player pl : players) {
            pl.setPlaceToNull();
        }
        online.cleanUp();
    }

    @Override
    public void resumeGame() {
    }

    @Override
    public void runClient() {
    }

    @Override
    public void runServer() {       
    }

    private interface update {

        void update();
    }

    private interface getInput {

        void getInput();
    }
}
