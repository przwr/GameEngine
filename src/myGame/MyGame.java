/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame;

import engine.Methods;
import engine.Sound;
import game.Game;
import game.IO;
import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.cameras.PlayersCamera;
import java.io.File;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author przemek
 */
public class MyGame extends Game {

    private final getInput[] ins = new getInput[2];
    private final update[] ups = new update[2];
    private boolean PAUSE = true;

//    private final change[] changes = new change[3];
//    private final boolean[] isChanged = new boolean[3];
//    private final NewMPlayer newPls[] = new NewMPlayer[3];
//    private final int removeIDs[] = new int[3];
//    private final PacketMPlayerUpdate[] plUps = new PacketMPlayerUpdate[4];
    public MyGame(String title, Settings settings, Controller[] controllers) {
        super(title, settings, controllers);
        players = new Player[4];
        players[0] = new MyPlayer(true, "Player 1");
        players[1] = new MyPlayer(false, "Player 2");
        players[2] = new MyPlayer(false, "Player 3");
        players[3] = new MyPlayer(false, "Player 4");
        settings.Up(players[1].ctrl.getActionsCount(), players, controllers);
        IO.ReadFile(new File("res/input.ini"), settings, false);
        menu = new MyMenu(this, 2, 2, 1, settings);
        menuPl = new MyPlayer(true, "Menu");
        menu.players = new GameObject[1];
        menu.players[0] = menuPl;
        menuPl.addMenu(menu);
        players[0].addMenu(menu);
        players[1].addMenu(menu);
        players[2].addMenu(menu);
        players[3].addMenu(menu);
        online = new MyGameOnline(this, 3, 4);
        online.initChanges();
        initMethods();
    }

