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
import sounds.SoundBase;

/**
 * @author przemek
 */
public class MusicVolumeChoice extends MenuChoice {

    public MusicVolumeChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (button == ACTION || button == RIGHT) {
            Settings.musicVolume += 0.05f;
            if (Settings.musicVolume > 1.01f) {
                Settings.musicVolume = 0.00f;
            }
        } else {
            Settings.musicVolume -= 0.05f;
            if (Settings.musicVolume < 0.00f) {
                Settings.musicVolume = 1.0f;
            }
        }
        Settings.sounds.setMusicVolume(Settings.musicVolume);
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        int v = (int) (Settings.musicVolume * 200);
        if (v == 0) {
            return label + " " + Settings.language.menu.Off;
        }
        return label + v + "%";
    }
}
