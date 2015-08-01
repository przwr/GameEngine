/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import engine.ErrorHandler;
import engine.SoundBase;
import game.gameobject.Player;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static game.place.fbo.FrameBufferObject.*;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.glGetInteger;

/**
 * @author przemek
 */
public class Settings {

    public static final ArrayList<Language> languages = new ArrayList<>();
    private static final int MIN_WIDTH = 1024;
    private static final int MIN_HEIGHT = 768;
    private static final int MAX_WIDTH = 1920;
    private static final int MAX_HEIGHT = 1200;
    private static final DisplayMode display = Display.getDesktopDisplayMode();
    private static final int depth = display.getBitsPerPixel();
    public static DisplayMode[] modesTemp;
    public static DisplayMode[] modes;
    public static int currentMode;
    public static boolean fullScreen;
    public static boolean horizontalSplitScreen;
    public static boolean joinSplitScreen;
    public static int playersCount = 1;
    public static float volume = 0.5f;
    public static SoundBase sounds;
    public static int resolutionWidth;
    public static int resolutionHeight;
    public static int frequency;
    public static boolean verticalSynchronization;
    public static int samplesCount = 0;
    public static String languageName;
    public static Language language;
    public static int actionsCount;
    public static Player[] players;
    public static Controller[] controllers;
    public static int maxSamples;
    public static int supportedFrameBufferObjectVersion;
    public static boolean multiSampleSupported;
    public static boolean shadowOff;
    public static boolean scaled;
    public static double nativeScale;
    public static String serverIP = "127.0.0.1";
    private static int modesCount;

    public static void initialize() {
        try {
            modesTemp = Display.getAvailableDisplayModes();
        } catch (LWJGLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
        DisplayMode temp;
        if (modesTemp[0].getWidth() >= MIN_WIDTH && modesTemp[0].getWidth() <= MAX_WIDTH
                && modesTemp[0].getHeight() >= MIN_HEIGHT && modesTemp[0].getHeight() <= MAX_HEIGHT && modesTemp[0].getBitsPerPixel() == depth) {
            modesCount++;
        }
        int i, j;
        for (i = 1; i < modesTemp.length; i++) {
            if (modesTemp[i].getWidth() >= MIN_WIDTH && modesTemp[i].getWidth() <= MAX_WIDTH && modesTemp[i].getHeight() >= MIN_HEIGHT && modesTemp[i].getHeight() <= MAX_HEIGHT && modesTemp[i].getBitsPerPixel() == depth) {
                modesCount++;
            }
            temp = modesTemp[i];
            for (j = i; j > 0 && isBigger(modesTemp[j - 1], temp); j--) {
                modesTemp[j] = modesTemp[j - 1];
            }
            modesTemp[j] = temp;
        }
        modes = new DisplayMode[modesCount];
        i = 0;
        for (DisplayMode mode : modesTemp) {
            if (mode.getWidth() >= MIN_WIDTH && mode.getWidth() <= MAX_WIDTH && mode.getHeight() >= MIN_HEIGHT && mode.getHeight() <= MAX_HEIGHT && mode.getBitsPerPixel() == depth) {
                modes[i++] = mode;
            }
        }
        resolutionWidth = modes[0].getWidth();
        resolutionHeight = modes[0].getHeight();
        frequency = modes[0].getFrequency();
        languages.add(new LangPL());
        languages.add(new LangENG());
        language = languages.get(0);
        languageName = language.lang;
    }

    private static boolean isBigger(DisplayMode checked, DisplayMode temp) {
        return checked.getBitsPerPixel() > temp.getBitsPerPixel() || checked.getWidth() > temp.getWidth() || checked.getWidth() == temp.getWidth() && checked.getHeight() > temp.getHeight() || checked.getWidth() == temp.getWidth() && checked.getHeight() == temp.getHeight() && checked.getFrequency() > temp.getFrequency();
    }

    public static void calculateScale() {
        nativeScale = ((int) ((resolutionHeight / 1024d / 0.25)) * 0.25) >= 1 ? 1 : (int) ((resolutionHeight / 1024d / 0.25)) * 0.25;
        scaled = true;
    }

    public static void update(int actionsCount, Player[] players, Controller[] controllers) {
        Settings.actionsCount = actionsCount;
        Settings.players = players;
        Settings.controllers = controllers;
        try {
            GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, samplesCount, GL_RGBA8, 10, 10, false);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
            supportedFrameBufferObjectVersion = NATIVE;
            multiSampleSupported = true;
            maxSamples = glGetInteger(GL30.GL_MAX_SAMPLES) / 2;
            maxSamples = maxSamples > 8 ? 8 : maxSamples;
            samplesCount = (samplesCount > maxSamples) ? maxSamples : samplesCount;
        } catch (Exception exception) {
            if (GLContext.getCapabilities().GL_ARB_framebuffer_object) {
                supportedFrameBufferObjectVersion = ARB;
                try {
                    ARBTextureMultisample.glTexImage2DMultisample(ARBTextureMultisample.GL_TEXTURE_2D_MULTISAMPLE, samplesCount, GL_RGBA8, 10, 10, false);
                    ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
                    multiSampleSupported = true;
                    maxSamples = glGetInteger(GL30.GL_MAX_SAMPLES) / 2;
                    maxSamples = maxSamples > 8 ? 8 : maxSamples;
                    samplesCount = (samplesCount > maxSamples) ? maxSamples : samplesCount;
                } catch (Exception exception2) {
                    multiSampleSupported = false;
                }
            } else if (GLContext.getCapabilities().GL_EXT_framebuffer_object) {
                supportedFrameBufferObjectVersion = EXT;
                multiSampleSupported = false;
            } else {
                ErrorHandler.javaError(language.menu.FBOError);
            }
        }
    }
}
