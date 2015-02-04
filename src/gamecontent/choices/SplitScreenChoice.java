/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;
import game.place.SplitScreen;

/**
 *
 * @author przemek
 */
public class SplitScreenChoice extends MenuChoice {

    public SplitScreenChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() != null) {
            Settings.horizontalSplitScreen = !Settings.horizontalSplitScreen;
        } else {
            if (menu.game.getPlace().playersCount == 2) {
                menu.game.getPlace().changeSSMode = true;
                SplitScreen.changeSSMode2(menu.game.getPlace());
            } else if (menu.game.getPlace().playersCount == 3) {
                menu.game.getPlace().changeSSMode = true;
                SplitScreen.changeSSMode3(menu.game.getPlace());
            } else {
                Settings.horizontalSplitScreen = !Settings.horizontalSplitScreen;
            }
        }
        AnalizerSettings.update();
    }

    @Override
    public String getLabel() {
        if (Settings.horizontalSplitScreen) {
            return label + Settings.language.menu.Horizontal + " [2/2]";
        } else {
            return label + Settings.language.menu.Vertical + " [1/2]";
        }
    }
}
