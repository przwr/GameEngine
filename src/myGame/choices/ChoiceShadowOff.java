/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class ChoiceShadowOff extends MenuChoice {

    public ChoiceShadowOff(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.shadowOff = !settings.shadowOff;
        AnalizerSettings.Update(settings);
    }

    @Override
    public String getLabel() {
        if (settings.shadowOff) {
            return label + settings.language.On;
        } else {
            return label + settings.language.Off;
        }
    }
}
