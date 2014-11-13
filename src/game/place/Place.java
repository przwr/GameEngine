/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Area;
import engine.Drawer;
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
    public ArrayList<GameObject> foregroundTiles = new ArrayList<>();
    public ArrayList<GameObject> onTopObject = new ArrayList<>();

    public final Tile[] tiles;

    public Place(Game game, int width, int height, int sTile, Settings settings) {
        super(game, width, height, settings);
        this.sTile = sTile;
        tiles = new Tile[width / sTile * height / sTile];
        sounds = new SoundBase();
        sprites = new SpriteBase(SCALE());
    }

    @Override
    public abstract void generate();

    @Override
    public abstract void update();

    public SpriteBase getSprites() {
        return sprites;
    }

    public Sprite getSprite(String textureKey) {
        return sprites.getSprite(textureKey);
    }
    
    public SpriteSheet getSpriteSheet(String textureKey) {
        return sprites.getSpriteSheet(textureKey);
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
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                sprites.setLastTex(-1);
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
        Drawer.refresh(this);
        for (int y = 0; y < height / sTile; y++) {
            if (cam.getSY() < (y + 1) * sTile && cam.getEY() > y * sTile) {
                for (int x = 0; x < width / sTile; x++) {
                    if (cam.getSX() < (x + 1) * sTile && cam.getEX() > x * sTile) {
                        Tile t = tiles[x + y * height / sTile];
                        if (t != null) {
                            t.renderSpecific(0, cam.getXOffEffect() + x * sTile, cam.getYOffEffect() + y * sTile);
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
        Drawer.refresh(this);
        sortObjects(depthObj);
        int y = 0;
        for (GameObject go : depthObj) {
            while (y < foregroundTiles.size() && foregroundTiles.get(y).getDepth() < go.getDepth()) {
                foregroundTiles.get(y).render(cam.getXOffEffect(), cam.getYOffEffect());
                y++;
            }
            go.render(cam.getXOffEffect(), cam.getYOffEffect());
        }
        for (int i = y; i < foregroundTiles.size(); i++) {
            foregroundTiles.get(i).render(cam.getXOffEffect(), cam.getYOffEffect());
            //System.err.println(i + " " + foregroundTiles.size());
        }
    }

    private void renderTop(Camera cam) {
        Drawer.refresh(this);
        sortObjects(onTopObject);
        for (GameObject go : onTopObject) {
            // if (cam.getSY() <= go.getY() + (go.getHeight() >> 1) && cam.getEY() >= go.getY() - (go.getHeight() >> 1)
            //         && cam.getSX() <= go.getX() + (go.getWidth() >> 2) && cam.getEX() >= go.getX() - (go.getWidth() >> 2)) {
            go.render(cam.getXOffEffect(), cam.getYOffEffect());
            //}
        }
    }

    public void sortObjects(ArrayList<GameObject> a) {
        Collections.sort(a, new ObjectsComparator());
    }

    public void addFGTile(GameObject t, int x, int y, int depth, boolean replace) {
        if (replace) {
            tiles[x / sTile + y / sTile * height / sTile] = null;
            for (GameObject s : foregroundTiles) {
                if (s.getX() == x && s.getY() == y) {
                    foregroundTiles.remove(s);
                }
            }
        }
        t.setX(x);
        t.setY(y);
        t.setDepth(depth);
        foregroundTiles.add(t);
        sortObjects(foregroundTiles);
    }

    public void deleteFGTile(GameObject t) {
        foregroundTiles.remove(t);
        sortObjects(foregroundTiles);
    }

    public void deleteFGTile(int x, int y) {
        for (GameObject s : foregroundTiles) {
            if (s.getX() == x && s.getY() == y) {
                foregroundTiles.remove(s);
            }
        }
        sortObjects(foregroundTiles);
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
            depthObj.add(go);
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
            depthObj.remove(go);
        }
    }

    private class ObjectsComparator implements Comparator<GameObject> {

        @Override
        public int compare(GameObject o1, GameObject o2) {
            return o1.getDepth() - o2.getDepth();
        }

    }
}
