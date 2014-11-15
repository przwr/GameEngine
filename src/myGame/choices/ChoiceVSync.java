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
public class ChoiceVSync extends MenuChoice {

    public ChoiceVSync(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.vSync = !settings.vSync;
        AnalizerSettings.Update(settings);
    }

    @Override
    public String getLabel() {
        if (settings.vSync) {
            return label + settings.language.On;
        } else {
            return label + settings.language.Off;
        }
    }
}
