/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.systemcommunication;

import engine.Main;
import game.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author przemek
 */
public final class IO {

    public static void setSettingsFromFile(File file) {
        try (BufferedReader read = new BufferedReader(new FileReader(file))) {
            Settings.initialize();
            String line;
            while ((line = read.readLine()) != null) {
                AnalyzerSettings.analyzeSetting(line);
            }
            read.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loadInputFromFile(File file) {
        try (BufferedReader read = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = read.readLine()) != null) {
                AnalyzerInput.AnaliseInput(line);
            }
            read.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ArrayList<File> getAllSpecificFilesList(File folder, String extension) {
        ArrayList<File> list = new ArrayList<>();
        File[] files = folder.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                list.addAll(getSpecificFilesList(f, extension));
            } else if (f.getPath().endsWith(extension)) {
                list.add(f);
            }
        }
        return list;
    }

    public static ArrayList<File> getSpecificFilesList(File folder, String extension) {
        ArrayList<File> list = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    list.add(f);
                }
            }
            for (File f : files) {
                if (f.getPath().endsWith(extension)) {
                    list.add(f);
                }
            }
        }
        return list;
    }

    public static ArrayList<File> getSpecificFilesList(String folder, String extension) {
        File f = new File(folder);
        return getSpecificFilesList(f, extension);
    }

    public static String getFilePath(File f) {
        return f.getPath().replace(File.separatorChar + f.getName(), "");
    }
}
