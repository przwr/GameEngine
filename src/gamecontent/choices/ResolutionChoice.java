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
public class ResolutionChoice extends MenuChoice {

    public ResolutionChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        Settings.currentMode++;
        if (Settings.currentMode >= Settings.modes.length) {
            Settings.currentMode = 0;
        }
        Settings.resolutionWidth = Settings.modes[Settings.currentMode].getWidth();
        Settings.resolutionHeight = Settings.modes[Settings.currentMode].getHeight();
        Settings.frequency = Settings.modes[Settings.currentMode].getFrequency();
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        return label + Settings.resolutionWidth + " x " + Settings.resolutionHeight + " @ " + Settings.frequency + " Hz [" + (Settings.currentMode + 1) + "/" + Settings.modes.length + "]";
    }
}
