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

/**
 * @author przemek
 */
public class SmoothShadowsChoice extends MenuChoice {

    public SmoothShadowsChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (Settings.multiSampleSupported) {
            if (button == ACTION || button == RIGHT) {
                Settings.samplesCount *= 2;
                if (Settings.samplesCount == 0)
                    Settings.samplesCount = 2;
            } else {
                Settings.samplesCount /= 2;
                if (Settings.samplesCount == 0)
                    Settings.samplesCount = Settings.maxSamples;
            }
            if (Settings.samplesCount == 1 || Settings.samplesCount > Settings.maxSamples) {
                Settings.samplesCount = 0;
            }
            AnalyzerSettings.update();
        } else {
            Settings.samplesCount = 0;
            AnalyzerSettings.update();
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
