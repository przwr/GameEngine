/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import engine.systemcommunication.AnalyzerSettings;
import engine.systemcommunication.PlayerControllers;
import engine.systemcommunication.Time;
import engine.utilities.Delay;
import engine.utilities.ErrorHandler;
import engine.utilities.SimpleKeyboard;
import engine.view.Popup;
import game.Game;
import game.Settings;
import game.place.Console;
import gamecontent.MyGame;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.opengl.ImageIOImageData;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static engine.systemcommunication.IO.setSettingsFromFile;
import static game.Settings.calculateScale;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

/**
 * @author przemek
 */
public class Main {

    public static final boolean DEBUG = false;
    public static final boolean LOG = false;
    public static boolean SHOW_INTERACTIVE_COLLISION;
    private static final Delay delay = Delay.createInMilliseconds(200);
    private static final Date date = new Date();
    public static final String STARTED_DATE = date.toString().replaceAll(" |:", "_");
    public static boolean pause, enter = true;
    private static Game game;
    private static Popup pop;
    private static Controller[] controllers;
    private static boolean lastFrame;

    private static SimpleKeyboard key;
    private static Console console;

    public static void run() {
        setSettingsFromFile(new File("res/settings.ini"));
        initializeDisplay();
        initializeOpenGL();
        calculateScale();
        initializeGame();
        Time.initialize();
        refreshGammaAndBrightness();
        if (LOG) {
            ErrorHandler.logToFile("\n-------------------- Game Started at " + STARTED_DATE + " -------------------- \n\n");
        }
        delay.start();
        gameLoop();
        cleanUp();
    }

    private static void initializeDisplay() {
        try {
            tryInitializeDisplay();
        } catch (LWJGLException | IOException exception) {
            ErrorHandler.javaError(exception.toString());
        }
    }

    private static void tryInitializeDisplay() throws LWJGLException, IOException {
        setIcon();
        setDisplayMode(Settings.resolutionWidth, Settings.resolutionHeight, Settings.frequency, Settings.fullScreen);
        createDisplay();
        Display.setResizable(false);
        Display.setVSyncEnabled(Settings.verticalSynchronization);
        Display.setDisplayConfiguration(2f, 0f, 1f);
        Keyboard.create();
        Mouse.create();
        Mouse.setNativeCursor(CursorLoader.get().getCursor("res/cursor.png", 1, 1));
        Controllers.create();
        controllers = PlayerControllers.initialize();
    }

    private static void setDisplayMode(int width, int height, int frequency, boolean fullscreen) {
        if (((Display.getDisplayMode().getWidth() != width) || (Display.getDisplayMode().getHeight() != height)) || (Display.isFullscreen() != fullscreen)) {
            try {
                setNewMode(width, height, frequency, fullscreen);
            } catch (LWJGLException exception) {
                ErrorHandler.javaError("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + " " + exception.getMessage());
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
            ErrorHandler.error("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
            return;
        }
        Display.setDisplayMode(targetDisplayMode);
        Display.setFullscreen(fullscreen);
    }

    private static DisplayMode setFullScreen(int width, int height, int frequency) {
        DisplayMode targetDisplayMode = null;
        for (DisplayMode current : Settings.modesTemp) {
            if ((current.getWidth() == width) && (current.getHeight() == height) && (current.getFrequency() == frequency)) {
                if (((targetDisplayMode == null) || (current.getBitsPerPixel() >= targetDisplayMode.getBitsPerPixel()))) {
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
        AnalyzerSettings.update();
    }

    private static void createDisplay() {
        try {
            Display.create(new PixelFormat(32, 0, 24, 0, Settings.samplesCount));
        } catch (Exception exception) {
            Display.destroy();
            ErrorHandler.javaError(exception.getMessage());
        }
    }

    private static void setIcon() {
        try {
            Display.setIcon(new ByteBuffer[]{
                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/textures/icon32.png")), false, false, null),
                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/textures/icon16.png")), false, false, null)
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
        game = new MyGame("Crossroads (PROTOTYPE)", controllers);
        Display.setTitle(game.getTitle());
        pop = new Popup("Amble-Regular");
    }

    private static void gameLoop() {
        key = new SimpleKeyboard();
        while (isRunning()) {
            key.keyboardStart();
            Time.update();
            if (delay.isOver()) {
                delay.start();
                String info;
                int frames = (int) (60 / Time.getDelta());
                if (game != null && game.getPlace() != null) {
                    info = " [" + frames + " fps] " + game.getPlace().getTime();
                    console = game.getPlace().getConsole();
                    if (key.keyPressed(Keyboard.KEY_F1)) {
                        console.setStatsRendered(!console.areStatsRendered());
                    }
                    if (key.keyPressed(Keyboard.KEY_F2)) {
                        SHOW_INTERACTIVE_COLLISION = !SHOW_INTERACTIVE_COLLISION;
                        System.out.println(SHOW_INTERACTIVE_COLLISION);
                        console.printMessage("SHOW/HIDE INTERACTIVE");
                    }
                    if (console.areStatsRendered()) {
                        console.clearStats();
                        console.printStats(info);
                    }
                } else {
                    info = " [" + frames + " fps]";
                }
                Display.setTitle(game.getTitle() + info);
                if (LOG) {
                    ErrorHandler.logToFile(info + "\n");
                }
            }
            if (!pause) {
                update();
            } else {
                PopMessageIfNeeded();
            }
            render();
            key.keyboardEnd();
        }
    }

    private static boolean isRunning() {
        return !Display.isCloseRequested() && !game.exitFlag;
    }

    private static void PopMessageIfNeeded() {
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
        try {
            game.getInput();
            game.update();
        } catch (Exception exception) {
            game.endGame();
            game.update();
            ErrorHandler.exception(exception);
        }
    }

    private static void render() {
        try {
            game.render();
        } catch (Exception exception) {
            game.endGame();
            game.update();
            ErrorHandler.exception(exception);
        }
        renderMessageIfNeeded();
        Display.sync(60);
        Display.update();
        resolveGamma();
        lastFrame = Display.isActive();
    }

    private static void renderMessageIfNeeded() {
        if (pop.getId() != -1) {
            pause = true;
            pop.renderMessages();
        }
    }

    private static void resolveGamma() {
        if (Display.isActive()) {
            if (!lastFrame) {
                refreshGammaAndBrightness();
            }
        } else if (lastFrame) {
            resetGammaAndBrightness();
        }
    }

    public static void refreshGammaAndBrightness() {
        try {
            Display.setDisplayConfiguration(Settings.gameGamma, Settings.gameBrightness, 1f);
        } catch (LWJGLException exception) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, exception);
        }
    }

    public static void resetGammaAndBrightness() {
        try {
            Display.setDisplayConfiguration(Settings.defaultGamma, Settings.defaultBrightness, 1f);
        } catch (LWJGLException exception) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, exception);
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
        if (game != null) {
            game.endGame();
        }
        AL.destroy();
        Keyboard.destroy();
        Mouse.destroy();
        Controllers.destroy();
        resetGammaAndBrightness();
        Display.destroy();
        System.exit(0);
    }
}
