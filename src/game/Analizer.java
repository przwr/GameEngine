/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.io.BufferedWriter;
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
                settings.fullscreen = true;
            } else if (0 == p[1].compareTo("Off")) {
                settings.fullscreen = false;
            }
        } else if (0 == name.compareTo("FullScreen:")) {

        } else if (0 == name.compareTo("Tail")) {

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
        FileWriter fw = null;
        try {
            fw = new FileWriter("settings.ini");

            if (settings.fullscreen) {
                fw.write("FullScreen: On");
            } else {
                fw.write("FullScreen: Off");
            }
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
