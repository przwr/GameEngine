/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Area;
import collision.Figure;
import engine.Point;
import game.Methods;
import game.gameobject.GameObject;
import game.place.cameras.Camera;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author przemek
 */
public class Renderer {

    private static final int w = Display.getWidth();
    private static final int h = Display.getHeight();
    private static final FBORendererRegular fbFrame = new FBORendererRegular(w, h);
    private static final Figure[] shades = new Figure[4096];
    private static int nrShades;
    private static int savedShadowed;
    private static GameObject[] activeEmitters;
    private static final Point center = new Point(0, 0);
    private static final Point[] tempPoints = new Point[4];
    private static final Point[] points = new Point[4];
    private static final Point[] leftWallPoints = new Point[4];
    private static final Point[] rightWallPoints = new Point[4];
    private static boolean isLeftWall;
    private static boolean isRightWall;
    private static boolean leftWallColor;
    private static boolean rightWallColor;
//    private static final double SCALE = ((int) (((double) h / 1024d / 0.03125)) * 0.03125) >= 1 ? 1 : (int) (((double) h / 1024d / 0.03125)) * 0.03125;
//    private static final Sprite sprb = new Sprite("rockb", (int) (SCALE * 64), (int) (SCALE * 64), null);
//    private static final Sprite sprw = new Sprite("rockw", (int) (SCALE * 64), (int) (SCALE * 64), null);
    private static int shP1 = 0;
    private static int shP2 = 2;
    private static int shX, shY;
    private static double angle, temp, al1, bl1, al2, bl2;
    private static float shadeColor;

    public static void preRendLightsFBO(Place place) {
        int nr = 0;

        for (GameObject emitter : place.emitters) {
            if (emitter.isEmits()) {
//                ... jak u graczy
            }
        }
        GameObject player;
        for (int p = 0; p < place.playersLength; p++) {
            player = place.players[p];
            if (player.isEmitter() && player.isEmits()) {
                findShades(player, place);
                player.getLight().fbo.activate();
                glDisable(GL_TEXTURE_2D);
                clearScreen(1);
                glEnable(GL_BLEND);
                glEnable(GL_TEXTURE_2D);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                for (int f = 0; f < nrShades; f++) {    //iteracja po Shades - tych co dają cień
                    calculateShadow(player, shades[f]);
                    drawShadow(player);
                    calculateWalls(shades[f], player);
                    drawWalls(player);
                    shadeColor = ((float) player.getMidY() - (float) shades[f].getCentralY()) / (shades[f].getHeight() - shades[f].getShadowHeight());
                    glColor3f(shadeColor, shadeColor, shadeColor);
                    shades[f].getOwner().renderShadow((shades[f].getX()) + player.getLight().getSX() / 2 - (player.getMidX()), shades[f].getY() + player.getLight().getSY() / 2 - (player.getMidY()) + h - player.getLight().getSY(), player.getMidY() > shades[f].getCentralY());
                }
                glColor3f(1f, 1f, 1f);
                glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                player.getLight().render(h - player.getLight().getSY());
                player.getLight().fbo.deactivate();
                activeEmitters[nr] = player; //zapisanie emittera do korespondującej tablicy
                nr++;
            }
        }
        savedShadowed = nr;
    }

    private static void findShades(GameObject src, Place place) {
        // Powinno sortować według wysoskości - najpier te, które są najwyżej na planszy, a później coraz niższe,
        // obiekty tej samej wysokości powinny być renderowane w kolejności od najdalszych od źródła, do najbliższych.
        int nr = 0;
        int dist = (int) (Math.sqrt(src.getLight().getSX() * src.getLight().getSX() + src.getLight().getSY() * src.getLight().getSY())) / 2;
        int distFromCenter;
        for (Area a : place.areas) {    //iteracja po Shades - tych co dają cień
            for (Figure f : a.parts) {
                distFromCenter = (int) ((Math.sqrt(src.getWidth() * src.getWidth() + src.getHeight() * src.getHeight())) / 2 + (Math.sqrt(f.getWidth() * f.getWidth() + f.getHeight() * f.getHeight())) / 2);
                if (Methods.PointDistance(f.getCentralX(), f.getCentralY(), src.getMidX(), src.getMidY()) < dist + distFromCenter) {
                    shades[nr++] = f;
                }
            }
        }
        for (GameObject shade : place.solidObj) {
            distFromCenter = (int) ((Math.sqrt(src.getWidth() * src.getWidth() + src.getHeight() * src.getHeight())) / 2 + (Math.sqrt(shade.getCollision().getWidth() * shade.getCollision().getWidth() + shade.getCollision().getHeight() * shade.getCollision().getHeight())) / 2);
            if (Methods.PointDistance(shade.getCollision().getCentralX(), shade.getCollision().getCentralY(), src.getMidX(), src.getMidY()) < dist + distFromCenter) {
                shades[nr++] = shade.getCollision();
            }
        }
        nrShades = nr;
        int i, j;
        Figure tmp;
        for (i = 1; i < nrShades; i++) {
            tmp = shades[i];
            for (j = i; j > 0 && isSmaller(shades[j - 1], shades[j], src); j--) {
                shades[j] = shades[j - 1];
            }
            shades[j] = tmp;
        }
    }

