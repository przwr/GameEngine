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
public class ChoiceRunServer extends AbstractMenuChoice {

    public ChoiceRunServer(String label, AbstractMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        menu.game.online.runServer();
        if (menu.game.online.server != null) {
            menu.setCurrent(0);
        }
    }
}
