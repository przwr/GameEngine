/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.FontBase;
import game.Game;
import game.gameobject.GameObject;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

/**
 *
 * @author przemek
 */
public abstract class ScreenPlace {

    public final Game game;
    public float red, green, blue;
    public FontBase fonts;
    public GameObject[] players;

    public ScreenPlace(Game game) {
        this.game = game;
    }

    public abstract void update();

    public abstract void render();

    public void renderMessage(int i, int x, int y, String message, Color color) {
        if (fonts != null) {
            fonts.write(i).drawString(x - fonts.write(i).getWidth(message) / 2,
                    y - (4 * fonts.write(i).getHeight()) / 3, message, color);
        }
    }
}
