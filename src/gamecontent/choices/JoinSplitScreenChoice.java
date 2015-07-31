/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.SplitScreen;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 * @author przemek
 */
public class JoinSplitScreenChoice extends MenuChoice {

    public JoinSplitScreenChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
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
                return label + Settings.language.menu.On;
            } else {
                return label + Settings.language.menu.Off + Settings.language.menu.MustBeClose;
            }
        } else {
            return label + Settings.language.menu.StartGame;
        }
    }
}
