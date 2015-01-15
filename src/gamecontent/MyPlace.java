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
import engine.FontsHandler;
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
    private final Place place;
    private final update[] ups = new update[2];

    public MyPlace(Game game, int width, int height, int tileSize, Settings settnig, boolean isHost) {
        super(game, width, height, tileSize, settnig);
        place = this;
        changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_END));
        generate(isHost);
    }

    private void generate(boolean isHost) {
        PolanaMap p = new PolanaMap(mapId++, this, width, height, tileSize);
        KamiennaMap k = new KamiennaMap(mapId++, this, width, height, tileSize);
        if (isHost) {
            for (int i = 0; i < 1000; i++) {
                p.addObj(new MyMob(192 + 192 * (i % 50), 2048 + 192 * (i / 50), 0, 8, 128, 112, 4, 512, "rabbit", this, true, p.mobID++));
            }
        }
        maps.add(p);
        maps.add(k);
        //sounds.init("res", settings);
        this.r = 0.75f;
        this.g = 0.75f;
        this.b = 0.75f;
        fonts = new FontsHandler(20);
        fonts.add("Amble-Regular", (int) (settings.SCALE * 24));
        SoundStore.get().poll(0);
        initMethods();
    }

    @Override
    public void update() {
        ups[game.mode].up();
    }

    private void initMethods() {
        ups[0] = new update() {
            @Override
            public void up() {
                /*
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
                 */
                if (playersLength > 1) {
                    changeSplitScreenJoin.act();
                    changeSplitScreenMode.act();
                    if (changeSplitScreenJoin.isOn()) {
                        settings.joinSS = !settings.joinSS;
                    }
                    if (changeSplitScreenMode.isOn()) {
                        changeSSMode = true;
                    }
                    cams[playersLength - 2].update();
                }
                for (int i = 0; i < playersLength; i++) {
                    ((Player) players[i]).update();
                }
                for (Map m : maps) {
                    for (Mob mob : m.sMobs) {
                        mob.update(game.place);
                    }
                }
            }
        };
        ups[1] = new update() {
            @Override
            public void up() {
                tempMaps.clear();
                Map map;
                if (game.online.server != null) {
                    for (int i = 0; i < playersLength; i++) {
                        map = players[i].getMap();
                        if (!tempMaps.contains(map)) {
                            for (Mob mob : map.sMobs) {
                                mob.update(place);
                            }
                            tempMaps.add(map);
                        }
                    }
                } else if (game.online.client != null) {
                    map = players[0].getMap();
                    for (Mob mob : map.sMobs) {
                        mob.updateHard();
                    }
                }
                ((Player) players[0]).sendUpdate(place);
                for (int i = 1; i < playersLength; i++) {
                    ((Entity) players[i]).updateSoft();
                    ((Entity) players[i]).updateOnline();
                }
            }
        };
    }

    @Override
    public int getPlayersLenght() {
        if (game.mode == 0) {
            return playersLength;
        } else {
            return 1;
        }
    }

    @Override
    protected void renderText(Camera cam) {
    }

    private interface update {

        void up();
    }
}
