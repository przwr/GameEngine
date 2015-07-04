/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.Drawer;
import engine.Methods;
import game.Game;
import static engine.inout.IO.loadInputFromFile;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.Settings;
import game.place.Map;
import engine.SplitScreen;
import game.place.cameras.PlayersCamera;
import gamedesigner.ObjectPlace;
import gamedesigner.ObjectPlayer;
import java.io.File;
import navmeshpathfinding.PathFindingModule;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

/**
 *
 * @author przemek
 */
public class MyGame extends Game {

    private final getInput[] inputs = new getInput[2];
    private final updateType[] ups = new updateType[2];

    public boolean designer = false;

    public MyGame(String title, Controller[] controllers) {
        super(title);
        SplitScreen.initialzie();
        players = new Player[4];
        players[0] = new MyPlayer(true, "Player 1");
        players[1] = new MyPlayer(false, "Player 2");
        players[2] = new MyPlayer(false, "Player 3");
        players[3] = new MyPlayer(false, "Player 4");
        Settings.update(players[0].controler.getActionsCount(), players, controllers);
        loadInputFromFile(new File("res/input.ini"));
        menu = new MyMenu(this);
        menuPlayer = new MyPlayer(true, "Menu");
        menu.players = new GameObject[1];
        menu.players[0] = menuPlayer;
        menuPlayer.setMenu(menu);
        players[0].setMenu(menu);
        players[1].setMenu(menu);
        players[2].setMenu(menu);
        players[3].setMenu(menu);
        online = new MyGameOnline(this, 3, 4);
        online.initializeChanges();
        initializeMethods();
    }

