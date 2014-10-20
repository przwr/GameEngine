/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Point;
import game.Methods;
import game.gameobject.GameObject;
import game.place.cameras.Camera;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import sprites.Sprite;

/**
 *
 * @author przemek
 */
public class Renderer {

    private static final int lightTex = makeTexture(null, 2048, 2048);
    private static int savedShadowed;
    private static int[] shadows, lights;
    private static final Point center = new Point(0, 0);
    private static final Point[] tempPoints = new Point[4];
    private static final Point[] points = new Point[4];
    private static final Sprite sprb = new Sprite("rockb", 64, 64, null);
    private static final Sprite sprw = new Sprite("rockw", 64, 64, null);
    private static final Sprite alpha = new Sprite("alpha", 2048, 2048, null);
    private static final int shDif = 1024;
    private static int shP1 = 0;
    private static int shP2 = 2;
    private static int shX, shY;
    private static double angle, temp, al1, bl1, al2, bl2;

    public static void preRenderShadows(Camera cam, float xStart, float yStart, float xSize, float ySize, ArrayList<GameObject> emitters, ArrayList<GameObject> players) {
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
                    calculateShadow(player, null, 384, 384);
                    drawShadow(cam);
                }
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                glColor3f(1f, 1f, 1f);
                {
//              dla każdego obiektu rzucającego światło
                    //tiles[6 + 6 * height / sTile].renderShadow(384, 384, cam.getXOffEffect(), cam.getYOffEffect(), player.getMidY() > 416);
                    glPushMatrix();
                    glTranslatef(384 + cam.getXOffEffect(), 384 + cam.getYOffEffect(), 0);
                    if (player.getMidY() > 416) {
                        sprw.render();
                    } else {
                        sprb.render();
                    }
                    glPopMatrix();
                }
                frameSave(shadows[nr++], xStart, yStart, xSize, ySize);
            }
        }
    }

    private static void calculateShadow(GameObject src, GameObject shade, int xS, int yS) {
        center.set(src.getMidX(), src.getMidY());
        tempPoints[0].set(xS, yS + 32);
        tempPoints[1].set(xS + 64, yS + 32);
        tempPoints[2].set(xS, yS + 64);
        tempPoints[3].set(xS + 64, yS + 64);
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

    public static void drawShadow(Camera cam) {
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

    public static void preRenderLights(Camera cam, float xStart, float yStart, float xSize, float ySize, ArrayList<GameObject> emitters, ArrayList<GameObject> players, Place place) {
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
                player.renderLight(place, cam.getXOffEffect(), cam.getYOffEffect());
                glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                drawTex(shadows[nr], Display.getWidth(), Display.getHeight());
                frameSave(lights[nr++], xStart, yStart, xSize, ySize);
            }
        }
        savedShadowed = nr;
    }

    public static void preRenderShadowedLights(Camera cam, float xStart, float yStart, float xSize, float ySize) {
        clearScreen(0);
        glColor3f(1, 1, 1);
        glBlendFunc(GL_ONE, GL_ONE);
        for (int i = 0; i < savedShadowed; i++) {
            drawTex(lights[i], Display.getWidth(), Display.getHeight());
        }
        frameSave(lightTex, xStart, yStart, xSize, ySize);
    }

    public static void renderLights(float r, float g, float b) {
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

    public static void makeShadows(ArrayList<GameObject> emitters, ArrayList<GameObject> players) {
        shadows = new int[emitters.size() + players.size()];
        for (int i = 0; i < emitters.size() + players.size(); i++) {
            shadows[i] = makeTexture(null, 2048, 2048);
        }
        lights = new int[emitters.size() + players.size()];
        for (int i = 0; i < emitters.size() + players.size(); i++) {
            lights[i] = makeTexture(null, 2048, 2048);
        }
        for (int i = 0; i < 4; i++) {
            points[i] = new Point(0, 0);
            tempPoints[i] = new Point(0, 0);
        }
    }
}