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
    private static GameObject[] activeEmitters;
    private static GameObject player;
    private static final Point center = new Point(0, 0);
    private static final Point[] tempPoints = new Point[4];
    private static final Point[] points = new Point[4];
    private static final Point[] leftWallPoints = new Point[4];
    private static final Point[] rightWallPoints = new Point[4];
    private static boolean isLeftWall, isRightWall, leftWallColor;
    private static Figure tmp, other, left, right;
    private static int nrShades, savedShadowed, shDif, dist, distFromCenter, lightX, lightY, shP1, shP2, shX, shY, XL1, XL2, XR1, XR2, YL, YR;
    private static double angle, temp, al1, bl1, al2, bl2, XOL, XO2, XOR;
    private static float shadeColor, lightColor, lightBrightness, lightStrength;

    public static void preRendLightsFBO(Place place) {
        savedShadowed = 0;
        for (GameObject emitter : place.emitters) {
            if (emitter.isEmits()) {
                findShades(emitter, place);
                emitter.getLight().fbo.activate();
                clearScreen(1);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                for (int f = 0; f < nrShades; f++) {    //iteracja po Shades - tych co dają cień
                    calculateShadow(emitter, shades[f]);
                    drawShadow(emitter);
                    calculateWalls(shades[f], emitter);
                    drawWalls(emitter);
                    shadeColor = (emitter.getMidY() - shades[f].getCentralY()) / (shades[f].getShadowHeight());
                    glColor3f(shadeColor, shadeColor, shadeColor);
                    shades[f].getOwner().renderShadow((shades[f].getX()) + emitter.getLight().getSX() / 2 - (emitter.getMidX()),
                            shades[f].getY() + emitter.getLight().getSY() / 2 - (emitter.getMidY()) + h - emitter.getLight().getSY(), emitter.getMidY() > shades[f].getCentralY());
                }
                glColor3f(1f, 1f, 1f);
                glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                emitter.getLight().render(h - emitter.getLight().getSY());
                emitter.getLight().fbo.deactivate();
                activeEmitters[savedShadowed] = emitter; //zapisanie emittera do korespondującej tablicy
                savedShadowed++;
            }
        }
        // Docelowo iteracja po graczach nie będzie potrzebna - nie będą oni źródłem światła, a raczej jakieś obiekty.
        for (int p = 0; p < place.playersLength; p++) {
            player = place.players[p];
            if (player.isEmitter() && player.isEmits()) {
                findShades(player, place);
                player.getLight().fbo.activate();
                clearScreen(1);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                for (int f = 0; f < nrShades; f++) {    //iteracja po Shades - tych co dają cień
                    calculateShadow(player, shades[f]);
                    drawShadow(player);
                    calculateWalls(shades[f], player);
                    drawWalls(player);
                    shadeColor = ((float) player.getMidY() - (float) shades[f].getCentralY()) / (shades[f].getShadowHeight());
                    glColor3f(shadeColor, shadeColor, shadeColor);
                    shades[f].getOwner().renderShadow((shades[f].getX()) + player.getLight().getSX() / 2 - (player.getMidX()),
                            shades[f].getY() + player.getLight().getSY() / 2 - (player.getMidY()) + h - player.getLight().getSY(), player.getMidY() > shades[f].getCentralY());
                }
                glColor3f(1f, 1f, 1f);
                glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                player.getLight().render(h - player.getLight().getSY());
                player.getLight().fbo.deactivate();
                activeEmitters[savedShadowed] = player; //zapisanie emittera do korespondującej tablicy
                savedShadowed++;
            }
        }
    }

    private static void findShades(GameObject src, Place place) {
        // Powinno sortować według wysoskości - najpier te, które są najwyżej na planszy, a później coraz niższe,
        // obiekty tej samej wysokości powinny być renderowane w kolejności od najdalszych od źródła, do najbliższych.
        nrShades = 0;
        dist = (int) (Math.sqrt(src.getLight().getSX() * src.getLight().getSX() + src.getLight().getSY() * src.getLight().getSY())) / 2;
        for (Area a : place.areas) {    //iteracja po Shades - tych co dają cień
            for (Figure f : a.parts) {
                distFromCenter = (int) ((Math.sqrt(src.getWidth() * src.getWidth() + src.getHeight() * src.getHeight())) / 2
                        + (Math.sqrt(f.getWidth() * f.getWidth() + f.getHeight() * f.getHeight())) / 2);
                if (Methods.PointDistance(f.getCentralX(), f.getCentralY(), src.getMidX(), src.getMidY()) < dist + distFromCenter) {
                    shades[nrShades++] = f;
                }
            }
        }
//        for (GameObject shade : place.solidObj) {
//            distFromCenter = (int) ((Math.sqrt(src.getWidth() * src.getWidth() + src.getHeight() * src.getHeight())) / 2
//                    + (Math.sqrt(shade.getCollision().getWidth() * shade.getCollision().getWidth() + shade.getCollision().getHeight() * shade.getCollision().getHeight())) / 2);
//            if (Methods.PointDistance(shade.getCollision().getCentralX(), shade.getCollision().getCentralY(), src.getMidX(), src.getMidY()) < dist + distFromCenter) {
//                shades[nrShades++] = shade.getCollision();
//            }
//        }
        for (int i = 1, j; i < nrShades; i++) {
            tmp = shades[i];
            for (j = i; j > 0 && isSmaller(shades[j - 1], shades[j], src); j--) {
                shades[j] = shades[j - 1];
            }
            shades[j] = tmp;
        }
    }

    private static boolean isSmaller(Figure checked, Figure temp, GameObject src) {
        if (checked.getCentralY() > temp.getCentralY()) {
            return true;
        } else if (checked.getCentralY() == temp.getCentralY()
                && Methods.PointDistance(src.getMidX(), src.getMidY(), checked.getX() + checked.getWidth() / 2, checked.getY() + checked.getHeight() / 2)
                > Methods.PointDistance(src.getMidX(), src.getMidY(), temp.getX() + temp.getWidth() / 2, temp.getY() + temp.getHeight() / 2)) {
            return true;
        }
        return false;
    }

    public static void preRenderShadowedLightsFBO(Camera cam) {
        fbFrame.activate();
        clearScreen(0);
        glColor3f(1, 1, 1);
        glBlendFunc(GL_ONE, GL_ONE);
        for (int i = 0; i < savedShadowed; i++) {
            drawLight(activeEmitters[i].getLight().fbo.getTexture(), w, h, activeEmitters[i], cam);
        }
        fbFrame.deactivate();
    }

    private static void calculateShadow(GameObject src, Figure shade) {
        center.set(src.getMidX(), src.getMidY());
        tempPoints[0].set(shade.getX(), shade.getY() + shade.getShadowHeight());
        tempPoints[1].set(shade.getEndX(), shade.getY() + shade.getShadowHeight());
        tempPoints[2].set(shade.getX(), shade.getEndY());
        tempPoints[3].set(shade.getEndX(), shade.getEndY());
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
        shDif = (int) (Math.sqrt(src.getLight().getSX() * src.getLight().getSX() + src.getLight().getSY() * src.getLight().getSY()));
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
        left = null;
        right = null;
        for (int i = 0; i < nrShades; i++) {
            other = shades[i];
            if (other != f && other.getShadowHeight() != 0 && other.getY() < f.getY() && other.getEndY() < src.getMidY() && f.getYOfShadow() < src.getMidY()) {
                XOL = ((other.getEndY() - bl1) / al1);
                XO2 = ((other.getYOfShadow() - bl1) / al1);
                XOR = ((other.getEndY() - bl2) / al2);
                if ((XOL > other.getX() && XOL < other.getEndX()) || (XO2 >= other.getX() && XO2 <= other.getEndX())) {
                    if (left != null) {
                        if (Math.abs(f.getY() - other.getY()) < Math.abs(f.getY() - left.getY())) {
                            left = other;
                        }
                    } else {
                        left = other;
                    }
                }
                if (XOR > other.getX() && XOR < other.getEndX()) {
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
        if (left != null) {     //czy lewy koniec pada na ścianę?
            YL = left.getEndY();
            XL1 = (int) ((YL - bl1) / al1);
            if (Math.abs(al1) >= 1 && XL1 >= left.getX() && XL1 <= (left.getEndX())) { //dodaj światło
                XL2 = al1 > 0 ? left.getX() : left.getEndX();
                leftWallPoints[0].set(XL1, YL - left.getShadowHeight());
                leftWallPoints[1].set(XL1, YL);
                leftWallPoints[2].set(XL2, YL);
                leftWallPoints[3].set(XL2, YL - left.getShadowHeight());
                leftWallColor = isLeftWall = true;
            } else if (XL1 > left.getX() && XL1 < (left.getEndX())) { //dodaj cień
                XL2 = al1 > 0 ? left.getX() : left.getEndX();
                leftWallPoints[0].set(XL1, YL);
                leftWallPoints[1].set(XL1, YL - left.getShadowHeight());
                leftWallPoints[2].set(XL2, YL - left.getShadowHeight());
                leftWallPoints[3].set(XL2, YL);
                leftWallColor = false;
                isLeftWall = true;
            } else {
                left.getOwner().renderShadow((left.getX()) + src.getLight().getSX() / 2 - (src.getMidX()),
                        left.getY() + src.getLight().getSY() / 2 - (src.getMidY()) + h - src.getLight().getSY(), false);
            }
        }
        if (right != null) {     //czy prawy koniec pada na ścianę?
            YR = right.getEndY();
            XR1 = (int) ((YR - bl2) / al2);
            if (Math.abs(al2) > 1 && XR1 >= right.getX() && XR1 <= (right.getEndX())) {     // dodaj światło
                XR2 = al2 > 0 ? right.getX() : right.getEndX();
                rightWallPoints[0].set(XR1, YR - right.getShadowHeight());
                rightWallPoints[1].set(XR1, YR);
                rightWallPoints[2].set(XR2, YR);
                rightWallPoints[3].set(XR2, YR - right.getShadowHeight());
                isRightWall = true;
            }
        }
    }

    private static void drawWalls(GameObject emitter) {
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

    private static void drawShadow(GameObject emitter) {
        lightY = emitter.getLight().getSY();
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef(emitter.getLight().getSX() / 2 - emitter.getMidX(), lightY / 2 - emitter.getMidY() + h - lightY, 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glVertex2f(points[1].getX(), points[1].getY());
        glEnd();
        glPopMatrix();
    }

    private static void clearScreen(float color) {
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glColor3f(color, color, color);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, h);
        glVertex2f(w, h);
        glVertex2f(w, 0);
        glEnd();
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
    }

    private static void drawLight(int textureHandle, float w, float h, GameObject emitter, Camera cam) {
        lightX = (int) emitter.getLight().getSX();
        lightY = (int) emitter.getLight().getSY();
        glPushMatrix();
        glTranslatef(emitter.getMidX() - emitter.getLight().getSX() / 2 + cam.getXOffEffect(), emitter.getMidY() - emitter.getLight().getSY() / 2 + cam.getYOffEffect(), 0);
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(1, 1);
        glVertex2f(lightX, 0);
        glTexCoord2f(1, 0);
        glVertex2f(lightX, lightY);
        glTexCoord2f(0, 0);
        glVertex2f(0, lightY);
        glEnd();
        glPopMatrix();
    }

    public static void renderLights(float r, float g, float b, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart, float xTEnd, float yTEnd) {
        lightBrightness = Math.max(b, Math.max(r, g));
        lightStrength = 6 - (int) (10 * lightBrightness);
        if (lightStrength <= 2) {
            lightStrength = 2;
            lightColor = 1.00f - 0.95f * lightBrightness;
        } else {
            lightStrength = 3;
            lightColor = 1.00f - 1.5f * lightBrightness;
        }
        glColor3f(lightColor, lightColor, lightColor);
        glBlendFunc(GL_DST_COLOR, GL_ONE);
        for (int i = 0; i < lightStrength; i++) {
            drawTex(fbFrame.getTexture(), w, h, xStart, yStart, xEnd, yEnd, xTStart, yTStart, xTEnd, yTEnd);
        }
    }

    private static void frameSave(int txtrHandle, float xStart, float yStart) {
        glColor3f(1, 1, 1);
        glReadBuffer(GL_BACK);
        glBindTexture(GL_TEXTURE_2D, txtrHandle);
        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (int) (xStart * w), (int) (yStart * h), w, h);
    }

    private static void drawTex(int textureHandle, float w, float h, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart, float xTEnd, float yTEnd) {
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
