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
import Engine.DisplayDevice;
import Engine.SoundBase;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Display;

/**
 *
 * @author przemek
 */
public class Settings {

    public DisplayMode[] modes;
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
    public String lang = "PL";
    public ArrayList<Language> languages = new ArrayList<>();
    public Language language;           // ustawiony w konstruktorze na domyślny
    public int actionsNr;
    public Player[] players;
    public Controller[] controllers;

    public Settings() {
        try {
            modes = Display.getAvailableDisplayModes();
        } catch (LWJGLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        DisplayMode temp;
        for (int k = 0; k < modes.length - 1; k++) {
            for (int i = 0; i < modes.length - 1; i++) {
                if (modes[i].getWidth() > modes[i + 1].getWidth()) {
                    temp = modes[i];
                    modes[i] = modes[i + 1];
                    modes[i + 1] = temp;
                } else if (modes[i].getWidth() == modes[i + 1].getWidth()) {
                    if (modes[i].getHeight() > modes[i + 1].getHeight()) {
                        temp = modes[i];
                        modes[i] = modes[i + 1];
                        modes[i + 1] = temp;
                    } else if (modes[i].getFrequency() > modes[i + 1].getFrequency()) {
                        temp = modes[i];
                        modes[i] = modes[i + 1];
                        modes[i + 1] = temp;
                    }
                }
            }
        }
        languages.add(new LangPL());
        languages.add(new LangENG());
        language = languages.get(0);
    }

    public void Up(int nr, Player[]players, Controller[] controllers) {
        actionsNr = nr;
        this.players = players;
        this.controllers = controllers;
    }
}
