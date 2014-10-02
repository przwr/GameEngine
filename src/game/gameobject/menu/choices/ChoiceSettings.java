/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MyMenu;

/**
 *
 * @author przemek
 */
public class ChoiceSettings extends MenuChoice {

    public ChoiceSettings(String label, MyMenu menu, Settings settings) {
        super(label, menu, settings);
    }    
    
    @Override
    public void action() {
        menu.setCurrent(1);
    }
    
}
