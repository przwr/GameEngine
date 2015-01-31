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

    public static Settings getSettingsFromFile(File file) {
        try (BufferedReader read = new BufferedReader(new FileReader(file));) {
            Settings settings = new Settings();
            String line;
            while ((line = read.readLine()) != null) {
                AnalizerSettings.analizeSetting(line, settings);
            }
            return settings;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void loadInputFromFile(File file, Settings settings) {
        try (BufferedReader read = new BufferedReader(new FileReader(file));) {
            String line;
            while ((line = read.readLine()) != null) {
                AnalizerInput.AnalizeInput(line, settings);
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
