/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame.choices;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;
import game.place.SplitScreen;

/**
 *
 * @author przemek
 */
public class ChoiceJoinSS extends MenuChoice {

    public ChoiceJoinSS(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() != null) {
            if (settings.joinSS) {
                settings.joinSS = false;
            } else if (SplitScreen.isClose(menu.game.getPlace())) {
                settings.joinSS = true;
            } else {
                
            }
        }
    }

    @Override
    public String getLabel() {
        if (menu.game.getPlace() != null) {
            if (settings.joinSS) {
                return label + settings.language.On;
            } else {
                return label + settings.language.Off + settings.language.MustBeClose;
            }
        } else {
            return label + settings.language.StartGame;
        }
    }
}
