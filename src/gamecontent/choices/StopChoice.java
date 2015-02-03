/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.Sound;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class StopChoice extends MenuChoice {

    public StopChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() != null) {
            if (Settings.sounds != null) {
                for (Sound s : Settings.sounds.getSoundsList()) {
                    s.stop();
                }
            }
            menu.game.endGame();
        }
    }
}