    private static boolean isSmaller(Figure checked, Figure temp, GameObject src) {
        // if (Methods.PointDistance(src.getMidX(), src.getMidY(), checked.getX() + checked.getWidth() / 2, checked.getY() + checked.getHeight() / 2) > Methods.PointDistance(src.getMidX(), src.getMidY(), temp.getX() + temp.getWidth() / 2, temp.getY() + temp.getHeight() / 2)) {
        if (checked.getCentralY() > temp.getCentralY()) {
            return true;
        } else if (checked.getCentralY() == temp.getCentralY() && Methods.PointDistance(src.getMidX(), src.getMidY(), checked.getX() + checked.getWidth() / 2, checked.getY() + checked.getHeight() / 2) > Methods.PointDistance(src.getMidX(), src.getMidY(), temp.getX() + temp.getWidth() / 2, temp.getY() + temp.getHeight() / 2)) {
            return true;
        }
        return false;
    }

    public static void preRenderShadowedLightsFBO(Camera cam) {
        fbFrame.activate();
        clearScreen(0);
        glColor3f(1, 1, 1);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        for (int i = 0; i < savedShadowed; i++) {
            drawLight(activeEmitters[i].getLight().fbo.getTexture(), w, h, activeEmitters[i], cam);
        }
        fbFrame.deactivate();
    }

