/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.view.SplitScreen;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * @author przemek
 */
public class JoinSplitScreenChoice extends MenuChoice {

    public JoinSplitScreenChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (menu.game.getPlace() != null) {
            if (Settings.joinSplitScreen) {
                Settings.joinSplitScreen = false;
            } else if (SplitScreen.isClose(menu.game.getPlace())) {
                Settings.joinSplitScreen = true;
            }
        }
    }

    @Override
    public String getLabel() {
        if (menu.game.getPlace() != null) {
            if (Settings.joinSplitScreen) {
                return label + " [DELETE] " + Settings.language.menu.On;
            } else {
                return label + " [DELETE] " + Settings.language.menu.Off + Settings.language.menu.MustBeClose;
            }
        } else {
            return label + " [DELETE] " + Settings.language.menu.StartGame;
        }
    }
}
