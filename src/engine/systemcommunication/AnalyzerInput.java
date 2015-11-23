/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.systemcommunication;

import game.Settings;
import game.gameobject.entities.Player;
import game.gameobject.inputs.AnyInput;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author przemek
 */
public class AnalyzerInput {

    public static void AnaliseInput(String name) {
        String[] p = name.split("\\s+");
        int players = Integer.parseInt(p[0]);
        int act = Integer.parseInt(p[1]);
        int type = Integer.parseInt(p[2]);
        int[] table = new int[p.length - 3];
        for (int i = 0; i < p.length - 3; i++) {
            table[i] = Integer.parseInt(p[i + 3]);
        }
        if (players != 1 || act >= 4) {
            Settings.players[players - 1].getController().actions[act].setInput(AnyInput.createInput(type, table));
        }
    }

    public static void Update() {
        FileWriter fw;
        try {
            fw = new FileWriter("res/input.ini");
            int p = 1;
            for (Player pl : Settings.players) {
                if (pl.getController() != null) {
                    for (int i = 0; i < Settings.actionsCount; i++) {
                        if (pl.getController().actions[i] != null && pl.getController().actions[i].input != null) {
                            fw.write(p + " " + i + " " + pl.getController().actions[i].input.toString() + "\n");
                        }
                    }
                }
                p++;
            }
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalyzerInput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
