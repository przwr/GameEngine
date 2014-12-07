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
        //sounds.init("res", settings);
        Area a = new Area(13 * sTile, 13 * sTile, sTile);
        for (int y = 0; y < height / sTile; y++) {
            for (int x = 0; x < width / sTile; x++) {
                if ((x * y) < 600) {
                    tiles[x + y * height / sTile] = GRASS;
                } else {
                    if (tiles[x - 1 + y * height / sTile] == GRASS || tiles[x + (y - 1) * height / sTile] == GRASS) {
                        a.addFigure(new Rectangle(x * sTile - 13 * sTile, y * sTile - 13 * sTile, sTile, sTile, false, true, 0, a));
                    }
                    tiles[x + y * height / sTile] = ROCK;
                }
            }
        }
        Area testa = new Area(6 * sTile, 5 * sTile, sTile, false, true);
        Area testb = new Area(8 * sTile, 5 * sTile, sTile, false, true);
        Area testc = new Area(7 * sTile, 7 * sTile, sTile, false, true);
        Area testd = new Area(9 * sTile, 7 * sTile, sTile, false, true);
        Area teste = new Area(11 * sTile, 7 * sTile, sTile, false, true);
        Area testf = new Area(5 * sTile, 9 * sTile, sTile, false, true);

//        testa.addFigure(new Rectangle(0, 0, sTile, sTile, true, true, testa));
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, this);
        addFGTile(fgt, 6 * sTile, 5 * sTile, 0, true);
        testa.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, this);
        addFGTile(fgt, 6 * sTile, 4 * sTile, 2 * sTile, true);
        testa.addPiece(fgt);

//        testb.addFigure(new Rectangle(0, 0, 2 * sTile, sTile, true, true, testb));
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, this);
        addFGTile(fgt, 8 * sTile, 5 * sTile, 0, true);
        testb.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, this);
        addFGTile(fgt, 8 * sTile, 4 * sTile, 2 * sTile, true);
        testb.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, this);
        addFGTile(fgt, 9 * sTile, 5 * sTile, 0, true);
        testb.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, this);
        addFGTile(fgt, 9 * sTile, 4 * sTile, 2 * sTile, true);
        testb.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, this);
        addFGTile(fgt, 10 * sTile, 5 * sTile, 0, true);
        testb.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, this);
        addFGTile(fgt, 10 * sTile, 4 * sTile, 2 * sTile, true);
        testb.addPiece(fgt);

//        testc.addFigure(new Rectangle(0, 0, sTile, sTile, true, true, testc));
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, this);
        addFGTile(fgt, 7 * sTile, 7 * sTile, 0, true);
        testc.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, this);
        addFGTile(fgt, 7 * sTile, 6 * sTile, 2 * sTile, true);
        testc.addPiece(fgt);

//        testd.addFigure(new Rectangle(0, 0, sTile, 2 * sTile, true, true, testd));
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, this);
        addFGTile(fgt, 9 * sTile, 8 * sTile, 0, true);
        testd.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, this);
        fgt.setSolid(true);
        addFGTile(fgt, 9 * sTile, 7 * sTile, 0, true);
        testd.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, this);
        addFGTile(fgt, 9 * sTile, 6 * sTile, 4 * sTile, true);
        testd.addPiece(fgt);

        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 7, 2, true, 0, 0, this);
        addFGTile(fgt, 11 * sTile, 7 * sTile, 0, true);
        teste.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 1, 1, false, sTile, 0, this);
        addFGTile(fgt, 11 * sTile, 6 * sTile, 2 * sTile, true);
        teste.addPiece(fgt);

        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 7, 2, true, 0, sTile, this);
        addFGTile(fgt, 5 * sTile, 9 * sTile, 0, true);
        testf.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 7, 2, true, sTile, 0, this);
        fgt.setSolid(false);
        addFGTile(fgt, 5 * sTile, 8 * sTile, 2 * sTile, true);
        testf.addPiece(fgt);
        fgt = new FGTile(getSpriteSheet("tlo"), sTile, 1, 1, false, 2 * sTile, 0, this);
        addFGTile(fgt, 5 * sTile, 7 * sTile, 4 * sTile, true);
        testf.addPiece(fgt);

        Area border = new Area(0, 0, sTile, true, false);
        border.addFigure(new Line(0, 0, width, 0, border));
        border.addFigure(new Line(0, 0, 0, height, border));
        border.addFigure(new Line(width, 0, 0, height, border));
        border.addFigure(new Line(0, height, width, 0, border));
        areas.add(a);
        areas.add(testa);
        areas.add(testb);
        areas.add(testc);
        areas.add(testd);
        areas.add(teste);
        areas.add(testf);
        areas.add(border);
        if (isHost) {
            addObj(new MyMob(1280, 1024, 0, 8, 128, 112, 4, 512, "rabbit", this, true, mobID++));
            addObj(new MyMob(1024, 1664, 0, 8, 128, 112, 4, 512, "rabbit", this, true, mobID++));
        }
        this.r = 0.75f;
        this.g = 0.75f;
        this.b = 0.75f;
        fonts = new FontsHandler(20);
        fonts.add("Amble-Regular", Methods.RoundHU(SCALE() * 24));
        SoundStore.get().poll(0);
        initMethods();
    }

    @Override
    public void update() {
        ups[game.mode].update();
    }

    @Override
    protected void renderText(Camera cam) {
        for (int p = 0; p < playersLength; p++) {
            if (cam.getSY() <= players[p].getY() + (players[p].Height() + fonts.write(0).getHeight()) && cam.getEY() >= players[p].getY() - (players[p].Height() + fonts.write(0).getHeight())
                    && cam.getSX() <= players[p].getX() + (fonts.write(0).getWidth(players[p].getName())) && cam.getEX() >= players[p].getX() - (fonts.write(0).getWidth(players[p].getName()))) {
                ((MyPlayer) players[p]).renderName(this, cam);
            }
        }
        for (Mob mob : sMobs) {
            if (cam.getSY() <= mob.getY() + (mob.Height() + fonts.write(0).getHeight()) && cam.getEY() >= mob.getY() - (mob.Height() + fonts.write(0).getHeight())
                    && cam.getSX() <= mob.getX() + (fonts.write(0).getWidth(mob.getName())) && cam.getEX() >= mob.getX() - (fonts.write(0).getWidth(mob.getName()))) {
                mob.renderName(this, cam);
            }
        }
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
                    changeSplitScreenJoin.Do();
                    changeSplitScreenMode.Do();
                    if (changeSplitScreenJoin.isOn()) {
                        settings.joinSS = !settings.joinSS;
                    }
                    if (changeSplitScreenMode.isOn()) {
                        changeSSMode = true;
                    }
                    cams[playersLength - 2].update();
                }
                for (int i = 0; i < playersLength; i++) {
                    ((Player) players[i]).update(place);
                }
                for (Mob mob : sMobs) {
                    mob.update(game.place);
                }
            }
        };
        ups[1] = new update() {
            @Override
            public void update() {
                if (game.online.server != null) {
                    for (Mob mob : sMobs) {
                        mob.update(place);
                    }
                } else {
                    for (Mob mob : sMobs) {
                        mob.updateHard();
                    }
                }
                ((Player) players[0]).sendUpdate(place);
                for (int i = 1; i < playersLength; i++) {
                    ((Entity) players[i]).updateSoft();
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

    private interface update {

        void update();
    }
}
