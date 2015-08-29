/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.inout;

import game.Settings;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author przemek
 */
public final class AnalyzerSettings {

    private AnalyzerSettings() {
    }

    public static void analyzeSetting(String name) {
        final String[] p = name.split("\\s+");
        switch (p[0]) {
            case "FullScreen:":
                if (0 == p[1].compareTo("On")) {
                    Settings.fullScreen = true;
                }
                break;
            case "SplitMode:":
                if (0 == p[1].compareTo("H")) {
                    Settings.horizontalSplitScreen = true;
                }
                break;
            case "VSync:":
                if (0 == p[1].compareTo("On")) {
                    Settings.verticalSynchronization = true;
                }
                break;
            case "NumberOfSamples:":
                int samplesCount = Integer.parseInt(p[1]);
                Settings.samplesCount = (samplesCount > 64 || samplesCount < 0) ? 0 : samplesCount;
                break;
            case "ShadowOff:":
                if (0 == p[1].compareTo("On")) {
                    Settings.shadowOff = true;
                }
                break;
            case "NumberOfPlayers:":
                int playersCount = Integer.parseInt(p[1]);
                Settings.playersCount = (playersCount > 4 || playersCount < 1) ? 1 : playersCount;
                break;
            case "ServerIP:":
                Settings.serverIP = p[1];
                break;
            case "ResolutionWidth:":
                final int w = Integer.parseInt(p[1]);
                Settings.resolutionWidth = (w <= 0) ? Settings.modes[0].getWidth() : w;
                for (int index = 0; index < Settings.modes.length; index++) {
                    if (Settings.modes[index].getWidth() == Settings.resolutionWidth && Settings.modes[index].getHeight() == Settings.resolutionHeight
                            && Settings.modes[index].getFrequency() == Settings.frequency) {
                        Settings.currentMode = index;
                    }
                }
                break;
            case "ResolutionHeight:":
                final int h = Integer.parseInt(p[1]);
                Settings.resolutionHeight = (h <= 0) ? Settings.modes[0].getHeight() : h;
                for (int i = 0; i < Settings.modes.length; i++) {
                    if (Settings.modes[i].getWidth() == Settings.resolutionWidth && Settings.modes[i].getHeight() == Settings.resolutionHeight && Settings.modes[i].getFrequency() == Settings.frequency) {
                        Settings.currentMode = i;
                    }
                }
                break;
            case "ResolutionFreq:":
                final int f = Integer.parseInt(p[1]);
                Settings.frequency = (f <= 0) ? Settings.modes[0].getFrequency() : f;
                for (int i = 0; i < Settings.modes.length; i++) {
                    if (Settings.modes[i].getWidth() == Settings.resolutionWidth && Settings.modes[i].getHeight() == Settings.resolutionHeight && Settings.modes[i].getFrequency() == Settings.frequency) {
                        Settings.currentMode = i;
                    }
                }
                break;
            case "Volume:":
                final float v = Float.parseFloat(p[1]);
                if (v >= -0.01f && v <= 1.01f) {
                    Settings.volume = v;
                }
                break;
            case "Language:":
                if (0 == p[1].compareTo("PL")) {
                    Settings.languageName = "PL";
                    Settings.language = Settings.languages.get(0);
                } else if (0 == p[1].compareTo("ENG")) {
                    Settings.languageName = "ENG";
                    Settings.language = Settings.languages.get(1);
                }
                break;
            case "DefaultGamma:":
                final float d = Float.parseFloat(p[1]);
                if (d >= 0 && d <= 2f) {
                    Settings.defaultGamma = d;
                }
                break;
            case "GameGamma:":
                final float g = Float.parseFloat(p[1]);
                if (g >= 1f && g <= 3f) {
                    Settings.gameGamma = g;
                }
                break;
            default:
        }
    }

    public static void update() {
        FileWriter writer;
        try {
            writer = new FileWriter("res/settings.ini");
            if (Settings.fullScreen) {
                writer.write("FullScreen: On\n");
            } else {
                writer.write("FullScreen: Off\n");
            }
            if (Settings.horizontalSplitScreen) {
                writer.write("SplitMode: H\n");
            } else {
                writer.write("SplitMode: V\n");
            }
            if (Settings.verticalSynchronization) {
                writer.write("VSync: On\n");
            } else {
                writer.write("VSync: Off\n");
            }
            if (Settings.shadowOff) {
                writer.write("ShadowOff: On\n");
            } else {
                writer.write("ShadowOff: Off\n");
            }
            writer.write("ServerIP: " + Settings.serverIP + "\n");
            writer.write("NumberOfPlayers: " + Settings.playersCount + "\n");
            writer.write("NumberOfSamples: " + Settings.samplesCount + "\n");
            writer.write("ResolutionWidth: " + Settings.resolutionWidth + "\n");
            writer.write("ResolutionHeight: " + Settings.resolutionHeight + "\n");
            writer.write("ResolutionFreq: " + Settings.frequency + "\n");
            {
                final int v = (int) (Settings.volume * 10);
                final float vol = (float) v / 10;
                writer.write("Volume: " + vol + "\n");
            }
            writer.write("Language: " + Settings.languageName + "\n");
            writer.write("DefaultGamma: " + Settings.defaultGamma + "\n");
            writer.write("GameGamma: " + Settings.gameGamma + "\n");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalyzerSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
