   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.choices;

import game.Settings;
import game.gameobject.AbstractControler;
import game.gameobject.AbstractPlayer;
import game.gameobject.menu.AbstractMenuChoice;
import game.place.AbstractMenu;

/**
 *
 * @author przemek
 */
public class ChoiceNMapButton extends AbstractMenuChoice {

    private final AbstractControler ctrl;
    private final int i;

    public ChoiceNMapButton(String label, AbstractMenu menu, Settings settings, AbstractPlayer ctrl, int i) {
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
            return label + ": [" + ctrl.inputs[i].getLabel() + "] - " + settings.language.m.Unchangable;
        } else {
            return label + ": " + settings.language.m.Empty + " - " + settings.language.m.Unchangable;
        }
    }
}
