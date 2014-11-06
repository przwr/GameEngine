   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class ChoiceResolution extends MenuChoice {

    public ChoiceResolution(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.curMode++;
        if (settings.curMode >= settings.modes.length) {
            settings.curMode = 0;
        }
        settings.resWidth = settings.modes[settings.curMode].getWidth();
        settings.resHeight = settings.modes[settings.curMode].getHeight();
        settings.freq = settings.modes[settings.curMode].getFrequency();
        AnalizerSettings.Update(settings);
    }

    @Override
    public String getLabel() {
        return label + settings.resWidth + " x " + settings.resHeight + " @ " + settings.freq + " Hz [" + (settings.curMode + 1) + "/" + settings.modes.length + "]";
    }
}
