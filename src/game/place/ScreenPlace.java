/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.FontsHandler;
import game.Game;
import game.Settings;
import game.gameobject.GameObject;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public abstract class ScreenPlace {

    public final Game game;
    public final Settings settings;
    public final int width, height;
    public float r, g, b;
    protected FontsHandler fonts;
    public GameObject[] players;

    public ScreenPlace(Game game, int width, int height, Settings settings) {
        this.width = width;
        this.height = height;
        this.settings = settings;
        this.game = game;
    }

    public abstract void update();

    public abstract void render();

    public void renderMessage(int i, int x, int y, String ms, Color color) {
        fonts.write(i).drawString(x - fonts.write(i).getWidth(ms) / 2, y - (4 * fonts.write(i).getHeight()) / 3, ms, color);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float SCALE() {
        return settings.SCALE;
    }
}