    private static void calculateShadow(GameObject src, Figure shade) {
        center.set(src.getMidX(), src.getMidY());
        tempPoints[0].set(shade.getX(), shade.getY() + shade.getShadowHeight());
        tempPoints[1].set(shade.getX() + shade.getWidth(), shade.getY() + shade.getShadowHeight());
        tempPoints[2].set(shade.getX(), shade.getY() + shade.getHeight());
        tempPoints[3].set(shade.getX() + shade.getWidth(), shade.getY() + shade.getHeight());
//        System.arraycopy(shade.getCollision().listPoints(), 0, tempPoints, 0, 4);
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

        int shDif = (int) (Math.sqrt(src.getLight().getSX() * src.getLight().getSX() + src.getLight().getSY() * src.getLight().getSY()));

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

    private static void calculateWalls(Figure f, GameObject src) {
        Figure other;
        Figure left = null;
        Figure right = null;
        int XL1 = 0;
        int YL = 0;
        for (int i = 0; i < nrShades; i++) {
            other = shades[i];
            if (other != f && other.getShadowHeight() != 0 && other.getY() < f.getY() && other.getY() + other.getHeight() < src.getMidY() && f.getY() + f.getHeight() < src.getMidY()) {
                double XO = ((other.getY() + other.getHeight() - bl1) / al1);
                double XO2 = ((other.getY() + other.getHeight() - other.getShadowHeight() - bl1) / al1);
                if ((XO > other.getX() && XO < (other.getX() + other.getWidth())) || (XO2 >= other.getX() && XO2 <= (other.getX() + other.getWidth()))) {
                    if (left != null) {
                        if (Math.abs(f.getY() - other.getY()) < Math.abs(f.getY() - left.getY())) {
                            left = other;
                        }
                    } else {
                        left = other;
                    }
                }
            }
        }
        for (int i = 0; i < nrShades; i++) {
            other = shades[i];
            if (other != f && other.getShadowHeight() != 0 && other.getY() < f.getY() && other.getY() + other.getHeight() < src.getMidY() && f.getY() + f.getHeight() < src.getMidY()) {
                double XO = ((other.getY() + other.getHeight() - bl2) / al2);
                if (XO > other.getX() && XO < (other.getX() + other.getWidth())) {
                    if (right != null) {
                        if (Math.abs(f.getY() - other.getY()) < Math.abs(f.getY() - right.getY())) {
                            right = other;
                        }
                    } else {
                        right = other;
                    }
                }
            }
        }
        if (left != null) {
            YL = left.getY() + left.getHeight();
            XL1 = (int) ((YL - bl1) / al1);
        }
        if (left != null) {     //czy lewy koniec pada na ścianę?
            int hight = left.getHeight() - left.getShadowHeight();
            if (Math.abs(al1) >= 1 && XL1 >= left.getX() && XL1 <= (left.getX() + left.getWidth())) { //dodaj światło
                leftWallPoints[0].set(XL1, YL - hight);
                leftWallPoints[1].set(XL1, YL);
                int XL2 = al1 > 0 ? left.getX() : left.getX() + left.getWidth();
                leftWallPoints[2].set(XL2, YL);
                leftWallPoints[3].set(XL2, YL - hight);
                leftWallColor = true;
                isLeftWall = true;
            } else if (XL1 > left.getX() && XL1 < (left.getX() + left.getWidth())) { //dodaj cień
                leftWallPoints[0].set(XL1, YL);
                leftWallPoints[1].set(XL1, YL - hight);
                int XL2 = al1 > 0 ? left.getX() : left.getX() + left.getWidth();
                leftWallPoints[2].set(XL2, YL - hight);
                leftWallPoints[3].set(XL2, YL);
                leftWallColor = false;
                isLeftWall = true;
            } else {
                left.getOwner().renderShadow((left.getX()) + src.getLight().getSX() / 2 - (src.getMidX()), left.getY() + src.getLight().getSY() / 2 - (src.getMidY()) + h - src.getLight().getSY(), false);
            }
        }
        if (right != null) {     //czy prawy koniec pada na ścianę?
            int YR = right.getY() + right.getHeight();
            int hight = right.getHeight() - right.getShadowHeight();
            double XR1 = ((YR - bl2) / al2);
            if (Math.abs(al2) > 1 && XR1 >= right.getX() && XR1 <= (right.getX() + right.getWidth())) {// dodaj światło
                rightWallPoints[0].set((int) XR1, YR - hight);
                rightWallPoints[1].set((int) XR1, YR);
                int XR2 = al2 > 0 ? right.getX() : right.getX() + right.getWidth();
                rightWallPoints[2].set(XR2, YR);
                rightWallPoints[3].set(XR2, YR - hight);
                isRightWall = true;
            }
        }
    }

    public static void drawWalls(GameObject emitter) {
        glDisable(GL_TEXTURE_2D);
        int lX = emitter.getLight().getSX();
        int lY = emitter.getLight().getSY();
        if (isRightWall) {
            glColor3f(1, 1, 1);
            glPushMatrix();
            glTranslatef(lX / 2 - emitter.getMidX(), lY / 2 - emitter.getMidY() + h - lY, 0);
            glBegin(GL_QUADS);
            glVertex2f(rightWallPoints[0].getX(), rightWallPoints[0].getY());
            glVertex2f(rightWallPoints[1].getX(), rightWallPoints[1].getY());
            glVertex2f(rightWallPoints[2].getX(), rightWallPoints[2].getY());
            glVertex2f(rightWallPoints[3].getX(), rightWallPoints[3].getY());
            glEnd();
            glPopMatrix();
            isRightWall = false;
        }
        if (isLeftWall) {
            if (leftWallColor) {
                glColor3f(1, 1, 1);
            } else {
                glColor3f(0, 0, 0);
            }
            glPushMatrix();
            glTranslatef(lX / 2 - emitter.getMidX(), lY / 2 - emitter.getMidY() + h - lY, 0);
            glBegin(GL_QUADS);
            glVertex2f(leftWallPoints[0].getX(), leftWallPoints[0].getY());
            glVertex2f(leftWallPoints[1].getX(), leftWallPoints[1].getY());
            glVertex2f(leftWallPoints[2].getX(), leftWallPoints[2].getY());
            glVertex2f(leftWallPoints[3].getX(), leftWallPoints[3].getY());
            glEnd();
            glPopMatrix();
            isLeftWall = false;
        }
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawShadow(GameObject emitter, float color, int off) {
        int lX = emitter.getLight().getSX();
        int lY = emitter.getLight().getSY();
        glColor3f(color, color, color);
        glPushMatrix();
        glTranslatef(lX / 2 - emitter.getMidX(), lY / 2 - emitter.getMidY() + h - lY, 0);
        glBegin(GL_QUADS);
        if (points[0].getX() > points[1].getX()) {
            glVertex2f(points[0].getX() - off, points[0].getY());
            glVertex2f(points[2].getX(), points[2].getY());
            glVertex2f(points[3].getX(), points[3].getY());
            glVertex2f(points[1].getX() + off, points[1].getY());

        } else if (points[0].getX() < points[1].getX()) {
            glVertex2f(points[0].getX() + off, points[0].getY());
            glVertex2f(points[2].getX(), points[2].getY());
            glVertex2f(points[3].getX(), points[3].getY());
            glVertex2f(points[1].getX() - off, points[1].getY());
        } else {
            if (points[0].getY() > points[1].getY()) {
                glVertex2f(points[0].getX(), points[0].getY() - off);
                glVertex2f(points[2].getX(), points[2].getY());
                glVertex2f(points[3].getX(), points[3].getY());
                glVertex2f(points[1].getX(), points[1].getY() + off);
            } else {
                glVertex2f(points[0].getX(), points[0].getY() + off);
                glVertex2f(points[2].getX(), points[2].getY());
                glVertex2f(points[3].getX(), points[3].getY());
                glVertex2f(points[1].getX(), points[1].getY() - off);
            }
        }
        glEnd();
        glPopMatrix();
    }

    public static void drawShadow(GameObject emitter) {
        int lX = emitter.getLight().getSX();
        int lY = emitter.getLight().getSY();
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef(lX / 2 - emitter.getMidX(), lY / 2 - emitter.getMidY() + h - lY, 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glVertex2f(points[1].getX(), points[1].getY());
        glEnd();
        glPopMatrix();
//        if (isSmooth) {
//            glPushMatrix();
//            glTranslatef(lX / 2 - emitter.getMidX(), lY / 2 - emitter.getMidY() + h - lY, 0);
//            glEnable(GL_BLEND);
//            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//            glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);
//            glEnable(GL_POLYGON_SMOOTH);
//
//            glBegin(GL_LINES);
//            glVertex2f(points[0].getX(), points[0].getY());
//            glVertex2f(points[3].getX(), points[3].getY());
//            glEnd();
//
//            glBegin(GL_QUADS);
//            glVertex2f(points[0].getX(), points[0].getY());
//            glVertex2f(points[2].getX(), points[2].getY());
//            glVertex2f(points[3].getX(), points[3].getY());
//            glVertex2f(points[1].getX(), points[1].getY());
//            glEnd();
//
//            glDisable(GL_POLYGON_SMOOTH);
//            glDisable(GL_BLEND);
//            glPopMatrix();
//        }
    }

    public static void clearScreen(float color) {
        glDisable(GL_BLEND);
        glColor3f(color, color, color);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, h);
        glVertex2f(w, h);
        glVertex2f(w, 0);
        glEnd();
    }

    public static void drawLight(int textureHandle, float w, float h, GameObject emitter, Camera cam) {
        int lX = (int) emitter.getLight().getSX();
        int lY = (int) emitter.getLight().getSY();
        glPushMatrix();
        glTranslatef(emitter.getMidX() - emitter.getLight().getSX() / 2 + cam.getXOffEffect(), emitter.getMidY() - emitter.getLight().getSY() / 2 + cam.getYOffEffect(), 0);
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(1, 1);
        glVertex2f(lX, 0);
        glTexCoord2f(1, 0);
        glVertex2f(lX, lY);
        glTexCoord2f(0, 0);
        glVertex2f(0, lY);
        glEnd();
        glPopMatrix();
    }

    public static void renderLights(float r, float g, float b, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart, float xTEnd, float yTEnd) {
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
            drawTex(fbFrame.getTexture(), w, h, xStart, yStart, xEnd, yEnd, xTStart, yTStart, xTEnd, yTEnd);
        }
    }

    public static int allocateTexture() {
        int textureHandle = glGenTextures();
        return textureHandle;
    }

    public static void frameSave(int txtrHandle, float xStart, float yStart) {
        glColor3f(1, 1, 1);
        glReadBuffer(GL_BACK);
        glBindTexture(GL_TEXTURE_2D, txtrHandle);
        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (int) (xStart * w), (int) (yStart * h), w, h);
    }

