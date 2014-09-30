/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu;

import game.gameobject.menu.MyMenu;

/**
 *
 * @author przemek
 */
public abstract class MenuChoice {

    private String label;
    protected MyMenu menu;

    public MenuChoice(String label, MyMenu menu) {
        this.label = label;
        this.menu = menu;
    }

    public abstract void action();

    public String getLabel() {
        return label;
    }
    
}
