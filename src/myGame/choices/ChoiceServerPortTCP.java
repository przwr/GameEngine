/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame.choices;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;
import net.KryoUtil;

/**
 *
 * @author przemek
 */
public class ChoiceServerPortTCP extends MenuChoice {

    public ChoiceServerPortTCP(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
    }

    @Override
    public String getLabel() {
        return label + " TCP: " + KryoUtil.TCP_PORT + " - " + settings.language.m.Unchangable;
    }
}
