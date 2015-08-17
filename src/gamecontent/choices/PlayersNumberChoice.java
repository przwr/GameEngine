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
public class PlayersNumberChoice extends MenuChoice {

    public PlayersNumberChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        Settings.playersCount++;
        if (Settings.playersCount > 4) {
            Settings.playersCount = 1;
        }
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        return label + "[" + Settings.playersCount + "/4]";
    }
}
