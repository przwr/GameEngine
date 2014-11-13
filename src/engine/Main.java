/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.AnalizerSettings;
import game.Game;
import game.IO;
import myGame.MyGame;
import game.Settings;
import gameDesigner.GameDesigner;
import java.io.File;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.*;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import org.lwjgl.opengl.PixelFormat;

/**
 *
 * @author przemek
 */
public class Main {

    private static Game game;
    private static final Settings settings = new Settings();
    private static Controller[] controllers;
    private static GameDesigner designer;

    public static void main(String[] args) {
        try {
            IO.ReadFile(new File("res/settings.ini"), settings, true);
            initDisplay();
            initGL();
            initGame();
            gameLoop();
            cleanUp();
        } catch (Exception ex) {
            Methods.Exception(ex);
            cleanUp();
        }
        System.exit(0); //Zabija wszystkie wątki
    }

    private static void initGame() {
        game = new MyGame("Pervert Rabbits Attack", settings, controllers);
        Display.setTitle(game.getTitle());
        designer = new GameDesigner();
    }

    private static void getInput() {
        game.getInput();
        
        //-----PROJEKTOWANIE GRY! (>^o')>= ==== ----//
        if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
            designer.setVisible(true);
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
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_BLEND);
        glEnable(GL_SCISSOR_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glMatrixMode(GL_PROJECTION);
        glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glClearColor(0, 0, 0, 0);
    }

    private static void gameLoop() {
        Time.init();
        while (!Display.isCloseRequested() && !game.exitFlag) {
            Time.update();
            Display.setTitle(game.getTitle() + " [" + (int) (60 / Time.getDelta()) + " fps]");
            getInput();
            update();
            render();
        }
    }

    private static void cleanUp() {
        try {
            Display.setDisplayConfiguration(1f, 0f, 1.0f);
        } catch (LWJGLException ex) {
            Methods.Exception(ex);
        }
        AL.destroy();
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
        Controllers.destroy();
        //designer.dispose();
    }

    private static void initDisplay() {
        try {
            setDisplayMode(settings.resWidth, settings.resHeight, settings.freq, settings.fullScreen);
            Display.create(new PixelFormat(32, 0, 24, 0, 0));
            Display.setResizable(false);
            if (settings.vSync) {
                Display.setVSyncEnabled(true);
            } else {
                Display.setVSyncEnabled(false);
            }
            Display.setDisplayConfiguration(2f, 0f, 1.0f);
            System.err.println((Display.getParent()));
            Keyboard.create();
            Mouse.create();
            Controllers.create();
            controllers = Controlers.init();
        } catch (LWJGLException ex) {
            Methods.Exception(ex);
        }
    }

    private static void setDisplayMode(int width, int height, int freq, boolean fullscreen) {
        // return if requested DisplayMode is already set
        if ((Display.getDisplayMode().getWidth() == width)
                && (Display.getDisplayMode().getHeight() == height)
                && (Display.isFullscreen() == fullscreen)) {
            return;
        }
        try {
            DisplayMode targetDisplayMode = null;
            if (fullscreen) {
                for (DisplayMode current : settings.tmpmodes) {
                    if ((current.getWidth() == width) && (current.getHeight() == height) && (current.getFrequency() == freq)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
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
                settings.resWidth = Display.getDesktopDisplayMode().getWidth();
                settings.resHeight = Display.getDesktopDisplayMode().getHeight();
                for (int i = 0; i < settings.tmpmodes.length; i++) {
                    if (settings.tmpmodes[i].getWidth() == settings.resWidth && settings.tmpmodes[i].getHeight() == settings.resHeight && settings.tmpmodes[i].getFrequency() == settings.freq) {
                        settings.curMode = i;
                    }
                }
                AnalizerSettings.Update(settings);
                return;
            }
            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
        }
    }
}
