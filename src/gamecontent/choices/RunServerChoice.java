/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * @author przemek
 */
public class RunServerChoice extends MenuChoice {

    public RunServerChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        menu.game.online.runServer();
        if (menu.game.online.server != null) {
            menu.setDefaultRoot();
        }
    }
}
