/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MyMenu;

/**
 *
 * @author przemek
 */
public class ChoicePlayers extends MenuChoice {



    public ChoicePlayers(String label, MyMenu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
       menu.addPlayer();
        if (menu.nrPlayers > 4) {
            menu.setToOnePlayer();
        }
    }

    @Override
    public String getLabel() {
        return label + menu.nrPlayers;
    }
}
