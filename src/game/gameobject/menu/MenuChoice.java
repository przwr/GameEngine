/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu;

import game.place.Menu;

/**
 *
 * @author przemek
 */
public abstract class MenuChoice {

    protected String label;
    protected Menu menu;

    public abstract void action();

    public MenuChoice(String label, Menu menu) {
        this.label = label;
        this.menu = menu;
    }

    public String getLabel() {
        return label;
    }
}