    public static void drawTex(int textureHandle, float w, float h, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart, float xTEnd, float yTEnd) {
        glPushMatrix();
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glBegin(GL_QUADS);
        glTexCoord2f(xTStart, yTEnd);
        glVertex2f(xStart * w, yStart * h);
        glTexCoord2f(xTEnd, yTEnd);
        glVertex2f(xEnd * w, yStart * h);
        glTexCoord2f(xTEnd, yTStart);
        glVertex2f(xEnd * w, yEnd * h);
        glTexCoord2f(xTStart, yTStart);
        glVertex2f(xStart * w, yEnd * h);
        glEnd();
        glPopMatrix();
    }

    public static void initVariables(ArrayList<GameObject> emitters, GameObject[] players) {
        activeEmitters = new GameObject[emitters.size() + 4];
        for (int i = 0; i < 4; i++) {
            points[i] = new Point(0, 0);
            tempPoints[i] = new Point(0, 0);
        }
        for (int i = 0; i < 4; i++) {
            leftWallPoints[i] = new Point(0, 0);
            rightWallPoints[i] = new Point(0, 0);
        }
    }

    public static void border(int ssMode) {
        glViewport(0, 0, w, h);
        if (ssMode != 0) {
            glDisable(GL_BLEND);
            glColor3f(0, 0, 0);
            if (ssMode == 1) {
                glBegin(GL_QUADS);
                glVertex2f(0, h / 4 - 1);
                glVertex2f(0, h / 4 + 1);
                glVertex2f(w, h / 4 + 1);
                glVertex2f(w, h / 4 - 1);
                glEnd();
                glEnable(GL_BLEND);
            } else if (ssMode == 2) {
                glBegin(GL_QUADS);
                glVertex2f(w / 4 - 1, 0);
                glVertex2f(w / 4 - 1, h);
                glVertex2f(w / 4 + 1, h);
                glVertex2f(w / 4 + 1, 0);
                glEnd();
                glEnable(GL_BLEND);
            } else if (ssMode == 3) {
                glBegin(GL_QUADS);
                glVertex2f(0, h / 4 - 1);
                glVertex2f(0, h / 4 + 1);
                glVertex2f(w / 2, h / 4 + 1);
                glVertex2f(w / 2, h / 4 - 1);
                glEnd();
                glBegin(GL_QUADS);
                glVertex2f(w / 4 - 1, h / 4);
                glVertex2f(w / 4 - 1, h / 2);
                glVertex2f(w / 4 + 1, h / 2);
                glVertex2f(w / 4 + 1, h / 4);
                glEnd();
                glEnable(GL_BLEND);
            } else if (ssMode == 4) {
                glBegin(GL_QUADS);
                glVertex2f(w / 4, h / 4 - 1);
                glVertex2f(w / 4, h / 4 + 1);
                glVertex2f(w / 2, h / 4 + 1);
                glVertex2f(w / 2, h / 4 - 1);
                glEnd();
                glBegin(GL_QUADS);
                glVertex2f(w / 4 - 1, 0);
                glVertex2f(w / 4 - 1, h / 2);
                glVertex2f(w / 4 + 1, h / 2);
                glVertex2f(w / 4 + 1, 0);
                glEnd();
                glEnable(GL_BLEND);
            } else if (ssMode == 5) {
                glBegin(GL_QUADS);
                glVertex2f(0, h / 4 - 1);
                glVertex2f(0, h / 4 + 1);
                glVertex2f(w, h / 4 + 1);
                glVertex2f(w, h / 4 - 1);
                glEnd();
                glBegin(GL_QUADS);
                glVertex2f(w / 4 - 1, 0);
                glVertex2f(w / 4 - 1, h);
                glVertex2f(w / 4 + 1, h);
                glVertex2f(w / 4 + 1, 0);
                glEnd();
                glEnable(GL_BLEND);
            }
        }
    }
}
