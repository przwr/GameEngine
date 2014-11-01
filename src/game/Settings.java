/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.myGame.MyPlayer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import engine.SoundBase;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class Settings {

    public DisplayMode[] tmpmodes;
    public DisplayMode[] modes;
    public int modesNr;
    public DisplayMode display = Display.getDesktopDisplayMode();
    public int curMode;
    public boolean fullScreen;
    public boolean hSplitScreen;
    public boolean joinSS;
    public int nrPlayers = 1;
    public float volume = 0.5f;
    public SoundBase sounds;
    public int resWidth = display.getWidth();
    public int resHeight = display.getHeight();
    public double SCALE;
    public int freq = display.getFrequency();
    public int depth = display.getBitsPerPixel();
    public boolean vSync;
    public boolean smoothShadows;
    public String lang = "PL";
    public ArrayList<Language> languages = new ArrayList<>();
    public Language language;           // ustawiony w konstruktorze na domyślny
    public int actionsNr;
    public MyPlayer[] players;
    public Controller[] controllers;
    public int worldSeed;

    public Settings() {
        int minW = 1024;
        int minH = 768;
        int maxW = 1920;
        int maxH = 1200;
        try {
            tmpmodes = Display.getAvailableDisplayModes();
        } catch (LWJGLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        DisplayMode temp;
        if (tmpmodes[0].getWidth() >= minW && tmpmodes[0].getWidth() <= maxW && tmpmodes[0].getHeight() >= minH && tmpmodes[0].getHeight() <= maxH && tmpmodes[0].getBitsPerPixel() == depth) {
            modesNr++;
        }
        for (int i = 1; i < tmpmodes.length; i++) {
            if (tmpmodes[i].getWidth() >= minW && tmpmodes[i].getWidth() <= maxW && tmpmodes[i].getHeight() >= minH && tmpmodes[i].getHeight() <= maxH && tmpmodes[i].getBitsPerPixel() == depth) {
                modesNr++;
            }
            temp = tmpmodes[i];
            int j;
            for (j = i; j > 0 && isBiger(tmpmodes[j - 1], temp); j--) {
                tmpmodes[j] = tmpmodes[j - 1];
            }
            tmpmodes[j] = temp;
        }
        modes = new DisplayMode[modesNr];
        int i = 0;
        for (DisplayMode mode : tmpmodes) {
            if (mode.getWidth() >= minW && mode.getWidth() <= maxW && mode.getHeight() >= minH && mode.getHeight() <= maxH && mode.getBitsPerPixel() == depth) {
                modes[i++] = mode;
            }
        }
        languages.add(new LangPL());
        languages.add(new LangENG());
        language = languages.get(0);
    }

    private boolean isBiger(DisplayMode checked, DisplayMode temp) {
        if (checked.getBitsPerPixel() > temp.getBitsPerPixel()) {
            return true;
        } else if (checked.getWidth() > temp.getWidth()) {
            return true;
        } else if (checked.getWidth() == temp.getWidth() && checked.getHeight() > temp.getHeight()) {
            return true;
        } else {
            return checked.getWidth() == temp.getWidth() && checked.getHeight() == temp.getHeight() && checked.getFrequency() > temp.getFrequency();
        }
    }

    public void Up(int nr, MyPlayer[] players, Controller[] controllers) {
        actionsNr = nr;
        this.players = players;
        this.controllers = controllers;
        this.SCALE = ((int) (((double) resHeight / 1024d / 0.03125)) * 0.03125) >= 1 ? 1 : (int) (((double) resHeight / 1024d / 0.03125)) * 0.03125;
    }
}
