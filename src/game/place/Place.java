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
import game.myGame.MyPlayer;
import java.awt.Rectangle;
import java.util.ArrayList;
import game.gameobject.GameObject;
import game.place.cameras.PlayersCamera;
import engine.Physics;
import engine.FontsHandler;
import engine.SoundBase;
import game.place.cameras.TwoPlayersCamera;
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
    public boolean isSplit;
    public boolean changeSS;
    public int ssMode;

    float camXStart, camYStart;

    public ArrayList<Mob> sMobs = new ArrayList<>();
    public ArrayList<Mob> fMobs = new ArrayList<>();
    public ArrayList<GameObject> solidObj = new ArrayList<>();
    public ArrayList<GameObject> flatObj = new ArrayList<>();
    public GameObject[] players;
    public ArrayList<GameObject> emitters = new ArrayList<>();

    public FontsHandler fonts;

    public final int width, height, sTile;
    public float r, g, b;
    public Camera[] cams = new Camera[4];
    public Camera[] camsfor2 = new Camera[4];
    public Camera camfor2;

    public final Tile[] tiles;

    public Place(Game game, int width, int height, int sTile, Settings settings) {
        this.width = width;
        this.height = height;
        this.sTile = sTile;
        this.settings = settings;
        tiles = new Tile[width / sTile * height / sTile];
        fonts = null;
        this.game = game;
        sounds = new SoundBase();
        sprites = new SpriteBase();
    }

    public abstract void generate();

    public abstract void update();

    public void addCamera(GameObject go, int ssX, int ssY, int num) {
        this.cams[num] = new PlayersCamera(this, go, ssX, ssY, num);
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

    public void render() {
        Renderer.preRendLightsFBO(camXStart, camXStart, this, emitters, players);
        Camera cam;
        for (int p = 0; p < players.length; p++) {
            GameObject player = players[p];
            cam = (((MyPlayer) player).getCam());
            if ((players.length == 2 && !isClose())) {
                cam = (((MyPlayer) player).getCam());
                if (settings.hSplitScreen) {
                    ssMode = 1;
                    if (player == players[0]) {
                        glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                        camYStart = camXStart = 0f;
                    } else {
                        glViewport(0, 0, Display.getWidth(), Display.getHeight() / 2);
                        camYStart = 0f;
                        camYStart = -0.5f;
                    }
                } else {
                    ssMode = 2;
                    if (player == players[0]) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                        camXStart = camYStart = 0f;
                    } else {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight());
                        camXStart = 0.5f;
                        camYStart = 0f;
                    }
                }
            } else if (players.length == 2 && settings.joinSS && isClose()) {
                cam = camfor2;
                ssMode = 0;
                if (player == players[0]) {
                    camYStart = camXStart = 0f;
                } else {
                    break;
                }
            } else if (players.length == 3) {
                if (settings.hSplitScreen) {
                    ssMode = 3;
                    if (player == players[0]) {
                        glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                        camXStart = camYStart = 0f;
                    } else if (player == players[1]) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                        camXStart = 0f;
                        camYStart = -0.5f;
                    } else if (player == players[2]) {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        camXStart = 0.5f;
                        camYStart = -0.5f;
                    }
                } else {
                    ssMode = 4;
                    if (player == players[0]) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                        camXStart = camYStart = 0f;
                    } else if (player == players[1]) {
                        glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                        camXStart = 0.5f;
                        camYStart = 0f;
                    } else if (player == players[2]) {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        camXStart = 0.5f;
                        camYStart = -0.5f;
                    }
                }
            } else if (players.length == 4) {
                ssMode = 5;
                if (player == players[0]) {
                    glViewport(0, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                    glOrtho(-0.5, 0.5, -0.5, 0.5, 1.0, -1.0);
                    camXStart = camYStart = 0.0f;
                } else if (player == players[1]) {
                    glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                    camXStart = 0.5f;
                    camYStart = 0f;
                } else if (player == players[2]) {
                    glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    camXStart = 0f;
                    camYStart = -0.5f;
                } else if (player == players[3]) {
                    glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    camXStart = 0.5f;
                    camYStart = -0.5f;
                }
            }
            Renderer.preRenderShadowedLightsFBO(cam, camXStart, camYStart, p);
            glColor3f(r, g, b);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            renderBack(cam);
            renderObj(cam);
            renderText(cam);
            Renderer.renderLights(r, g, b, p);
        }
        Renderer.border(ssMode);
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

    private boolean isClose() {
        if (settings.joinSS) {
            if (Math.abs(players[0].getMidX() - players[1].getMidX()) < Display.getWidth() / 2 && Math.abs(players[0].getMidY() - players[1].getMidY()) < Display.getHeight() / 2) {
                isSplit = false;
                return true;
            } else if (!isSplit) {
                if (Math.abs(players[0].getMidX() - players[1].getMidX()) < Math.abs(players[0].getMidY() - players[1].getMidY())) {
                    settings.hSplitScreen = true;
                    if (players[0].getMidY() > players[1].getMidY()) {
                        GameObject temp = players[0];
                        players[0] = players[1];
                        players[1] = temp;
                        camsfor2[0].setGo(players[0]);
                        camsfor2[1].setGo(players[0]);
                        camsfor2[2].setGo(players[1]);
                        camsfor2[3].setGo(players[1]);
                    }
                    ((MyPlayer) players[0]).addCamera(camsfor2[0]);
                    ((MyPlayer) players[1]).addCamera(camsfor2[2]);
                } else {
                    settings.hSplitScreen = false;
                    if (players[0].getMidX() > players[1].getMidX()) {
                        GameObject temp = players[0];
                        players[0] = players[1];
                        players[1] = temp;
                        camsfor2[0].setGo(players[0]);
                        camsfor2[1].setGo(players[0]);
                        camsfor2[2].setGo(players[1]);
                        camsfor2[3].setGo(players[1]);
                    }
                    ((MyPlayer) players[0]).addCamera(camsfor2[1]);
                    ((MyPlayer) players[1]).addCamera(camsfor2[3]);
                }
                ((PlayersCamera) camsfor2[0]).reInit(0);
                ((PlayersCamera) camsfor2[1]).reInit(0);
                ((PlayersCamera) camsfor2[2]).reInit(1);
                ((PlayersCamera) camsfor2[3]).reInit(1);
                ((MyPlayer) players[0]).getCam().update();
                ((MyPlayer) players[1]).getCam().update();
                isSplit = true;
            } else if (changeSS) {
                if (settings.hSplitScreen) {
                    settings.hSplitScreen = false;
                    if (players[0].getMidX() > players[1].getMidX()) {
                        GameObject temp = players[0];
                        players[0] = players[1];
                        players[1] = temp;
                        camsfor2[0].setGo(players[0]);
                        camsfor2[1].setGo(players[0]);
                        camsfor2[2].setGo(players[1]);
                        camsfor2[3].setGo(players[1]);
                    }
                    ((MyPlayer) players[0]).addCamera(camsfor2[1]);
                    ((MyPlayer) players[1]).addCamera(camsfor2[3]);

                } else {
                    settings.hSplitScreen = true;
                    if (players[0].getMidY() > players[1].getMidY()) {
                        GameObject temp = players[0];
                        players[0] = players[1];
                        players[1] = temp;
                        camsfor2[0].setGo(players[0]);
                        camsfor2[1].setGo(players[0]);
                        camsfor2[2].setGo(players[1]);
                        camsfor2[3].setGo(players[1]);
                    }
                    ((MyPlayer) players[0]).addCamera(camsfor2[0]);
                    ((MyPlayer) players[1]).addCamera(camsfor2[2]);
                }
                ((PlayersCamera) camsfor2[0]).reInit(0);
                ((PlayersCamera) camsfor2[1]).reInit(0);
                ((PlayersCamera) camsfor2[2]).reInit(1);
                ((PlayersCamera) camsfor2[3]).reInit(1);
                ((MyPlayer) players[0]).getCam().update();
                ((MyPlayer) players[1]).getCam().update();
                changeSS = false;
            }
        }
        return false;
    }

    public void addCamerasFor2(MyPlayer player1, MyPlayer player2) {
        camfor2 = new TwoPlayersCamera(this, player1, player2);
        camsfor2[0] = new PlayersCamera(this, player1, 2, 4, 0);
        camsfor2[1] = new PlayersCamera(this, player1, 4, 2, 0);
        camsfor2[2] = new PlayersCamera(this, player2, 2, 4, 1);
        camsfor2[3] = new PlayersCamera(this, player2, 4, 2, 1);
    }

    protected void renderObj(Camera cam) {
        renderBottom(cam);
        renderTop(cam);
    }

    protected abstract void renderText(Camera cam);

    public void makeShadows() {
        Renderer.initVariables(emitters, players);
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

    public boolean isPlCObj(int magX, int magY, MyPlayer player) {
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
