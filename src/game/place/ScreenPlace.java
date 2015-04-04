/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.FontBase;
import engine.inout.FontHandler;
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
    public FontHandler standardFont;

    public ScreenPlace(Game game) {
        this.game = game;
    }

    public abstract void update();

    public abstract void render();
}
