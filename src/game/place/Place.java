/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.BackgroundLoader;
import engine.Main;
import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Point;
import engine.view.Renderer;
import engine.view.SplitScreen;
import game.Game;
import game.ScreenPlace;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.logic.DayCycle;
import game.place.cameras.Camera;
import game.place.map.Map;
import org.newdawn.slick.Color;
import sprites.Sprite;
import sprites.SpriteBase;
import sprites.SpriteSheet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static game.Game.OFFLINE;
import static game.Game.ONLINE;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public abstract class Place extends ScreenPlace {

    protected static final Set<Map> tempMaps = new HashSet<>();
    private static final renderType[] renders = new renderType[2];
    public static int tileSize, tileSquared, tileHalf, tileDoubleSize, xAreaInPixels, yAreaInPixels, progress;
    public static Camera currentCamera;
    public static float camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd;
    public static float staticShadowAlpha = 0f;
    protected static DayCycle dayCycle;
    private static Delay loading = Delay.createInMilliseconds(500, true);
    public final ArrayList<Map> maps = new ArrayList<>();
    public final ArrayList<Map> mapsToAdd1 = new ArrayList<>();
    public final ArrayList<Map> mapsToAdd2 = new ArrayList<>();
    public final Camera[] cameras = new Camera[3];
    public Map loadingMap;
    public boolean changeSSMode, singleCamera, firstMapsToAddActive;
    public int splitScreenMode, playersCount;
    protected SpriteBase sprites;
    protected short mapIDCounter = 0;
    boolean g = true;
    private Console console;

    {
        loading.terminate();
    }

    {
        renders[OFFLINE] = () -> {
            SplitScreen.setSingleCamera(this);
            for (int p = 0; p < playersCount; p++) {
                for (Map map : tempMaps) {
                    if (!Settings.shadowOff) {
                        Renderer.findVisibleLights(map, playersCount);
                        Renderer.preRenderLights(map);
                    }
                    staticShadowAlpha = 0.2f * DayCycle.calculateShadowAlpha(map.getLightColor());
                    if (staticShadowAlpha > 0.001f) {
                        Renderer.findVisibleStaticShadows(map, playersCount);
                    }
                }
            }
            for (int player = 0; player < playersCount; player++) {
                currentCamera = (players[player].getCamera());
                Map map = players[player].getMap();
                if (map != null) {
//                    Drawer.setCurrentColor(map.getLightColor());
                    SplitScreen.setSplitScreen(this, playersCount, player);
                    if (player == 0 || !singleCamera) {
                        if (Main.backgroundLoader.isFirstLoaded() || Main.backgroundLoader.allLoaded()) {
                            glEnable(GL_SCISSOR_TEST);
                            Renderer.preRenderShadowedLights(currentCamera);
                            Renderer.preRenderStaticShadows(currentCamera);
                            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                            map.updateCamerasVariables(currentCamera);
                            manageFading(map);

                            renderCurrentCamera(map);
                            console.render();
                            glDisable(GL_SCISSOR_TEST);
                            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                        } else {
                            Drawer.clearScreen(0);
                            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                            if (loading.isOver()) {
                                loading.start();
                                progress++;
                                if (progress > 3) {
                                    progress = 0;
                                }
                            }
                            game.showLoading(progress);
                        }
                    }
                }
            }
            Renderer.resetOrthogonal(splitScreenMode);
            Renderer.border(splitScreenMode);
        };

        renders[ONLINE] = () -> {
            Map map = players[0].getMap();
            if (map != null) {
                Drawer.setCurrentColor(map.getLightColor());
                Renderer.findVisibleLights(map, 1);
                if (!Settings.shadowOff) {
                    Renderer.preRenderLights(map);
                }
                currentCamera = (players[0].getCamera());
                SplitScreen.setSplitScreen(this, 1, 0);
                if (Main.backgroundLoader.isFirstLoaded() || Main.backgroundLoader.allLoaded()) {
                    glEnable(GL_SCISSOR_TEST);
                    Renderer.preRenderShadowedLights(currentCamera);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    Renderer.preRenderStaticShadows(currentCamera);
                    map.updateCamerasVariables(currentCamera);
                    manageFading(map);

                    renderCurrentCamera(map);
                    console.render();

                    glDisable(GL_SCISSOR_TEST);
                } else {
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    if (loading.isOver()) {
                        loading.start();
                        progress++;
                        if (progress > 3) {
                            progress = 0;
                        }
                    }
                    game.showLoading(progress);
                }
            }
        };
    }

    protected Place(Game game, int tileSize) {
        super(game);
        Place.tileSize = tileSize;
        Place.tileDoubleSize = tileSize + tileSize;
        Place.tileSquared = tileSize * tileSize;
        Place.tileHalf = tileSize / 2;
        sprites = new SpriteBase();
        console = new Console(this);
        dayCycle = new DayCycle();
    }

    public static double getCurrentScale() {
        if (currentCamera != null) {
            return currentCamera.getScale();
        }
        return Settings.nativeScale;
    }

    public static DayCycle getDayCycle() {
        return dayCycle;
    }

    public static Color getLightColor() {
        return dayCycle.getShade();
    }

    private void renderCurrentCamera(Map map) {
        Drawer.regularShader.scaleTranslateDefault(currentCamera.getXOffsetEffect(), currentCamera.getYOffsetEffect(), (float) Place
                .getCurrentScale());
        map.renderBackground(currentCamera);
        if (staticShadowAlpha > 0.001f) {
            Drawer.regularShader.resetDefaultMatrix();
            Renderer.renderStaticShadows(camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
            Drawer.regularShader.scaleTranslateDefault(currentCamera.getXOffsetEffect(), currentCamera.getYOffsetEffect(), (float) Place
                    .getCurrentScale());
        }
        map.renderCorpses(currentCamera);
        map.renderObjects(currentCamera);
        Drawer.regularShader.resetDefaultMatrix();
        if (map.getVisibleLights().size() > 0) {
            Renderer.renderLights(map.getLightColor(), camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
        }
        Drawer.setCurrentColor(Color.white);
        currentCamera.neutralizeEffect();
//        if (g) {
        currentCamera.renderGUI();
//            g = false;
//        } else {
//            g = true;
//        }
    }

    private void manageFading(Map map) {
        if (!singleCamera) {
            if (currentCamera.isFading() || currentCamera.isFaded()) {
                float fadingValue = currentCamera.getFadingValue();
                if (fadingValue < 0) {
                    fadingValue = 0;
                }
                Color c = map.getLightColor();
                Drawer.setCurrentColor(new Color(c.r * fadingValue, c.g * fadingValue, c.b * fadingValue));
            }
        }
    }

    private boolean isMergedCamera() {
        for (int i = 0; i < cameras.length; i++) {
            if (cameras[i] == currentCamera) {
                return true;
            }
        }
        return false;
    }

    public abstract void generateAsGuest();

    public abstract void generateAsHost();

    public void addGUI(GUIObject gui) {
        for (GameObject player : players) {
            if (player != null) {
                ((Player) player).addGui(gui);
            }
        }
    }

    public SpriteBase getSprites() {
        return sprites;
    }

    public Sprite getSprite(String textureKey, String folder, boolean... now) {
        return sprites.getSprite(textureKey, folder, now);
    }

    public Sprite getSpriteInSize(String textureKey, String folder, int width, int height, boolean... now) {
        return sprites.getSpriteInSize(textureKey, folder, width, height, now);
    }

    public SpriteSheet getSpriteSheet(String textureKey, String folder, boolean... now) {
        return sprites.getSpriteSheet(textureKey, folder, now);
    }

    public SpriteSheet getSpriteSheetSetScale(String textureKey, String folder, boolean... now) {
        return sprites.getSpriteSheetSetScale(textureKey, folder, now);
    }

    public Point[] getStartPointFromFile(String folder) {
        return sprites.getStartPointFromFile(folder);
    }

    //    public void addDebugConsoles() {
//        for (GameObject player : players) {
//            if (player != null) {
//                Camera camera = ((Player) player).getCamera();
//                if (camera != null) {
//                    camera.addGUI(gui);
//                }
//            }
//        }
//    }
    public Console getConsole() {
        return console;
    }

    public void printMessage(String message) {
        console.printMessage(message);
    }

    @Override
    public void render() {
        renders[game.mode].render();
    }

    public void addMap(Map map) {
        maps.add(map);
    }

    public Map getMapByName(String name) {
        for (Map map : maps) {
            if (map != null && map.getName().equals(name)) {
                return map;
            }
        }
        return null;
    }

    public Map getMapById(short mapID) {
        if (maps.get(mapID).getID() == mapID) {
            return maps.get(mapID);
        }
        for (Map m : maps) {
            if (m.getID() == mapID) {
                return m;
            }
        }
        return null;
    }

    public synchronized void addMapsToAdd() {
        ArrayList<Map> mapsToAdd = firstMapsToAddActive ? mapsToAdd1 : mapsToAdd2;
        for (Map map : mapsToAdd) {
            if (map != null && getMapByName(map.getName()) == null) {
                maps.add(map);
            }
        }
        mapsToAdd.clear();
        firstMapsToAddActive = !firstMapsToAddActive;
    }

    public void makeShadows() {
        Renderer.initializeVariables();
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public String getTime() {
        return dayCycle.toString();
    }

    public short getTimeInMinutes() {
        return dayCycle.getTime();
    }

    public Map getLoadingMap() {
        return loadingMap;
    }

    public void cleanUp() {
        for (Map map : maps) {
            map.clear();
        }
        maps.clear();
        mapsToAdd1.clear();
        mapsToAdd2.clear();
        tempMaps.clear();
        BackgroundLoader.base = sprites;
        sprites = null;
        Main.backgroundLoader.unloadAllTextures();
    }

    private interface renderType {
        void render();
    }

}
