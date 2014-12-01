/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameDesigner.gameEdit;

import myGame.*;
import collision.Area;
import collision.Line;
import game.gameobject.Mob;
import game.Game;
import game.Settings;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.Tile;
import engine.FontsHandler;
import game.gameobject.Player;
import org.newdawn.slick.openal.SoundStore;

/**
 *
 * @author przemek
 */
public class EditorPlace extends Place {

    private final Place place;

    private final update[] ups = new update[2];

    private final Tile GRASS = new Tile(getSpriteSheet("tlo"), sTile, 1, 8, this);
    final Tile ROCK = new Tile(getSpriteSheet("tlo"), sTile, 1, 1, this);

    public EditorPlace(Game game, int width, int height, int tileSize, Settings settnig) {
        super(game, width, height, tileSize, settnig);
        place = this;
        generate();
    }

    @Override
    public void generate() {
        Area border = new Area(0, 0, sTile, true);
        border.addFigure(new Line(0, 0, width, 0, border));
        border.addFigure(new Line(0, 0, 0, height, border));
        border.addFigure(new Line(width, 0, 0, height, border));
        border.addFigure(new Line(0, height, width, 0, border));
        areas.add(border);
        this.r = 1f;
        this.g = 1f;
        this.b = 1f;
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
        for (int p = 0; p < playersLength; p++) {
            if (cam.getSY() <= players[p].getY() + (players[p].getHeight() << 2) && cam.getEY() >= players[p].getY() - (players[p].getHeight() << 2)
                    && cam.getSX() <= players[p].getX() + (players[p].getWidth() << 2) && cam.getEX() >= players[p].getX() - (players[p].getWidth() << 2)) {
                ((MyPlayer) players[p]).renderName(this, cam);
            }
        }
        for (Mob mob : sMobs) {
            if (cam.getSY() <= mob.getY() + (mob.getHeight() << 2) && cam.getEY() >= mob.getY() - (mob.getHeight() << 2)
                    && cam.getSX() <= mob.getX() + (mob.getWidth() << 2) && cam.getEX() >= mob.getX() - (mob.getWidth() << 2)) {
                mob.renderName(this, cam);
            }
        }
    }

    private void initMethods() {
        ups[0] = new update() {
            @Override
            public void update() {
                ((Player) players[0]).update(place);
                for (Mob mob : sMobs) {
                    mob.update(game.place);
                }
            }
        };
        ups[1] = new update() {
            @Override
            public void update() {
                ((Player) players[0]).sendUpdate(place);
                for (int i = 1; i < playersLength; i++) {
                    ((Player) players[i]).update(place);
                }
                for (Mob mob : sMobs) {
                    mob.update(place);
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
