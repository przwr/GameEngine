/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.Main;
import engine.utilities.Drawer;
import engine.utilities.ErrorHandler;
import engine.utilities.Methods;
import engine.utilities.Timer;
import engine.view.Renderer;
import engine.view.SplitScreen;
import game.Game;
import game.Settings;
import game.gameobject.entities.Player;
import game.logic.maploader.MapLoaderModule;
import game.logic.navmeshpathfinding.PathFindingModule;
import game.place.Place;
import game.place.cameras.PlayersCamera;
import game.place.map.Map;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import gamecontent.environment.Bush;
import gamecontent.environment.GrassClump;
import gamecontent.environment.Tree;
import gamedesigner.ObjectPlace;
import gamedesigner.ObjectPlayer;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.io.File;

import static engine.systemcommunication.IO.loadInputFromFile;

/**
 * @author przemek
 */
public class MyGame extends Game {

    private final getInput[] inputs = new getInput[2];
    private final updateType[] ups = new updateType[2];
    public TextPiece loading;
    Timer timer = new Timer("Render", 300);
    private boolean designer = false;

    public MyGame(String title, Controller[] controllers) {
        super(title);
        font = TextMaster.getFont("Lato-Regular");
        loading = new TextPiece(Settings.language.menu.Loading + " . . .", 36, font, Display.getWidth(), true);
        SplitScreen.initialise();
        players = new Player[4];
        players[0] = new MyPlayer(true, "Player 1");
        players[1] = new MyPlayer(false, "Player 2");
        players[2] = new MyPlayer(false, "Player 3");
        players[3] = new MyPlayer(false, "Player 4");
        Settings.update(players[0].getController().getActionsCount(), players, controllers);
        loadInputFromFile(new File("res/input.ini"));
        menu = new MyMenu(this);
        menuPlayer = new MyPlayer(true, "Menu");
        menu.players = new Player[1];
        menu.players[0] = menuPlayer;
        menuPlayer.setMenu(menu);
        pathFinding = new PathFindingModule();
        mapLoader = new MapLoaderModule(this);
        players[0].setMenu(menu);
        players[1].setMenu(menu);
        players[2].setMenu(menu);
        players[3].setMenu(menu);
        online = new MyGameOnline(this, 3, 4);
        online.initializeChanges();
        initializeMethods();
    }

    private void initializeMethods() {
        inputs[OFFLINE] = () -> {
            if (!pauseFlag) {
                pause();
                if (running) {
                    Player player;
                    for (int i = 0; i < players.length; i++) {
                        player = players[i];
                        if (player.isMenuOn()) {
                            if (player.isInGame()) {
                                if (player.isNotFirst()) {
                                    removePlayerOffline(i);
                                } else {
                                    running = false;
                                    Main.backgroundLoader.resetFirstLoaded();
                                    soundPause();
                                    Place.getDayCycle().stopTime();
                                }
                            } else {
                                addPlayerOffline(i);
                            }
                        }
                        if (player.isInGame()) {
                            player.getInput();
                        }
                    }
                } else if (place == null) {
                    if (menuPlayer.isMenuOn()) {
                        menu.back();
                    } else {
                        menuPlayer.getMenuInput();
                    }
                } else {
                    for (Player player : players) {
                        if (!menu.isMapping && player.isMenuOn()) {
                            menu.back();
                        } else {
                            player.getMenuInput();
                        }
                    }
                }
            } else {
                resume();
            }
        };
        ups[OFFLINE] = () -> {
            if (!pauseFlag) {
                if (running) {
                    place.update();
                } else {
                    //---------------------- <('.'<) OBJECT DESIGNER ----------------------------//
                    if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
                        endGame();
                        setDesignerMode(true);
                        startGame();
                        menu.setDefaultRoot();
                    }
                    //---------------------------------------------------------------------------//
                    if (Main.key.keyPressed(Keyboard.KEY_F2)) {
                        Main.TEST = !Main.TEST;
                        System.out.println("Test is now " + (Main.TEST ? "on" : "off"));
                    }
                    menu.update();
                }
            }
        };
        inputs[ONLINE] = () -> {
            if (running) {
                if (players[0].isMenuOn()) {
                    running = false;
                    soundPause();
                }
                if (players[0].isInGame()) {
                    players[0].getInput();
                }
            } else if (place == null) {
                if (menuPlayer.isMenuOn()) {
                    menu.back();
                } else {
                    menuPlayer.getMenuInput();
                }
            } else if (!menu.isMapping && players[0].isMenuOn()) {
                menu.back();
            } else {
                players[0].getMenuInput();
            }
        };
        ups[ONLINE] = () -> {
            if ((online.client == null && online.server == null) || (online.client != null && !online.client.isConnected)) {
                endGame();
                ErrorHandler.error(Settings.language.menu.Disconnected);
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
            players[0] = new ObjectPlayer(true, "Mapper-Player");
            Settings.playersCount = 1;
            Settings.players[0] = players[0];
        } else if (!(place instanceof MyPlace)) {
            players[0] = new MyPlayer(true, "Player 1");
        }
        loadInputFromFile(new File("res/input.ini"));
        players[0].setMenu(menu);
        Settings.players[0] = players[0];
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
//        timer.start();
        if (running && place != null) {
            place.render();
        } else {
            Drawer.clearScreen(0);
            menu.render();
        }
//        timer.stop();
    }

