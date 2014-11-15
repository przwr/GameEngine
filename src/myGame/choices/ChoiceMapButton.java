   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame.choices;

import engine.Controlers;
import game.AnalizerInput;
import game.Settings;
import game.gameobject.AnyInput;
import game.gameobject.Controler;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;
import org.lwjgl.input.Controllers;

/**
 *
 * @author przemek
 */
public class ChoiceMapButton extends MenuChoice {

    private final Controler ctrl;
    private final int i;
    private Thread thread;
    private final Runnable run;

    public ChoiceMapButton(String label, final Menu menu, final Settings settings, final Controler ctrl, final int i) {
        super(label, menu, settings);
        this.i = i;
        this.ctrl = ctrl;
        run = new Runnable() {
            @Override
            public void run() {
                int noiseAx[] = new int[2 * Controllers.getControllerCount()];
                findNoiseAx(noiseAx);
                main:
                while (true) {
                    AnyInput in = Controlers.mapInput(noiseAx, ctrl.actions[2].in);
                    if (in != null) {
                        if (in.getType() == -1 || (ctrl.actions[3] != null && ctrl.actions[3].in != null && ctrl.actions[3].in.toString().equals(in.toString()))) {
                            break;
                        }
                        if (in.getType() == -2) {
                            ctrl.actions[i].in = null;
                            break;
                        }
                        if (i < 4) {
                            for (int k = 0; k < 4; k++) {
                                if (ctrl.actions[k] != null && ctrl.actions[k].in != null && ctrl.actions[k].in.toString().equals(in.toString())) {
                                    AnyInput temp = ctrl.actions[i].in;
                                    ctrl.actions[k].in = temp;
                                    set(in);
                                    break main;
                                }
                            }
                        } else {
                            for (int k = 4; k < ctrl.actions.length; k++) {
                                if (ctrl.actions[k] != null && ctrl.actions[k].in != null && ctrl.actions[k].in.toString().equals(in.toString())) {
                                    AnyInput temp = ctrl.actions[i].in;
                                    ctrl.actions[k].in = temp;
                                    set(in);
                                    break main;
                                }
                            }
                        }
                        set(in);
                        break;
                    }
                }
                end();
            }
        };
    }

    @Override
    public void action() {
        if (ctrl != null && ctrl.actions[i] != null) {
            menu.isMapping = true;
            thread = new Thread(run);
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
            return label + ": " + settings.language.Empty;
        }
    }

    private void findNoiseAx(int noiseAx[]) {
        for (int i = 0; i < noiseAx.length; i++) {
            noiseAx[i] = -1;
        }
        for (int k = 0; k < Controllers.getControllerCount(); k++) {
            if (Controlers.getControllers()[k] != null) {
                int a;
                for (a = 0; a < Controlers.getControllers()[k].getAxisCount(); a++) {
                    if (Controlers.getControllers()[k].getAxisValue(a) > 0.9f || Controlers.getControllers()[k].getAxisValue(a) < -0.9f) {
                        noiseAx[k] = a;
                        break;
                    }
                }
                for (a = 0; a < Controlers.getControllers()[k].getAxisCount(); a++) {
                    if (a != noiseAx[k] && Controlers.getControllers()[k].getAxisValue(a) > 0.9f || Controlers.getControllers()[k].getAxisValue(a) < -0.9f) {
                        noiseAx[Controllers.getControllerCount() + k] = a;
                        break;
                    }
                }
            }
        }
    }

    private void set(AnyInput in) {
        ctrl.actions[i].in = in;
        AnalizerInput.Update(settings);
    }

    private void end() {
        thread = null;
        menu.delay.restart();
        menu.isMapping = false;
    }
}
