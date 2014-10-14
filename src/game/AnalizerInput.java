/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.gameobject.AnyInput;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author przemek
 */
public class AnalizerInput {
    /* @args Grid
     * */

    public static void AnalizeInput(String name, Settings settings) {
        String[] p = name.split("\\s+");
        if (0 == p[0].compareTo("1")) {
            int act = Integer.parseInt(p[1]);
            if (act > 3) {
                int type = Integer.parseInt(p[2]);
                int padnr = Integer.parseInt(p[3]);
                int key = Integer.parseInt(p[4]);
                settings.player1.ctrl.actions[act].in = AnyInput.CreateInput(type, padnr, key, settings);
            }
        } else if (0 == p[0].compareTo("2")) {
            int act = Integer.parseInt(p[1]);
            int type = Integer.parseInt(p[2]);
            int padnr = Integer.parseInt(p[3]);
            int key = Integer.parseInt(p[4]);
            settings.player2.ctrl.actions[act].in = AnyInput.CreateInput(type, padnr, key, settings);
        } else if (0 == p[0].compareTo("3")) {
            int act = Integer.parseInt(p[1]);
            int type = Integer.parseInt(p[2]);
            int padnr = Integer.parseInt(p[3]);
            int key = Integer.parseInt(p[4]);
            settings.player3.ctrl.actions[act].in = AnyInput.CreateInput(type, padnr, key, settings);
        } else if (0 == p[0].compareTo("4")) {
            int act = Integer.parseInt(p[1]);
            int type = Integer.parseInt(p[2]);
            int padnr = Integer.parseInt(p[3]);
            int key = Integer.parseInt(p[4]);
            settings.player4.ctrl.actions[act].in = AnyInput.CreateInput(type, padnr, key, settings);
        }
        Update(settings);
    }

    public static void Update(Settings settings) {
        FileWriter fw;
        try {
            fw = new FileWriter("res/input.ini");
            if (settings.player1.ctrl != null) {
                fw.write("Player 1\n");
                for (int i = 4; i < settings.actionsNr; i++) {
                    if (settings.player1.ctrl.actions[i].in != null) {
                        fw.write("1 " + i + " " + settings.player1.ctrl.actions[i].in.getType() + " " + settings.player1.ctrl.actions[i].in.getPadNr() + " " + settings.player1.ctrl.actions[i].in.getKey() + "\n");
                    }
                }
            }
            if (settings.player2.ctrl != null) {
                fw.write("Player 2\n");
                for (int i = 0; i < settings.actionsNr; i++) {
                    if (settings.player2.ctrl.actions[i].in != null) {
                        fw.write("2 " + i + " " + settings.player2.ctrl.actions[i].in.getType() + " " + settings.player2.ctrl.actions[i].in.getPadNr() + " " + settings.player2.ctrl.actions[i].in.getKey() + "\n");
                    }
                }
            }
            if (settings.player3.ctrl != null) {
                fw.write("Player 3\n");
                for (int i = 0; i < settings.actionsNr; i++) {
                    if (settings.player3.ctrl.actions[i].in != null) {
                        fw.write("3 " + i + " " + settings.player3.ctrl.actions[i].in.getType() + " " + settings.player3.ctrl.actions[i].in.getPadNr() + " " + settings.player3.ctrl.actions[i].in.getKey() + "\n");
                    }
                }
            }
            if (settings.player4.ctrl != null) {
                fw.write("Player 4\n");
                for (int i = 0; i < settings.actionsNr; i++) {
                    if (settings.player4.ctrl.actions[i].in != null) {
                        fw.write("4 " + i + " " + settings.player4.ctrl.actions[i].in.getType() + " " + settings.player4.ctrl.actions[i].in.getPadNr() + " " + settings.player4.ctrl.actions[i].in.getKey() + "\n");
                    }
                }
            }
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalizerInput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
