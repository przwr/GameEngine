/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.gameobject.AnyInput;
import game.gameobject.Player;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author przemek
 */
public class AnalizerInput {

    public static void AnalizeInput(String name, Settings settings) {
        String[] p = name.split("\\s+");
        int pl = Integer.parseInt(p[0]);
        int act = Integer.parseInt(p[1]);
        int type = Integer.parseInt(p[2]);
        int[] table = new int[p.length - 3];
        for (int i = 0; i < p.length - 3; i++) {
            table[i] = Integer.parseInt(p[i + 3]);
        }
        if (pl != 1 || act >= 4) {
            settings.players[pl - 1].ctrl.actions[act].in = AnyInput.createInput(type, table, settings);
        }
    }

    public static void Update(Settings settings) {
        FileWriter fw;
        try {
            fw = new FileWriter("res/input.ini");
            int p = 1;
            for (Player pl : settings.players) {
                if (pl.ctrl != null) {
                    for (int i = 0; i < settings.actionsNr; i++) {
                        if (pl.ctrl.actions[i].in != null) {
                            fw.write(p + " " + i + " " + pl.ctrl.actions[i].in.toString() + "\n");
                        }
                    }
                }
                p++;
            }

            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalizerInput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private AnalizerInput() {
    }
}
