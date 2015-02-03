/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class SmoothShadowsChoice extends MenuChoice {

    public SmoothShadowsChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        if (Settings.multiSampleSupported) {
            Settings.samplesCount *= 2;
            if (Settings.samplesCount == 0) {
                Settings.samplesCount = 2;
            }
            if (Settings.samplesCount > Settings.maxSamples) {
                Settings.samplesCount = 0;
            }
            AnalizerSettings.update();
        } else {
            Settings.samplesCount = 0;
            AnalizerSettings.update();
        }
    }

    @Override
    public String getLabel() {
        if (!Settings.multiSampleSupported) {
            return label + Settings.language.menu.Off + " (" + Settings.language.menu.Unsupported + ")";
        } else if (Settings.samplesCount == 0) {
            return label + Settings.language.menu.Off;
        }
        return label + Settings.samplesCount + "x";

    }
}
