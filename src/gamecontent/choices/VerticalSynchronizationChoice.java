/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class VerticalSynchronizationChoice extends MenuChoice {

    public VerticalSynchronizationChoice(String label, Menu menu) {
        super(label, menu);
    }

    @Override
    public void action() {
        Settings.verticalSynchronization = !Settings.verticalSynchronization;
        AnalizerSettings.update();
    }

    @Override
    public String getLabel() {
        if (Settings.verticalSynchronization) {
            return label + Settings.language.menu.On;
        } else {
            return label + Settings.language.menu.Off;
        }
    }
}
