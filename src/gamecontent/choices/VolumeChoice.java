/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.Sound;
import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class VolumeChoice extends MenuChoice {

    public VolumeChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        Settings.volume += 0.05f;
        if (Settings.volume > 1.01f) {
            Settings.volume = 0.00f;
        }
        if(Settings.sounds != null){
            for(Sound s: Settings.sounds.getSoundsList()){
                s.updateGain();
            }
        }
        AnalizerSettings.update();
    }

    @Override
    public String getLabel() {
        int v = (int)(Settings.volume * 100);
            return label + v + "%";
    }
}
