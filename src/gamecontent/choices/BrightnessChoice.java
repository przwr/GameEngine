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
            float fl = (100 * menu.game.getPlace().red + 5f) / 100;
            menu.game.getPlace().red = fl;
            menu.game.getPlace().green = fl;
            menu.game.getPlace().blue = fl;
            if (menu.game.getPlace().red > 1.00f) {
                menu.game.getPlace().red = 0.0f;
                menu.game.getPlace().green = 0.0f;
                menu.game.getPlace().blue = 0.0f;
            }
        }
    }

    @Override
    public String getLabel() {
        return (menu.game.getPlace() != null) ? label + menu.game.getPlace().red
                : label + Settings.language.menu.StartGame;
    }
}
