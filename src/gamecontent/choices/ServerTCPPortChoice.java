/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;
import net.KryoUtil;

/**
 * @author przemek
 */
public class ServerTCPPortChoice extends MenuChoice {

    public ServerTCPPortChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
    }

    @Override
    public String getLabel() {
        return label + " TCP: " + KryoUtil.TCP_PORT + " - " + Settings.language.menu.Unchangeable;
    }
}
