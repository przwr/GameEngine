/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Drawer;
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

    protected final SoundBase sounds;
    protected final SpriteBase sprites;
    public final int tileSize;

    private final renderType[] renders = new renderType[2];
    private final Place place;

    public Camera currentCam;
    public Camera[] cameras = new Camera[3];
    public boolean isSplit, changeSSMode, singleCamera;
    public float camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd;
    public int splitScreenMode, playersCount;

    protected short mapId = 0;

    public final ArrayList<Map> maps = new ArrayList<>();
    protected final ArrayList<Map> tempMaps = new ArrayList<>();

    public final Tile[] tiles;

    public Place(Game game, int width, int height, int sTile) {
        super(game, width, height);
        this.tileSize = sTile;
        tiles = new Tile[width / sTile * height / sTile];
        sounds = new SoundBase();
        sprites = new SpriteBase(Settings.scale);
        place = this;
        initializeMethods();
    }

    protected abstract void renderText(Camera cam);

    public abstract void generateAsGuest();
    
    public abstract void generateAsHost();

    public void addGUI(GUIObject go) {
        for (GameObject p : players) {
            if (p != null) {
                Camera c = ((Player) p).getCamera();
                if (c != null) {
                    c.addGUI(go);
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
            for (int p = 0; p < playersCount; p++) {
                currentCam = (((Player) players[p]).getCamera());
                map = players[p].getMap();
                SplitScreen.setSplitScreen(place, playersCount, p);
                if (p == 0 || !singleCamera) {
                    glEnable(GL_SCISSOR_TEST);
                    Renderer.preRenderShadowedLights(place, currentCam);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    if (map != null) {
                        Drawer.drawRectangleInBlack(currentCam.getXOffsetEffect() + currentCam.getXStart(), currentCam.getYOffset() + currentCam.getYStart(), currentCam.getWidth(), currentCam.getHeight());
                        map.renderBackground(currentCam);
                        map.renderObjects(currentCam);
                        map.renderText(currentCam);
                        if (map.visibleLights.size() > 0) {
                            Renderer.renderLights(red, green, blue, camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                        }
                        currentCam.renderGUI();
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
            currentCam = (((Player) players[0]).getCamera());
            SplitScreen.setSplitScreen(place, 1, 0);
            glEnable(GL_SCISSOR_TEST);
            Renderer.preRenderShadowedLights(place, currentCam);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            if (map != null) {
                map.renderBackground(currentCam);
                map.renderObjects(currentCam);
                map.renderText(currentCam);
                if (map.visibleLights.size() > 0) {
                    Renderer.renderLights(red, green, blue, camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                }
                currentCam.renderGUI();
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
        Renderer.initializeVariables(this);
    }

    public int getPlayersLenght() {
        return playersCount;
    }

    private interface renderType {

        void render();
    }
}
