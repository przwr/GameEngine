/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class ChoiceFullScreen extends MenuChoice {

    public ChoiceFullScreen(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.fullScreen = !settings.fullScreen;
        AnalizerSettings.Update(settings);
    }

    @Override
    public String getLabel() {
        if (settings.fullScreen) {
            return label + settings.language.On;
        } else {
            return label + settings.language.Off;
        }
    }
}
