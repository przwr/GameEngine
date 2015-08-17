/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.inout.AnalyzerSettings;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
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
        if (Settings.sounds != null) {
            Settings.sounds.getSoundsList().forEach(engine.Sound::updateGain);
        }
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        int v = (int) (Settings.volume * 100);
        return label + v + "%";
    }
}
