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
import engine.SoundBase;
import engine.Point;
import engine.Sprite;
import game.Methods;
import org.lwjgl.opengl.Display;
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

    public Game game;
    public Settings settings;
    protected final SoundBase sounds;
    protected final SpriteBase sprites;

    private final int lightTex;
    private int savedShadowed;
    protected int[] shadows, lights;
    Point center;
    Point[] tempPoints = new Point[4];
    Point[] points = new Point[4];
    Sprite sprb = new Sprite("rockb", 64, 64);
    Sprite sprw = new Sprite("rockw", 64, 64);
    Sprite alpha = new Sprite("alpha", 2048, 2048);
    int shDif = 1024;
    int shP1 = 0;
    int shP2 = 2;
    int shX, shY;
    float camXStart, camXSize, camYStart, camYSize;
    double angle, temp, al1, bl1, al2, bl2;

    public ArrayList<Mob> sMobs = new ArrayList<>();
    public ArrayList<Mob> fMobs = new ArrayList<>();
    public ArrayList<GameObject> solidObj = new ArrayList<>();
    public ArrayList<GameObject> flatObj = new ArrayList<>();
    public ArrayList<GameObject> players = new ArrayList<>();
    public ArrayList<GameObject> emitters = new ArrayList<>();

    public FontsHandler fonts;

    public final int width, height, sTile;
    public float r, g, b;
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
        emptyTex = settings.emptyTex;
        sounds = new SoundBase();
        sprites = new SpriteBase();
        for (int i = 0; i < 4; i++) {
            points[i] = new Point(0, 0);
            tempPoints[i] = new Point(0, 0);
        }
        center = new Point(0, 0);
    }

    public abstract void generate();

    public abstract void update();

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addCamera(GameObject go, int ssX, int ssY, int num) {
        this.cams[num] = new PlayersCamera(this, go, ssX, ssY);
    }

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
    
    public abstract void generate();

    public abstract void update();

    public void render() {
        Camera cam;
        for (GameObject player : players) {
            cam = (((Player) player).getCam());

            if (players.size() == 2) {
                if (settings.hSplitScreen) {
                    if (player == players.get(0)) {
                        glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                        camXStart = camXSize = 0f;
                        camYStart = camYSize = 0.5f;
                    } else {
                        glViewport(0, 0, Display.getWidth(), Display.getHeight() / 2);
                        camXStart = camXSize = camYStart = 0f;
                        camYSize = 0.5f;
                    }
                } else {
                    if (player == players.get(0)) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                        camXStart = camXSize = camYStart = camYSize = 0f;
                    } else {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight());
                        camXStart = 0.5f;
                        camXSize = camYStart = camYSize = 0f;
                    }
                }
            } else if (players.size() == 3) {
                if (settings.hSplitScreen) {
                    if (player == players.get(0)) {
                        glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                        camXStart = camXSize = 0f;
                        camYStart = camYSize = 0.5f;
                    } else if (player == players.get(1)) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                        camXStart = camXSize = camYStart = 0f;
                        camYSize = 0.5f;
                    } else if (player == players.get(2)) {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        camXStart = camYSize = 0.5f;
                        camXSize = camYStart = 0f;
                    }
                } else {
                    if (player == players.get(0)) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                        camXStart = camXSize = camYStart = camYSize = 0f;
                    } else if (player == players.get(1)) {
                        glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                        camXStart = 0.5f;
                        camXSize = camYStart = camYSize = 0f;
                    } else if (player == players.get(2)) {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        camXStart = camYSize = 0.5f;
                        camXSize = camYStart = 0f;
                    }
                }
            } else if (players.size() == 4) {
                if (player == players.get(0)) {
                    glViewport(0, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                    glOrtho(-0.5, 0.5, -0.5, 0.5, 1.0, -1.0);
                    camXStart = camXSize = 0.0f;
                    camYStart = camYSize = 0.5f;
                } else if (player == players.get(1)) {
                    glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                    camXStart = 0.5f;
                    camXSize = camYStart = camYSize = 0f;
                } else if (player == players.get(2)) {
                    glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    camXStart = camXSize = camYStart = 0f;
                    camYSize = 0.5f;
                } else if (player == players.get(3)) {
                    glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    camXStart = camYSize = 0.5f;
                    camXSize = camYStart = 0f;
                }
            }

            preRenderShadows(cam, camXStart, camYStart, camXSize, camYSize);
            preRenderLights(cam, camXStart, camYStart, camXSize, camYSize);
            preRenderShadowedLights(cam, camXStart, camYStart, camXSize, camYSize);
            glColor3f(r, g, b);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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

    protected void preRenderShadows(Camera cam, float xStart, float yStart, float xSize, float ySize) {
        glBlendFunc(GL_ONE, GL_ONE);
        int nr = 0;
        for (GameObject emitter : emitters) {
            if (emitter.isEmits()) {
//                ... jak u graczy
            }
        }
        for (GameObject player : players) {
            if (player.isEmitter() && player.isEmits()) {
                glDisable(GL_BLEND);
                glColor3f(1f, 1f, 1f);
                alpha.render();
                {
//              dla każdego obiektu rzucającego światło
                    calculateShadow(player, tiles[6 + 6 * height / sTile], 384, 384);
                    drawShadow(cam);
                }
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                glColor3f(1f, 1f, 1f);
                {
//              dla każdego obiektu rzucającego światło
                    tiles[6 + 6 * height / sTile].renderShadow(384, 384, cam.getXOffEffect(), cam.getYOffEffect(), player.getMidY() > 416);
                }
                frameSave(shadows[nr++], xStart, yStart, xSize, ySize);
            }
        }
    }

    private void calculateShadow(GameObject src, GameObject shade, int xS, int yS) {
        center.set(src.getMidX(), src.getMidY());
        tempPoints[0].set(xS, yS);
        tempPoints[1].set(xS + sTile, yS);
        tempPoints[2].set(xS, yS + sTile);
        tempPoints[3].set(xS + sTile, yS + sTile);
        angle = 0;
        for (int p = 0; p < 4; p++) {
            for (int s = p + 1; s < 4; s++) {
                temp = Methods.ThreePointAngle(tempPoints[p].getX(), tempPoints[p].getY(), tempPoints[s].getX(), tempPoints[s].getY(), center.getX(), center.getY());
                if (temp > angle) {
                    angle = temp;
                    shP1 = p;
                    shP2 = s;
                }
            }
        }
        points[0] = tempPoints[shP1];
        points[1] = tempPoints[shP2];
        
        if (points[0].getX() == center.getX()) {
            points[2].set(points[0].getX(), points[0].getY() + (points[0].getY() > center.getY() ? shDif : -shDif));
        } else if (points[0].getY() == center.getY()) {
            points[2].set(points[0].getX() + (points[0].getX() > center.getX() ? shDif : -shDif), points[0].getY());
        } else {
            al1 = ((double) center.getY() - (double) points[0].getY()) / ((double) center.getX() - (double) points[0].getX());
            bl1 = (double) points[0].getY() - al1 * (double) points[0].getX();
            if (al1 > 0) {
                shX = points[0].getX() + (points[0].getY() > center.getY() ? shDif : -shDif);
                shY = (int) (al1 * (double) shX + bl1);
            } else if (al1 < 0) {
                shX = points[0].getX() + (points[0].getY() > center.getY() ? -shDif : shDif);
                shY = (int) (al1 * (double) shX + bl1);
            } else {
                shX = points[0].getX();
                shY = points[0].getY() + (points[0].getY() > center.getY() ? shDif : -shDif);
            }
            points[2].set(shX, shY);
        }

        if (points[1].getX() == center.getX()) {
            points[3].set(points[1].getX(), points[1].getY() + (points[1].getY() > center.getY() ? shDif : -shDif));
        } else if (points[1].getY() == center.getY()) {
            points[3].set(points[1].getX() + (points[1].getX() > center.getX() ? shDif : -shDif), points[1].getY());
        } else {
            al2 = ((double) center.getY() - (double) points[1].getY()) / ((double) center.getX() - (double) points[1].getX());
            bl2 = (double) points[1].getY() - al2 * (double) points[1].getX();
            if (al2 > 0) {
                shX = points[1].getX() + (points[1].getY() > center.getY() ? shDif : -shDif);
                shY = (int) (al2 * (double) shX + bl2);
            } else if (al2 < 0) {
                shX = points[1].getX() + (points[1].getY() > center.getY() ? -shDif : shDif);
                shY = (int) (al2 * (double) shX + bl2);
            } else {
                shX = points[1].getX();
                shY = points[1].getY() + (points[1].getY() > center.getY() ? shDif : -shDif);
            }
            points[3].set(shX, shY);
        }
    }

    public void drawShadow(Camera cam) {
        glColor3f(0, 0, 0);
        glDisable(GL_BLEND);
        glPushMatrix();
        glTranslatef(cam.getXOffEffect(), cam.getYOffEffect(), 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glVertex2f(points[1].getX(), points[1].getY());
        glEnd();
        glPopMatrix();
        glEnable(GL_BLEND);
    }

    public static void clearScreen(float color) {
        glDisable(GL_BLEND);
        glColor3f(color, color, color);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, 2048);
        glVertex2f(2048, 2048);
        glVertex2f(2048, 0);
        glEnd();
        glEnable(GL_BLEND);
    }

    protected void preRenderLights(Camera cam, float xStart, float yStart, float xSize, float ySize) {
        int nr = 0;
        for (GameObject emitter : emitters) {
            if (emitter.isEmits()) {
//                ... jak u graczy
            }
        }
        for (GameObject player : players) {
            if (player.isEmitter() && player.isEmits()) {
                clearScreen(0);
                glColor3f(1, 1, 1);
                glBlendFunc(GL_ONE, GL_ONE);
                player.renderLight(this, cam.getXOffEffect(), cam.getYOffEffect());
                glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                drawTex(shadows[nr], Display.getWidth(), Display.getHeight());
                frameSave(lights[nr++], xStart, yStart, xSize, ySize);
            }
        }
        savedShadowed = nr;
    }

    protected void preRenderShadowedLights(Camera cam, float xStart, float yStart, float xSize, float ySize) {
        clearScreen(0);
        glColor3f(1, 1, 1);
        glBlendFunc(GL_ONE, GL_ONE);
        for (int i = 0; i < savedShadowed; i++) {
            drawTex(lights[i], Display.getWidth(), Display.getHeight());
        }
        frameSave(lightTex, xStart, yStart, xSize, ySize);
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
            drawTex(lightTex, Display.getWidth(), Display.getHeight());
        }
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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

    public static void frameSave(int txtrHandle, float xStart, float yStart, float xSize, float ySize) {
        int w = Display.getWidth();
        int h = Display.getHeight();
        glColor3f(1, 1, 1);
        glReadBuffer(GL_BACK);
        glBindTexture(GL_TEXTURE_2D, txtrHandle);
        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, (int) (w * xSize), (int) (h * ySize), (int) (xStart * w), (int) (yStart * h), w, h);
    }

    public static void drawTex(int textureHandle, float w, float h) {
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

    public void makeShadows() {
        shadows = new int[emitters.size() + players.size()];
        for (int i = 0; i < emitters.size() + players.size(); i++) {
            shadows[i] = makeTexture(null, 2048, 2048);
        }
        lights = new int[emitters.size() + players.size()];
        for (int i = 0; i < emitters.size() + players.size(); i++) {
            lights[i] = makeTexture(null, 2048, 2048);
        }
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
