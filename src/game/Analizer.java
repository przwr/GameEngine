/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author przemek
 */
public class Analizer {
    /* @args Grid
     * */

    public static void AnalizeSetting(String name, Settings settings) {
        String[] p = name.split("\\s+");
        if (0 == p[0].compareTo("FullScreen:")) {
            if (0 == p[1].compareTo("On")) {
                settings.fullScreen = true;
            } else if (0 == p[1].compareTo("Off")) {
                settings.fullScreen = false;
            }
        } else if (0 == p[0].compareTo("SplitMode:")) {
            if (0 == p[1].compareTo("H")) {
                settings.hSplitScreen = true;
            } else if (0 == p[1].compareTo("V")) {
                settings.hSplitScreen = false;
            }
        } else if (0 == p[0].compareTo("Number_Of_Players:")) {
            int n = Integer.parseInt(p[1]);
            if (n > 4 || n < 1) {
                settings.nrPlayers = 1;
            } else {
                settings.nrPlayers = n;
            }

        } else if (0 == name.compareTo("Wire")) {

        } else if (0 == name.compareTo("Diode")) {

        } else if (0 == name.compareTo("Copy")) {

        } else if (0 == name.compareTo("Clock")) {

        } else if (0 == name.compareTo("OR")) {

        } else if (0 == name.compareTo("XOR")) {

        }
        Save(settings);
    }

    public static void Save(Settings settings) {
        FileWriter fw;
        try {
            fw = new FileWriter("res/settings.ini");
            if (settings.fullScreen) {
                fw.write("FullScreen: On\n");
            } else {
                fw.write("FullScreen: Off\n");
            }
            if (settings.hSplitScreen) {
                fw.write("SplitMode: H\n");
            } else {
                fw.write("SplitMode: V\n");
            }
            fw.write("Number_Of_Players: " + settings.nrPlayers + "\n");
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Analizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void AnalizeSetting1(String s) {
        int row = 0;
        int col = 0;
        String[] p = s.split("\\s+");
        try {
            col = Integer.parseInt(p[0]);
            row = Integer.parseInt(p[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException("W linii \"" + s + "\" - zbyt mało danych!");
        } catch (NumberFormatException e) {
            throw new IllegalStateException("W linii \"" + s + "\" - powinny być tylko liczby całkowiete!");
        }

        if (row > 1000 || col > 1000) {
            throw new IllegalStateException("Za duże wymiary siatki: " + col + " x " + row + " Maksymalny wymiar to 1000!");
        } else {
        }
    }

}
