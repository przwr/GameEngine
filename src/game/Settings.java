/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.gameobject.Player;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import engine.DisplayDevice;
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
    public DisplayDevice display = new DisplayDevice();
    public int curMode;
    public boolean fullScreen = true;
    public boolean hSplitScreen;
    public int nrPlayers = 1;
    public float volume = 0.5f;
    public SoundBase sounds;
    public int resWidth = display.getWidth();
    public int resHeight = display.getHeight();
    public int freq = display.getFreq();
    public boolean aa = true;
    public boolean vSync = true;
    public String lang = "PL";
    public ArrayList<Language> languages = new ArrayList<>();
    public Language language;           // ustawiony w konstruktorze na domyślny
    public int actionsNr;
    public Player[] players;
    public Controller[] controllers;

    public Settings() {
        int minW = 1024;
        int maxW = 1920;

        try {
            tmpmodes = Display.getAvailableDisplayModes();
        } catch (LWJGLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        DisplayMode temp;
        if (tmpmodes[0].getWidth() >= minW && tmpmodes[0].getHeight() <= maxW && tmpmodes[0].getBitsPerPixel() == 24) {
            modesNr++;
        }
        for (int i = 1; i < tmpmodes.length; i++) {
            if (tmpmodes[i].getWidth() >= minW && tmpmodes[i].getHeight() <= maxW && tmpmodes[i].getBitsPerPixel() == 24) {
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
            if (mode.getWidth() >= minW && mode.getHeight() <= maxW && mode.getBitsPerPixel() == 24) {
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
        } else return checked.getWidth() == temp.getWidth() && checked.getHeight() == temp.getHeight() && checked.getFrequency() > temp.getFrequency();
    }

    public void Up(int nr, Player[] players, Controller[] controllers) {
        actionsNr = nr;
        this.players = players;
        this.controllers = controllers;
    }
}