/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyGame.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class ChoicePlayers extends MenuChoice {

    public ChoicePlayers(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.nrPlayers++;
        if (settings.nrPlayers > 4) {
            settings.nrPlayers = 1;
        }
        AnalizerSettings.update(settings);
    }

    @Override
    public String getLabel() {
        return label + "[" + settings.nrPlayers + "/4]";
    }
}
