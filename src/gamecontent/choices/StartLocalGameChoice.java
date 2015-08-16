/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.Main;
import game.menu.Menu;
import game.menu.MenuChoice;
import gamecontent.MyGame;

/**
 * @author przemek
 */
public class StartLocalGameChoice extends MenuChoice {

    public StartLocalGameChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        ((MyGame) menu.game).setDesignerMode(false);
        menu.game.startGame();
        Main.refreshGamma();
        menu.setDefaultRoot();
    }
}
