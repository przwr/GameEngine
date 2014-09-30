/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MyMenu;

/**
 *
 * @author przemek
 */
public class ChoiceDesktopFullScreen extends MenuChoice {

    public ChoiceDesktopFullScreen(String label, MyMenu menu) {
        super(label, menu);
    }    
    
    @Override
    public void action() {
        menu.game.setDesktopFullScreen();
    }
    
}
