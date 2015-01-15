/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choice;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class SettingsChoice extends MenuChoice {

    public SettingsChoice(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }    
    
    @Override
    public void action() {
        menu.setCurrent(1);
    }
    
}
