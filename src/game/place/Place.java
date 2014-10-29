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
import java.awt.Rectangle;
import java.util.ArrayList;
import game.gameobject.GameObject;
import game.place.cameras.PlayersCamera;
import engine.Physics;
import engine.FontsHandler;
import engine.SoundBase;
import game.place.cameras.FourPlayersCamera;
import game.place.cameras.ThreePlayersCamera;
import game.place.cameras.TwoPlayersCamera;
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

    public final int width, height, sTile;
    public float r, g, b;

    public Camera cam;
    public Camera[] cams = new Camera[4];
    public Camera[] camsfor2 = new Camera[4];
    public Camera camfor2;
    public Camera camfor3;
    public Camera camfor4;
    public boolean isSplit;
    public boolean changeSSMode;
    public int ssMode;

    float camXStart, camYStart;

    public ArrayList<Mob> sMobs = new ArrayList<>();
    public ArrayList<Mob> fMobs = new ArrayList<>();
    public ArrayList<GameObject> solidObj = new ArrayList<>();
    public ArrayList<GameObject> flatObj = new ArrayList<>();
    public GameObject[] players;
    public ArrayList<GameObject> emitters = new ArrayList<>();
    public ArrayList<Area> areas = new ArrayList<>();

    public FontsHandler fonts;

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

    public void addCamerasFor2(GameObject player1, GameObject player2) {
        camfor2 = new TwoPlayersCamera(this, player1, player2);
        camsfor2[0] = new PlayersCamera(this, player1, 2, 4, 0);
        camsfor2[1] = new PlayersCamera(this, player1, 4, 2, 0);
        camsfor2[2] = new PlayersCamera(this, player2, 2, 4, 1);
        camsfor2[3] = new PlayersCamera(this, player2, 4, 2, 1);
    }

    public void addCameraFor3(GameObject player1, GameObject player2, GameObject player3) {
        camfor3 = new ThreePlayersCamera(this, player1, player2, player3);
    }

    public void addCameraFor4(GameObject player1, GameObject player2, GameObject player3, GameObject player4) {
        camfor4 = new FourPlayersCamera(this, player1, player2, player3, player4);
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
        for (int p = 0; p < players.length; p++) {
            GameObject player = players[p];
            cam = (((MyPlayer) player).getCam());
            if (players.length > 1) {
                SplitScreen.setSplitScreen(this, player);
            }
            Renderer.preRenderShadowedLightsFBO(cam, camXStart, camYStart, p);
            renderBack(cam);
            renderObj(cam);
            renderText(cam);
            Renderer.renderLights(r, g, b, p);
        }
        Renderer.border(ssMode);
    }

    protected void renderBack(Camera cam) {
        glColor3f(r, g, b);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
