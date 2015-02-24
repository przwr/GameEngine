/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class BrightnessChoice extends MenuChoice {

    public BrightnessChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() != null) {
            float newValue = (100 * menu.game.getPlace().color.r + 5f) / 100;
            menu.game.getPlace().color.r = newValue;
            menu.game.getPlace().color.g = newValue;
            menu.game.getPlace().color.b = newValue;
            if (menu.game.getPlace().color.r > 1.00f) {
                menu.game.getPlace().color.r = 0.0f;
                menu.game.getPlace().color.g = 0.0f;
                menu.game.getPlace().color.b = 0.0f;
            }
        }
    }

    @Override
    public String getLabel() {
        return (menu.game.getPlace() != null) ? label + menu.game.getPlace().color.r
                : label + Settings.language.menu.StartGame;
    }
}
