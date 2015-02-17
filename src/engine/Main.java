/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.AnalizerSettings;
import game.Game;
import static game.IO.setSettingsFromFile;
import game.Settings;
import static game.Settings.calculateScale;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import gamecontent.MyGame;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Cursor;
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
    public static Controller[] controllers;
    public static boolean pause, enter = true;
    private static boolean lastFrame;

    public static void run() {
        setSettingsFromFile(new File("res/settings.ini"));
        initializeDisplay();
        initializeOpenGL();
        calculateScale();
        initializeGame();
        Time.initialize();
        refreshGamma();
        gameLoop();
        cleanUp();
    }

    private static void initializeDisplay() {
        try {
            tryInitializeDisplay();
        } catch (LWJGLException exception) {
            Methods.javaError(exception.toString());
        }
    }

    private static void tryInitializeDisplay() throws LWJGLException {
        setIcon();
        setDisplayMode(Settings.resolutionWidth, Settings.resolutionHeight, Settings.frequency, Settings.fullScreen);
        createDisplay();
        Display.setResizable(false);
        Display.setVSyncEnabled(Settings.verticalSynchronization);
        Display.setDisplayConfiguration(2f, 0f, 1f);
        Keyboard.create();
        Mouse.create();
        Cursor emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
        Mouse.setNativeCursor(emptyCursor);
        Controllers.create();
        controllers = Controlers.initialize();
    }

    private static void setDisplayMode(int width, int height, int frequency, boolean fullscreen) {
        if (((Display.getDisplayMode().getWidth() != width) || (Display.getDisplayMode().getHeight() != height)) || (Display.isFullscreen() != fullscreen)) {
            try {
                setNewMode(width, height, frequency, fullscreen);
            } catch (LWJGLException exception) {
                Methods.javaError("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + " " + exception.getMessage());
            }
        }
    }

    private static void setNewMode(int width, int height, int frequency, boolean fullscreen) throws LWJGLException {
        DisplayMode targetDisplayMode;
        if (fullscreen) {
            targetDisplayMode = setFullScreen(width, height, frequency);
        } else {
            targetDisplayMode = new DisplayMode(width, height);
        }
        if (targetDisplayMode == null) {
            updateSettingsToDesktopMode();
            Methods.error("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
            return;
        }
        Display.setDisplayMode(targetDisplayMode);
        Display.setFullscreen(fullscreen);
    }

    private static DisplayMode setFullScreen(int width, int height, int frequency) {
        DisplayMode targetDisplayMode = null;
        for (DisplayMode current : Settings.modesTemp) {
            if ((current.getWidth() == width) && (current.getHeight() == height) && (current.getFrequency() == frequency)) {
                if (((targetDisplayMode == null) || (current.getFrequency() >= frequency))
                        && ((targetDisplayMode == null) || (current.getBitsPerPixel() >= targetDisplayMode.getBitsPerPixel()))) {
                    targetDisplayMode = current;
                }
                if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel())
                        && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                    targetDisplayMode = current;
                }
            }
        }
        return targetDisplayMode;
    }

    private static void updateSettingsToDesktopMode() {
        Settings.resolutionWidth = Display.getDesktopDisplayMode().getWidth();
        Settings.resolutionHeight = Display.getDesktopDisplayMode().getHeight();
        for (int i = 0; i < Settings.modesTemp.length; i++) {
            if (Settings.modesTemp[i].getWidth() == Settings.resolutionWidth && Settings.modesTemp[i].getHeight() == Settings.resolutionHeight
                    && Settings.modesTemp[i].getFrequency() == Settings.frequency) {
                Settings.currentMode = i;
            }
        }
        AnalizerSettings.update();
    }

    private static void createDisplay() {
        try {
            Display.create(new PixelFormat(32, 0, 24, 0, 0));
        } catch (Exception exception) {
            Display.destroy();
            Methods.javaError(exception.getMessage());
        }
    }

    private static void setIcon() {
        try {
            Display.setIcon(new ByteBuffer[]{
                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon32.png")), false, false, null),
                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon16.png")), false, false, null)
            });
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void initializeOpenGL() {
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

    private static void initializeGame() {
        game = new MyGame("Pervert Rabbits Attack", controllers);
        Display.setTitle(game.getTitle());
        pop = new Popup("Amble-Regular");
    }

    private static void gameLoop() {
        while (isRunning()) {
            Time.update();
            Display.setTitle(game.getTitle() + " [" + (int) (60 / Time.getDelta()) + " fps] x" + Settings.scale);
            if (!pause) {
                update();
            } else {
                resumeIfNeeded();
            }
            render();
        }
    }

    private static boolean isRunning() {
        return !Display.isCloseRequested() && !game.exitFlag;
    }

    private static void resumeIfNeeded() {
        if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
            game.getMenu().delay.start();
            if (!enter) {
                pop.popMessage();
            }
        } else {
            enter = false;
        }
    }

    private static void update() {
        game.getInput();
        game.update();
    }

    private static void render() {
        game.render();
        popMessageIfNeeded();
        Display.sync(60);
        Display.update();
        resolveGamma();
        lastFrame = Display.isActive();
    }

    private static void popMessageIfNeeded() {
        if (pop.getId() != -1) {
            pause = true;
            pop.renderMesagges();
        }
    }

    private static void resolveGamma() {
        if (Display.isActive()) {
            if (!lastFrame) {
                refreshGamma();
            }
        } else if (lastFrame) {
            resetGamma();
        }
    }

    public static void refreshGamma() {
        try {
            Display.setDisplayConfiguration(1f, 0f, 1f);
            Display.setDisplayConfiguration(2f, 0f, 1f);
        } catch (LWJGLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void resetGamma() {
        try {
            Display.setDisplayConfiguration(2f, 0f, 1f);
            Display.setDisplayConfiguration(1f, 0f, 1f);
        } catch (LWJGLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void addMessage(String message) {
        try {
            pop.addMessage(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static String getTitle() {
        return game.getTitle();
    }

    public static void cleanUp() {
        game.endGame();
        AL.destroy();
        Keyboard.destroy();
        Mouse.destroy();
        Controllers.destroy();
        resetGamma();
        Display.destroy();
        System.exit(0);
    }

}
