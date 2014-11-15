/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame.choices;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class ChoicePlayerCtrl extends MenuChoice {

    public ChoicePlayerCtrl(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        String[] p = label.split("\\s+");
        int pos = Integer.parseInt(p[1]) + 2;
        menu.setCurrent(pos);
    }

}
