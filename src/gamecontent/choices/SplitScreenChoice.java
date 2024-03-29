/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.systemcommunication.AnalyzerSettings;
import engine.view.SplitScreen;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * @author przemek
 */
public class SplitScreenChoice extends MenuChoice {

    public SplitScreenChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (menu.game.getPlace() == null) {
            Settings.horizontalSplitScreen = !Settings.horizontalSplitScreen;
        } else {
            if (menu.game.getPlace().playersCount == 2) {
                menu.game.getPlace().changeSSMode = true;
                SplitScreen.changeSplitScreenMode2(menu.game.getPlace());
            } else if (menu.game.getPlace().playersCount == 3) {
                menu.game.getPlace().changeSSMode = true;
                SplitScreen.changeSplitScreenMode3(menu.game.getPlace());
            } else {
                Settings.horizontalSplitScreen = !Settings.horizontalSplitScreen;
            }
        }
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        return label + " [INSERT] " + (Settings.horizontalSplitScreen ? Settings.language.menu.Horizontal : Settings.language.menu.Vertical);
    }
}
