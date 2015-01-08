/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import collision.Area;
import collision.Line;
import game.gameobject.Mob;
import game.Game;
import game.Settings;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.Tile;
import engine.FontsHandler;
import engine.Methods;
import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.Entity;
import game.gameobject.Player;
import game.gameobject.inputs.InputKeyBoard;
import game.place.FGTile;
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
    private FGTile fgt;

    private final update[] ups = new update[2];

    private final Tile GRASS = new Tile(getSpriteSheet("tlo"), sTile, 1, 8, this);
    final Tile ROCK = new Tile(getSpriteSheet("tlo"), sTile, 1, 1, this);

    public MyPlace(Game game, int width, int height, int tileSize, Settings settnig, boolean isHost) {
        super(game, width, height, tileSize, settnig);
        place = this;
        changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_END));
        generate(isHost);
    }

    private void generate(boolean isHost) {
        PolanaMap p = new PolanaMap(this, width, height, sTile);
        KamiennaMap k = new KamiennaMap(this, width, height, sTile);
        if (isHost) {
            for (int i = 0; i < 1000; i++) {
                p.addObj(new MyMob(192 + 192 * (i % 50), 1440 + 192 * (i / 50), 0, 8, 128, 112, 4, 512, "rabbit", this, true, mobID++));
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
        ups[game.mode].update();
    }

    @Override
    protected void renderText(Camera cam) {
        /*       for (int p = 0; p < playersLength; p++) {
         if (cam.getSY() <= players[p].getY() + (players[p].Height() + fonts.write(0).getHeight()) && cam.getEY() >= players[p].getY() - (players[p].Height() << 2)
         && cam.getSX() <= players[p].getX() + (fonts.write(0).getWidth(players[p].getName())) && cam.getEX() >= players[p].getX() - (fonts.write(0).getWidth(players[p].getName()))) {
         ((MyPlayer) players[p]).renderName(this, cam);
         }
         }
         for (Mob mob : sMobs) {
         if (cam.getSY() <= mob.getY() + (mob.Height() + fonts.write(0).getHeight()) && cam.getEY() >= mob.getY() - (mob.Height() << 2)
         && cam.getSX() <= mob.getX() + (fonts.write(0).getWidth(mob.getName())) && cam.getEX() >= mob.getX() - (fonts.write(0).getWidth(mob.getName()))) {
         mob.renderName(this, cam);
         }
         }*/
    }

    private void initMethods() {
        ups[0] = new update() {
            @Override
            public void update() {
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
                    if (cams[playersLength - 2].map != null) {
                        cams[playersLength - 2].update();
                    }
                }
                for (int i = 0; i < playersLength; i++) {
                    ((Player) players[i]).update(place);
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
            public void update() {
                /*if (game.online.server != null) {
                 for (Mob mob : sMobs) {
                 mob.update(place);
                 }
                 } else if (game.online.client != null) {
                 for (Mob mob : sMobs) {
                 mob.updateHard();
                 }
                 }
                 ((Player) players[0]).sendUpdate(place);
                 for (int i = 1; i < playersLength; i++) {
                 ((Entity) players[i]).updateSoft();
                 ((Entity) players[i]).updateOnline();
                 }*/
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

    private interface update {

        void update();
    }
}
