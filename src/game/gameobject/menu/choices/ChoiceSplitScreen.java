/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.myGame.MyMenu;

/**
 *
 * @author przemek
 */
public class ChoiceSplitScreen extends MenuChoice {

    public ChoiceSplitScreen(String label, MyMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() == null) {
            settings.hSplitScreen = !settings.hSplitScreen;
        }
        AnalizerSettings.Update(settings);
    }

    @Override
    public String getLabel() {
        if (menu.game.getPlace() != null) {
            return label + settings.language.End_Game;
        } else {
            if (settings.hSplitScreen) {
                return label + settings.language.Horizontal + " [2/2]";
            } else {
                return label + settings.language.Vertical + " [1/2]";
            }
        }
    }
}
