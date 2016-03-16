/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.Main;
import engine.systemcommunication.AnalyzerInput;
import engine.systemcommunication.AnalyzerSettings;
import engine.systemcommunication.IO;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;
import java.io.File;

/**
 *
 * @author Wojtek
 */
public class DefaultInputChoice extends MenuChoice {

    private final int player;
    private final int template;

    public DefaultInputChoice(String label, int player, int template, Menu menu) {
        super(label, menu);
        this.player = player;
        this.template = template;
    }

    @Override
    public void action(int button) {
        if (button == ACTION) {
            IO.loadDefaultInputFromFile(new File("res/defaultInput.ini"), player, template);
            AnalyzerInput.update();
            menu.back();
        }
    }
}
