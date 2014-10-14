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
import openGLEngine.Controlers;
import org.lwjgl.input.Keyboard;

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
                            while (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                                AnyInput in = Controlers.mapInput();
                                if (in != null) {
                                    if (i < 4) {
                                        for (int k = 0; k < 4; k++) {
                                            if (ctrl.actions[k] != null && ctrl.actions[k].in != null && ctrl.actions[k].in.getType() == in.getType() && ctrl.actions[k].in.getKey() == in.getKey() && ctrl.actions[k].in.getPadNr() == in.getPadNr()) {
                                                AnyInput temp = ctrl.actions[i].in;
                                                ctrl.actions[k].in = temp;
                                                ctrl.actions[i].in = in;
                                                thread = null;
                                                AnalizerInput.Update(settings);
                                                break main;
                                            }
                                        }
                                    } else {
                                        for (int k = 4; k < ctrl.actions.length; k++) {
                                            if (ctrl.actions[k] != null && ctrl.actions[k].in != null && ctrl.actions[k].in.getType() == in.getType() && ctrl.actions[k].in.getKey() == in.getKey() && ctrl.actions[k].in.getPadNr() == in.getPadNr()) {
                                                AnyInput temp = ctrl.actions[i].in;
                                                ctrl.actions[k].in = temp;
                                                ctrl.actions[i].in = in;
                                                thread = null;
                                                AnalizerInput.Update(settings);
                                                break main;
                                            }
                                        }
                                    }
                                    ctrl.actions[i].in = in;
                                    thread = null;
                                    AnalizerInput.Update(settings);
                                    break main;
                                }
                            }
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