    private void initMethods() {
        ins[0] = new getInput() {
            @Override
            public void getInput() {
//                if (!pauseFlag) {
//                    pause();
                if (runFlag) {
                    Player pl;
                    for (int p = 0; p < players.length; p++) {
                        pl = players[p];
                        if (pl.isMenuOn()) {
                            if (pl.getPlace() != null) {
                                if (!pl.isFirst) {
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
//                } else {
//                    resume();
//                }
            }
        };
        ins[1] = new getInput() {
            @Override
            public void getInput() {
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
            }
        };
        ups[0] = new update() {
            @Override
            public void update() {
//                if (!pauseFlag) {
                if (runFlag) {
                    place.update();
                } else {
                    menu.update();
                }
//                }
            }
        };
        ups[1] = new update() {
            @Override
            public void update() {
                if (online.client != null && !online.client.isConnected) {
                    endGame();
                    mode = 0;
                    Methods.Error(settings.language.Disconnected);
                }
                online.up();
                if (runFlag) {
                    place.update();
                } else {
                    if (place != null) {
                        place.update();
                    }
                    menu.update();
                }
            }
        };
    }

    @Override

    public void getInput() {
        ins[mode].getInput();
    }

    @Override
    public void update() {
        ups[mode].update();
    }

    @Override
    public void render() {
        if (runFlag) {
            place.render();
        } else {
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
        place = new MyPlace(this, (int) (settings.SCALE * 2304), (int) (settings.SCALE * 2304), (int) (settings.SCALE * 64), settings);
        place.players = new GameObject[4];
        place.playersLength = nrPl;
        if (nrPl == 1) {
            players[0].init(4, 4, 56, 56, 64, 64, place, 256, 256);
            players[0].addCamera(new PlayersCamera(place, players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        } else if (nrPl == 2) {
            players[0].init(4, 4, 56, 56, 64, 64, place, 256, 256);
            players[1].init(4, 4, 56, 56, 64, 64, place, 512, 1024);
            if (settings.hSplitScreen) {
                players[0].addCamera(new PlayersCamera(place, players[0], 2, 4, 0));
                players[1].addCamera(new PlayersCamera(place, players[1], 2, 4, 1));
            } else {
                players[0].addCamera(new PlayersCamera(place, players[0], 4, 2, 0));
                players[1].addCamera(new PlayersCamera(place, players[1], 4, 2, 1));
            }
            place.cams[0] = new PlayersCamera(place, players[0], players[1]);
        } else if (nrPl == 3) {
            players[0].init(4, 4, 56, 56, 64, 64, place, 256, 256);
            players[1].init(4, 4, 56, 56, 64, 64, place, 512, 1024);
            players[2].init(4, 4, 56, 56, 64, 64, place, 1024, 512);
            if (settings.hSplitScreen) {
                players[0].addCamera(new PlayersCamera(place, players[0], 2, 4, 0));
            } else {
                players[0].addCamera(new PlayersCamera(place, players[0], 4, 2, 0));
            }
            players[1].addCamera(new PlayersCamera(place, players[1], 4, 4, 1));
            players[2].addCamera(new PlayersCamera(place, players[2], 4, 4, 2));
            place.cams[1] = new PlayersCamera(place, players[0], players[1], players[2]);
        } else if (nrPl == 4) {
            players[0].init(4, 4, 56, 56, 64, 64, place, 256, 256);
            players[1].init(4, 4, 56, 56, 64, 64, place, 512, 1024);
            players[2].init(4, 4, 56, 56, 64, 64, place, 1024, 512);
            players[3].init(4, 4, 56, 56, 64, 64, place, 1024, 1024);
            players[0].addCamera(new PlayersCamera(place, players[0], 4, 4, 0));
            players[1].addCamera(new PlayersCamera(place, players[1], 4, 4, 1));
            players[2].addCamera(new PlayersCamera(place, players[2], 4, 4, 2));
            players[3].addCamera(new PlayersCamera(place, players[3], 4, 4, 3));
            place.cams[2] = new PlayersCamera(place, players[0], players[1], players[2], players[3]);
        }
        System.arraycopy(players, 0, place.players, 0, 4);
        place.makeShadows();
        mode = 0;
        started = runFlag = true;
    }

    private void addPlayerOffline(int p) {
        if (p < 4 && place.playersLength < 4) {
            players[p].init(4, 4, 56, 56, 64, 64, place, p * 256, p * 265);
            ((Player) place.players[p]).addCamera(new PlayersCamera(place, place.players[p], 2, 2, p));
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
        if (place.playersLength > 1 && !players[p].isFirst) {
            ((Player) place.players[p]).setPlaceToNull();
            place.deleteObj(place.players[p]);
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
        for (int c = 0; c < place.playersLength; c++) {
            if (place.playersLength == 1) {
                ((PlayersCamera) ((Player) place.players[0]).getCam()).reInit(2, 2);
            } else if (place.playersLength == 2) {
                if (place.cams[0] == null) {
                    place.cams[0] = new PlayersCamera(place, players[0], players[1]);
                }
                if (settings.hSplitScreen) {
                    ((PlayersCamera) ((Player) place.players[c]).getCam()).reInit(2, 4);
                } else {
                    ((PlayersCamera) ((Player) place.players[c]).getCam()).reInit(4, 2);
                }
            } else if (place.playersLength == 3) {
                if (place.cams[1] == null) {
                    place.cams[1] = new PlayersCamera(place, players[0], players[1], players[2]);
                }
                if (c == 0) {
                    if (settings.hSplitScreen) {
                        ((PlayersCamera) ((Player) place.players[c]).getCam()).reInit(2, 4);
                    } else {
                        ((PlayersCamera) ((Player) place.players[c]).getCam()).reInit(4, 2);
                    }
                } else {
                    ((PlayersCamera) ((Player) place.players[c]).getCam()).reInit(4, 4);
                }
            } else {
                if (place.cams[2] == null) {
                    place.cams[2] = new PlayersCamera(place, players[0], players[1], players[2], players[3]);
                }
                ((PlayersCamera) ((Player) place.players[c]).getCam()).reInit(4, 4);
            }
        }
    }

    @Override
    public void runClient() {
        place = new MyPlace(this, (int) (settings.SCALE * 2304), (int) (settings.SCALE * 2304), (int) (settings.SCALE * 64), settings);
        place.players = new GameObject[4];
        place.playersLength = 1;
        players[0].init(4, 4, 56, 56, 64, 64, place);
        players[0].addCamera(new PlayersCamera(place, players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        System.arraycopy(players, 0, place.players, 0, 1);
        place.makeShadows();
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

    private interface update {

        void update();
    }

    private interface getInput {

        void getInput();
    }
}
