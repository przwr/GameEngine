/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.Main;
import engine.utilities.Delay;
import game.Game;
import game.Settings;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.inputs.Action;
import game.gameobject.inputs.InputKeyBoard;
import game.logic.navmeshpathfinding.navigationmesh.NavigationMeshGenerator;
import game.place.Place;
import game.place.map.Area;
import game.place.map.LoadingMap;
import game.place.map.Map;
import gamecontent.maps.CaveTest;
import gamecontent.maps.GladeMap;
import gamecontent.maps.StoneMap;
import gamecontent.maps.TestMap;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static game.Game.OFFLINE;
import static game.Game.ONLINE;
import static game.place.map.Area.X_IN_TILES;
import static game.place.map.Area.Y_IN_TILES;

/**
 * @author przemek
 */
public class MyPlace extends Place {

    private final Action changeSplitScreenMode;
    private final Action changeSplitScreenJoin;
    private final updater[] updates = new updater[2];
    private final Delay delay = Delay.createInMilliseconds(100, true);
    private ArrayList<Map> unloadedMaps = new ArrayList<>();
    private Map map;

    {
        updates[OFFLINE] = () -> {
//            System.out.println("UPDATE " + System.currentTimeMillis() + ": ");
            updateInputs();
            updateAreasOffline();
            updatePlayersOffline();
            updateMobsOffline();
            updateEntitiesOffline();
            updateObjectsOffline();
            updateInteractiveObjectsOffline();
            dayCycle.updateTime();
        };
        updates[ONLINE] = () -> {
            updateAreasOnline();
            updateMobsOnline();
            updatePlayersOnline();
            updateInteractiveObjectsOnline();
            dayCycle.updateTime();
            throw new UnsupportedOperationException("There is no updateEntitiesOnline");
        };
        delay.start();
    }

