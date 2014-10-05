/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.Analizer;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.gameobject.menu.MyMenu;

/**
 *
 * @author przemek
 */
public class ChoiceSplitScreen extends MenuChoice {

    public ChoiceSplitScreen(String label, MyMenu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        if (menu.game.getPlace() == null) {
            settings.hSplitScreen = !settings.hSplitScreen;
            Analizer.Save(settings);
        }
    }

    @Override
    public String getLabel() {
        if (menu.game.getPlace() != null) {
            if (settings.hSplitScreen) {
                return label + "Zakończ grę by zmienić";
            } else {
                return label + "Zakończ grę by zmienić";
            }

        } else {
            if (settings.hSplitScreen) {
                return label + "Poziomo (H)";
            } else {
                return label + "Pionowo (V)";
            }
        }
    }
}
