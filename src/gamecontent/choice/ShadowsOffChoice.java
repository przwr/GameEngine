/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choice;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class ShadowsOffChoice extends MenuChoice {

    public ShadowsOffChoice(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.shadowOff = !settings.shadowOff;
        AnalizerSettings.update(settings);
    }

    @Override
    public String getLabel() {
        if (settings.shadowOff) {
            return label + settings.language.m.On;
        } else {
            return label + settings.language.m.Off;
        }
    }
}
