/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.menu;

import engine.Delay;
import game.Game;
import game.place.ScreenPlace;
import org.lwjgl.opengl.Display;

/**
 * @author przemek
 */
public abstract class Menu extends ScreenPlace {

    protected final int widthHalf = Display.getWidth();
    protected final int heightHalf = Display.getHeight();
    public boolean isMapping;
    public Delay delay;
    protected MenuChoice root;
    protected MenuChoice defaultRoot;

    protected Menu(Game game) {
        super(game);
    }

    @Override
    public void update() {
    }

    @Override
    public abstract void render();

    public void setChosen(int i) {
        if (!isMapping && delay.isOver()) {
            root.changeCurrent(i);
        }
    }

    public void choice() {
        if (!isMapping && delay.isOver()) {
            root.actionCurrent();
        }
    }

    public void back() {
        if (root.getPrevious() != null) {
            root = root.getPrevious();
        } else {
            game.resumeGame();
        }
    }

    public void setRoot(MenuChoice root) {
        if (!isMapping && delay.isOver()) {
            this.root = root;
        }
    }

    public void setDefaultRoot() {
        this.root = defaultRoot;
    }

    protected void setFirstRoot(MenuChoice root) {
        this.root = root;
        this.defaultRoot = root;
    }
}