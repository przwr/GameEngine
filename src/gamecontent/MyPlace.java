/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.Delay;
import game.Game;
import game.Settings;
import game.place.Place;
import engine.FontBase;
import engine.Main;
import static game.Game.OFFLINE;
import static game.Game.ONLINE;
import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.Entity;
import game.gameobject.Player;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Map;
import navmeshpathfinding.NavigationMeshGenerator;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.SoundStore;

/**
 *
 * @author przemek
 */
public class MyPlace extends Place {

    private final Action changeSplitScreenMode;
    private final Action changeSplitScreenJoin;
    private final updater[] updates = new updater[2];
    private final Delay delay = new Delay(100);
    private Map map;

    {
        updates[OFFLINE] = () -> {
            updateInputs();
            updateAreas();
            updatePlayersOffline();
            updateMobsOffline();
            dayCycle.updateTime();
        };
        updates[ONLINE] = () -> {
            updateMobsOnline();
            updatePlayersOnline();
            dayCycle.updateTime();
        };
        delay.start();
    }

    public MyPlace(Game game, int tileSize) {
        super(game, tileSize);
        dayCycle.setTime(7, 30);
        changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_END));
    }

    @Override
    public void generateAsGuest() {
        GladeMap polana = new GladeMap(mapIDcounter++, this, 4096, 8192, tileSize);
        maps.add(polana);
        StoneMap kamienna = new StoneMap(mapIDcounter++, this, 10240, 10240, tileSize);
        maps.add(kamienna);
//        sounds.initialize("res");
        SoundStore.get().poll(0);
        NavigationMeshGenerator.clear();
    }

    @Override
    public void generateAsHost() {
        generateAsGuest();
        maps.stream().forEach((map) -> {
            map.populate();
        });
    }

    @Override
    public void update() {
        updates[game.mode].update();
    }

    private void updateAreas() {
        tempMaps.clear();
        for (int i = 0; i < playersCount; i++) {
            map = players[i].getMap();
            if (!tempMaps.contains(map)) {
                map.clearAreasToUpdate();
                tempMaps.add(map);
            }
            map.addAreasToUpdate(map.getNearAreas(players[i].getArea()));
        }
    }

    private void updatePlayersOffline() {
        if (playersCount > 1) {
            changeSplitScreenJoin.act();
            changeSplitScreenMode.act();
            if (changeSplitScreenJoin.isOn()) {
                Settings.joinSplitScreen = !Settings.joinSplitScreen;
            }
            if (changeSplitScreenMode.isOn()) {
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
        tempMaps.stream().forEach((map) -> {
            map.updateMobsFromAreasToUpdate();
        });
    }

    private void updateMobsOnline() {
        if (game.online.server != null) {
            updateAreas();
            tempMaps.stream().forEach((map) -> {
                map.updateMobsFromAreasToUpdate();
            });
        } else if (game.online.client != null) {
            Map map = players[0].getMap();
            map.clearAreasToUpdate();
            map.addAreasToUpdate(map.getNearAreas(players[0].getArea()));
            map.hardUpdateMobsFromAreasToUpdate();
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

        if (Keyboard.isKeyDown(Keyboard.KEY_PRIOR)) {
            Main.refreshGamma();
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_NEXT)) {
            Main.resetGamma();
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

    private interface updater {

        void update();
    }
}
