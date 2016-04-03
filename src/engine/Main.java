/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import engine.systemcommunication.AnalyzerSettings;
import engine.systemcommunication.PlayerControllers;
import engine.systemcommunication.Time;
import engine.utilities.*;
import engine.view.Popup;
import engine.view.Renderer;
import engine.view.SplitScreen;
import game.Game;
import game.Settings;
import game.logic.navmeshpathfinding.Window;
import game.place.Console;
import game.place.map.Area;
import gamecontent.MyGame;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.cmd.Shell;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.*;
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
import static game.Settings.players;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

/**
 * @author przemek
 */
public class Main {

    public static final boolean DEBUG = true;
    public static final boolean LOG = false;
    private static final Delay delay = Delay.createInMilliseconds(500, true);
    private static final Date date = new Date();
    public static final String STARTED_DATE = date.toString().replaceAll(" |:", "_");
    public static boolean SHOW_INTERACTIVE_COLLISION, SHOW_AREAS, SHOW_MESH, pause, enter = true, TEST = true;
    public static Window meshWindow;
    public static BackgroundLoader backgroundLoader;
    public static SimpleKeyboard key = new SimpleKeyboard();
    static long variableYieldTime;
    private static Game game;
    private static Popup pop;
    private static Controller[] controllers;
    private static boolean lastFrame;
    private static Console console;
    private static Shell shell;
    private static long lastTime;
    private static String info;

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
        delay.terminate();
        lastTime = System.nanoTime();
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
            PixelFormat pixelFormat = new PixelFormat(32, 0, 24, 0, Settings.samplesCount);
            ContextAttribs contextAttributes = new ContextAttribs(1, 1);
            contextAttributes.withForwardCompatible(true);
            Display.create(pixelFormat, contextAttributes);
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
        while (!Display.isCreated()) {
        }
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_BLEND);
        glEnable(GL_SCISSOR_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1); // TODO Do wywalenia, tylko chwilowo tekst z tego korzysta
        glClearColor(0, 0, 0, 0);
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        restartBackGroundLoader();
    }

    public static void restartBackGroundLoader() {
        if (backgroundLoader != null) {
            backgroundLoader.cleanup();
            backgroundLoader = null;
        }
        backgroundLoader = new BackgroundLoader() {
            @Override
            Drawable getDrawable() throws LWJGLException {
                return new Pbuffer(2, 2, new PixelFormat(8, 24, 0), Display.getDrawable());
            }
        };
        try {
            backgroundLoader.start();
        } catch (LWJGLException e) {
            ErrorHandler.error("Failed to start background thread. " + e.getMessage());
        }
    }

    private static void initializeGame() {
        shell = new Shell();
        game = new MyGame("Crossroads (PROTOTYPE)", controllers);
        backgroundLoader.setGame(game);
        Display.setTitle(game.getTitle());
        Renderer.setUpDisplay();
        Drawer.setUpDisplay();
        SplitScreen.setUpDisplay();
        pop = new Popup("Amble-Regular");
    }

    private static void gameLoop() {
        while (isRunning()) {
            Time.update();
            key.keyboardStart();
            if (game != null && game.getPlace() != null) {
                console = game.getPlace().getConsole();
                fInput();
            }
            loggingAndStats();
            if (!pause) {
                update();
            } else {
                PopMessageIfNeeded();
            }
            render();
            key.keyboardEnd();
        }
    }

    private static void loggingAndStats() {
        if (delay.isOver()) {
            delay.start();
            if (console != null && console.areStatsRendered() || LOG) {
                int frames = Math.round(60 / Time.getDelta());
                try {
                    CpuPerc cpu = shell.getSigar().getCpuPerc();
                    String cpuUsage = CpuPerc.format(cpu.getCombined());
                    Mem mem = shell.getSigar().getMem();
                    float memoryUsage = Math.round((long) (mem.getUsedPercent() * mem.getTotal()) / 1073741824 / 10f) / 10f;
                    float totalMemory = Math.round(100 * mem.getTotal() / 1073741824 / 10f) / 10f;
                    info = " [ FPS: " + frames + " | MEM: " + memoryUsage + " / " + totalMemory + " GB | CPU: " + cpuUsage + " ]";
                } catch (SigarException e) {
                }
//                Display.setTitle(game.getTitle() + info);
                if (LOG) {
                    ErrorHandler.logToFile(info + "\n");
                }
                if (console.areStatsRendered() && game != null && game.getPlace() != null) {
                    info = game.getPlace().getTime() + info;
                    console.clearStats();
                    console.printStats(info + " Player 1: " + game.getPlayerCoordinates());
                }
            }
        }
    }

    private static void fInput() {
        if (key.keyPressed(Keyboard.KEY_F1)) {
            console.setStatsRendered(!console.areStatsRendered());
        }
        if (key.keyPressed(Keyboard.KEY_F2)) {
            SHOW_INTERACTIVE_COLLISION = !SHOW_INTERACTIVE_COLLISION;
            console.printMessage("SHOW/HIDE INTERACTIVE");
        }
//        if (key.keyPressed(Keyboard.KEY_F3)) {
//            console.printMessage("You've just clicked the F3 button. Why did you do that?");
//        }
        if (key.keyPressed(Keyboard.KEY_F3)) {
            SHOW_AREAS = !SHOW_AREAS;
            console.printMessage("SHOW/HIDE AREAS");
        }
        if (key.keyPressed(Keyboard.KEY_F4)) {
            SHOW_MESH = !SHOW_MESH;
            if (SHOW_MESH) {
                if (Main.meshWindow == null) {
                    Main.meshWindow = new Window();
                    if (players[0].getMap() != null) {
                        Area area = players[0].getMap().getArea(players[0].getArea());
                        Main.meshWindow.addVariables(area.getNavigationMesh(), null, null, null);
                        Main.meshWindow.repaint();
                    }
                }
                Main.meshWindow.setVisible(true);
            } else {
                if (Main.meshWindow != null) {
                    Main.meshWindow.setVisible(false);
                }
            }
        }
        if (key.keyPressed(Keyboard.KEY_F12)) {
            Methods.pasteToClipBoard(game.getSimplePlayerCoordinates());
            console.printMessage("PLAYER COORDINATES PASTED TO CLIPBOARD");
        }
    }

    private static boolean isRunning() {
        return !Display.isCloseRequested() && Display.isCreated() && !game.exitFlag;
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
            exception.printStackTrace();
            game.endGame();
            game.update();
            ErrorHandler.exception(exception);
        }

    }

    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        try {
            game.render();
        } catch (Exception exception) {
            game.endGame();
            game.update();
            ErrorHandler.exception(exception);
        }
        renderMessageIfNeeded();
        resolveGamma();
        Display.update();
        lastFrame = Display.isActive();
        sync(Settings.framesLimit);
    }

    public static void sync(int fps) {
        long sleepTime = 1000000000 / fps;
        long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % 1000000);
        long overSleep = 0;
        try {
            while (true) {
                long t = System.nanoTime() - lastTime;
                if (t < sleepTime - yieldTime) {
                    Thread.sleep(1);
                } else if (t < sleepTime) {
                    Thread.yield();
                } else {
                    overSleep = t - sleepTime;
                    break;
                }
            }
        } catch (InterruptedException e) {
        }
        lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);
        if (overSleep > variableYieldTime) {
            variableYieldTime = Math.min(variableYieldTime + 200 * 1000, sleepTime);
        } else if (overSleep < variableYieldTime - 200 * 1000) {
            variableYieldTime = Math.max(variableYieldTime - 2 * 1000, 0);
        }
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
            if (Display.isCreated()) {
                Display.setDisplayConfiguration(Settings.defaultGamma, Settings.defaultBrightness, 1f);
            }
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
        backgroundLoader.cleanup();
        AL.destroy();
        Keyboard.destroy();
        Mouse.destroy();
        Controllers.destroy();
        resetGammaAndBrightness();
        Display.destroy();
    }
}
