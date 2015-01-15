/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choice;

import engine.Sound;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class StopChoice extends MenuChoice {

    public StopChoice(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() != null) {
            if (settings.sounds != null) {
                for (Sound s : settings.sounds.getSoundsList()) {
                    s.stop();
                }
            }
            menu.game.endGame();
        }
    }
}