    @Override
    public void resumeGame() {
        if (place != null) {
            soundResume();
            if (mode == OFFLINE) {
                Place.getDayCycle().resumeTime();
            }
            running = true;
        }
    }

    @Override
    public void startGame() {
        Main.backgroundLoader.resetFirstLoaded();
        Drawer.setShaders();
        //Settings.sounds.init();
        int playersCount = Settings.playersCount;
        if (designer) {
            place = new ObjectPlace(this, 64);
        } else {
            place = new MyPlace(this, 64);
        }
        place.players = new Player[4];
        place.playersCount = playersCount;
        Place.progress = 2;
        loading(1);
        switch (playersCount) {
            case 1:
                if (Main.TEST) {
                    players[0].initializeSetPosition(56, 104, place, 1676, 1197);
                } else {
                    players[0].initializeSetPosition(56, 104, place, 256, 256);
//                    players[0].initializeSetPosition(56, 104, place, 1024, 3000);
                }
                players[0].setCamera(new PlayersCamera(players[0], 2, 2, 0)); // 2 i 2 to tryb SS
                break;
            case 2:
                players[0].initializeSetPosition(56, 104, place, 256, 256);
                players[1].initializeSetPosition(56, 104, place, 512, 1024);
                if (Settings.horizontalSplitScreen) {
                    players[0].setCamera(new PlayersCamera(players[0], 2, 4, 0));
                    players[1].setCamera(new PlayersCamera(players[1], 2, 4, 1));
                } else {
                    players[0].setCamera(new PlayersCamera(players[0], 4, 2, 0));
                    players[1].setCamera(new PlayersCamera(players[1], 4, 2, 1));
                }
                Settings.joinSplitScreen = true;
                break;
            case 3:
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
                Settings.joinSplitScreen = true;
                break;
            case 4:
                players[0].initializeSetPosition(56, 104, place, 256, 256);
                players[1].initializeSetPosition(56, 104, place, 512, 1024);
                players[2].initializeSetPosition(56, 104, place, 1024, 512);
                players[3].initializeSetPosition(56, 104, place, 1024, 1024);
                players[0].setCamera(new PlayersCamera(players[0], 4, 4, 0));
                players[1].setCamera(new PlayersCamera(players[1], 4, 4, 1));
                players[2].setCamera(new PlayersCamera(players[2], 4, 4, 2));
                players[3].setCamera(new PlayersCamera(players[3], 4, 4, 3));
                Settings.joinSplitScreen = true;
                break;
            default:
                break;
        }
        loading(2);
        System.arraycopy(players, 0, place.players, 0, 4);
        place.makeShadows();
        mode = 0;
        place.generateAsHost();
        for (int i = 0; i < playersCount; i++) {
            Map map = place.maps.get(0);
            players[i].changeMap(map, players[i].getX(), players[i].getY());
        }
        updatePlayersCameras();
        pathThread = new Thread(pathFinding);
        pathThread.start();
        pathThread.setPriority(Thread.MIN_PRIORITY);
        mapThread = new Thread(mapLoader);
        mapThread.start();
        mapThread.setPriority(Thread.MIN_PRIORITY);
        started = running = true;
    }

    public void loading(int progress) {
        Drawer.clearScreen(0);
        showLoading(progress);
        Display.sync(60);
        Display.update();
    }

    @Override
    public void showLoading(int progress) {
        Drawer.clearScreen(0);
        TextMaster.renderFirstCharactersOnce(loading, 0, Display.getHeight() / 2 - (int) (Settings.nativeScale * loading.getFontSize() / 2), loading
                .getTextString().length() - 6 + progress);
    }

    private void addPlayerOffline(int i) {
        if (i < 4 && place.playersCount < 4) {
            if (place instanceof ObjectPlace && i != 0) {
                players[i].initializeSetPosition(56, 104, place, players[0].getX(), players[0].getY());
            } else {
                players[i].initializeSetPosition(56, 104, place, i * 256, i * 265);
            }
            place.players[i].setCamera(new PlayersCamera(place.players[i], 2, 2, i));
            players[i].changeMap(players[0].getMap(), players[i].getX(), players[i].getY());
            players[i].updateAreaPlacement();
            if (i != place.playersCount) {
                Player tempG = players[place.playersCount];
                Player tempP = place.players[place.playersCount];
                players[place.playersCount] = players[i];
                place.players[place.playersCount] = place.players[i];
                players[i] = tempG;
                place.players[i] = tempP;
            }
            place.playersCount++;
            Settings.joinSplitScreen = true;
            updatePlayersCameras();
        }
    }

