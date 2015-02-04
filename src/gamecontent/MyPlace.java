/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import game.gameobject.Mob;
import game.Game;
import game.Settings;
import game.place.cameras.Camera;
import game.place.Place;
import engine.FontBase;
import engine.Methods;
import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.Entity;
import game.gameobject.Player;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Map;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.SoundStore;

/**
 *
 * @author przemek
 */
public class MyPlace extends Place {

    private final Action changeSplitScreenMode;
    private final Action changeSplitScreenJoin;
    private final update[] updates = new update[2];

    public MyPlace(Game game, int tileSize) {
        super(game, tileSize);
        changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_END));
    }

    @Override
    public void generateAsGuest() {
        GladeMap polana = new GladeMap(mapId++, this, Methods.roundHalfUp(Settings.scale * 10240), Methods.roundHalfUp(Settings.scale * 10240), getTileSize());
        StoneMap kamienna = new StoneMap(mapId++, this, Methods.roundHalfUp(Settings.scale * 10240), Methods.roundHalfUp(Settings.scale * 10240), getTileSize());
        maps.add(polana);
        maps.add(kamienna);
        // sounds.initialize("res");
        this.red = 0.75f;
        this.green = 0.75f;
        this.blue = 0.75f;
        fonts = new FontBase(20);
        fonts.add("Amble-Regular", (int) (Settings.scale * 24));
        SoundStore.get().poll(0);
        initMethods();
    }

    @Override
    public void generateAsHost() {
        GladeMap polana = new GladeMap(mapId++, this, Methods.roundHalfUp(Settings.scale * 10240), Methods.roundHalfUp(Settings.scale * 10240), getTileSize());
        StoneMap kamienna = new StoneMap(mapId++, this, Methods.roundHalfUp(Settings.scale * 10240), Methods.roundHalfUp(Settings.scale * 10240), getTileSize());
        maps.add(polana);
        maps.add(kamienna);
        //   sounds.initialize("res");
        this.red = 0.75f;
        this.green = 0.75f;
        this.blue = 0.75f;
        fonts = new FontBase(20);
        fonts.add("Amble-Regular", (int) (Settings.scale * 24));
        SoundStore.get().poll(0);
        initMethods();
    }

    @Override
    public void update() {
        updates[game.mode].update();
    }

    private void initMethods() {
        updates[0] = () -> {
            if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
                sounds.getSound("MumboMountain").resume();
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_2)) {
                sounds.getSound("MumboMountain").pause();
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_3)) {
                sounds.getSound("MumboMountain").stop();
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_4)) {
                sounds.getSound("MumboMountain").addPitch(0.05f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_5)) {
                sounds.getSound("MumboMountain").addPitch(-0.05f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_6)) {
                sounds.getSound("MumboMountain").addGainModifier(0.05f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_7)) {
                sounds.getSound("MumboMountain").addGainModifier(-0.05f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_8)) {
                sounds.getSound("MumboMountain").resume();
                sounds.getSound("MumboMountain").smoothStart(0.5);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_9)) {
                sounds.getSound("MumboMountain").fade(0.5, true);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_0)) {
                sounds.getSound("MumboMountain").fade(0.5, false);
            }

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
            maps.stream().forEach((map) -> {
                map.getSolidMobs().stream().forEach((mob) -> {
                    mob.update();
                });
            });
        };
        updates[1] = () -> {
            tempMaps.clear();
            Map map;
            if (game.online.server != null) {
                for (int i = 0; i < playersCount; i++) {
                    map = players[i].getMap();
                    if (!tempMaps.contains(map)) {
                        for (Mob mob : map.getSolidMobs()) {
                            mob.update();
                        }
                        tempMaps.add(map);
                    }
                }
            } else if (game.online.client != null) {
                map = players[0].getMap();
                for (Mob mob : map.getSolidMobs()) {
                    mob.updateHard();
                }
            }
            ((Player) players[0]).sendUpdate();
            for (int i = 1; i < playersCount; i++) {
                ((Entity) players[i]).updateSoft();
                ((Entity) players[i]).updateOnline();
            }
        };
    }

    @Override
    public int getPlayersCount() {
        if (game.mode == 0) {
            return playersCount;
        } else {
            return 1;
        }
    }

    @Override
    protected void renderText(Camera cam) {
    }

    private interface update {

        void update();
    }
}
