   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.menu.choices;

import game.AnalizerInput;
import game.Settings;
import game.gameobject.AnyInput;
import game.gameobject.Player;
import game.gameobject.menu.MenuChoice;
import game.myGame.MyController;
import game.myGame.MyMenu;
import engine.Controlers;

/**
 *
 * @author przemek
 */
public class ChoiceMapButton extends MenuChoice {

    private final MyController ctrl;
    private final int i;
    private Thread thread;

    public ChoiceMapButton(String label, MyMenu menu, Settings settings, Player ctrl, int i) {
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
        if (ctrl != null && ctrl.actions[i] != null) {
            thread = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            main:
                            while (true) {
                                AnyInput in = Controlers.mapInput();
                                if (in != null) {
                                    if (in.getType() == -1) {
                                        break;
                                    }
                                    if (i < 4) {
                                        for (int k = 0; k < 4; k++) {
                                            if (ctrl.actions[k] != null && ctrl.actions[k].in != null && ctrl.actions[k].in.toString().equals(in.toString())) {
                                                AnyInput temp = ctrl.actions[i].in;
                                                ctrl.actions[k].in = temp;
                                                ctrl.actions[i].in = in;
                                                AnalizerInput.Update(settings);
                                                break main;
                                            }
                                        }
                                    } else {
                                        for (int k = 4; k < ctrl.actions.length; k++) {
                                            if (ctrl.actions[k] != null && ctrl.actions[k].in != null && ctrl.actions[k].in.toString().equals(in.toString())) {
                                                AnyInput temp = ctrl.actions[i].in;
                                                ctrl.actions[k].in = temp;
                                                ctrl.actions[i].in = in;
                                                AnalizerInput.Update(settings);
                                                break main;
                                            }
                                        }
                                    }
                                    ctrl.actions[i].in = in;
                                    AnalizerInput.Update(settings);
                                    break;
                                }
                            }
                            thread.interrupt();
                            thread = null;
                        }
                    });
            thread.start();
        }
    }

    @Override
    public String getLabel() {
        if (thread != null) {
            return label + ": " + settings.language.PushButton;
        } else if (ctrl != null && ctrl.actions[i] != null && ctrl.actions[i].in != null) {
            return label + ": <" + ctrl.actions[i].in.getLabel() + ">";
        } else {
            return label + ": " + "<brak>";
        }
    }
}
