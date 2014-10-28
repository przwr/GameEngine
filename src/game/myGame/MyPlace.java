/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.myGame;

import game.gameobject.Mob;
import game.Game;
import game.Settings;
import game.gameobject.GameObject;
import game.place.BasicTile;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.SolidTile;
import game.place.Tile;
import java.awt.Font;
import engine.FontsHandler;
import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.inputs.InputKeyBoard;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.SoundStore;

/**
 *
 * @author przemek
 */
public class MyPlace extends Place {

    final Action changeSplitScreenMode;

    final Tile GRASS = new BasicTile(getSpriteSheet("tlo", sTile, sTile), "Grass", sTile, 1, 8, this);
    final Tile ROCK = new SolidTile(getSpriteSheet("tlo", sTile, sTile), "Rock", sTile, 1, 1, this);

    public MyPlace(Game game, int width, int height, int tileSize, Settings settings) {
        super(game, width, height, tileSize, settings);
        this.changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT), null) {

            @Override
            public void Act() {
                changeSSMode = true;
            }
        };
        generate();
    }

    @Override
    public final void generate() {
        //sounds.init("res", settings);
        for (int y = 0; y < height / sTile; y++) {
            for (int x = 0; x < width / sTile; x++) {
                if ((x * y) < 850) {
                    tiles[x + y * height / sTile] = GRASS;
                } else {
                    tiles[x + y * height / sTile] = ROCK;
                }
            }
        }
        tiles[6 + 6 * height / sTile] = ROCK;
        tiles[5 + 4 * height / sTile] = ROCK;
        tiles[7 + 4 * height / sTile] = ROCK;
        addObj(new Mob(1280, 512, 0, 8, 128, 112, 128, 128, 4, 256, "rabbit", this, true));
        addObj(new Mob(1280, 256, 0, 8, 128, 112, 128, 128, 4, 256, "rabbit", this, true));
        this.r = 0.5f;
        this.g = 0.5f;
        this.b = 0.5f;
        fonts = new FontsHandler(20);
        fonts.add("Arial", Font.PLAIN, 24);
        SoundStore.get().poll(0);
    }

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
        if (players.length == 2) {
            changeSplitScreenMode.Do();
            camfor2.update();
        } else if (players.length == 3) {
            changeSplitScreenMode.Do();
            camfor3.update();
        } else if (players.length == 4) {
            camfor4.update();
        }
        for (Mob mob : sMobs) {
            mob.update(players);
        }
    }

    @Override
    protected void renderText(Camera cam) {
        for (GameObject player : players) {
            ((MyPlayer) player).renderName(this, cam);
        }

        for (Mob mob : sMobs) {
            mob.renderName(this, cam);
        }
    }
}
