/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.systemcommunication;

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
                    if (Settings.modes[index] != null && Settings.modes[index].getWidth() == Settings.resolutionWidth && Settings.modes[index].getHeight() ==
                            Settings.resolutionHeight && Settings.modes[index].getFrequency() == Settings.frequency) {
                        Settings.currentMode = index;
                    }
                }
                break;
            case "ResolutionHeight:":
                final int h = Integer.parseInt(p[1]);
                Settings.resolutionHeight = (h <= 0) ? Settings.modes[0].getHeight() : h;
                for (int i = 0; i < Settings.modes.length; i++) {
                    if (Settings.modes[i] != null && Settings.modes[i].getWidth() == Settings.resolutionWidth && Settings.modes[i].getHeight() == Settings
                            .resolutionHeight && Settings
                            .modes[i].getFrequency() == Settings.frequency) {
                        Settings.currentMode = i;
                    }
                }
                break;
            case "ResolutionFreq:":
                final int f = Integer.parseInt(p[1]);
                Settings.frequency = (f <= 0) ? Settings.modes[0].getFrequency() : f;
                for (int i = 0; i < Settings.modes.length; i++) {
                    if (Settings.modes[i] != null && Settings.modes[i].getWidth() == Settings.resolutionWidth && Settings.modes[i].getHeight() == Settings
                            .resolutionHeight && Settings
                            .modes[i].getFrequency() == Settings.frequency) {
                        Settings.currentMode = i;
                    }
                }
                break;
            case "SoundVolume:":
                final float sv = Float.parseFloat(p[1]);
                if (sv >= -0.01f && sv <= 1.01f) {
                    Settings.soundVolume = sv;
                    Settings.sounds.setSoundVolume(sv);
                }
                break;
            case "MusicVolume:":
                final float mv = Float.parseFloat(p[1]);
                if (mv >= -0.01f && mv <= 1.01f) {
                    Settings.musicVolume = mv;
                    Settings.sounds.setMusicVolume(mv);
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
                if (d >= 0f && d <= 2f) {
                    Settings.defaultGamma = d;
                }
                break;
            case "GameGamma:":
                final float g = Float.parseFloat(p[1]);
                if (g >= 1f && g <= 3f) {
                    Settings.gameGamma = g;
                }
                break;
            case "DefaultBrightness:":
                final float db = Float.parseFloat(p[1]);
                if (db >= -0.25f && db <= 0.25f) {
                    Settings.defaultBrightness = db;
                }
                break;
            case "GameBrightness:":
                final float b = Float.parseFloat(p[1]);
                if (b >= -0.25f && b <= 0.25f) {
                    Settings.gameBrightness = b;
                }
                break;
            case "FramesLimit:":
                int framesLimit = Integer.parseInt(p[1]);
                Settings.framesLimit = (framesLimit < 24 || framesLimit > 120) ? 60 : framesLimit;
                Settings.currentFramesCap = Settings.framesLimit;
                break;
            case "AutoFrames:":
                if (0 == p[1].compareTo("On")) {
                    Settings.autoFrames = true;
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
            if (Settings.autoFrames) {
                writer.write("AutoFrames: On\n");
            } else {
                writer.write("AutoFrames: Off\n");
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
                int v = (int) (Settings.soundVolume * 10);
                float vol = (float) v / 10;
                writer.write("SoundVolume: " + vol + "\n");
                v = (int) (Settings.musicVolume * 10);
                vol = (float) v / 10;
                writer.write("MusicVolume: " + vol + "\n");
            }
            writer.write("Language: " + Settings.languageName + "\n");
            writer.write("DefaultGamma: " + Settings.defaultGamma + "\n");
            writer.write("GameGamma: " + Settings.gameGamma + "\n");
            writer.write("DefaultBrightness: " + Settings.defaultBrightness + "\n");
            writer.write("GameBrightness: " + Settings.gameBrightness + "\n");
            writer.write("FramesLimit: " + Settings.framesLimit + "\n");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalyzerSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
