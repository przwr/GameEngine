/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.AnalizerSettings;
import game.Game;
import game.IO;
import game.Settings;
import gamedesigner.GameDesigner;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import gamecontent.MyGame;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.opengl.ImageIOImageData;

/**
 *
 * @author przemek
 */
public class Main {

    public static boolean DEBUG = false;
    public static Game game;
    public static Popup pop;
    public static final Settings settings = new Settings();
    public static Controller[] controllers;
    public static GameDesigner designer = null;
    public static boolean gameStop = false;
    public static boolean pause, ENTER = true;
    private static boolean lastFrame;

    public static void run() {
        IO.readFile(new File("res/settings.ini"), settings, true);
        initDisplay();
        initGL();
        initGame();
        gameLoop();
        cleanUp();
    }

    private static void initGame() {
        game = new MyGame("Pervert Rabbits Attack", settings, controllers);
        Display.setTitle(game.getTitle());
        pop = new Popup("Amble-Regular", settings.SCALE);
    }

    private static void getInput() {
        game.getInput();

        //-----PROJEKTOWANIE GRY! (>^o')>= ==== ----//
        if (Keyboard.isKeyDown(Keyboard.KEY_F1) && !gameStop) {
            if (designer == null) {
                designer = new GameDesigner();
            }
            designer.setVisible(true);
            gameStop = true;
        }
        //------------------------------------------//
    }

    private static void update() {
        game.update();
    }

    private static void render() {
        game.render();
        if (pop.i != -1) {
            pause = true;
            pop.renderMesagges();
        }
        Display.sync(60);
        Display.update();
        if (Display.isActive()) {
            if (!lastFrame) {
                try {
                    Display.setDisplayConfiguration(1f, 0f, 1f);
                    Display.setDisplayConfiguration(2f, 0f, 1f);
                } catch (LWJGLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (lastFrame) {
            try {
                Display.setDisplayConfiguration(2f, 0f, 1f);
                Display.setDisplayConfiguration(1f, 0f, 1f);
            } catch (LWJGLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        lastFrame = Display.isActive();
    }

    public static void addMessage(String msg) {
        try {
            pop.addMessage(msg);
        } catch (Exception e) {
        }
    }

    public static String getTitle() {
        return game.getTitle();
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
            if (!gameStop) {
                Display.setTitle(game.getTitle() + " [" + (int) (60 / Time.getDelta()) + " fps]");
                if (!pause) {
                    getInput();
                    update();
                } else {
                    if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) || Mouse.isButtonDown(0)) {
                        game.getMenu().delay.restart();
                        if (!ENTER) {
                            pop.popMessage();
                        }
                    } else {
                        ENTER = false;
                    }
                }
                render();
            } else {
                Display.update();
            }
        }
    }

    public static void cleanUp() {
        game.endGame();
        AL.destroy();
        Keyboard.destroy();
        Mouse.destroy();
        Controllers.destroy();
//        try {
//            Display.update();
//            Display.setDisplayConfiguration(2f, 0f, 1f);
//            Display.setDisplayConfiguration(1f, 0f, 1f);
//        } catch (LWJGLException ex) {
//        }
        Display.destroy();
        System.exit(0);
    }

    private static void initDisplay() {
        try {
            setDisplayMode(settings.resWidth, settings.resHeight, settings.freq, settings.fullScreen);
            try {
                Display.create(new PixelFormat(32, 0, 24, 0, settings.nrSamples));
            } catch (Exception e0) {
                Display.destroy();
                try {
                    Display.create(new PixelFormat(32, 0, 24, 0, settings.nrSamples / 2));
                } catch (Exception e1) {
                    Display.destroy();
                    try {
                        Display.create(new PixelFormat(32, 0, 24, 0, settings.nrSamples / 4));
                    } catch (Exception e2) {
                        Display.destroy();
                        Display.create(new PixelFormat(32, 0, 24, 0, 0));
                    }
                }
            }
            Display.setResizable(false);
            Display.setVSyncEnabled(settings.vSync);
//            Display.update();
//            Display.setDisplayConfiguration(1f, 0f, 1f);
            Display.setDisplayConfiguration(2f, 0f, 1f);
            try {
                Display.setIcon(new ByteBuffer[]{
                    new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon32.png")), false, false, null),
                    new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon16.png")), false, false, null)
                });
            } catch (IOException e) {
            }
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
                Methods.Error("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
                settings.resWidth = Display.getDesktopDisplayMode().getWidth();
                settings.resHeight = Display.getDesktopDisplayMode().getHeight();
                for (int i = 0; i < settings.tmpmodes.length; i++) {
                    if (settings.tmpmodes[i].getWidth() == settings.resWidth && settings.tmpmodes[i].getHeight() == settings.resHeight && settings.tmpmodes[i].getFrequency() == settings.freq) {
                        settings.curMode = i;
                    }
                }
                AnalizerSettings.update(settings);
                return;
            }
            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);
        } catch (LWJGLException e) {
            Methods.Error("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
        }
    }
}
