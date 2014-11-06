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
public class ChoiceSmoothShadows extends MenuChoice {

    public ChoiceSmoothShadows(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.nrSamples = settings.nrSamples * 2;
        if (settings.nrSamples > 8) {
            settings.nrSamples = 1;
        }
        AnalizerSettings.Update(settings);
    }

    @Override
    public String getLabel() {
        if (settings.nrSamples == 1) {
            return label + settings.language.Off;
        }
        return label + settings.nrSamples + "x";
    }
}
