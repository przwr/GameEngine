/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openGLEngine;

import game.Analizer;
import game.Game;
import game.IO;
import game.Settings;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.PixelFormat;

/**
 *
 * @author przemek
 */
public class Main {

    private static Game game;
    private static DisplayDevice display;
    private static Settings settings;
//    public static int fragmentShader;
//    public static int shaderProgram;

    public static void main(String[] args) {
        display = new DisplayDevice();
        settings = new Settings();
        IO.ReadFile(new File("res/settings.ini"), settings);
        initDisplay();
        initGL();
        initGame();
        gameLoop();
        cleanUp();
    }

    private static void initGame() {
        game = new Game("Engine", settings);
        Display.setTitle(game.getTitle());
    }

    private static void getInput() {
        game.getInput();
        if (game.fullScreen) {
            desktopFullScreen();
        }
    }

    private static void desktopFullScreen() {
        if (settings.fullscreen) {
//            try {
//                Display.setFullscreen(false);
//            } catch (LWJGLException ex) {
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            }
            settings.fullscreen = false;
            Analizer.Save(settings);
            game.fullScreen = false;
        } else {
//            setDisplayMode(Display.getWidth(), Display.getHeight(), true);
            settings.fullscreen = true;
            Analizer.Save(settings);
            game.fullScreen = false;
        }
        try {
            Display.setDisplayConfiguration(2f, 0f, 1.0f);
        } catch (LWJGLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void update() {
        game.update();
    }

    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        glLoadIdentity();
        game.render();
        Display.sync(60);
        Display.update();
    }

    private static void initGL() {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glClearColor(0, 0, 0, 0);
    }

    private static void gameLoop() {
        Time.init();
        while (!Display.isCloseRequested() && !game.exitFlag) {
            Time.update();
            getInput();
            update();
            render();
        }
    }

    private static void cleanUp() {
        try {
            Display.setDisplayConfiguration(1f, 0f, 1.0f);

        } catch (LWJGLException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        AL.destroy();
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
        Controllers.destroy();
    }

    private static void initDisplay() {
        try {
            setDisplayMode(display.getWidth(), display.getHeight(), settings.fullscreen);
            //Display.setDisplayMode(new DisplayMode(display.getWidth(), display.getHeight()));
            Display.create(new PixelFormat(0, 16, 1));
            Display.setResizable(false);
            Keyboard.create();
            Mouse.create();
            Controllers.create();
            Display.setVSyncEnabled(true);

        } catch (LWJGLException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Display.setDisplayConfiguration(2f, 0f, 1.0f);
        } catch (LWJGLException ex) {
            Logger.getLogger(Main.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void setDisplayMode(int width, int height, boolean fullscreen) {
        // return if requested DisplayMode is already set
        if ((Display.getDisplayMode().getWidth() == width)
                && (Display.getDisplayMode().getHeight() == height)
                && (Display.isFullscreen() == fullscreen)) {
            return;
        }
        try {
            DisplayMode targetDisplayMode = null;
            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;
                for (DisplayMode current : modes) {
                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }
                        // if we've found a match for bpp and frequence against the 
                        // original display mode then it's probably best to go for this one
                        // since it's most likely compatible with the monitor
                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel())
                                && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width, height);
            }
            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
                return;
            }
            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
        }
    }

}
