   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import game.Settings;
import game.gameobject.Controler;
import game.gameobject.Player;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class NotMapButtonChoice extends MenuChoice {

    private final Controler ctrl;
    private final int i;

    public NotMapButtonChoice(String label, Menu menu, Settings settings, Player ctrl, int i) {
        super(label, menu, settings);
        this.i = i;
        if (ctrl == null) {
            this.ctrl = null;
        } else {
            this.ctrl = ctrl.controler;
        }
    }

    @Override
    public void action() {

    }

    @Override
    public String getLabel() {
        if (ctrl != null && ctrl.actions[i] != null && ctrl.actions[i].in != null) {
            return label + ": [" + ctrl.inputs[i].getLabel() + "] - " + settings.language.m.Unchangable;
        } else {
            return label + ": " + settings.language.m.Empty + " - " + settings.language.m.Unchangable;
        }
    }
}
