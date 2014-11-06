/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu;

import game.Settings;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public abstract class MenuChoice {

    protected String label;
    protected Menu menu;
    protected Settings settings;

    public MenuChoice(String label, Menu menu, Settings settings) {
        this.label = label;
        this.menu = menu;
        this.settings = settings;
    }

    public abstract void action();

    public String getLabel() {
        return label;
    }

}
