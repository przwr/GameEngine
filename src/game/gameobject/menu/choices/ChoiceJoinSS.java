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
public class ChoiceJoinSS extends MenuChoice {

    public ChoiceJoinSS(String label, MyMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.joinSS = !settings.joinSS;
        AnalizerSettings.Update(settings);
    }

    @Override
    public String getLabel() {
        if (settings.joinSS) {
            return label + settings.language.On;
        } else {
            return label + settings.language.Off;
        }
    }
}
