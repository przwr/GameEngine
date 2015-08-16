/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gamecontent.choices;

import engine.inout.AnalyzerInput;
import engine.inout.PlayerControllers;
import game.Settings;
import game.gameobject.Action;
import game.gameobject.AnyInput;
import game.gameobject.PlayerController;
import game.gameobject.inputs.InputExitMapping;
import game.gameobject.inputs.InputNull;
import game.menu.Menu;
import game.menu.MenuChoice;

/**
 * @author przemek
 */
public class MapButtonChoice extends MenuChoice {

    private final PlayerController ctrl;
    private final int i;
    private final Runnable run;
    private Thread thread;
    private boolean mapped;
    private int maxAxNr;

    public MapButtonChoice(String label, final Menu menu, final PlayerController ctrl, final int i) {
        super(label, menu);
        this.i = i;
        this.ctrl = ctrl;
        run = () -> {
            int noiseAx[] = findNoiseAx();
            mapped = true;
            while (mapped) {
                AnyInput in = PlayerControllers.mapInput(noiseAx, maxAxNr, ctrl.actions[2].input);
                if (in != null) {
                    if (in instanceof InputExitMapping || (ctrl.actions[3] != null && ctrl.actions[3].input != null && ctrl.actions[3].input.toString().equals(in.toString()))) {
                        break;
                    }
                    if (in instanceof InputNull) {
                        ctrl.actions[i].input = null;
                        break;
                    }
                    if (i < 4) {
                        for (Action action : ctrl.actions) {
                            if (action != null && action.input != null && action.input.toString().equals(in.toString())) {
                                action.input = ctrl.actions[i].input;
                                set(in);
                            }
                        }
                    } else {
                        for (int k = 4; k < ctrl.actions.length; k++) {
                            if (ctrl.actions[k] != null && ctrl.actions[k].input != null && ctrl.actions[k].input.toString().equals(in.toString())) {
                                ctrl.actions[k].input = ctrl.actions[i].input;
                                set(in);
                            }
                        }
                    }
                    set(in);
                }
            }
            end();
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
            return label + ": " + Settings.language.menu.PushButton;
        } else if (ctrl != null && ctrl.actions[i] != null && ctrl.actions[i].input != null) {
            return label + ": [" + ctrl.actions[i].input.getLabel() + "]";
        } else {
            return label + ": " + Settings.language.menu.Empty;
        }
    }

    private int[] findNoiseAx() {
        int size = PlayerControllers.getControllers().length;
        maxAxNr = 0;
        int curAxNr;
        int a, k;
        for (k = 0; k < size; k++) {
            curAxNr = PlayerControllers.getControllers()[k].getAxisCount();
            maxAxNr = curAxNr > maxAxNr ? curAxNr : maxAxNr;
        }
        int noiseAx[] = new int[maxAxNr * size];
        for (int i = 0; i < noiseAx.length; i++) {
            noiseAx[i] = -1;
        }
        for (k = 0; k < size; k++) {
            if (PlayerControllers.getControllers()[k] != null) {
                for (a = 0; a < PlayerControllers.getControllers()[k].getAxisCount(); a++) {
                    if (PlayerControllers.getControllers()[k].getAxisValue(a) > 0.3f || PlayerControllers.getControllers()[k].getAxisValue(a) < -0.3f) {
                        noiseAx[k * maxAxNr + a] = a;
                    }
                }
            }
        }
        return noiseAx;
    }

    private void set(AnyInput in) {
        ctrl.actions[i].input = in;
        AnalyzerInput.Update();
        mapped = false;
    }

    private void end() {
        thread = null;
        menu.delay.start();
        menu.isMapping = false;
    }
}
