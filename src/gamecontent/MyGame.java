/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.Drawer;
import engine.Methods;
import engine.Sound;
import game.Game;
import static game.IO.loadInputFromFile;
import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.Map;
import game.place.cameras.PlayersCamera;
import gamedesigner.ObjectPlace;
import gamedesigner.ObjectPlayer;
import java.io.File;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 *
 * @author przemek
 */
public class MyGame extends Game {

    private final getInput[] ins = new getInput[2];
    private final updateType[] ups = new updateType[2];

    private boolean designer = false;

    public MyGame(String title, Settings settings, Controller[] controllers) {
        super(title, settings);
        players = new Player[4];
        players[0] = new MyPlayer(true, "Player 1");
        players[1] = new MyPlayer(false, "Player 2");
        players[2] = new MyPlayer(false, "Player 3");
        players[3] = new MyPlayer(false, "Player 4");
        settings.update(players[1].controler.getActionsCount(), players, controllers);
        loadInputFromFile(new File("res/input.ini"), settings);
        menu = new MyMenu(this, 2, 2, 1, settings);
        menuPl = new MyPlayer(true, "Menu");
        menu.players = new GameObject[1];
        menu.players[0] = menuPl;
        menuPl.setMenu(menu);
        players[0].setMenu(menu);
        players[1].setMenu(menu);
        players[2].setMenu(menu);
        players[3].setMenu(menu);
        online = new MyGameOnline(this, 3, 4);
        online.initializeChanges();
        initializeMethods();
    }

