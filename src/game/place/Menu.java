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
 *
 * @author przemek
 */
public abstract class Menu extends ScreenPlace {

    protected int current;
    protected MenuOptions[] menus;

    protected final int widthHalf = Display.getWidth();
    protected final int heightHalf = Display.getHeight();
    public boolean isMapping;
    public Delay delay;

    public Menu(Game game) {
        super(game);
    }

    @Override
    public abstract void update();

    @Override
    public abstract void render();

    protected abstract void renderText();

    public abstract void back();

    public void setChoosen(int i) {
        if (!isMapping && delay.isOver()) {
            menus[current].setChoosen(i);
        }
    }

    public void choice() {
        if (!isMapping && delay.isOver()) {
            menus[current].getChoosen().action();
        }
    }

    public void setCurrent(int i) {
        if (!isMapping && delay.isOver()) {
            current = i;
        }
    }
}
