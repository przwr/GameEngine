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
public class StartChoice extends MenuChoice {

    public StartChoice(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        if (!menu.game.started) {
            menu.setCurrent(7);
        } else {
            menu.game.resumeGame();
        }
    }

    @Override
    public String getLabel() {
        if (!menu.game.started) {
            return label;
        } else {
            return settings.language.m.Resume;
        }
    }
}