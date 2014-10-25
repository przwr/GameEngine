   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.Settings;
import game.myGame.MyPlayer;
import game.gameobject.menu.MenuChoice;
import game.myGame.MyController;
import game.myGame.MyMenu;

/**
 *
 * @author przemek
 */
public class ChoiceNMapButton extends MenuChoice {

    private final MyController ctrl;
    private final int i;

    public ChoiceNMapButton(String label, MyMenu menu, Settings settings, MyPlayer ctrl, int i) {
        super(label, menu, settings);
        this.i = i;
        if (ctrl == null) {
            this.ctrl = null;
        } else {
            this.ctrl = ctrl.ctrl;
        }
    }

    @Override
    public void action() {

    }

    @Override
    public String getLabel() {
        if (ctrl != null && ctrl.actions[i] != null && ctrl.actions[i].in != null) {
            return label + ": <" + ctrl.inputs[i].getLabel() + "> - " + settings.language.Unchangable;
        } else {
            return label + ": " + "<brak>" + " - " + settings.language.Unchangable;
        }
    }
}
