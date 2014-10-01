/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.Game;
import game.place.Tile;
import game.place.SolidTile;
import game.place.BasicTile;
import game.place.cameras.Camera;
import game.place.Place;
import java.awt.Font;
import openGLEngine.FontsHandler;
import openGLEngine.SoundBase;

/**
 *
 * @author przemek
 */
public class MyPlace extends Place {

    private SoundBase sounds = new SoundBase();
    final Tile GRASS = new BasicTile("grass", "Grass", sTile);
    final Tile ROCK = new SolidTile("rock", "Rock", sTile);

    public MyPlace(Game game, int width, int height, int tileSize) {
        super(game, width, height, tileSize);
        generate();
    }

    @Override
    public final void generate() {
        sounds.init("src/res");
        for (int y = 0; y < height / sTile; y++) {
            for (int x = 0; x < width / sTile; x++) {
                if ((x * y) < 200) {
                    tiles[x + y * height / sTile] = GRASS;
                } else {
                    tiles[x + y * height / sTile] = ROCK;
                }
            }
        }
        addObj(new Mob(512, 512, 0, 8, 128, 112, 128, 128, 4, 256, "rabbit", this, true));
        addObj(new Mob(512, 256, 0, 8, 128, 112, 128, 128, 4, 256, "rabbit", this, true));
        this.r = 0.5f;
        this.g = 0.5f;
        this.b = 0.5f;
        fonts = new FontsHandler(20);
        fonts.add("Arial", Font.PLAIN, 24);
        sounds.getSound("MumboMountain").playAsMusic(1.0f , 1.0f, true);
    }

    @Override
    public void update() {
        for (Mob mob : sMobs) {
            mob.update(players);
        }
    }

    @Override
    protected void renderText(Camera cam) {
        for (GameObject player : players) {
            ((Player) player).renderName(this, (Player) player, cam);
        }

        for (Mob mob : sMobs) {
            mob.renderName(this, cam);
        }
    }
}
