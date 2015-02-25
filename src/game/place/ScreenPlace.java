/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Drawer;
import engine.FontBase;
import game.Game;
import game.gameobject.GameObject;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public abstract class ScreenPlace {

    public final Game game;    
    public Color color;
    public FontBase fonts;
    public GameObject[] players;

    public ScreenPlace(Game game) {
        this.game = game;
    }

    public abstract void update();

    public abstract void render();

    public void renderMessageCentered(int i, int x, int y, String message, Color color) {
        if (fonts != null) {
            Drawer.bindFontTexture();
            fonts.getFont(i).drawLine(message, x - fonts.getFont(i).getWidth(message) / 2,
                    y - (4 * fonts.getFont(i).getHeight()) / 3, color);
        }
    }

    public void renderMessage(int i, int x, int y, String message, Color color) {
        if (fonts != null) {
            Drawer.bindFontTexture();
            fonts.getFont(i).drawLine(message, x, y, color);
        }
    }
}
