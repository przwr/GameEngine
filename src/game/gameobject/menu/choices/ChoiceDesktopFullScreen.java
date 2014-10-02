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
public class ChoiceDesktopFullScreen extends MenuChoice {

    public ChoiceDesktopFullScreen(String label, MyMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        menu.game.setDesktopFullScreen();
    }

    @Override
    public String getLabel() {
        if (settings.fullscreen) {
            return label + "Wł";
        } else {
            return label + "Wył";
        }
    }
}
