/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.SplitScreen;
import engine.Renderer;
import engine.Drawer;
import game.Game;
import game.place.cameras.Camera;
import java.util.ArrayList;
import engine.SoundBase;
import static game.Game.OFFLINE;
import static game.Game.ONLINE;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.GameObject;
import game.gameobject.Player;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import sprites.Sprite;
import sprites.SpriteBase;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public abstract class Place extends ScreenPlace {

    public static int tileSize;
    public static int tileSquared;
    public static int tileHalf;

    protected static DayCycle dayCycle;

    public final ArrayList<Map> maps = new ArrayList<>();
    protected static final ArrayList<Map> tempMaps = new ArrayList<>();
    private static final renderType[] renders = new renderType[2];

    public Camera currentCamera;
    public Camera[] cameras = new Camera[3];
    public boolean isSplit, changeSSMode, singleCamera;
    public float camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd;
    public int splitScreenMode, playersCount;

    protected SoundBase sounds;
    protected SpriteBase sprites;
    protected short mapIDcounter = 0;

    private Console console;

    {
        renders[OFFLINE] = () -> {
            tempMaps.clear();
            Map map;
            for (int p = 0; p < playersCount; p++) {
                map = players[p].getMap();
                if (!tempMaps.contains(map)) {
                    Renderer.findVisibleLights(map, playersCount);
                    if (!Settings.shadowOff) {
                        Renderer.preRendLights(map);
                    }
                    tempMaps.add(map);
                }
            }
            for (int player = 0; player < playersCount; player++) {
                currentCamera = (((Player) players[player]).getCamera());
                map = players[player].getMap();
                if (map != null) {
                    Drawer.setCurrentColor(map.getLightColor());
                    SplitScreen.setSplitScreen(this, playersCount, player);
                    if (player == 0 || !singleCamera) {
                        glEnable(GL_SCISSOR_TEST);
                        Renderer.preRenderShadowedLights(this, currentCamera);
                        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                        map.updateCamerasVariables(currentCamera);
                        map.renderBackground(currentCamera);
                        map.renderObjects(currentCamera);
                        if (map.getVisibleLights().size() > 0) {
                            Renderer.renderLights(map.getLightColor(), camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                        }
                        currentCamera.renderGUI();
                        console.setCamera(currentCamera);
                        console.render(0, 0);
                    }
                    glDisable(GL_SCISSOR_TEST);
                }
            }
            Renderer.resetOrtho(splitScreenMode);
            Renderer.border(splitScreenMode);
        };
        renders[ONLINE] = () -> {
            Map map = players[0].getMap();
            if (map != null) {
                Drawer.setCurrentColor(map.getLightColor());
                Renderer.findVisibleLights(map, 1);
                if (!Settings.shadowOff) {
                    Renderer.preRendLights(map);
                }
                currentCamera = (((Player) players[0]).getCamera());
                SplitScreen.setSplitScreen(this, 1, 0);
                glEnable(GL_SCISSOR_TEST);
                Renderer.preRenderShadowedLights(this, currentCamera);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                map.updateCamerasVariables(currentCamera);
                map.renderBackground(currentCamera);
                map.renderObjects(currentCamera);
                if (map.getVisibleLights().size() > 0) {
                    Renderer.renderLights(map.getLightColor(), camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                }
                currentCamera.renderGUI();
                console.setCamera(currentCamera);
                console.render(0, 0);
            }
            glDisable(GL_SCISSOR_TEST);
        };
    }

    public Place(Game game, int tileSize) {
        super(game);
        Place.tileSize = tileSize;
        Place.tileSquared = tileSize * tileSize;
        Place.tileHalf = tileSize / 2;
        sounds = new SoundBase();
        sprites = new SpriteBase();
        console = new Console(this);
        dayCycle = new DayCycle();
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

    public Sprite getSprite(String textureKey) {
        return sprites.getSprite(textureKey);
    }

    public Sprite getSpriteInSize(String textureKey, int width, int height) {
        return sprites.getSpriteInSize(textureKey, width, height);
    }

    public SpriteSheet getSpriteSheet(String textureKey) {
        return sprites.getSpriteSheet(textureKey);
    }

    public SpriteSheet getSpriteSheetSetScale(String textureKey) {
        return sprites.getSpriteSheetSetScale(textureKey);
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
    public void printMessage(String message) {
        console.write(message);
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
            if (map.getName().equals(name)) {
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

    public void makeShadows() {
        Renderer.initializeVariables();
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public Color getLightColor() {
        return dayCycle.getColor();
    }

    private interface renderType {

        void render();
    }
}
