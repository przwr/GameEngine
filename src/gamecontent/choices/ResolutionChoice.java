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
public class ResolutionChoice extends MenuChoice {

    public ResolutionChoice(String label, Menu menu, Settings settings) {
        super(label, menu, settings);
    }

    @Override
    public void action() {
        settings.curentMode++;
        if (settings.curentMode >= settings.modes.length) {
            settings.curentMode = 0;
        }
        settings.resWidth = settings.modes[settings.curentMode].getWidth();
        settings.resHeight = settings.modes[settings.curentMode].getHeight();
        settings.freq = settings.modes[settings.curentMode].getFrequency();
        AnalizerSettings.update(settings);
    }

    @Override
    public String getLabel() {
        return label + settings.resWidth + " x " + settings.resHeight + " @ " + settings.freq + " Hz [" + (settings.curentMode + 1) + "/" + settings.modes.length + "]";
    }
}
