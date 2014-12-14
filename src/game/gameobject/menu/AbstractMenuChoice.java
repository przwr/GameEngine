/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu;

import game.Settings;
import game.place.AbstractMenu;

/**
 *
 * @author przemek
 */
public abstract class AbstractMenuChoice {

    protected String label;
    protected AbstractMenu menu;
    protected Settings settings;

    public AbstractMenuChoice(String label, AbstractMenu menu, Settings settings) {
        this.label = label;
        this.menu = menu;
        this.settings = settings;
    }

    public abstract void action();

    public String getLabel() {
        return label;
    }

}
