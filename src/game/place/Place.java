/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.Game;
import game.place.cameras.Camera;
import java.util.ArrayList;
import engine.SoundBase;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.GameObject;
import game.gameobject.Player;
import static org.lwjgl.opengl.GL11.*;
import sprites.Sprite;
import sprites.SpriteBase;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public abstract class Place extends ScreenPlace {

    protected SoundBase sounds;
    protected SpriteBase sprites;
    private final int tileSize;

    private final renderType[] renders = new renderType[2];

    public Camera currentCamera;
    public Camera[] cameras = new Camera[3];
    public boolean isSplit, changeSSMode, singleCamera;
    public float camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd;
    public int splitScreenMode, playersCount;

    protected short mapID = 0;

    public final ArrayList<Map> maps = new ArrayList<>();
    protected final ArrayList<Map> tempMaps = new ArrayList<>();

    private Console console;

    public Place(Game game, int tileSize) {
        super(game);
        this.tileSize = tileSize;
        sounds = new SoundBase();
        sprites = new SpriteBase();
        initializeMethods();
        console = new Console(this);
    }

    public abstract void generateAsGuest();

    public abstract void generateAsHost();

    public void addGUI(GUIObject gui) {
        for (GameObject player : players) {
            if (player != null) {
                Camera camera = ((Player) player).getCamera();
                if (camera != null) {
                    camera.addGUI(gui);
                }
            }
        }
    }
    
    public SpriteBase getSprites() {
        return sprites;
    }

    public Sprite getSprite(String textureKey) {
        return sprites.getSprite(textureKey);
    }

    public SpriteSheet getSpriteSheet(String textureKey) {
        return sprites.getSpriteSheet(textureKey);
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
    
    private void initializeMethods() {
        renders[0] = () -> {
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
                SplitScreen.setSplitScreen(this, playersCount, player);
                if (player == 0 || !singleCamera) {
                    glEnable(GL_SCISSOR_TEST);
                    Renderer.preRenderShadowedLights(this, currentCamera);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    if (map != null) {
                        map.updateCamerasVariables(currentCamera);
                        map.renderBackground(currentCamera);
                        map.renderObjects(currentCamera);
                        if (map.visibleLights.size() > 0) {
                            Renderer.renderLights(red, green, blue, camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                        }
                        currentCamera.renderGUI();
                    }
                    glDisable(GL_SCISSOR_TEST);
                }
            }
            Renderer.resetOrtho(splitScreenMode);
            Renderer.border(splitScreenMode);
        };
        renders[1] = () -> {
            Map map = players[0].getMap();
            Renderer.findVisibleLights(map, 1);
            if (!Settings.shadowOff) {
                Renderer.preRendLights(map);
            }
            currentCamera = (((Player) players[0]).getCamera());
            SplitScreen.setSplitScreen(this, 1, 0);
            glEnable(GL_SCISSOR_TEST);
            Renderer.preRenderShadowedLights(this, currentCamera);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            if (map != null) {
                map.updateCamerasVariables(currentCamera);
                map.renderBackground(currentCamera);
                map.renderObjects(currentCamera);
                if (map.visibleLights.size() > 0) {
                    Renderer.renderLights(red, green, blue, camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                }
                currentCamera.renderGUI();
            }
            glDisable(GL_SCISSOR_TEST);
        };
    }

    @Override
    public void render() {
        renders[game.mode].render();
    }

    public void addMap(Map map) {
        maps.add(map);
    }

    public int getTileSize() {
        return tileSize;
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

    private interface renderType {

        void render();
    }
}