    private void initializeMethods() {
        inputs[0] = () -> {
            if (!pauseFlag) {
                pause();
                if (running) {
                    Player player;
                    for (int i = 0; i < players.length; i++) {
                        player = players[i];
                        if (player.isMenuOn()) {
                            if (player.isInGame()) {
                                if (!player.isFirst()) {
                                    removePlayerOffline(i);
                                } else {
                                    running = false;
                                    soundPause();
                                }
                            } else {
                                addPlayerOffline(i);
                            }
                        }
                        if (player.isInGame()) {
                            player.getInput();
                        }
                    }
                } else {
                    if (place == null) {
                        menuPlayer.getMenuInput();
                    } else {
                        for (Player player : players) {
                            if (player.isMenuOn()) {
                                menu.back();
                            } else {
                                player.getMenuInput();
                            }
                        }
                    }
                }
            } else {
                resume();
            }
        };
        inputs[1] = () -> {
            if (running) {
                if (players[0].isMenuOn()) {
                    running = false;
                    soundPause();
                }
                if (players[0].isInGame()) {
                    players[0].getInput();
                }
            } else {
                if (place == null) {
                    menuPlayer.getMenuInput();
                } else {
                    players[0].getMenuInput();
                }
            }
        };
        ups[0] = () -> {
            if (!pauseFlag) {
                if (running) {
                    place.update();
                } else {
                    //---------------------- <('.'<) OBJECT DESIGNER ----------------------------//
                    if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
                        setDesignerMode(true);
                        endGame();
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
                Methods.error(Settings.language.menu.Disconnected);
            } else {
                online.update();
            }
            if (place != null) {
                place.update();
            }
            menu.update();
        };
    }

    public void setDesignerMode(boolean designer) {
        this.designer = designer;
        if (designer) {
            players[0] = new ObjectPlayer(true, "Mapper");
            players[0].setMenu(menu);
            Settings.playersCount = 1;
        } else if (!(place instanceof MyPlace)) {
            players[0] = new MyPlayer(true, "Player 1");
            players[0].setMenu(menu);
            loadInputFromFile(new File("res/input.ini"));
        }
    }

    @Override
    public void getInput() {
        inputs[mode].get();
    }

    @Override
    public void update() {
        ups[mode].update();
    }

    @Override
    public void render() {
        if (running && place != null) {
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
            running = true;
        }
    }

    @Override
    public void startGame() {
        int playersCount = Settings.playersCount;
        if (designer) {
            place = new ObjectPlace(this, 64);
        } else {
            place = new MyPlace(this, 64);
        }
        Drawer.place = place;
        place.players = new GameObject[4];
        place.playersCount = playersCount;
        if (playersCount == 1) {
            players[0].initializeSetPosition(56, 104, place, 256, 256);
            players[0].setCamera(new PlayersCamera(players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        } else if (playersCount == 2) {
            players[0].initializeSetPosition(56, 104, place, 256, 256);
            players[1].initializeSetPosition(56, 104, place, 512, 1024);
            if (Settings.horizontalSplitScreen) {
                players[0].setCamera(new PlayersCamera(players[0], 2, 4, 0));
                players[1].setCamera(new PlayersCamera(players[1], 2, 4, 1));
            } else {
                players[0].setCamera(new PlayersCamera(players[0], 4, 2, 0));
                players[1].setCamera(new PlayersCamera(players[1], 4, 2, 1));
            }
            place.cameras[0] = new PlayersCamera(players[0], players[1]);
        } else if (playersCount == 3) {
            players[0].initializeSetPosition(56, 104, place, 256, 256);
            players[1].initializeSetPosition(56, 104, place, 512, 1024);
            players[2].initializeSetPosition(56, 104, place, 1024, 512);
            if (Settings.horizontalSplitScreen) {
                players[0].setCamera(new PlayersCamera(players[0], 2, 4, 0));
            } else {
                players[0].setCamera(new PlayersCamera(players[0], 4, 2, 0));
            }
            players[1].setCamera(new PlayersCamera(players[1], 4, 4, 1));
            players[2].setCamera(new PlayersCamera(players[2], 4, 4, 2));
            place.cameras[1] = new PlayersCamera(players[0], players[1], players[2]);
        } else if (playersCount == 4) {
            players[0].initializeSetPosition(56, 104, place, 256, 256);
            players[1].initializeSetPosition(56, 104, place, 512, 1024);
            players[2].initializeSetPosition(56, 104, place, 1024, 512);
            players[3].initializeSetPosition(56, 104, place, 1024, 1024);
            players[0].setCamera(new PlayersCamera(players[0], 4, 4, 0));
            players[1].setCamera(new PlayersCamera(players[1], 4, 4, 1));
            players[2].setCamera(new PlayersCamera(players[2], 4, 4, 2));
            players[3].setCamera(new PlayersCamera(players[3], 4, 4, 3));
            place.cameras[2] = new PlayersCamera(players[0], players[1], players[2], players[3]);
        }
        System.arraycopy(players, 0, place.players, 0, 4);
        place.makeShadows();
        mode = 0;
        place.generateAsHost();
        for (int p = 0; p < playersCount; p++) {
            Map map = place.maps.get(0);
            players[p].changeMap(map);
        }

        PathFindingModule path = new PathFindingModule();
        Thread thread = new Thread(path);
        thread.start();
       
        started = running = true;
    }

    private void addPlayerOffline(int player) {
        if (player < 4 && place.playersCount < 4) {
            players[player].initializeSetPosition(56, 104, place, player * 256, player * 265);
            ((Player) place.players[player]).setCamera(new PlayersCamera(place.players[player], 2, 2, player));
            players[player].changeMap(players[0].getMap());
            if (player != place.playersCount) {
                Player tempG = players[place.playersCount];
                GameObject tempP = place.players[place.playersCount];
                players[place.playersCount] = players[player];
                place.players[place.playersCount] = place.players[player];
                players[player] = tempG;
                place.players[player] = tempP;
            }
            place.playersCount++;
            Settings.joinSplitScreen = true;
            updatePlayersCameras();
        }
    }

    private void removePlayerOffline(int i) {
        if (place.playersCount > 1 && !players[i].isFirst()) {
            ((Player) place.players[i]).setNotInGame();
            place.players[i].getMap().deleteObject(place.players[i]);
            if (i != place.playersCount - 1) {
                Player tempPlayer = players[place.playersCount - 1];
                GameObject tempP = place.players[place.playersCount - 1];
                players[place.playersCount - 1] = players[i];
                place.players[place.playersCount - 1] = place.players[i];
                players[i] = tempPlayer;
                place.players[i] = tempP;
            }
            place.playersCount--;
            place.splitScreenMode = 0;
            updatePlayersCameras();
        }
    }

    private void updatePlayersCameras() {
        for (int nr = 0; nr < place.playersCount; nr++) {
            if (place.playersCount == 1) {
                ((PlayersCamera) ((Player) place.players[0]).getCamera()).reInitialize(2, 2, 0);
            } else if (place.playersCount == 2) {
                if (place.cameras[0] == null) {
                    place.cameras[0] = new PlayersCamera(players[0], players[1]);
                    place.cameras[0].setMap(players[0].getMap());
                }
                if (Settings.horizontalSplitScreen) {
                    ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(2, 4, 1);
                } else {
                    ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(4, 2, 1);
                }
            } else if (place.playersCount == 3) {
                if (place.cameras[1] == null) {
                    place.cameras[1] = new PlayersCamera(players[0], players[1], players[2]);
                    place.cameras[1].setMap(players[0].getMap());
                }
                if (nr == 0) {
                    if (Settings.horizontalSplitScreen) {
                        ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(2, 4, 2);
                    } else {
                        ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(4, 2, 2);
                    }
                } else {
                    ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(4, 4, 2);
                }
            } else {
                if (place.cameras[2] == null) {
                    place.cameras[2] = new PlayersCamera(players[0], players[1], players[2], players[3]);
                    place.cameras[2].setMap(players[0].getMap());
                }
                ((PlayersCamera) ((Player) place.players[nr]).getCamera()).reInitialize(4, 4, 3);
            }
        }
    }

    @Override
    public void runClient() {
        place = new MyPlace(this, 64);
        place.players = new GameObject[4];
        place.playersCount = 1;
        players[0].initialize(56, 104, place);
        players[0].setCamera(new PlayersCamera(players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        System.arraycopy(players, 0, place.players, 0, 1);
        place.makeShadows();
        place.generateAsGuest();
        Map map = place.getMapById((short) 0);
        players[0].changeMap(map);
        started = running = true;
    }

    @Override
    public void runServer() {
        place = new MyPlace(this, 64);
        place.players = new GameObject[4];
        place.playersCount = 1;
        players[0].initialize(56, 104, place);
        players[0].setCamera(new PlayersCamera(players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        System.arraycopy(players, 0, place.players, 0, 1);
        place.makeShadows();
        place.generateAsHost();
        Map map = place.getMapById((short) 0);
        players[0].changeMap(map);
        started = running = true;
    }

    @Override
    public void endGame() {
        running = started = false;
        place = null;
        Settings.sounds = null;
        for (Player player : players) {
            player.setNotInGame();
        }
        online.cleanUp();
        mode = 0;
    }

    private void soundPause() {
        if (Settings.sounds != null) {
            Settings.sounds.getSoundsList().stream().forEach((sound) -> {
                if (sound.isPlaying()) {
                    if (sound.isPaused()) {
                        sound.setStoped(true);
                    } else {
                        sound.fade(0.01, true);
                    }
                } else {
                    sound.setStoped(true);
                }
            });
        }
    }

    private void soundResume() {
        if (Settings.sounds != null) {
            Settings.sounds.getSoundsList().stream().forEach((sound) -> {
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
