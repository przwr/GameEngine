/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.Game;
import game.Settings;
import game.place.cameras.Camera;
import game.gameobject.Mob;
import game.gameobject.Player;
import java.awt.Rectangle;
import java.util.ArrayList;
import game.gameobject.GameObject;
import game.place.cameras.PlayersCamera;
import java.nio.ByteBuffer;
import engine.Physics;
import engine.FontsHandler;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public abstract class Place {

    public Game game;
    public Settings settings;

    private final int lightTex;

    public ArrayList<Mob> sMobs = new ArrayList<>();
    public ArrayList<Mob> fMobs = new ArrayList<>();
    public ArrayList<GameObject> solidObj = new ArrayList<>();
    public ArrayList<GameObject> flatObj = new ArrayList<>();
    public ArrayList<GameObject> players = new ArrayList<>();
    public ArrayList<GameObject> emitters = new ArrayList<>();

    public FontsHandler fonts;

    public final int width;
    public final int height;
    public final int sTile;
    public float r;
    public float g;
    public float b;
//    public Camera cam1;
//    public Camera cam2;
//    public Camera cam3;
//    public Camera cam4;
    public Camera[] cams = new Camera[4];

    public final Tile[] tiles;

    public Place(Game game, int width, int height, int sTile, Settings settings) {
        this.width = width;
        this.height = height;
        this.sTile = sTile;
        this.settings = settings;
        tiles = new Tile[width / sTile * height / sTile];
        fonts = null;
        this.game = game;
        lightTex = makeTexture(null, 2048, 2048);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addCamera(GameObject go, int ssX, int ssY, int num) {
        this.cams[num] = new PlayersCamera(this, go, ssX, ssY);
    }

//    public void addCamera2(GameObject go, int ssX, int ssY) {
//        this.cam2 = new PlayersCamera(this, go, ssX, ssY);
//    }
//
//    public void addCamera3(GameObject go, int ssX, int ssY) {
//        this.cam3 = new PlayersCamera(this, go, ssX, ssY);
//    }
//
//    public void addCamera4(GameObject go, int ssX, int ssY) {
//        this.cam4 = new PlayersCamera(this, go, ssX, ssY);
//    }

    public abstract void generate();

    public abstract void update();

    public void shakeCam(Camera cam) {
        cam.shake();
    }

    public void render() {
        Camera cam;
        for (GameObject player : players) {
            cam = (((Player) player).getCam());
            float camXStart = 0f;
            float camXSize = 0f;
            float camYStart = 0f;
            float camYSize = 0f;
            if (players.size() == 2) {
                if (settings.hSplitScreen) {
                    if (player == players.get(0)) {
                        glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                        camXStart = 0f;
                        camXSize = 0f;
                        camYStart = 0.5f;
                        camYSize = 0.5f;
                    } else {
                        glViewport(0, 0, Display.getWidth(), Display.getHeight() / 2);
                        camXStart = 0f;
                        camXSize = 0f;
                        camYStart = 0f;
                        camYSize = 0.5f;
                    }
                } else {
                    if (player == players.get(0)) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                        camXStart = 0f;
                        camXSize = 0f;
                        camYStart = 0f;
                        camYSize = 0f;
                    } else {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight());
                        camXStart = 0.5f;
                        camXSize = 0.0f;
                        camYStart = 0f;
                        camYSize = 0f;
                    }
                }
            } else if (players.size() == 3) {
                if (settings.hSplitScreen) {
                    if (player == players.get(0)) {
                        glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                        camXStart = 0f;
                        camXSize = 0f;
                        camYStart = 0.5f;
                        camYSize = 0.5f;
                    } else if (player == players.get(1)) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                        camXStart = 0f;
                        camXSize = 0f;
                        camYStart = 0f;
                        camYSize = 0.5f;
                    } else if (player == players.get(2)) {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        camXStart = 0.5f;
                        camXSize = 0f;
                        camYStart = 0f;
                        camYSize = 0.5f;
                    }
                } else {
                    if (player == players.get(0)) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                        camXStart = 0f;
                        camXSize = 0f;
                        camYStart = 0f;
                        camYSize = 0f;
                    } else if (player == players.get(1)) {
                        glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                        camXStart = 0.5f;
                        camXSize = 0f;
                        camYStart = 0f;
                        camYSize = 0f;
                    } else if (player == players.get(2)) {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        camXStart = 0.5f;
                        camXSize = 0f;
                        camYStart = 0f;
                        camYSize = 0.5f;
                    }
                }
            } else if (players.size() == 4) {
                if (player == players.get(0)) {
                    glViewport(0, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                    glOrtho(-0.5, 0.5, -0.5, 0.5, 1.0, -1.0);
                    camXStart = 0.0f;
                    camXSize = 0.0f;
                    camYStart = 0.5f;
                    camYSize = 0.5f;
                } else if (player == players.get(1)) {
                    glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                    camXStart = 0.5f;
                    camXSize = 0f;
                    camYStart = 0f;
                    camYSize = 0f;
                } else if (player == players.get(2)) {
                    glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    camXStart = 0f;
                    camXSize = 0f;
                    camYStart = 0f;
                    camYSize = 0.5f;
                } else if (player == players.get(3)) {
                    glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    camXStart = 0.5f;
                    camXSize = 0f;
                    camYStart = 0f;
                    camYSize = 0.5f;
                }
            }
            preRenderLights(cam, camXStart, camYStart, camXSize, camYSize);
            glColor3f(r, g, b);
            renderBack(cam);
            renderObj(cam);
            renderText(cam);
            renderLights();
        }
    }

    protected void renderBack(Camera cam) {
        for (int y = 0; y < height / sTile; y++) {
            for (int x = 0; x < width / sTile; x++) {
                Tile t = tiles[x + y * height / sTile];
                if (t != null) {
                    t.render(0, cam.getXOffEffect() + x * sTile, cam.getYOffEffect() + y * sTile);
                }
            }
        }
    }

    protected void renderObj(Camera cam) {
        renderBottom(cam);
        renderTop(cam);
    }

    protected abstract void renderText(Camera cam);

    protected void preRenderLights(Camera cam, float xStart, float yStart, float xSize, float ySize) {
        glBlendFunc(GL_ONE, GL_ONE);
        for (GameObject emitter : emitters) {
            if (emitter.isEmits()) {
                emitter.renderLight(this, cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject player : players) {
            if (player.isEmitter() && player.isEmits()) {
                player.renderLight(this, cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        frameSave(lightTex, xStart, yStart, xSize, ySize);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    protected void renderLights() {
        float brightness = Math.max(b, Math.max(r, g));
        float strength = 6 - (int) (10 * brightness);
        float val;
        if (strength <= 2) {
            strength = 2;
            val = 1.00f - 0.95f * brightness;
        } else {
            strength = 3;
            val = 1.00f - 1.5f * brightness;
        }
        glColor3f(val, val, val);
        glBlendFunc(GL_DST_COLOR, GL_ONE);
        for (int i = 0; i < strength; i++) {
            drawQuad(lightTex, Display.getWidth(), Display.getHeight());
        }
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void renderMessage(int i, int x, int y, String ms, Color color) {
        fonts.write(i).drawString(x - fonts.write(i).getWidth(ms) / 2, y - (4 * fonts.write(i).getHeight()) / 3, ms, color);
    }

    private void renderBottom(Camera cam) {
        for (GameObject go : flatObj) {
            if (!go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject go : solidObj) {
            if (!go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject go : fMobs) {
            if (!go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject go : sMobs) {
            if (!go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject go : players) {
            if (!go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
    }

    private void renderTop(Camera cam) {
        for (GameObject go : flatObj) {
            if (go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject go : solidObj) {
            if (go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject go : fMobs) {
            if (go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject go : sMobs) {
            if (go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject go : players) {
            if (go.isOnTop()) {
                go.render(cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
    }

    public static int allocateTexture() {
        int textureHandle = glGenTextures();
        return textureHandle;
    }

    public static int makeTexture(ByteBuffer pixels, int w, int h) {
        int textureHandle = allocateTexture();
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); //GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); //GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        return textureHandle;
    }

    public void frameSave(int txtrHandle, float xStart, float yStart, float xSize, float ySize) {
        int w = Display.getWidth();
        int h = Display.getHeight();
        glColor4f(1, 1, 1, 1);
        glReadBuffer(GL_BACK);
        glBindTexture(GL_TEXTURE_2D, txtrHandle);
        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, (int) (w * xSize), (int) (h * ySize), (int) (xStart * w), (int) (yStart * h), w, h);
    }

    public static void drawQuad(int textureHandle, float w, float h) {
        glPushMatrix();
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glBegin(GL_QUADS);
        glTexCoord2f(0, h / 2048.0f);
        glVertex2f(0, 0);
        glTexCoord2f(w / 2048.0f, h / 2048.0f);
        glVertex2f(w, 0);
        glTexCoord2f(w / 2048.0f, 0);
        glVertex2f(w, h);
        glTexCoord2f(0, 0);
        glVertex2f(0, h);
        glEnd();
        glPopMatrix();
    }

    protected void addObj(GameObject go) {
        if (go.isEmitter()) {
            emitters.add(go);
        }
        if (go.getClass() == Mob.class) {
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
    
    public boolean isObjCTl(int magX, int magY, GameObject go) {
        int ySizeInTiles = height / sTile;
        int xBegInTile = (go.getX() + go.getSX()) / sTile;
        int yBegInTile = (go.getY() + go.getSY()) / sTile;
        int xEndInTile = ((go.getWidth() % sTile != 0) ? 1 : 0) + xBegInTile + go.getWidth() / sTile;
        int yEndInTile = ((go.getHeight() % sTile != 0) ? 1 : 0) + yBegInTile + go.getHeight() / sTile;
        Rectangle rec = new Rectangle();
        for (int i = xBegInTile - 1; i <= xEndInTile; i++) {
            try {
                if (tiles[i + (yBegInTile - 1) * ySizeInTiles].isSolid()) {
                    rec.setRect(i * sTile, (yBegInTile - 1) * sTile, sTile, sTile);
                    if (Physics.checkCollision(rec, go, 0, magY) != null) {
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                if (tiles[i + (yEndInTile) * ySizeInTiles].isSolid()) {
                    rec.setRect(i * sTile, (yEndInTile) * sTile, sTile, sTile);
                    if (Physics.checkCollision(rec, go, 0, magY) != null) {
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        for (int i = yBegInTile - 1; i <= yEndInTile; i++) {
            try {
                if (tiles[(xBegInTile - 1) + i * ySizeInTiles].isSolid()) {
                    rec.setRect((xBegInTile - 1) * sTile, i * sTile, sTile, sTile);
                    if (Physics.checkCollision(rec, go, magX, 0) != null) {
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                if (tiles[(xEndInTile) + i * ySizeInTiles].isSolid()) {
                    rec.setRect((xEndInTile) * sTile, i * sTile, sTile, sTile);
                    if (Physics.checkCollision(rec, go, magX, 0) != null) {
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        return false;
    }

    public boolean isPlCObj(int magX, int magY, Player player) {
        Rectangle rec = new Rectangle();
        for (GameObject go : sMobs) {
            rec.setRect(go.getBegOfX(), go.getBegOfY(), go.getWidth(), go.getHeight());
            if (Physics.checkCollision(rec, player, magX, magY) != null) {
                return true;
            }
        }
        for (GameObject go : solidObj) {
            rec.setRect(go.getBegOfX(), go.getBegOfY(), go.getWidth(), go.getHeight());
            if (Physics.checkCollision(rec, player, magX, magY) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isObjCObj(int magX, int magY, GameObject gameObject) {
        Rectangle rec = new Rectangle();
        for (GameObject player : players) {
            rec.setRect(player.getBegOfX(), player.getBegOfY(), player.getWidth(), player.getHeight());
            if (Physics.checkCollision(rec, gameObject, magX, magY) != null) {
                return true;
            }
        }
        for (GameObject go : sMobs) {
            if (gameObject != go) {
                rec.setRect(go.getBegOfX(), go.getBegOfY(), go.getWidth(), go.getHeight());
                if (Physics.checkCollision(rec, gameObject, magX, magY) != null) {
                    return true;
                }
            }
        }
        for (GameObject go : solidObj) {
            if (gameObject != go) {
                rec.setRect(go.getBegOfX(), go.getBegOfY(), go.getWidth(), go.getHeight());
                if (Physics.checkCollision(rec, gameObject, magX, magY) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
