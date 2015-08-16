/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * @author przemek
 */
public class StartChoice extends MenuChoice {

    public StartChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() == null) {
            menu.setRoot(this);
        } else {
            menu.game.resumeGame();
        }
    }

    @Override
    public String getLabel() {
        if (menu.game.getPlace() == null) {
            return label;
        } else {
            return Settings.language.menu.Resume;
        }
    }
}
