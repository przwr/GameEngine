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
import game.myGame.MyPlayer;
import java.util.ArrayList;
import game.gameobject.GameObject;
import engine.FontsHandler;
import engine.SoundBase;
import game.myGame.MyMob;
import java.util.Collections;
import java.util.Comparator;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import sprites.Sprite;
import sprites.SpriteBase;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public abstract class Place {

    public final Game game;
    public final Settings settings;
    protected final SoundBase sounds;
    protected final SpriteBase sprites;
    protected FontsHandler fonts;
    public final int width, height, sTile;

    public Camera cam, camfor2, camfor3, camfor4;
    public boolean isSplit, changeSSMode;
    public float r, g, b, camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd;
    public int ssMode, SX, SY, EX, EY, playersLength;

    public ArrayList<Mob> sMobs = new ArrayList<>();
    public ArrayList<Mob> fMobs = new ArrayList<>();
    public ArrayList<GameObject> solidObj = new ArrayList<>();
    public ArrayList<GameObject> flatObj = new ArrayList<>();
    public GameObject[] players;
    public ArrayList<GameObject> emitters = new ArrayList<>();
    public ArrayList<GameObject> visibleLights = new ArrayList<>();
    public ArrayList<Area> areas = new ArrayList<>();
    public ArrayList<GameObject> depthObj = new ArrayList<>();
    public ArrayList<GameObject> onTopObject = new ArrayList<>();
    public final Tile[] tiles;

    public Place(Game game, int width, int height, int sTile, Settings settings) {
        this.width = width;
        this.height = height;
        this.sTile = sTile;
        this.settings = settings;
        tiles = new Tile[width / sTile * height / sTile];
        this.game = game;
        sounds = new SoundBase();
        sprites = new SpriteBase();
    }

    public abstract void generate();

    public abstract void update();

    public SpriteBase getSprites() {
        return sprites;
    }

    public Sprite getSprite(String textureKey, int sx, int sy) {
        return sprites.getSprite(textureKey, sx, sy);
    }

    public SpriteSheet getSpriteSheet(String textureKey, int sx, int sy) {
        return sprites.getSpriteSheet(textureKey, sx, sy);
    }

    public SoundBase getSounds() {
        return sounds;
    }

    public void render() {
        Renderer.findVisibleLights(this);
        Renderer.preRendLightsFBO(this);
        glEnable(GL_SCISSOR_TEST);
        for (int p = 0; p < playersLength; p++) {
            cam = (((MyPlayer) players[p]).getCam());
            SplitScreen.setSplitScreen(this, p);
            Renderer.preRenderShadowedLightsFBO(cam);
            renderBack(cam);
            renderObj(cam);
            renderText(cam);
            Renderer.renderLights(r, g, b, camXStart, camYStart, camXEnd, camYEnd, camXTStart, camYTStart, camXTEnd, camYTEnd);
        }
        glDisable(GL_SCISSOR_TEST);
        Renderer.border(ssMode);
    }

    protected void renderBack(Camera cam) {
        glColor3f(r, g, b);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        SX = cam.getSX();
        EX = cam.getEX();
        SY = cam.getSY();
        EY = cam.getEY();
        for (int y = 0; y < height / sTile; y++) {
            if (SY < (y + 1) * sTile && EY > y * sTile) {
                for (int x = 0; x < width / sTile; x++) {
                    if (SX < (x + 1) * sTile && EX > x * sTile) {
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

    public void renderMessage(int i, int x, int y, String ms, Color color) {
        fonts.write(i).drawString(x - fonts.write(i).getWidth(ms) / 2, y - (4 * fonts.write(i).getHeight()) / 3, ms, color);
    }

    private void renderBottom(Camera cam) {
        sortObjects(false);
        for (GameObject go : depthObj) {
            go.render(cam.getXOffEffect(), cam.getYOffEffect());
        }
    }

    private void renderTop(Camera cam) {
        sortObjects(true);
        for (GameObject go : onTopObject) {
            go.render(cam.getXOffEffect(), cam.getYOffEffect());
        }
    }

    public void sortObjects(boolean top) {
        if (top) {
            Collections.sort(onTopObject, new ObjectsComparator());
        } else {
            Collections.sort(depthObj, new ObjectsComparator());
        }
    }

    public void addObj(GameObject go) {
        if (go.getClass() != MyPlayer.class) {
            if (go.isEmitter()) {
                emitters.add(go);
            }
            if (go.getClass() == MyMob.class) {
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
        if (go.getClass() != MyPlayer.class) {
            if (go.isEmitter()) {
                emitters.remove(go);
            }
            if (go.getClass() == MyMob.class) {
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double SCALE() {
        return settings.SCALE;
    }

    private class ObjectsComparator implements Comparator<GameObject> {

        @Override
        public int compare(GameObject o1, GameObject o2) {
            return o1.getDepth() - o2.getDepth();
        }

    }
}
