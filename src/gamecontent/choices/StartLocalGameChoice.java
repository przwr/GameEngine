/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.Main;
import game.Settings;
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
    public void action(int button) {
        if (button == ACTION) {
            System.out.println(Settings.language.menu.Loading + " . . .");
            ((MyGame) menu.game).loading(0);
            ((MyGame) menu.game).setDesignerMode(false);
            menu.game.startGame();
            Main.refreshGammaAndBrightness();
            menu.setDefaultRoot();
        }
    }
}
