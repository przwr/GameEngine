/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import engine.Main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author przemek
 */
public final class IO {

    public static void readFile(File file, Settings settings, boolean isSettings) {
        try (BufferedReader read = new BufferedReader(new FileReader(file));) {
            String str;
            while ((str = read.readLine()) != null) {
                if (isSettings) {
                    AnalizerSettings.analizeSetting(str, settings);
                } else {
                    AnalizerInput.AnalizeInput(str, settings);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private IO() {
    }

}