    private void removePlayerOffline(int i) {
        if (place.playersCount > 1 && players[i].isNotFirst()) {
            place.players[i].setNotInGame();
            place.players[i].clearLights();
            place.players[i].getMap().deleteObject(place.players[i]);
            if (i != place.playersCount - 1) {
                Player tempPlayer = players[place.playersCount - 1];
                Player tempP = place.players[place.playersCount - 1];
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
            switch (place.playersCount) {
                case 1:
                    ((PlayersCamera) place.players[0].getCamera()).reInitialize(2, 2, 0);
                    break;
                case 2:
                    if (place.cameras[0] == null) {
                        place.cameras[0] = new PlayersCamera(players[0], players[1]);
                        place.cameras[0].setMap(players[0].getMap());
                    } else {
                        place.cameras[0].refreshOwners(players, place.playersCount);
                    }
                    if (Settings.horizontalSplitScreen) {
                        ((PlayersCamera) place.players[nr].getCamera()).reInitialize(2, 4, 1);
                    } else {
                        ((PlayersCamera) place.players[nr].getCamera()).reInitialize(4, 2, 1);
                    }
                    break;
                case 3:
                    if (place.cameras[1] == null) {
                        place.cameras[1] = new PlayersCamera(players[0], players[1], players[2]);
                        place.cameras[1].setMap(players[0].getMap());
                    } else {
                        place.cameras[1].refreshOwners(players, place.playersCount);
                    }
                    if (nr == 0) {
                        if (Settings.horizontalSplitScreen) {
                            ((PlayersCamera) place.players[nr].getCamera()).reInitialize(2, 4, 2);
                        } else {
                            ((PlayersCamera) place.players[nr].getCamera()).reInitialize(4, 2, 2);
                        }
                    } else {
                        ((PlayersCamera) place.players[nr].getCamera()).reInitialize(4, 4, 2);
                    }
                    break;
                default:
                    if (place.cameras[2] == null) {
                        place.cameras[2] = new PlayersCamera(players[0], players[1], players[2], players[3]);
                        place.cameras[2].setMap(players[0].getMap());
                    } else {
                        place.cameras[2].refreshOwners(players, place.playersCount);
                    }
                    ((PlayersCamera) place.players[nr].getCamera()).reInitialize(4, 4, 3);
                    break;
            }
        }
    }

    @Override
    public void runClient() {
        place = new MyPlace(this, 64);
        place.players = new Player[4];
        place.playersCount = 1;
        players[0].initialize(56, 104, place);
        players[0].setCamera(new PlayersCamera(players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        System.arraycopy(players, 0, place.players, 0, 1);
        place.makeShadows();
        place.generateAsGuest();
        Map map = place.getMapById((short) 0);
        players[0].changeMap(map, players[0].getX(), players[0].getY());
        players[0].updateAreaPlacement();
        started = running = true;
    }

    @Override
    public void runServer() {
        place = new MyPlace(this, 64);
        place.players = new Player[4];
        place.playersCount = 1;
        players[0].initialize(56, 104, place);
        players[0].setCamera(new PlayersCamera(players[0], 2, 2, 0)); // 2 i 2 to tryb SS
        System.arraycopy(players, 0, place.players, 0, 1);
        place.makeShadows();
        place.generateAsHost();
        Map map = place.getMapById((short) 0);
        players[0].changeMap(map, players[0].getX(), players[0].getY());
        players[0].updateAreaPlacement();
        pathThread = new Thread(pathFinding);
        pathThread.start();
        pathThread.setPriority(Thread.MIN_PRIORITY);
        started = running = true;
    }

    @Override
    public void endGame() {
        running = started = false;
        if (Settings.sounds != null)
            Settings.sounds.clear();
        PathFindingModule.stop();
        pathThread = null;
        mapLoader.stop();
        mapThread = null;
        online.cleanUp();
        online = null;
        online = new MyGameOnline(this, 3, 4);
        online.initializeChanges();
        Tree.instances.clear();
        Tree.fbos.clear();
        Bush.instances.clear();
        Bush.fbos.clear();
        GrassClump.instances.clear();
        GrassClump.fbos.clear();
        Place.currentCamera = null;
        Renderer.place = null;
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                players[i].clearLights();
            }
        }
        players[0] = new MyPlayer(true, "Player 1");
        players[1] = new MyPlayer(false, "Player 2");
        players[2] = new MyPlayer(false, "Player 3");
        players[3] = new MyPlayer(false, "Player 4");
        players[0].setMenu(menu);
        players[1].setMenu(menu);
        players[2].setMenu(menu);
        players[3].setMenu(menu);
        if (place != null) {
            place.cleanUp();
        }
        place = null;
        mode = 0;
        Main.restartBackGroundLoader();
        Methods.gc();
    }

    private void soundPause() {
        if (Settings.sounds != null) {
            Settings.sounds.pauseAllSounds();
        }
    }

    private void soundResume() {
        if (Settings.sounds != null) {
            Settings.sounds.resumeAllSounds();
        }
    }

    private interface updateType {

        void update();
    }

    private interface getInput {

        void get();
    }
}
