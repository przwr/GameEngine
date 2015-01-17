/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.Delay;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class JoinServerChoice extends MenuChoice {

    private String status;
    private final Delay delay;

    public JoinServerChoice(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
        status = "";
        delay = new Delay(2000);
    }

    @Override
    public void action() {
        menu.game.online.joinServer();
        if (menu.game.online.client != null) {
            menu.setCurrent(0);
        } else {
            status = " - " + settings.language.m.UnableToConnect;
            delay.restart();
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