    private void initializeMethods() {
        ins[0] = () -> {
            if (!pauseFlag) {
                pause();
                if (runFlag) {
                    Player pl;
                    for (int p = 0; p < players.length; p++) {
                        pl = players[p];
                        if (pl.isMenuOn()) {
                            if (pl.getPlace() != null) {
                                if (!pl.isFirst()) {
                                    removePlayerOffline(p);
                                } else {
                                    runFlag = false;
                                    soundPause();
                                }
                            } else {
                                addPlayerOffline(p);
                            }
                        }
                        if (pl.getPlace() != null) {
                            pl.getInput();
                        }
                    }
                } else {
                    if (place == null) {
                        menuPl.getMenuInput();
                    } else {
                        for (Player pl : players) {
                            if (pl.isMenuOn()) {
                                menu.back();
                            } else {
                                pl.getMenuInput();
                            }
                        }
                    }
                }
            } else {
                resume();
            }
        };
        ins[1] = () -> {
            if (runFlag) {
                if (players[0].isMenuOn()) {
                    runFlag = false;
                    soundPause();
                }
                if (players[0].getPlace() != null) {
                    players[0].getInput();
                }
            } else {
                if (place == null) {
                    menuPl.getMenuInput();
                } else {
                    players[0].getMenuInput();
                }
            }
        };
        ups[0] = () -> {
            if (!pauseFlag) {
                if (runFlag) {
                    place.update();
                } else {
                    //---------------------- <('.'<) OBJECT DESIGNER ----------------------------//
                    if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
                        designer = true;
                        players[0] = new ObjectPlayer(true, "Mapper");
                        players[0].setMenu(menu);
                        settings.nrPlayers = 1;
                        startGame();
                        menu.setCurrent(0);
                    }
                    //---------------------------------------------------------------------------//
                    menu.update();
                }
            }
        };
        ups[1] = () -> {
            if ((online.client == null && online.server == null) || (online.client != null && !online.client.isConnected)) {
                endGame();
                Methods.error(settings.language.m.Disconnected);
            } else {
                online.update();
            }
            if (place != null) {
                place.update();
            }
            menu.update();
        };
    }

    @Override

    public void getInput() {
        ins[mode].get();
    }

    @Override
    public void update() {
        ups[mode].update();
    }

    @Override
    public void render() {
        if (runFlag && place != null) {
            place.render();
        } else {
            glClear(GL_COLOR_BUFFER_BIT);
            menu.render();
        }
    }

    @Override
    public void resumeGame() {
        if (place != null) {
            soundResume();
            runFlag = true;
        }
    }

    @Override
    public void startGame() {
        int nrPl = settings.nrPlayers;
        if (!designer) {
            place = new MyPlace(this, Methods.roundHalfUp(settings.SCALE * 10240), Methods.roundHalfUp(settings.SCALE * 10240), Methods.roundHalfUp(settings.SCALE * 64), settings, true);
        } else {
            place = new ObjectPlace(this, Methods.roundHalfUp(settings.SCALE * 10240), Methods.roundHalfUp(settings.SCALE * 10240), Methods.roundHalfUp(settings.SCALE * 64), settings, true);
        }
        Drawer.setPlace(place);
        place.players = new GameObject[4];
        place.playersLength = nrPl;
        if (nrPl == 1) {
            players[0].initialize(4, 4, 56, 56, place, 256, 256);
            players[0].setCamera(new PlayersCamera(players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        } else if (nrPl == 2) {
            players[0].initialize(4, 4, 56, 56, place, 256, 256);
            players[1].initialize(4, 4, 56, 56, place, 512, 1024);
            if (settings.hSplitScreen) {
                players[0].setCamera(new PlayersCamera(players[0], 2, 4, 0));
                players[1].setCamera(new PlayersCamera(players[1], 2, 4, 1));
            } else {
                players[0].setCamera(new PlayersCamera(players[0], 4, 2, 0));
                players[1].setCamera(new PlayersCamera(players[1], 4, 2, 1));
            }
            place.cams[0] = new PlayersCamera(players[0], players[1]);
        } else if (nrPl == 3) {
            players[0].initialize(4, 4, 56, 56, place, 256, 256);
            players[1].initialize(4, 4, 56, 56, place, 512, 1024);
            players[2].initialize(4, 4, 56, 56, place, 1024, 512);
            if (settings.hSplitScreen) {
                players[0].setCamera(new PlayersCamera(players[0], 2, 4, 0));
            } else {
                players[0].setCamera(new PlayersCamera(players[0], 4, 2, 0));
            }
            players[1].setCamera(new PlayersCamera(players[1], 4, 4, 1));
            players[2].setCamera(new PlayersCamera(players[2], 4, 4, 2));
            place.cams[1] = new PlayersCamera(players[0], players[1], players[2]);
        } else if (nrPl == 4) {
            players[0].initialize(4, 4, 56, 56, place, 256, 256);
            players[1].initialize(4, 4, 56, 56, place, 512, 1024);
            players[2].initialize(4, 4, 56, 56, place, 1024, 512);
            players[3].initialize(4, 4, 56, 56, place, 1024, 1024);
            players[0].setCamera(new PlayersCamera(players[0], 4, 4, 0));
            players[1].setCamera(new PlayersCamera(players[1], 4, 4, 1));
            players[2].setCamera(new PlayersCamera(players[2], 4, 4, 2));
            players[3].setCamera(new PlayersCamera(players[3], 4, 4, 3));
            place.cams[2] = new PlayersCamera(players[0], players[1], players[2], players[3]);
        }
        System.arraycopy(players, 0, place.players, 0, 4);
        place.makeShadows();
        mode = 0;
        place.generate(true);
        started = runFlag = true;
        for (int p = 0; p < nrPl; p++) {
            Map map = place.maps.get(0);
            players[p].changeMap(map);
        }
    }

    private void addPlayerOffline(int p) {
        if (p < 4 && place.playersLength < 4) {
            players[p].initialize(4, 4, 56, 56, place, p * 256, p * 265);
            ((Player) place.players[p]).setCamera(new PlayersCamera(place.players[p], 2, 2, p));
            players[p].changeMap(players[0].getMap());
            if (p != place.playersLength) {
                Player tempG = players[place.playersLength];
                GameObject tempP = place.players[place.playersLength];
                players[place.playersLength] = players[p];
                place.players[place.playersLength] = place.players[p];
                players[p] = tempG;
                place.players[p] = tempP;
            }
            place.playersLength++;
            settings.joinSS = false;
            updatePlayersCam();
        }
    }

    private void removePlayerOffline(int p) {
        if (place.playersLength > 1 && !players[p].isFirst()) {
            ((Player) place.players[p]).setPlaceToNull();
            place.players[p].getMap().deleteObject(place.players[p]);
            if (p != place.playersLength - 1) {
                Player tempG = players[place.playersLength - 1];
                GameObject tempP = place.players[place.playersLength - 1];
                players[place.playersLength - 1] = players[p];
                place.players[place.playersLength - 1] = place.players[p];
                players[p] = tempG;
                place.players[p] = tempP;
            }
            place.playersLength--;
            place.ssMode = 0;
            updatePlayersCam();
        }
    }

    private void updatePlayersCam() {
        for (int nr = 0; nr < place.playersLength; nr++) {
            if (place.playersLength == 1) {
                ((PlayersCamera) ((Player) place.players[0]).getCamera()).reInitialize(2, 2);
            } else if (place.playersLength == 2) {
                if (place.cams[0] == null) {
                    place.cams[0] = new PlayersCamera(players[0], players[1]);
                    place.cams[0].setMap(players[0].getMap());
                }
                if (settings.hSplitScreen) {
                    ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(2, 4);
                } else {
                    ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(4, 2);
                }
            } else if (place.playersLength == 3) {
                if (place.cams[1] == null) {
                    place.cams[1] = new PlayersCamera(players[0], players[1], players[2]);
                    place.cams[1].setMap(players[0].getMap());
                }
                if (nr == 0) {
                    if (settings.hSplitScreen) {
                        ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(2, 4);
                    } else {
                        ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(4, 2);
                    }
                } else {
                    ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(4, 4);
                }
            } else {
                if (place.cams[2] == null) {
                    place.cams[2] = new PlayersCamera(players[0], players[1], players[2], players[3]);
                    place.cams[2].setMap(players[0].getMap());
                }
                ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(4, 4);
            }
        }
    }

    @Override
    public void runClient() {
        place = new MyPlace(this, Methods.roundHalfUp(settings.SCALE * 10240), Methods.roundHalfUp(settings.SCALE * 10240), Methods.roundHalfUp(settings.SCALE * 64), settings, false);
        Drawer.setPlace(place);
        place.players = new GameObject[4];
        place.playersLength = 1;
        players[0].initialize(4, 4, 56, 56, place);
        Map map = place.getMapById((short) 0);
        players[0].setCamera(new PlayersCamera(players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        System.arraycopy(players, 0, place.players, 0, 1);
        place.makeShadows();
        place.generate(false);
        started = runFlag = true;
        players[0].changeMap(map);
    }

    @Override
    public void runServer() {
        place = new MyPlace(this, Methods.roundHalfUp(settings.SCALE * 10240), Methods.roundHalfUp(settings.SCALE * 10240), Methods.roundHalfUp(settings.SCALE * 64), settings, true);
        Drawer.setPlace(place);
        place.players = new GameObject[4];
        place.playersLength = 1;
        players[0].initialize(4, 4, 56, 56, place);
        Map map = place.getMapById((short) 0);
        players[0].setCamera(new PlayersCamera(players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        System.arraycopy(players, 0, place.players, 0, 1);
        place.makeShadows();
        place.generate(true);
        started = runFlag = true;
        players[0].changeMap(map);
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
        mode = 0;
    }

    private void soundPause() {
        if (settings.sounds != null) {
            for (Sound s : settings.sounds.getSoundsList()) {
                if (s.isPlaying()) {
                    if (s.isPaused()) {
                        s.setStoped(true);
                    } else {
                        s.fade(0.01, true);
                    }
                } else {
                    s.setStoped(true);
                }
            }
        }
    }

    private void soundResume() {
        if (settings.sounds != null) {
            settings.sounds.getSoundsList().stream().forEach((sound) -> {
                if (sound.isStoped()) {
                    sound.setStoped(false);
                } else {
                    sound.resume();
                    sound.smoothStart(0.5);

                }
            });
        }
    }

    private interface updateType {

        void update();
    }

    private interface getInput {

        void get();
    }
}
