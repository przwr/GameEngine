/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.choices;

import game.Settings;
import game.gameobject.menu.AbstractMenuChoice;
import game.place.AbstractMenu;
import game.place.SplitScreen;

/**
 *
 * @author przemek
 */
public class ChoiceJoinSS extends AbstractMenuChoice {

    public ChoiceJoinSS(String label, AbstractMenu menu, Settings settings) {
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
                return label + settings.language.m.On;
            } else {
                return label + settings.language.m.Off + settings.language.m.MustBeClose;
            }
        } else {
            return label + settings.language.m.StartGame;
        }
    }
}
