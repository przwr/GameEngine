/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MyMenu;

/**
 *
 * @author przemek
 */
public class ChoiceBrightness extends MenuChoice {

    public ChoiceBrightness(String label, MyMenu menu) {
        super(label, menu);
    }

    @Override
    public void action() {

        if (menu.game.getPlace() != null) {
            float fl = (100 * menu.game.getPlace().r + 5f);
            fl = fl / 100;
            menu.game.getPlace().r = fl;
            menu.game.getPlace().g = fl;
            menu.game.getPlace().b = fl;
            if (menu.game.getPlace().r > 1.00f) {
                menu.game.getPlace().r = 0.05f;
                menu.game.getPlace().g = 0.05f;
                menu.game.getPlace().b = 0.05f;
            }
        }
    }

    @Override
    public String getLabel() {
        if (menu.game.getPlace() != null) {
            return label + menu.game.getPlace().r;
        } else {
            return label + "Uruchom grę!";
        }
    }
}
