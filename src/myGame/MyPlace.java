/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame;

import collision.Area;
import collision.Line;
import collision.Rectangle;
import game.gameobject.Mob;
import game.Game;
import game.Settings;
import game.place.BasicTile;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.SolidTile;
import game.place.Tile;
import java.awt.Font;
import engine.FontsHandler;
import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.GameObject;
import game.gameobject.inputs.InputKeyBoard;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.SoundStore;

/**
 *
 * @author przemek
 */
public class MyPlace extends Place {

    final Action changeSplitScreenMode;
    final Action changeSplitScreenJoin;

    final Tile GRASS = new BasicTile(getSpriteSheet("tlo", sTile, sTile), "Grass", sTile, 1, 8, this);
    final Tile ROCK = new SolidTile(getSpriteSheet("tlo", sTile, sTile), "Rock", sTile, 1, 1, this);

    public MyPlace(Game game, int width, int height, int tileSize, Settings settnig) {
        super(game, width, height, tileSize, settnig);
        this.changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT), null) {

            @Override
            public void Act() {
                changeSSMode = true;
            }
        };
        this.changeSplitScreenJoin = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_END), null) {

            @Override
            public void Act() {
                settings.joinSS = !settings.joinSS;
            }
        };
        generate();
    }

    @Override
    public final void generate() {
        //sounds.init("res", settings);
        Area a = new Area(13 * sTile, 13 * sTile, "rockb", "rockb", sTile);
        for (int y = 0; y < height / sTile; y++) {
            for (int x = 0; x < width / sTile; x++) {
                if ((x * y) < 600) {
                    tiles[x + y * height / sTile] = GRASS;
                } else {
                    if (tiles[x - 1 + y * height / sTile] == GRASS || tiles[x + (y - 1) * height / sTile] == GRASS) {
                        a.addFigure(new Rectangle(x * sTile - 13 * sTile, y * sTile - 13 * sTile, sTile, sTile, 0, a));
                    }
                    tiles[x + y * height / sTile] = ROCK;
                }
            }
        }
        Area test = new Area(6 * sTile, 6 * sTile, "rockw", "rockb", sTile);
        test.addFigure(new Rectangle(0, 0, sTile, sTile, 0, test));
        addFGTile(new BasicTile(getSpriteSheet("tlo", sTile, sTile), "Rock", sTile, 7, 2, this), 6 * sTile, 6 * sTile, 6 * sTile, true);
        addFGTile(new BasicTile(getSpriteSheet("tlo", sTile, sTile), "Rock", sTile, 1, 1, this), 6 * sTile, 5 * sTile, 6 * sTile, true);
        //tiles[6 + 6 * height / sTile] = ROCK;
        test.addFigure(new Rectangle(2 * sTile, 0, sTile, sTile, 0, test));
        addFGTile(new BasicTile(getSpriteSheet("tlo", sTile, sTile), "Rock", sTile, 7, 2, this), 8 * sTile, 6 * sTile, 6 * sTile, true);
        addFGTile(new BasicTile(getSpriteSheet("tlo", sTile, sTile), "Rock", sTile, 1, 1, this), 8 * sTile, 5 * sTile, 6 * sTile, true);
        //tiles[8 + 6 * height / sTile] = ROCK;
        test.addFigure(new Rectangle(1 * sTile, 1 * sTile, sTile, sTile, 0, test));
        addFGTile(new BasicTile(getSpriteSheet("tlo", sTile, sTile), "Rock", sTile, 7, 2, this), 7 * sTile, 7 * sTile, 7 * sTile, true);
        addFGTile(new BasicTile(getSpriteSheet("tlo", sTile, sTile), "Rock", sTile, 1, 1, this), 7 * sTile, 6 * sTile, 7 * sTile, true);
        //tiles[7 + 7 * height / sTile] = ROCK;
        Area border = new Area(0, 0, null, null, sTile);
        border.addFigure(new Line(0, 0, width, 0, border));
        border.addFigure(new Line(0, 0, 0, height, border));
        border.addFigure(new Line(width, 0, 0, height, border));
        border.addFigure(new Line(0, height, width, 0, border));
        areas.add(a);
        areas.add(test);
        areas.add(border);
        addObj(new MyMob(1280, 512, 0, 8, 128, 112, 128, 128, 4, 512, "rabbit", this, true, settings.SCALE));
        addObj(new MyMob(1280, 256, 0, 8, 128, 112, 128, 128, 4, 512, "rabbit", this, true, settings.SCALE));
        this.r = 0.8f;
        this.g = 0.8f;
        this.b = 0.8f;
        fonts = new FontsHandler(20);
        fonts.add("Arial", Font.PLAIN, (int) (settings.SCALE * 24));
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
        if (playersLength == 2) {
            changeSplitScreenJoin.Do();
            changeSplitScreenMode.Do();
            cams[0].update();
        } else if (playersLength == 3) {
            changeSplitScreenJoin.Do();
            changeSplitScreenMode.Do();
            cams[1].update();
        } else if (playersLength == 4) {
            changeSplitScreenJoin.Do();
            cams[2].update();
        }
        for (GameObject pl : players) {
            ((MyPlayer) pl).update(this);
        }
        for (Mob mob : sMobs) {
            mob.update(this);
        }
    }

    @Override
    protected void renderText(Camera cam) {
        for (int p = 0; p < playersLength; p++) {
            if (cam.getSY() <= players[p].getY() + (players[p].getHeight() << 2) && cam.getEY() >= players[p].getY() - (players[p].getHeight() << 2)
                    && cam.getSX() <= players[p].getY() + (players[p].getWidth() << 2) && cam.getEX() >= players[p].getX() - (players[p].getWidth() << 2)) {
                ((MyPlayer) players[p]).renderName(this, cam);
            }
        }

        for (Mob mob : sMobs) {
            if (cam.getSY() <= mob.getY() + (mob.getHeight() << 2) && cam.getEY() >= mob.getY() - (mob.getHeight() << 2)
                    && cam.getSX() <= mob.getY() + (mob.getWidth() << 2) && cam.getEX() >= mob.getX() - (mob.getWidth() << 2)) {
                mob.renderName(this, cam);
            }
        }
    }
}
