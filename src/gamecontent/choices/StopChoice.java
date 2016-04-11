/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.Launcher;
import game.menu.Menu;
import game.menu.MenuChoice;

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
                menu.game.endGame();
                Launcher.restart = true;
                menu.game.exit();
            }
        }
    }
}
