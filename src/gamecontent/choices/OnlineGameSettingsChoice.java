/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class OnlineGameSettingsChoice extends MenuChoice {

    public OnlineGameSettingsChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        menu.setCurrent(8);
    }
}
