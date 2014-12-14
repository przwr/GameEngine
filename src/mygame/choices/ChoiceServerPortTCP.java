/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.choices;

import game.Settings;
import game.gameobject.menu.AbstractMenuChoice;
import game.place.AbstractMenu;
import net.KryoUtil;

/**
 *
 * @author przemek
 */
public class ChoiceServerPortTCP extends AbstractMenuChoice {

    public ChoiceServerPortTCP(String label, AbstractMenu menu, Settings settings) {
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
