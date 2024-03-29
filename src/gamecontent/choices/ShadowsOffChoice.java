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
public class ShadowsOffChoice extends MenuChoice {

    public ShadowsOffChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        Settings.shadowOff = !Settings.shadowOff;
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        if (Settings.shadowOff) {
            return label + Settings.language.menu.On;
        } else {
            return label + Settings.language.menu.Off;
        }
    }
}
