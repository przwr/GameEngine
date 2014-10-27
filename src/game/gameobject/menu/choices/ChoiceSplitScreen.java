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
import game.place.SplitScreen;

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
        } else {
            if (menu.game.getPlace().players.length == 2) {
                menu.game.getPlace().changeSSMode = true;
                SplitScreen.changeSSMode2(menu.game.getPlace());
            } else if (menu.game.getPlace().players.length == 3) {
                menu.game.getPlace().changeSSMode = true;
                SplitScreen.changeSSMode3(menu.game.getPlace());
            }
        }
        AnalizerSettings.Update(settings);
    }

    @Override
    public String getLabel() {
        if (settings.hSplitScreen) {
            return label + settings.language.Horizontal + " [2/2]";
        } else {
            return label + settings.language.Vertical + " [1/2]";
        }
    }
}
