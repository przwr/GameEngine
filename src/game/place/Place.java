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
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.logic.DayCycle;
import game.place.cameras.Camera;
import game.place.map.Map;
import game.text.FontBase;
import gamecontent.MyPlayer;
import org.newdawn.slick.Color;
import sounds.SoundBase;
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
    protected static DayCycle dayCycle;
    private static Delay loading = Delay.createInMilliseconds(500, true);
    public final ArrayList<Map> maps = new ArrayList<>();
    public final ArrayList<Map> mapsToAdd1 = new ArrayList<>();
    public final ArrayList<Map> mapsToAdd2 = new ArrayList<>();
    public final Camera[] cameras = new Camera[3];
    protected final SpriteBase sprites;
    protected final SoundBase sounds;
    public Map loadingMap;
    public boolean changeSSMode, singleCamera, firstMapsToAddActive;
    public float camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd;
    public int splitScreenMode, playersCount;
    protected short mapIDCounter = 0;
    private Console console;

    {
        loading.terminate();
    }

    {
        renders[OFFLINE] = () -> {
            SplitScreen.setSingleCamera(this);
            for (int p = 0; p < playersCount; p++) {
                tempMaps.stream().forEach((map) -> {
                    Renderer.findVisibleLights(map, playersCount);
                    if (!Settings.shadowOff) {
                        Renderer.preRenderLights(map);
                    }
                });
//                if (!singleCamera) {
//                    currentCamera = (((Player) players[p]).getCamera());
//                    currentCamera.preRenderGUI();
//                }
            }
//            if (singleCamera) {
//                currentCamera = cameras[playersCount - 2];
//                cameras[playersCount - 2].preRenderGUI();
//            }
            for (int player = 0; player < playersCount; player++) {
                currentCamera = (((Player) players[player]).getCamera());
                Map map = players[player].getMap();
                try {
                    if (map != null) {
                        Drawer.setCurrentColor(map.getLightColor());
                        SplitScreen.setSplitScreen(this, playersCount, player);
                        if (player == 0 || !singleCamera) {
                            if (Main.backgroundLoader.isFirstLoaded() || Main.backgroundLoader.allLoaded()) {
                                glEnable(GL_SCISSOR_TEST);
                                Renderer.preRenderShadowedLights(currentCamera);
                                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                                map.updateCamerasVariables(currentCamera);
                                manageFading(map);
                                map.renderBackground(currentCamera);
                                map.renderObjects(currentCamera);
                                if (map.getVisibleLights().size() > 0) {
                                    Renderer.renderLights(map.getLightColor(), camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd,
                                            camYTEnd);
                                }
                                Drawer.setCurrentColor(Color.white);
                                glPopMatrix();
                                currentCamera.renderGUI();
                                currentCamera.neutralizeEffect();
                                console.setCamera(currentCamera);
                                console.render(0, 0);
                                glDisable(GL_SCISSOR_TEST);
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
                } catch (Exception e) { //BY WYJĄTEK PODCZAS RYSOWANIA NIE PSUŁ GRY :D
                    glPopMatrix();
                    throw e;
                }
            }
            Renderer.resetOrthogonal(splitScreenMode);
            Renderer.border(splitScreenMode);
        };

        renders[ONLINE] = () -> {
            Map map = players[0].getMap();
            try {
                if (map != null) {
                    Drawer.setCurrentColor(map.getLightColor());
                    Renderer.findVisibleLights(map, 1);
                    if (!Settings.shadowOff) {
                        Renderer.preRenderLights(map);
                    }
                    currentCamera = (((Player) players[0]).getCamera());
                    SplitScreen.setSplitScreen(this, 1, 0);
//                    currentCamera.preRenderGUI();
                    if (Main.backgroundLoader.isFirstLoaded() || Main.backgroundLoader.allLoaded()) {
                        glEnable(GL_SCISSOR_TEST);
                        Renderer.preRenderShadowedLights(currentCamera);
                        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                        map.updateCamerasVariables(currentCamera);
                        manageFading(map);
                        map.renderBackground(currentCamera);
                        map.renderObjects(currentCamera);
                        if (map.getVisibleLights().size() > 0) {
                            Renderer.renderLights(map.getLightColor(), camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                        }
                        Drawer.setCurrentColor(Color.white);
                        currentCamera.renderGUI();
                        currentCamera.neutralizeEffect();
                        console.setCamera(currentCamera);
                        console.render(0, 0);
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
            } catch (Exception e) {
                glPopMatrix();
                throw e;
            }
        };
    }

    protected Place(Game game, int tileSize) {
        super(game);
        Place.tileSize = tileSize;
        Place.tileDoubleSize = tileSize + tileSize;
        Place.tileSquared = tileSize * tileSize;
        Place.tileHalf = tileSize / 2;
        sounds = new SoundBase();
        sprites = new SpriteBase();
        console = new Console(this);
        dayCycle = new DayCycle();
        fonts = new FontBase(20);
        standardFont = fonts.add("Amble-Regular", (int) (Settings.nativeScale * 24));
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

    private void manageFading(Map map) {
        if (!singleCamera) {
            if (currentCamera.isFading() || currentCamera.isFaded()) {
                float fadingValue = currentCamera.getFadingValue();
                if (fadingValue < 0) {
                    // Hack, bo czasem coś się wali i nie wiem dlaczego
                    if (fadingValue < -125) {
                        currentCamera.setFaded(false);
                        for (GameObject owner : currentCamera.getOwners()) {
                            if (owner instanceof Entity) {
                                ((Entity) owner).setUnableToMove(false);
                                owner.setVisible(true);
                            }
                            if (owner instanceof MyPlayer) {
                                ((MyPlayer) owner).getGUI().setVisible(true);
                            }
                        }
                    }
                    fadingValue = 0;
                }
                Color c = map.getLightColor();
                if (currentCamera.isFaded()) {
                    Drawer.setCurrentColor(new Color(c.r * fadingValue, c.r * fadingValue, c.b * fadingValue));
                } else {
                    Drawer.setCurrentColor(new Color(c.r * fadingValue, c.r * fadingValue, c.b * fadingValue));
                }
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

    public SoundBase getSounds() {
        return sounds;
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
        sounds.cleanUp();
        BackgroundLoader.base = sprites;
        Main.backgroundLoader.unloadAllTextures();
    }

    private interface renderType {
        void render();
    }

}
