/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.Analizer;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MyMenu;

/**
 *
 * @author przemek
 */
public class ChoicePlayers extends MenuChoice {


    public ChoicePlayers(String label, MyMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
       menu.addPlayer();
        if (settings.nrPlayers > 4) {
            menu.setToOnePlayer();
        }
        Analizer.Save(settings);
    }

    @Override
    public String getLabel() {
        return label + settings.nrPlayers;
    }
}
