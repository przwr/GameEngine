/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.AbstractMenuChoice;
import game.place.AbstractMenu;

/**
 *
 * @author przemek
 */
public class ChoiceFullScreen extends AbstractMenuChoice {

    public ChoiceFullScreen(String label, AbstractMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.fullScreen = !settings.fullScreen;
        AnalizerSettings.update(settings);
    }

    @Override
    public String getLabel() {
        if (settings.fullScreen) {
            return label + settings.language.m.On;
        } else {
            return label + settings.language.m.Off;
        }
    }
}
