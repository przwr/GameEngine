/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.systemcommunication.AnalyzerSettings;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;
import sounds.Sound;

import java.util.Iterator;

/**
 * @author przemek
 */
public class VolumeChoice extends MenuChoice {

    public VolumeChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (button == ACTION || button == RIGHT) {
            Settings.volume += 0.05f;
            if (Settings.volume > 1.01f) {
                Settings.volume = 0.00f;
            }
        } else {
            Settings.volume -= 0.05f;
            if (Settings.volume < 0.00f) {
                Settings.volume = 1.0f;
            }
        }
        if (Settings.sounds != null) {
            Iterator it = Settings.sounds.getSoundsMap().entrySet().iterator();
            while (it.hasNext()) {
                java.util.Map.Entry<String, Sound> pair = (java.util.Map.Entry) it.next();
                pair.getValue().updateGain();
            }
        }
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        int v = (int) (Settings.volume * 100);
        if (v == 0) {
            return label + " " + Settings.language.menu.Off;
        }
        return label + v + "%";
    }
}