    public MyPlace(Game game, int tileSize) {
        super(game, tileSize);
        Area.X_IN_TILES = 32;
        Area.Y_IN_TILES = 24;
        Place.xAreaInPixels = X_IN_TILES * tileSize;
        Place.yAreaInPixels = Y_IN_TILES * tileSize;
//        dayCycle.setTime(12, 30);
//        dayCycle.setTime(21, 30);
        dayCycle.setTime(5, 30);
        changeSplitScreenMode = new Action(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new Action(new InputKeyBoard(Keyboard.KEY_END));
        loadingMap = new LoadingMap(this);
    }

    @Override
    public void generateAsGuest() {
        if (Main.TEST) {
            TestMap test = new TestMap(mapIDCounter++, this, tileSize);
            maps.add(test);
            CaveTest caveTest = new CaveTest(mapIDCounter++, this, tileSize);
            maps.add(caveTest);
        } else {
            GladeMap polana = new GladeMap(mapIDCounter++, this, 4096, 8192, tileSize);
            maps.add(polana);
            StoneMap kamienna = new StoneMap(mapIDCounter++, this, 10240, 10240, tileSize);
            maps.add(kamienna);
        }
        NavigationMeshGenerator.clear();
    }


    @Override
    public void generateAsHost() {
        generateAsGuest();
        maps.stream().forEach(Map::populate);
    }

    @Override
    public void update() {
        updates[game.mode].update();
    }

    private void updateAreasOffline() {
        tempMaps.clear();
        unloadedMaps.clear();
        for (int i = 0; i < playersCount; i++) {
            map = players[i].getMap();
            if (!tempMaps.contains(map)) {
                map.clearAreasToUpdate();
                tempMaps.add(map);
            }
            map.addAreasToUpdate(map.getNearAreas(players[i].getArea()));
        }
        for (Map map : maps) {
            if (!tempMaps.contains(map)) {
                unloadedMaps.add(map);
            }
        }
//        if (game.getMapLoader().isRunning()) { // TODO Wywalić, jak będzie wczytywane z pliku
//            for (Map map : unloadedMaps) {        // TODO Trzeba sprawdzić z aktualnie wczytywanymi mapami, żeby nie wywalać tych, co się właśnie wczytało.
//                maps.remove(map);
//            }
//        }
        addMapsToAdd();
        for (Map mapToUpdate : tempMaps) {
            mapToUpdate.updateAreasToUpdate();
//            if (game.getMapLoader().isRunning())// TODO Wywalić, jak będzie wczytywane z pliku
//                mapToUpdate.unloadUnNeededUpdate();
        }
        game.getMapLoader().updateList(tempMaps);
    }

    private void updateAreasOnline() {
        if (game.online.server != null) {
            updateAreasOffline();
        } else if (game.online.client != null) {
            Map curMap = players[0].getMap();
            curMap.clearAreasToUpdate();
            curMap.addAreasToUpdate(curMap.getNearAreas(players[0].getArea()));
            curMap.updateAreasToUpdate();
        }
    }

    private void updatePlayersOffline() {
        if (playersCount > 1) {
            changeSplitScreenJoin.updateActiveState();
            changeSplitScreenMode.updateActiveState();
            if (changeSplitScreenJoin.isKeyClicked()) {
                Settings.joinSplitScreen = !Settings.joinSplitScreen;
            }
            if (changeSplitScreenMode.isKeyClicked()) {
                changeSSMode = true;
            }
            cameras[playersCount - 2].updateSmooth();
        }
        for (int i = 0; i < playersCount; i++) {
            players[i].update();
        }
    }

    private void updatePlayersOnline() {
        ((Player) players[0]).sendUpdate();
        for (int i = 1; i < playersCount; i++) {
            ((Entity) players[i]).updateSoft();
            ((Entity) players[i]).updateOnline();
        }
    }

    private void updateEntitiesOffline() {
        for (Map m : tempMaps) {
            m.updateEntitesFromAreasToUpdate();
        }
    }

    private void updateMobsOffline() {
        tempMaps.stream().forEach(Map::updateMobsFromAreasToUpdate);
    }


    private void updateObjectsOffline() {
        tempMaps.stream().forEach(Map::updateObjectsFromAreasToUpdate);
    }

    private void updateMobsOnline() {
        if (game.online.server != null) {
            tempMaps.stream().forEach(Map::updateMobsFromAreasToUpdate);
        } else if (game.online.client != null) {
            players[0].getMap().hardUpdateMobsFromAreasToUpdate();
        }
    }

    private void updateInputs() {
//        String soundName = "cricket";
//        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
//            sounds.getSound(soundName).stop();
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
//            sounds.getSound(soundName).addGainModifier(0.05f);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
//            sounds.getSound(soundName).addGainModifier(-0.05f);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
//            sounds.getSound(soundName).resume();
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_6)) {
//            sounds.getSound(soundName).pause();
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_7)) {
//            sounds.getSound(soundName).resume();
//            sounds.getSound(soundName).smoothStart(0.5);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_8)) {
//            sounds.getSound(soundName).addPitch(0.05f);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_9)) {
//            sounds.getSound(soundName).addPitch(-0.05f);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_0)) {
//            sounds.getSound(soundName).fade(0.5, true);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_MINUS)) {
//            sounds.getSound(soundName).fade(0.5, false);
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) {
//            if (delay.isOver()) {
//                delay.start();
//                Settings.framesLimit = Settings.framesLimit == 30 ? 60 : 30;
//                AnalyzerSettings.update();
//            }
//        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
            if (delay.isOver()) {
                delay.start();
                dayCycle.addMinutes(5);
            }
        }
//
// if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
//            if (delay.isOver()) {
//                delay.start();
//                dayCycle.stopTime();
//            }
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
//            if (delay.isOver()) {
//                delay.start();
//                dayCycle.resumeTime();
//            }
//        }
//        if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR)) {
//            if (delay.isOver()) {
//                delay.start();
//                Settings.gameGamma += 0.1f;
//                if (Settings.gameGamma > 3.05f) {
//                    Settings.gameGamma = 1f;
//                }
//                Settings.gameGamma = (Math.round(Settings.gameGamma * 10)) / 10f;
//                Main.refreshGammaAndBrightness();
//            }
//        }
    }

    @Override
    public int getPlayersCount() {
        if (game.mode == 0) {
            return playersCount;
        } else {
            return 1;
        }
    }

    private void updateInteractiveObjectsOffline() {
        tempMaps.stream().forEach(Map::updateInteractiveObjectsFromAreasToUpdate);
    }

    private void updateInteractiveObjectsOnline() {
        System.out.println("Not supported yet.");
    }

    private interface updater {

        void update();
    }
}
