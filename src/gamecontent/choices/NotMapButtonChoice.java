/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gamecontent.choices;

import game.Settings;
import game.gameobject.entities.Player;
import game.gameobject.inputs.PlayerController;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * @author przemek
 */
public class NotMapButtonChoice extends MenuChoice {

    private final PlayerController ctrl;
    private final int i;

    public NotMapButtonChoice(String label, Menu menu, Player ctrl, int i) {
        super(label, menu);
        this.i = i;
        if (ctrl == null) {
            this.ctrl = null;
        } else {
            this.ctrl = ctrl.getController();
        }
    }

    @Override
    public void action(int button) {
    }

    @Override
    public String getLabel() {
        if (ctrl != null && ctrl.actions[i] != null && ctrl.actions[i].input != null) {
            return label + ": [" + ctrl.inputs[i].getLabel() + "] - " + Settings.language.menu.Unchangeable;
        } else {
            return label + ": " + Settings.language.menu.Empty + " - " + Settings.language.menu.Unchangeable;
        }
    }
}
