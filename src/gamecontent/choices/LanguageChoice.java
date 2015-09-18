/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.systemcommunication.AnalyzerSettings;
import game.Settings;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * @author przemek
 */
public class LanguageChoice extends MenuChoice {

    private int i;

    public LanguageChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action(int button) {
        for (i = 0; i < Settings.languages.size(); i++) {
            if (Settings.languages.get(i).lang.equals(Settings.languageName)) {
                if (button == LEFT) {
                    i--;
                } else {
                    i++;
                }
                break;
            }
        }
        if (i >= Settings.languages.size()) {
            i = 0;
        } else if (i < 0) {
            i = Settings.languages.size() - 1;
        }
        Settings.languageName = Settings.languages.get(i).lang;
        AnalyzerSettings.update();
    }

    @Override
    public String getLabel() {
        for (i = 0; i < Settings.languages.size(); i++) {
            if (Settings.languages.get(i).lang.equals(Settings.languageName)) {
                i++;
                break;
            }
        }
        i--;
        if (i >= Settings.languages.size()) {
            i = 0;
        }
        return label + Settings.languageName + " (" + (i + 1) + "/" + Settings.languages.size() + ")";
    }
}