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
import org.newdawn.slick.openal.SoundStore;

/**
 * @author przemek
 */
public class SoundVolumeChoice extends MenuChoice {

    public SoundVolumeChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (button == ACTION || button == RIGHT) {
            Settings.soundVolume += 0.05f;
            if (Settings.soundVolume > 1.01f) {
                Settings.soundVolume = 0.00f;
            }
        } else {
            Settings.soundVolume -= 0.05f;
            if (Settings.soundVolume < 0.00f) {
                Settings.soundVolume = 1.0f;
            }
        }
        SoundStore.get().setSoundVolume(Settings.soundVolume);
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        int v = (int) (Settings.soundVolume * 200);
        if (v == 0) {
            return label + " " + Settings.language.menu.Off;
        }
        return label + v + "%";
    }
}
