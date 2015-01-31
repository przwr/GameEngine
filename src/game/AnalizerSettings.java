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
public final class AnalizerSettings {

    public static void analizeSetting(String name, Settings settings) {
        final String[] p = name.split("\\s+");
        switch (p[0]) {
            case "FullScreen:":
                if (0 == p[1].compareTo("On")) {
                    settings.fullScreen = true;
                }
                break;
            case "SplitMode:":
                if (0 == p[1].compareTo("H")) {
                    settings.hSplitScreen = true;
                }
                break;
            case "VSync:":
                if (0 == p[1].compareTo("On")) {
                    settings.vSync = true;
                }
                break;
            case "NumberOfSamples:":
                final int ns = Integer.parseInt(p[1]);
                if (ns > 64 || ns < 0) {
                    settings.nrSamples = 0;
                } else {
                    settings.nrSamples = ns;
                }
                break;
            case "ShadowOff:":
                if (0 == p[1].compareTo("On")) {
                    settings.shadowOff = true;
                }
                break;
            case "NumberOfPlayers:":
                final int np = Integer.parseInt(p[1]);
                if (np > 4 || np < 1) {
                    settings.nrPlayers = 1;
                } else {
                    settings.nrPlayers = np;
                }
                break;
            case "ServerIP:":
                settings.serverIP = p[1];
                break;
            case "ResolutionWidth:":
                final int w = Integer.parseInt(p[1]);
                if (w <= 0) {
                    settings.resWidth = settings.modes[0].getWidth();
                } else {
                    settings.resWidth = w;
                }
                for (int i = 0; i < settings.modes.length; i++) {
                    if (settings.modes[i].getWidth() == settings.resWidth && settings.modes[i].getHeight() == settings.resHeight && settings.modes[i].getFrequency() == settings.freq) {
                        settings.curentMode = i;
                    }
                }
                break;
            case "ResolutionHeight:":
                final int h = Integer.parseInt(p[1]);
                if (h <= 0) {
                    settings.resHeight = settings.modes[0].getHeight();
                } else {
                    settings.resHeight = h;
                }
                for (int i = 0; i < settings.modes.length; i++) {
                    if (settings.modes[i].getWidth() == settings.resWidth && settings.modes[i].getHeight() == settings.resHeight && settings.modes[i].getFrequency() == settings.freq) {
                        settings.curentMode = i;
                    }
                }
                break;
            case "ResolutionFreq:":
                final int f = Integer.parseInt(p[1]);
                if (f <= 0) {
                    settings.freq = settings.modes[0].getFrequency();
                } else {
                    settings.freq = f;
                }
                for (int i = 0; i < settings.modes.length; i++) {
                    if (settings.modes[i].getWidth() == settings.resWidth && settings.modes[i].getHeight() == settings.resHeight && settings.modes[i].getFrequency() == settings.freq) {
                        settings.curentMode = i;
                    }
                }
                break;
            case "Volume:":
                final float v = Float.parseFloat(p[1]);
                if (v >= -0.01f && v <= 1.01f) {
                    settings.volume = v;
                }
                break;
            case "Language:":
                if (0 == p[1].compareTo("PL")) {
                    settings.lang = "PL";
                    settings.language = settings.languages.get(0);
                } else if (0 == p[1].compareTo("ENG")) {
                    settings.lang = "ENG";
                    settings.language = settings.languages.get(1);
                }
                break;
            default:
        }
    }

    public static void update(Settings settings) {
        FileWriter writer;
        try {
            writer = new FileWriter("res/settings.ini");
            if (settings.fullScreen) {
                writer.write("FullScreen: On\n");
            } else {
                writer.write("FullScreen: Off\n");
            }
            if (settings.hSplitScreen) {
                writer.write("SplitMode: H\n");
            } else {
                writer.write("SplitMode: V\n");
            }
            if (settings.vSync) {
                writer.write("VSync: On\n");
            } else {
                writer.write("VSync: Off\n");
            }
            if (settings.shadowOff) {
                writer.write("ShadowOff: On\n");
            } else {
                writer.write("ShadowOff: Off\n");
            }
            writer.write("ServerIP: " + settings.serverIP + "\n");
            writer.write("NumberOfPlayers: " + settings.nrPlayers + "\n");
            writer.write("NumberOfSamples: " + settings.nrSamples + "\n");
            writer.write("ResolutionWidth: " + settings.resWidth + "\n");
            writer.write("ResolutionHeight: " + settings.resHeight + "\n");
            writer.write("ResolutionFreq: " + settings.freq + "\n");
            {
                final int v = (int) (settings.volume * 10);
                final float vol = (float) v / 10;
                writer.write("Volume: " + vol + "\n");
            }
            writer.write("Language: " + settings.lang);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalizerSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private AnalizerSettings() {
    }
}
