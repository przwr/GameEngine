/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;
import sounds.Sound;

import java.util.Iterator;

/**
 * @author przemek
 */
public class StopChoice extends MenuChoice {

    public StopChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        if (button == ACTION) {
            if (menu.game.getPlace() != null) {
                if (Settings.sounds != null) {
                    Iterator it = Settings.sounds.getSoundsMap().entrySet().iterator();
                    while (it.hasNext()) {
                        java.util.Map.Entry<String, Sound> pair = (java.util.Map.Entry) it.next();
                        pair.getValue().stop();
                    }
                }
                menu.game.endGame();
            }
        }
    }
}
