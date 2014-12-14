/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.choices;

import game.Settings;
import game.gameobject.menu.AbstractMenuChoice;
import game.place.AbstractMenu;

/**
 *
 * @author przemek
 */
public class ChoicePlayerCtrl extends AbstractMenuChoice {

    public ChoicePlayerCtrl(String label, AbstractMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        String[] p = label.split("\\s+");
        int pos = Integer.parseInt(p[1]) + 2;
        menu.setCurrent(pos);
    }

}
