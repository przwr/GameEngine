/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

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
import game.text.FontBase;
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
import static game.place.map.Area.X_IN_TILES;
import static game.place.map.Area.Y_IN_TILES;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public abstract class Place extends ScreenPlace {

    protected static final Set<Map> tempMaps = new HashSet<>();
    private static final renderType[] renders = new renderType[2];
    public static int tileSize, tileSquared, tileHalf, xAreaInPixels, yAreaInPixels;
    public static Camera currentCamera;
    protected static DayCycle dayCycle;
    public final ArrayList<Map> maps = new ArrayList<>();
    public final ArrayList<Map> mapsToAdd1 = new ArrayList<>();
    public final ArrayList<Map> mapsToAdd2 = new ArrayList<>();
    public final Camera[] cameras = new Camera[3];
    protected final SpriteBase sprites;
    private final SoundBase sounds;
    public Map loadingMap;
    public boolean isSplit, changeSSMode, singleCamera, firstMapsToAddActive;
    public float camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd;
    public int splitScreenMode, playersCount;
    protected short mapIDCounter = 0;

    private Console console;

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
                if (!singleCamera) {
                    currentCamera = (((Player) players[p]).getCamera());
                    currentCamera.preRenderGUI();
                }
            }
            if (singleCamera) {
                for (Camera camera : cameras) {
                    currentCamera = cameras[playersCount - 2];
                    cameras[playersCount - 2].preRenderGUI();
                }
            }
            for (int player = 0; player < playersCount; player++) {
                currentCamera = (((Player) players[player]).getCamera());
                Map map = players[player].getMap();
                try {
                    if (map != null) {
                        Drawer.setCurrentColor(map.getLightColor());
                        SplitScreen.setSplitScreen(this, playersCount, player);
                        if (player == 0 || !singleCamera) {
                            glEnable(GL_SCISSOR_TEST);
                            Renderer.preRenderShadowedLights(currentCamera);
                            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                            map.updateCamerasVariables(currentCamera);
                            map.renderBackground(currentCamera);
                            map.renderObjects(currentCamera);
                            if (map.getVisibleLights().size() > 0) {
                                Renderer.renderLights(map.getLightColor(), camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                            }
                            Drawer.setCurrentColor(Color.white);
                            glPopMatrix();
                            currentCamera.renderGUI();
                            console.setCamera(currentCamera);
                            console.render(0, 0);
                            glDisable(GL_SCISSOR_TEST);
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
                    currentCamera.preRenderGUI();
                    glEnable(GL_SCISSOR_TEST);
                    Renderer.preRenderShadowedLights(currentCamera);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    map.updateCamerasVariables(currentCamera);
                    map.renderBackground(currentCamera);
                    map.renderObjects(currentCamera);
                    if (map.getVisibleLights().size() > 0) {
                        Renderer.renderLights(map.getLightColor(), camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                    }
                    Drawer.setCurrentColor(Color.white);
                    currentCamera.renderGUI();
                    console.setCamera(currentCamera);
                    console.render(0, 0);
                    glDisable(GL_SCISSOR_TEST);
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
        Place.tileSquared = tileSize * tileSize;
        Place.tileHalf = tileSize / 2;
        Place.xAreaInPixels = X_IN_TILES * tileSize;
        Place.yAreaInPixels = Y_IN_TILES * tileSize;
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

    public Sprite getSprite(String textureKey, String folder) {
        return sprites.getSprite(textureKey, folder);
    }

    public Sprite getSpriteInSize(String textureKey, String folder, int width, int height) {
        return sprites.getSpriteInSize(textureKey, folder, width, height);
    }

    public SpriteSheet getSpriteSheet(String textureKey, String folder) {
        return sprites.getSpriteSheet(textureKey, folder);
    }

    public SpriteSheet getSpriteSheetSetScale(String textureKey, String folder) {
        return sprites.getSpriteSheetSetScale(textureKey, folder);
    }

    public Point getStartPointFromFile(String textureKey, String folder) {
        return sprites.getStartPointFromFile(textureKey, folder);
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
            if (map != null && getMapByName(map.getName()) != map) {
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

    public Color getLightColor() {
        return dayCycle.getShade();
    }

    public String getTime() {
        return dayCycle.toString();
    }

    private interface renderType {

        void render();
    }
}
