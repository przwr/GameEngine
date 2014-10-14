/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author przemek
 */
public class IO {

    public static void ReadFile(File f, Settings settings) {
        try {
            FileReader fr = new FileReader(f);
            BufferedReader r = new BufferedReader(fr);
            String s;
            while ((s = r.readLine()) != null) {
                System.out.println("Przeczytano: " + s);
                AnalizerSettings.AnalizeSetting(s, settings);
            }
        } catch (IOException | IllegalStateException e) {
            System.out.println(e);
        }
    }

    public static void ReadFileInput(File f, Settings settings) {
        try {
            FileReader fr = new FileReader(f);
            BufferedReader r = new BufferedReader(fr);
            String s;
            while ((s = r.readLine()) != null) {
                System.out.println("Przeczytano: " + s);
                AnalizerInput.AnalizeInput(s, settings);
            }
        } catch (IOException | IllegalStateException e) {
            System.out.println(e);
        }
    }
}
