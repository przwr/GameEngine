/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class ChoiceStart extends MenuChoice {

    public ChoiceStart(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() == null) {
            menu.game.startGame();
        } else {
            menu.game.resumeGame();
        }
    }

    @Override
    public String getLabel() {
        if (menu.game.getPlace() == null) {
            return label;
        } else {
            return settings.language.Resume;
        }
    }

}
