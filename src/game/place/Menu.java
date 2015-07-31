/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Delay;
import game.Game;
import game.gameobject.menu.MenuOptions;
import org.lwjgl.opengl.Display;

/**
 * @author przemek
 */
public abstract class Menu extends ScreenPlace {

    protected final int widthHalf = Display.getWidth();
    protected final int heightHalf = Display.getHeight();
    public boolean isMapping;
    public Delay delay;
    protected int current;
    protected MenuOptions[] menus;

    protected Menu(Game game) {
        super(game);
    }

    @Override
    public void update() {
    }

    @Override
    public abstract void render();

    protected abstract void renderText();

    public abstract void back();

    public void setChosen(int i) {
        if (!isMapping && delay.isOver()) {
            menus[current].setChosen(i);
        }
    }

    public void choice() {
        if (!isMapping && delay.isOver()) {
            menus[current].getChosen().action();
        }
    }

    public void setCurrent(int i) {
        if (!isMapping && delay.isOver()) {
            current = i;
        }
    }
}
