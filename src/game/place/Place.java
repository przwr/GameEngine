/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.Game;
import game.Settings;
import game.place.cameras.Camera;
import java.util.ArrayList;
import engine.SoundBase;
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

    public Camera cam;
    public Camera[] cams = new Camera[3];
    public boolean isSplit, changeSSMode, singleCam;
    public float camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd;
    public int ssMode, playersLength;

    protected short mapId = 0;

    public final ArrayList<Map> maps = new ArrayList<>();
    protected final ArrayList<Map> tempMaps = new ArrayList<>();

    public final Tile[] tiles;

    public Place(Game game, int width, int height, int sTile, Settings settings) {
        super(game, width, height, settings);
        this.tileSize = sTile;
        tiles = new Tile[width / sTile * height / sTile];
        sounds = new SoundBase();
        sprites = new SpriteBase(scale());
        place = this;
        initMethods();
    }

    @Override
    abstract public void update();

    protected abstract void renderText(Camera cam);

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

    private void initMethods() {
        renders[0] = () -> {
            tempMaps.clear();
            Map map;
            for (int p = 0; p < playersLength; p++) {
                map = players[p].getMap();
                if (!tempMaps.contains(map)) {
                    Renderer.findVisibleLights(map, playersLength);
                    if (!settings.shadowOff) {
                        Renderer.preRendLights(map);
                    }
                    tempMaps.add(map);
                }
            }
            for (int p = 0; p < playersLength; p++) {
                cam = (((Player) players[p]).getCam());
                map = players[p].getMap();
                SplitScreen.setSplitScreen(place, playersLength, p);
                if (p == 0 || !singleCam) {
                    glEnable(GL_SCISSOR_TEST);
                    Renderer.preRenderShadowedLights(place, cam);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    if (map != null) {
                        map.renderBack(cam);
                        map.renderObj(cam);
                        map.renderText(cam);
                        if (map.visibleLights.size() > 0) {
                            Renderer.renderLights(red, green, blue, camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                        }
                    }
                    glDisable(GL_SCISSOR_TEST);
                }
            }
            Renderer.resetOrtho(ssMode);
            Renderer.border(ssMode);
        };
        renders[1] = () -> {
            Map m = players[0].getMap();
            Renderer.findVisibleLights(m, 1);
            if (!settings.shadowOff) {
                Renderer.preRendLights(m);
            }
            cam = (((Player) players[0]).getCam());
            SplitScreen.setSplitScreen(place, 1, 0);
            glEnable(GL_SCISSOR_TEST);
            Renderer.preRenderShadowedLights(place, cam);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            if (m != null) {
                m.renderBack(cam);
                m.renderObj(cam);
                m.renderText(cam);
            }
            Renderer.renderLights(red, green, blue, camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
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

    public Map getMapById(short id) {
        if (maps.get(id).getId() == id) {
            return maps.get(id);
        }
        for (Map m : maps) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public void makeShadows() {
        Renderer.initVariables(this);
    }

    public int getPlayersLenght() {
        return playersLength;
    }

    private interface renderType {

        void render();
    }
}
