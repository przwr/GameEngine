   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.choices;

import engine.Controlers;
import game.AnalizerInput;
import game.Settings;
import game.gameobject.Action;
import game.gameobject.AnyInput;
import game.gameobject.Controler;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;

/**
 *
 * @author przemek
 */
public class ChoiceMapButton extends MenuChoice {

    private final Controler ctrl;
    private final int i;
    private Thread thread;
    private final Runnable run;
    private boolean mapped;
    private int maxAxNr;

    public ChoiceMapButton(String label, final Menu menu, final Settings settings, final Controler ctrl, final int i) {
        super(label, menu, settings);
        this.i = i;
        this.ctrl = ctrl;
        run = new Runnable() {
            @Override
            public void run() {
                int noiseAx[] = findNoiseAx();
                mapped = true;
                while (mapped) {
                    AnyInput in = Controlers.mapInput(noiseAx, maxAxNr, ctrl.actions[2].in);
                    if (in != null) {
                        if (in.getType() == -1 || (ctrl.actions[3] != null && ctrl.actions[3].in != null && ctrl.actions[3].in.toString().equals(in.toString()))) {
                            break;
                        }
                        if (in.getType() == -2) {
                            ctrl.actions[i].in = null;
                            break;
                        }
                        if (i < 4) {
                            for (Action action : ctrl.actions) {
                                if (action != null && action.in != null && action.in.toString().equals(in.toString())) {
                                    AnyInput temp = ctrl.actions[i].in;
                                    action.in = temp;
                                    set(in);
                                }
                            }
                        } else {
                            for (int k = 4; k < ctrl.actions.length; k++) {
                                if (ctrl.actions[k] != null && ctrl.actions[k].in != null && ctrl.actions[k].in.toString().equals(in.toString())) {
                                    AnyInput temp = ctrl.actions[i].in;
                                    ctrl.actions[k].in = temp;
                                    set(in);
                                }
                            }
                        }
                        set(in);
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
            return label + ": " + settings.language.m.PushButton;
        } else if (ctrl != null && ctrl.actions[i] != null && ctrl.actions[i].in != null) {
            return label + ": [" + ctrl.actions[i].in.getLabel() + "]";
        } else {
            return label + ": " + settings.language.m.Empty;
        }
    }

    private int[] findNoiseAx() {
        int size = Controlers.getControllers().length;
        maxAxNr = 0;
        int curAxNr;
        int a, k;
        for (k = 0; k < size; k++) {
            curAxNr = Controlers.getControllers()[k].getAxisCount();
            maxAxNr = curAxNr > maxAxNr ? curAxNr : maxAxNr;
        }
        int noiseAx[] = new int[maxAxNr * size];
        for (int i = 0; i < noiseAx.length; i++) {
            noiseAx[i] = -1;
        }
        for (k = 0; k < size; k++) {
            if (Controlers.getControllers()[k] != null) {
                for (a = 0; a < Controlers.getControllers()[k].getAxisCount(); a++) {
                    if (Controlers.getControllers()[k].getAxisValue(a) > 0.3f || Controlers.getControllers()[k].getAxisValue(a) < -0.3f) {
                        noiseAx[k * maxAxNr + a] = a;
                    }
                }
            }
        }
        return noiseAx;
    }

    private void set(AnyInput in) {
        ctrl.actions[i].in = in;
        AnalizerInput.Update(settings);
        mapped = false;
    }

    private void end() {
        thread = null;
        menu.delay.restart();
        menu.isMapping = false;
    }
}
