/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Area;
import game.Game;
import game.Settings;
import game.place.cameras.Camera;
import game.gameobject.Mob;
import java.util.ArrayList;
import game.gameobject.GameObject;
import engine.SoundBase;
import game.gameobject.Player;
import java.util.Collections;
import java.util.Comparator;
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
    public final int sTile;

    public Camera cam;
    public Camera[] cams = new Camera[3];
    public boolean isSplit, changeSSMode, singleCam;
    public float camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd;
    public int ssMode, playersLength, nrVLights;

    public ArrayList<Mob> sMobs = new ArrayList<>();
    public ArrayList<Mob> fMobs = new ArrayList<>();
    public ArrayList<GameObject> solidObj = new ArrayList<>();
    public ArrayList<GameObject> flatObj = new ArrayList<>();
    public ArrayList<GameObject> emitters = new ArrayList<>();
    public GameObject[] visibleLights = new GameObject[2048];
    public ArrayList<Area> areas = new ArrayList<>();

    public ArrayList<GameObject> depthObj = new ArrayList<>();
    public ArrayList<GameObject> staleDepthObj = new ArrayList<>();
    public ArrayList<GameObject> onTopObject = new ArrayList<>();

    public final Tile[] tiles;

    public Place(Game game, int width, int height, int sTile, Settings settings) {
        super(game, width, height, settings);
        this.sTile = sTile;
        tiles = new Tile[width / sTile * height / sTile];
        sounds = new SoundBase();
        sprites = new SpriteBase();
    }

    @Override
    public abstract void generate();

    @Override
    public abstract void update();

    public SpriteBase getSprites() {
        return sprites;
    }

    public Sprite getSprite(String textureKey, int w, int h) {
        return sprites.getSprite(textureKey, w, h);
    }

    public Sprite getSprite(String textureKey, int w, int h, int sx, int sy) {
        return sprites.getSprite(textureKey, w, h, sx, sy);
    }

    public SpriteSheet getSpriteSheet(String textureKey, int sx, int sy) {
        return sprites.getSpriteSheet(textureKey, sx, sy);
    }

    public SoundBase getSounds() {
        return sounds;
    }

    @Override
    public void render() {
        Renderer.findVisibleLights(this);
        Renderer.preRendLightsFBO(this);
        for (int p = 0; p < playersLength; p++) {
            cam = (((Player) players[p]).getCam());
            SplitScreen.setSplitScreen(this, p);
            if (p == 0 || !singleCam) {
                glEnable(GL_SCISSOR_TEST);
                Renderer.preRenderShadowedLightsFBO(cam);
                renderBack(cam);
                renderObj(cam);
                renderText(cam);
                Renderer.renderLights(r, g, b, camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
                glDisable(GL_SCISSOR_TEST);
            }
        }
        Renderer.border(ssMode);
    }

    protected void renderBack(Camera cam) {
        glColor3f(r, g, b);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        for (int y = 0; y < height / sTile; y++) {
            if (cam.getSY() < (y + 1) * sTile && cam.getEY() > y * sTile) {
                for (int x = 0; x < width / sTile; x++) {
                    if (cam.getSX() < (x + 1) * sTile && cam.getEX() > x * sTile) {
                        Tile t = tiles[x + y * height / sTile];
                        if (t != null) {
                            t.render(0, cam.getXOffEffect() + x * sTile, cam.getYOffEffect() + y * sTile);
                        }
                    }
                }
            }

        }
    }

    protected void renderObj(Camera cam) {
        renderBottom(cam);
        renderTop(cam);
    }

    protected abstract void renderText(Camera cam);

    public void makeShadows() {
        Renderer.initVariables(this);
    }

    private void renderBottom(Camera cam) {
        sortObjects(depthObj);
        int i = 0;
        for (GameObject go : depthObj) {
            try {
                while (staleDepthObj.get(i).getDepth() < go.getDepth()) {
                    staleDepthObj.get(i).render(cam.getXOffEffect(), cam.getYOffEffect());
                    i++;
                }
            } catch (IndexOutOfBoundsException e) {
            }
            go.render(cam.getXOffEffect(), cam.getYOffEffect());
        }
    }

    private void renderTop(Camera cam) {
        sortObjects(onTopObject);
        for (GameObject go : onTopObject) {
            if (cam.getSY() <= go.getY() + (go.getHeight() >> 1) && cam.getEY() >= go.getY() - (go.getHeight() >> 1)
                    && cam.getSX() <= go.getY() + (go.getWidth() >> 2) && cam.getEX() >= go.getX() - (go.getWidth() >> 2)) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
    }

    public void sortObjects(ArrayList<GameObject> a) {
        Collections.sort(a, new ObjectsComparator());
    }

    public void addObj(GameObject go) {
        if (go.getClass() != Player.class) {
            if (go.isEmitter()) {
                emitters.add(go);
            }
            if (Mob.class.isAssignableFrom(go.getClass())) {
                if (go.isSolid()) {
                    sMobs.add((Mob) go);
                } else {
                    fMobs.add((Mob) go);
                }
            } else {
                if (go.isSolid()) {
                    solidObj.add(go);
                } else {
                    flatObj.add(go);
                }
            }
        }
        if (go.isOnTop()) {
            onTopObject.add(go);
        } else {
            if (go.isStale()) {
                staleDepthObj.add(go);
                sortObjects(staleDepthObj);
            } else {
                depthObj.add(go);
            }
        }
    }

    public void deleteObj(GameObject go) {
        if (go.getClass() != Player.class) {
            if (go.isEmitter()) {
                emitters.remove(go);
            }
            if (Mob.class.isAssignableFrom(go.getClass())) {
                if (go.isSolid()) {
                    sMobs.remove((Mob) go);
                } else {
                    fMobs.remove((Mob) go);
                }
            } else {
                if (go.isSolid()) {
                    solidObj.remove(go);
                } else {
                    flatObj.remove(go);
                }
            }
        }
        if (go.isOnTop()) {
            onTopObject.remove(go);
        } else {
            if (go.isStale()) {
                staleDepthObj.remove(go);
                sortObjects(staleDepthObj);
            } else {
                depthObj.remove(go);
            }
        }
    }

    private class ObjectsComparator implements Comparator<GameObject> {

        @Override
        public int compare(GameObject o1, GameObject o2) {
            return o1.getDepth() - o2.getDepth();
        }

    }
}
