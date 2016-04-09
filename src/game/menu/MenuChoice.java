/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.menu;

import game.text.fonts.TextPiece;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;

/**
 * @author przemek
 */
public class MenuChoice {

    protected final int ACTION = 0, RIGHT = 1, LEFT = 2;
    protected final String label;
    protected final Menu menu;
    protected final ArrayList<MenuChoice> choices = new ArrayList<>(1);
    protected MenuChoice previous;
    protected int current;
    protected boolean blockOnRun;
    private TextPiece text;

    public MenuChoice(String label, Menu menu) {
        this.label = label;
        this.menu = menu;
        this.text = new TextPiece(label, menu.fontSize, menu.font, Display.getWidth(), true);
    }

    public void action(int button) {
        if (button == ACTION)
            menu.setRoot(this);
    }

    public void addChoice(MenuChoice choice) {
        choice.setPrevious(this);
        this.choices.add(choice);
    }

    public String getLabel() {
        return label;
    }

    public MenuChoice getPrevious() {
        return previous;
    }

    public void setPrevious(MenuChoice previous) {
        this.previous = previous;
    }

    public void changeCurrent(int i) {
        current += i;
        if (current < 0) {
            current = choices.size() - 1;
        } else if (current >= choices.size()) {
            current = 0;
        }
    }

    public int getCurrent() {
        return current;
    }

    public int getSize() {
        return choices.size();
    }

    public MenuChoice getChoice(int i) {
        return choices.get(i);
    }

    public void actionCurrent(int button) {
        if (!choices.get(current).blockOnRun || !menu.game.started) {
            choices.get(current).action(button);
        }
    }

    public MenuChoice setBlockOnRun(boolean blockOnRun) {
        this.blockOnRun = blockOnRun;
        return this;
    }

    public boolean isBlocked() {
        return blockOnRun && menu.game.started;
    }

    public TextPiece getText() {
        text.setText(getLabel());
        return text;
    }
}
