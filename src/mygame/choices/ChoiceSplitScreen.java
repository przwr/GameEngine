/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;
import game.place.SplitScreen;

/**
 *
 * @author przemek
 */
public class ChoiceSplitScreen extends MenuChoice {

    public ChoiceSplitScreen(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() == null) {
            settings.hSplitScreen = !settings.hSplitScreen;
        } else {
            if (menu.game.getPlace().playersLength == 2) {
                menu.game.getPlace().changeSSMode = true;
                SplitScreen.changeSSMode2(menu.game.getPlace());
            } else if (menu.game.getPlace().playersLength == 3) {
                menu.game.getPlace().changeSSMode = true;
                SplitScreen.changeSSMode3(menu.game.getPlace());
            } else {
                settings.hSplitScreen = !settings.hSplitScreen;
            }
        }
        AnalizerSettings.update(settings);
    }

    @Override
    public String getLabel() {
        if (settings.hSplitScreen) {
            return label + settings.language.m.Horizontal + " [2/2]";
        } else {
            return label + settings.language.m.Vertical + " [1/2]";
        }
    }
}
