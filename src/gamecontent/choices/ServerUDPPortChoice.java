/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;
import net.KryoUtil;

/**
 * @author przemek
 */
public class ServerUDPPortChoice extends MenuChoice {

    public ServerUDPPortChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
    }

    @Override
    public String getLabel() {
        return label + " UDP: " + KryoUtil.UDP_PORT + " - " + Settings.language.menu.Unchangeable;
    }
}
