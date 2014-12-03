/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Delay;
import game.Game;
import game.Settings;
import game.gameobject.menu.MenuOpt;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public abstract class Menu extends ScreenPlace {

    protected int cur;
    protected MenuOpt[] menus;

    protected final int dWidth = Display.getWidth();
    protected final int dHeight = Display.getHeight(); //(int) (dWidth * ((double) Display.getHeight() / (double) Display.getWidth()));
    public boolean isMapping;
    public Delay delay;

    public Menu(Game game, int width, int height, Settings settings) {
        super(game, width, height, settings);
    }

    @Override
    public abstract void update();

    @Override
    public abstract void render();

    protected abstract void renderText();

    public abstract void back();

    public void setChoosen(int i) {
        if (!isMapping && delay.isOver()) {
            menus[cur].setChoosen(i);
        }
    }

    public void choice() {
        if (!isMapping && delay.isOver()) {
            menus[cur].getChoosen().action();
        }
    }

    public void setCurrent(int i) {
        if (!isMapping && delay.isOver()) {
            cur = i;
        }
    }

}
