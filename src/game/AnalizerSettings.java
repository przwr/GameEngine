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
public class AnalizerSettings {

    public static void AnalizeSetting(String name, Settings settings) {
        String[] p = name.split("\\s+");
        switch (p[0]) {
            case "FullScreen:":
                if (0 == p[1].compareTo("On")) {
                    settings.fullScreen = true;
                } else if (0 == p[1].compareTo("Off")) {
                    settings.fullScreen = false;
                }
                break;
            case "SplitMode:":
                if (0 == p[1].compareTo("H")) {
                    settings.hSplitScreen = true;
                } else if (0 == p[1].compareTo("V")) {
                    settings.hSplitScreen = false;
                }
                break;
            case "VSync:":
                if (0 == p[1].compareTo("On")) {
                    settings.vSync = true;
                } else if (0 == p[1].compareTo("Off")) {
                    settings.vSync = false;
                }
                break;
            case "NumberOfSamples:":
                int ns = Integer.parseInt(p[1]);
                if (ns > 64 || ns < 0) {
                    settings.nrSamples = 0;
                } else {
                    settings.nrSamples = ns;
                }
                break;
            case "NumberOfPlayers:":
                int np = Integer.parseInt(p[1]);
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
                int w = Integer.parseInt(p[1]);
                if (w <= 0) {
                    settings.resWidth = settings.modes[0].getWidth();
                } else {
                    settings.resWidth = w;
                }
                for (int i = 0; i < settings.modes.length; i++) {
                    if (settings.modes[i].getWidth() == settings.resWidth && settings.modes[i].getHeight() == settings.resHeight && settings.modes[i].getFrequency() == settings.freq) {
                        settings.curMode = i;
                    }
                }
                break;
            case "ResolutionHeight:":
                int h = Integer.parseInt(p[1]);
                if (h <= 0) {
                    settings.resHeight = settings.modes[0].getHeight();
                } else {
                    settings.resHeight = h;
                }
                for (int i = 0; i < settings.modes.length; i++) {
                    if (settings.modes[i].getWidth() == settings.resWidth && settings.modes[i].getHeight() == settings.resHeight && settings.modes[i].getFrequency() == settings.freq) {
                        settings.curMode = i;
                    }
                }
                break;
            case "ResolutionFreq:":
                int f = Integer.parseInt(p[1]);
                if (f <= 0) {
                    settings.freq = settings.modes[0].getFrequency();
                } else {
                    settings.freq = f;
                }
                for (int i = 0; i < settings.modes.length; i++) {
                    if (settings.modes[i].getWidth() == settings.resWidth && settings.modes[i].getHeight() == settings.resHeight && settings.modes[i].getFrequency() == settings.freq) {
                        settings.curMode = i;
                    }
                }
                break;
            case "Volume:":
                float v = Float.parseFloat(p[1]);
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
        }
    }

    public static void Update(Settings settings) {
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
            if (settings.vSync) {
                fw.write("VSync: On\n");
            } else {
                fw.write("VSync: Off\n");
            }
            fw.write("ServerIP: " + settings.serverIP + "\n");
            fw.write("NumberOfPlayers: " + settings.nrPlayers + "\n");            
            fw.write("NumberOfSamples: " + settings.nrSamples + "\n");
            fw.write("ResolutionWidth: " + settings.resWidth + "\n");
            fw.write("ResolutionHeight: " + settings.resHeight + "\n");
            fw.write("ResolutionFreq: " + settings.freq + "\n");
            {
                int v = (int) (settings.volume * 10);
                float vol = (float) v / 10;
                fw.write("Volume: " + vol + "\n");
            }
            fw.write("Language: " + settings.lang);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(AnalizerSettings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private AnalizerSettings() {
    }
}
