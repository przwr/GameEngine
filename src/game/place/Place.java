/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.Game;
import game.place.cameras.Camera;
import game.place.cameras.CameraFor2V;
import game.place.cameras.CameraFor1;
import game.gameobject.Mob;
import game.gameobject.Player;
import java.awt.Rectangle;
import java.util.ArrayList;
import game.gameobject.GameObject;
import game.place.cameras.CameraFor2H;
import game.place.cameras.CameraFor4;
import openGLEngine.Physics;
import openGLEngine.FontsHandler;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public abstract class Place {

    public Game game;

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
    public Camera cam1;
    public Camera cam2;
    public Camera cam3;
    public Camera cam4;

    public final Tile[] tiles;

    public Place(Game game, int width, int height, int sTile) {
        this.width = width;
        this.height = height;
        this.sTile = sTile;
        tiles = new Tile[width / sTile * height / sTile];
        fonts = null;
        this.game = game;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addCamera1For1(GameObject go, int x, int y) {
        this.cam1 = new CameraFor1(this, go, x, y);
    }

    public void addCamera1For2V(GameObject go, int x, int y) {
        this.cam1 = new CameraFor2V(this, go, x, y);
    }

    public void addCamera1For2H(GameObject go, int x, int y) {
        this.cam1 = new CameraFor2H(this, go, x, y);
    }

    public void addCamera2For2H(GameObject go, int x, int y) {
        this.cam2 = new CameraFor2H(this, go, x, y);
    }

    public void addCamera2For2V(GameObject go, int x, int y) {
        this.cam2 = new CameraFor2V(this, go, x, y);
    }

    public void addCamera1For4(GameObject go, int x, int y) {
        this.cam1 = new CameraFor4(this, go, x, y);
    }

    public void addCamera2For4(GameObject go, int x, int y) {
        this.cam2 = new CameraFor4(this, go, x, y);
    }

    public void addCamera3For4(GameObject go, int x, int y) {
        this.cam3 = new CameraFor4(this, go, x, y);
    }

    public void addCamera4For4(GameObject go, int x, int y) {
        this.cam4 = new CameraFor4(this, go, x, y);
    }

    public abstract void generate();

    public abstract void update();

    public void moveCam(int xPos, int yPos, Camera cam) {
        cam.move(xPos, yPos);
    }

    public void shakeCam(Camera cam) {
        cam.shake();
    }

    public void render() {
        Camera cam;
        for (GameObject player : players) {
            cam = (((Player) player).getCam());
            if (players.size() == 2) {
                if (game.splitMode) {
                    if (player == players.get(0)) {
                        glViewport(0, 0, Display.getWidth(), Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                    } else {
                        glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                    }
                } else {
                    if (player == players.get(0)) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                    } else {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight());
                    }
                }
            } else if (players.size() == 3) {
                if (game.splitMode) {
                    if (player == players.get(0)) {
                        glViewport(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                    } else if (player == players.get(1)) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                    } else if (player == players.get(2)) {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                    }
                } else {
                    if (player == players.get(0)) {
                        glViewport(0, 0, Display.getWidth() / 2, Display.getHeight());
                        glOrtho(-0.5, 0.5, -1.0, 1.0, 1.0, -1.0);
                    } else if (player == players.get(1)) {
                        glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                        glOrtho(-1.0, 1.0, -0.5, 0.5, 1.0, -1.0);
                    } else if (player == players.get(2)) {
                        glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                    }
                }
            } else if (players.size() == 4) {
                if (player == players.get(0)) {
                    glViewport(0, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                    glOrtho(-0.5, 0.5, -0.5, 0.5, 1.0, -1.0);
                } else if (player == players.get(1)) {
                    glViewport(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight() / 2);
                } else if (player == players.get(2)) {
                    glViewport(0, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                } else if (player == players.get(3)) {
                    glViewport(Display.getWidth() / 2, 0, Display.getWidth() / 2, Display.getHeight() / 2);
                }
            }
            glColor3f(r, g, b);
            renderBack(cam);
            renderObj(cam);
            renderLights(cam);
            renderText(cam);
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

    protected void renderLights(Camera cam) {
        glBlendFunc(GL_DST_COLOR, GL_ONE);
        for (GameObject emitter : emitters) {
            if (emitter.isEmits()) {
                emitter.renderLight(this, cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        for (GameObject player : players) {
            if (player.isEmitter() && player.isEmits()) {
                player.renderLight(this, cam);
            }
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
        Player thisPl = null;
        for (GameObject go : players) {
            if (!go.isOnTop()) {
                if (((Player) go).getCam() != cam) {
                    go.render(cam.getXOffEffect() - getXOff(((Player) go).getCam()), cam.getYOffEffect() - getYOff(((Player) go).getCam()));
                } else {
                    thisPl = (Player) go;
                }
            }
        }
        if (thisPl != null) {
            thisPl.render(cam.getXOffEffect() - getXOff(((Player) thisPl).getCam()), cam.getYOffEffect() - getYOff(((Player) thisPl).getCam()));
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
        Player thisPl = null;
        for (GameObject go : players) {
            if (go.isOnTop()) {
                if (((Player) go).getCam() != cam) {
                    go.render(cam.getXOffEffect() - getXOff(((Player) go).getCam()), cam.getYOffEffect() - getYOff(((Player) go).getCam()));
                } else {
                    thisPl = (Player) go;
                }
            }
        }
        if (thisPl != null) {
            thisPl.render(cam.getXOffEffect() - getXOff(((Player) thisPl).getCam()), cam.getYOffEffect() - getYOff(((Player) thisPl).getCam()));
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

    public boolean isPlCTl(int magX, int magY, GameObject go, Camera cam) {
        int ySizeInTiles = height / sTile;
        int xBeg = (go.getX() + go.getSX() + -cam.getXOff()) / sTile;
        int yBeg = (go.getY() + go.getSY() + -cam.getYOff()) / sTile;
        int xEnd = ((go.getWidth() % sTile != 0) ? 1 : 0) + xBeg + go.getWidth() / sTile;
        int yEnd = ((go.getHeight() % sTile != 0) ? 1 : 0) + yBeg + go.getHeight() / sTile;
        Rectangle rec = new Rectangle();
        for (int i = xBeg - 1; i <= xEnd; i++) {
            try {
                if (tiles[i + (yBeg - 1) * ySizeInTiles].isSolid()) {
                    rec.setRect(i * sTile + cam.getXOff(), (yBeg - 1) * sTile + cam.getYOff(), sTile, sTile);
                    if (Physics.checkCollision(rec, go, 0, magY) != null) {
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                if (tiles[i + (yEnd) * ySizeInTiles].isSolid()) {
                    rec.setRect(i * sTile + cam.getXOff(), (yEnd) * sTile + cam.getYOff(), sTile, sTile);
                    if (Physics.checkCollision(rec, go, 0, magY) != null) {
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        for (int i = yBeg - 1; i <= yEnd; i++) {
            try {
                if (tiles[(xBeg - 1) + i * ySizeInTiles].isSolid()) {
                    rec.setRect((xBeg - 1) * sTile + cam.getXOff(), i * sTile + cam.getYOff(), sTile, sTile);
                    if (Physics.checkCollision(rec, go, magX, 0) != null) {
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            try {
                if (tiles[(xEnd) + i * ySizeInTiles].isSolid()) {
                    rec.setRect((xEnd) * sTile + cam.getXOff(), i * sTile + cam.getYOff(), sTile, sTile);
                    if (Physics.checkCollision(rec, go, magX, 0) != null) {
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
            }
        }
        return false;
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
        Camera cam = player.getCam();
        Rectangle rec = new Rectangle();
        for (GameObject go : sMobs) {
            rec.setRect(go.getBegOfX() + cam.getXOff(), go.getBegOfY() + cam.getYOff(), go.getWidth(), go.getHeight());
            if (Physics.checkCollision(rec, player, magX, magY) != null) {
                return true;
            }
        }
        for (GameObject go : solidObj) {
            rec.setRect(go.getBegOfX() + cam.getXOff(), go.getBegOfY() + cam.getYOff(), go.getWidth(), go.getHeight());
            if (Physics.checkCollision(rec, player, magX, magY) != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isObjCObj(int magX, int magY, GameObject gameObject) {
        Rectangle rec = new Rectangle();
        for (GameObject player : players) {
            rec.setRect(player.getBegOfX() - ((Player) player).getCam().getXOff(), player.getBegOfY() - ((Player) player).getCam().getYOff(), player.getWidth(), player.getHeight());
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

    public int getXOff(Camera cam) {
        return cam.getXOff();
    }

    public int getYOff(Camera cam) {
        return cam.getYOff();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
