/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MyMenu;
import openGLEngine.Sound;

/**
 *
 * @author przemek
 */
public class ChoiceStop extends MenuChoice {

    public ChoiceStop(String label, MyMenu menu, Settings settings) {
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
