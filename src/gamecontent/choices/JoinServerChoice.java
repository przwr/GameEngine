/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.utilities.Delay;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * @author przemek
 */
public class JoinServerChoice extends MenuChoice {

    private final Delay delay;
    private String status;

    public JoinServerChoice(String label, Menu menu) {
        super(label, menu);
        status = "";
        delay = new Delay(2000);
    }

    @Override
    public void action(int button) {
        if (button == ACTION) {
            menu.game.online.joinServer();
            if (menu.game.online.client != null) {
                menu.setDefaultRoot();
            } else {
                status = " - " + Settings.language.menu.UnableToConnect;
                delay.start();
            }
        }
    }

    @Override
    public String getLabel() {
        if (delay.isOver()) {
            return label;
        }
        return label + status;
    }
}
