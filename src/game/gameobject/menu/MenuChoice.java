/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu;

import game.place.Menu;

/**
 * @author przemek
 */
public abstract class MenuChoice {

    protected final String label;
    protected final Menu menu;

    protected MenuChoice(String label, Menu menu) {
        this.label = label;
        this.menu = menu;
    }

    public abstract void action();

    public String getLabel() {
        return label;
    }
}
