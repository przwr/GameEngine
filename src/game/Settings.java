/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import openGLEngine.DisplayDevice;
import openGLEngine.SoundBase;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class Settings {

    public DisplayMode[] modes;
    public DisplayDevice display = new DisplayDevice();
    public int modesLength;
    public int curMode;
    public boolean fullScreen = true;
    public boolean hSplitScreen;
    public int nrPlayers = 1;
    public float volume = 0.5f;
    public SoundBase sounds;
    public int resWidth = display.getWidth();
    public int resHeight = display.getHeight();
    public String lang = "PL";
    public ArrayList<Language> languages = new ArrayList<>();
    public Language language;               // ustawiony w konstruktorze na domyślny

    public Settings() {
        try {
            modes = Display.getAvailableDisplayModes();
        } catch (LWJGLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        DisplayMode temp;
        modesLength = modes.length;
        for (int k = 0; k < modesLength - 1; k++) {
            for (int i = 0; i < modesLength - 1; i++) {
                if (modes[i].getWidth() > modes[i + 1].getWidth()) {
                    temp = modes[i];
                    modes[i] = modes[i + 1];
                    modes[i + 1] = temp;
                } else if (modes[i].getWidth() == modes[i + 1].getWidth()) {
                    if (modes[i].getHeight() > modes[i + 1].getHeight()) {
                        temp = modes[i];
                        modes[i] = modes[i + 1];
                        modes[i + 1] = temp;
                    }
                }
            }
        }
        for (int i = 0; i < modesLength - 1; i++) {
            if (modes[i].getWidth() == modes[i + 1].getWidth() && modes[i].getHeight() == modes[i + 1].getHeight()) {
                for (int k = i; k < modesLength - 1; k++) {
                    modes[k] = modes[k + 1];
                }
                modesLength--;
            }
        }
        if (modes[modesLength - 2].getWidth() == modes[modesLength - 1].getWidth() && modes[modesLength - 2].getHeight() == modes[modesLength - 1].getHeight()) {
            modesLength--;
        }
        languages.add(new LangPL());
        languages.add(new LangENG());
        language = languages.get(0);
    }
}
