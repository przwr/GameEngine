/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.choices;

import engine.Sound;
import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.AbstractMenuChoice;
import game.place.AbstractMenu;

/**
 *
 * @author przemek
 */
public class ChoiceVolume extends AbstractMenuChoice {

    public ChoiceVolume(String label, AbstractMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.volume += 0.05f;
        if (settings.volume > 1.01f) {
            settings.volume = 0.00f;
        }
        if(settings.sounds != null){
            for(Sound s: settings.sounds.getSoundsList()){
                s.updateGain();
            }
        }
        AnalizerSettings.update(settings);
    }

    @Override
    public String getLabel() {
        int v = (int)(settings.volume * 100);
            return label + v + "%";
    }
}
