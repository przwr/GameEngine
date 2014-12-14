/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.choices;

import game.Settings;
import game.gameobject.menu.AbstractMenuChoice;
import game.place.AbstractMenu;

/**
 *
 * @author przemek
 */
public class ChoiceBrightness extends AbstractMenuChoice {

    public ChoiceBrightness(String label, AbstractMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() != null) {
            float fl = (100 * menu.game.getPlace().r + 5f) / 100;
            menu.game.getPlace().r = fl;
            menu.game.getPlace().g = fl;
            menu.game.getPlace().b = fl;
            if (menu.game.getPlace().r > 1.00f) {
                menu.game.getPlace().r = 0.0f;
                menu.game.getPlace().g = 0.0f;
                menu.game.getPlace().b = 0.0f;
            }
        }
    }

    @Override
    public String getLabel() {
        if (menu.game.getPlace() != null) {
            return label + menu.game.getPlace().r;
        } else {
            return label + settings.language.m.StartGame;
        }
    }
}
