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
import game.place.map.LoadingMap;
import game.place.map.Map;
import gamecontent.maps.GladeMap;
import gamecontent.maps.StoneMap;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.SoundStore;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static game.Game.OFFLINE;
import static game.Game.ONLINE;

/**
 * @author przemek
 */
public class MyPlace extends Place {

    private final Action changeSplitScreenMode;
    private final Action changeSplitScreenJoin;
    private final updater[] updates = new updater[2];
    private final Delay delay = new Delay(100);
    private ArrayList<Map> unloadedMaps = new ArrayList<>();
    private Map map;

    {
        updates[OFFLINE] = () -> {
//            System.out.println("UPDATE " + System.currentTimeMillis() + ": ");
            updateInputs();
            updateAreasOffline();
            updatePlayersOffline();
            updateMobsOffline();
            updateInteractiveObjectsOffline();
            dayCycle.updateTime();
        };
        updates[ONLINE] = () -> {
            updateAreasOnline();
            updateMobsOnline();
            updatePlayersOnline();
            updateInteractiveObjectsOnline();
            dayCycle.updateTime();
        };
        delay.start();
    }

    public MyPlace(Game game, int tileSize) {
        super(game, tileSize);
        dayCycle.setTime(18, 0);
        changeSplitScreenMode = new Action(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new Action(new InputKeyBoard(Keyboard.KEY_END));
        loadingMap = new LoadingMap(this);
    }

    @Override
    public void generateAsGuest() {
        GladeMap polana = new GladeMap(mapIDCounter++, this, 4096, 8192, tileSize);
        maps.add(polana);
        StoneMap kamienna = new StoneMap(mapIDCounter++, this, 10240, 10240, tileSize);
        maps.add(kamienna);
//        sounds.initialize("res");
        SoundStore.get().poll(0);
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
        unloadedMaps.addAll(maps.stream().filter(map -> !tempMaps.contains(map)).collect(Collectors.toList()));
        if (game.getMapLoader().isRunning()) // TODO Wywalić, jak będzie wczytywane z pliku
            unloadedMaps.forEach(maps::remove);
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
            cameras[playersCount - 2].update();
        }
        for (int i = 0; i < playersCount; i++) {
            ((Player) players[i]).update();
        }
    }

    private void updatePlayersOnline() {
        ((Player) players[0]).sendUpdate();
        for (int i = 1; i < playersCount; i++) {
            ((Entity) players[i]).updateSoft();
            ((Entity) players[i]).updateOnline();
        }
    }

    private void updateMobsOffline() {
        tempMaps.stream().forEach(Map::updateMobsFromAreasToUpdate);
    }

    private void updateMobsOnline() {
        if (game.online.server != null) {
            tempMaps.stream().forEach(Map::updateMobsFromAreasToUpdate);
        } else if (game.online.client != null) {
            players[0].getMap().hardUpdateMobsFromAreasToUpdate();
        }
    }

    private void updateInputs() {
//            if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
//                sounds.getSound("MumboMountain").resume();
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
//                sounds.getSound("MumboMountain").pause();
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
//                sounds.getSound("MumboMountain").stop();
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
//                sounds.getSound("MumboMountain").addPitch(0.05f);
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
//                sounds.getSound("MumboMountain").addPitch(-0.05f);
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_6)) {
//                sounds.getSound("MumboMountain").addGainModifier(0.05f);
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_7)) {
//                sounds.getSound("MumboMountain").addGainModifier(-0.05f);
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_8)) {
//                sounds.getSound("MumboMountain").resume();
//                sounds.getSound("MumboMountain").smoothStart(0.5);
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_9)) {
//                sounds.getSound("MumboMountain").fade(0.5, true);
//            }
//            if (Keyboard.isKeyDown(Keyboard.KEY_0)) {
//                sounds.getSound("MumboMountain").fade(0.5, false);
//            }
        if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
            if (delay.isOver()) {
                delay.start();
                dayCycle.addMinutes(5);
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
            if (delay.isOver()) {
                delay.start();
                dayCycle.stopTime();
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
            if (delay.isOver()) {
                delay.start();
                dayCycle.resumeTime();
            }
        }


        if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR)) {
            if (delay.isOver()) {
                delay.start();
                Settings.gameGamma += 0.1f;
                if (Settings.gameGamma > 3.05f) {
                    Settings.gameGamma = 1f;
                }
                Settings.gameGamma = (Math.round(Settings.gameGamma * 10)) / 10f;
                Main.refreshGammaAndBrightness();
            }
        }
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
