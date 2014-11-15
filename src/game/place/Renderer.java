/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Area;
import collision.Figure;
import engine.Methods;
import engine.Point;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.cameras.Camera;
import java.util.Arrays;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 *
 * @author przemek
 */
public class Renderer {

    private static final int w = Display.getWidth(), h = Display.getHeight();
    private static FBORendererRegular fbFrame;
    private static final Figure[] shades = new Figure[4096];
    private static GameObject emitter, light;
    private static final Point center = new Point(0, 0);
    private static final Point[] tempPoints = new Point[32], points = new Point[4], leftWallPoints = new Point[4], rightWallPoints = new Point[4];
    private static final int[] SX = new int[7], EX = new int[7], SY = new int[7], EY = new int[7];
    private static boolean isLeftWall, isRightWall, leftWallColor, rightWallColor, isVisible;
    private static Figure shade, tmp, other, left, right;
    private static int nrShades, shDif, nrPoints, lightX, lightY, shP1, shP2, shX, shY, XL1, XL2, XR1, XR2, YL, YR;
    private static double angle, temp, al1, bl1, al2, bl2, XOL, XOL2, XOR, XOR2;
    private static float shadeColor, lightColor, lightBrightness, lightStrength;
    private static Camera cam;

    public static void findVisibleLights(Place place) {
        for (int p = 0; p < place.playersLength; p++) {
            cam = (((Player) place.players[p]).getCam());
            cam.nrVLights = 0;
            SX[p] = cam.getSX();
            EX[p] = cam.getEX();
            SY[p] = cam.getSY();
            EY[p] = cam.getEY();
        }
        for (int c = 0; c < 3; c++) {
            if (place.cams[c] != null) {
                cam = place.cams[c];
                cam.nrVLights = 0;
                SX[4 + c] = cam.getSX();            // 4 to maksymalna liczba graczy
                EX[4 + c] = cam.getEX();
                SY[4 + c] = cam.getSY();
                EY[4 + c] = cam.getEY();
            }
        }
        place.nrVLights = 0;
        for (GameObject light : place.emitters) {
            for (int p = 0; p < place.playersLength; p++) {
                if (place.singleCam && place.playersLength > 1) {
                    if (light.isEmits() && SY[2 + place.playersLength] <= light.getY() + (light.getLight().getSY() >> 1) && EY[2 + place.playersLength] >= light.getY() - (light.getLight().getSY() >> 1)
                            && SX[2 + place.playersLength] <= light.getX() + (light.getLight().getSX() >> 1) && EX[2 + place.playersLength] >= light.getX() - (light.getLight().getSX() >> 1)) {
                        isVisible = true;
                        place.cams[place.playersLength - 2].visibleLights[place.cams[place.playersLength - 2].nrVLights++] = light;
                    }
                } else {
                    for (int pi = 0; pi < place.playersLength; pi++) {
                        if (light.isEmits() && SY[pi] <= light.getY() + (light.getLight().getSY() >> 1) && EY[pi] >= light.getY() - (light.getLight().getSY() >> 1)
                                && SX[pi] <= light.getX() + (light.getLight().getSX() >> 1) && EX[pi] >= light.getX() - (light.getLight().getSX() >> 1)) {
                            isVisible = true;
                            (((Player) place.players[pi]).getCam()).visibleLights[(((Player) place.players[pi]).getCam()).nrVLights++] = light;
                        }
                    }
                }
                if (isVisible) {
                    place.visibleLights[place.nrVLights++] = light;
                    isVisible = false;
                }
            }
        }
        // Docelowo iteracja po graczach nie będzie potrzebna - nie będą oni źródłem światła, a raczej jakieś obiekty.
        for (int p = 0; p < place.playersLength; p++) {
            light = place.players[p];
            if (place.singleCam && place.playersLength > 1) {
                if (light.isEmits() && SY[2 + place.playersLength] <= light.getY() + (light.getLight().getSY() >> 1) && EY[2 + place.playersLength] >= light.getY() - (light.getLight().getSY() >> 1)
                        && SX[2 + place.playersLength] <= light.getX() + (light.getLight().getSX() >> 1) && EX[2 + place.playersLength] >= light.getX() - (light.getLight().getSX() >> 1)) {
                    isVisible = true;
                    place.cams[place.playersLength - 2].visibleLights[place.cams[place.playersLength - 2].nrVLights++] = light;
                }
            } else {
                for (int pi = 0; pi < place.playersLength; pi++) {
                    if (light.isEmits() && SY[pi] <= light.getY() + (light.getLight().getSY() >> 1) && EY[pi] >= light.getY() - (light.getLight().getSY() >> 1)
                            && SX[pi] <= light.getX() + (light.getLight().getSX() >> 1) && EX[pi] >= light.getX() - (light.getLight().getSX() >> 1)) {
                        isVisible = true;
                        (((Player) place.players[pi]).getCam()).visibleLights[(((Player) place.players[pi]).getCam()).nrVLights++] = light;
                    }
                }
            }
            if (isVisible) {
                place.visibleLights[place.nrVLights++] = light;
                isVisible = false;
            }
        }
    }

    public static void preRendLightsFBO(Place place) {
        for (int l = 0; l < place.nrVLights; l++) {
            emitter = place.visibleLights[l];
            findShades(emitter, place);
            emitter.getLight().fbo.activate();
            clearFBO(1);
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            for (int f = 0; f < nrShades; f++) {    //iteracja po Shades - tych co dają cień
                shade = shades[f];
                if (shade != emitter.getCollision()) {
                    if (shade.canGiveShadow()) {
                        calculateShadow(emitter, shade);
                        drawShadow(emitter);
                        calculateWalls(shade, emitter);
                        drawWalls(emitter);
                    }
                    shadeColor = (emitter.getY() - shade.getCentralY()) / (float) ((shade.getHeight() + emitter.getHeight()) / 2);
                    if (shade.getOwner().getClass() == Area.class) {
                        shade.getOwner().renderShadow((shade.getX()) + emitter.getLight().getSX() / 2 - (emitter.getX()),
                                shade.getY() + emitter.getLight().getSY() / 2 - (emitter.getY()) + h - emitter.getLight().getSY(), shade.canBeLit() && emitter.getY() >= shade.getCentralY(), shadeColor);
                    } else {
                        shade.getOwner().renderShadow(emitter.getLight().getSX() / 2 - (emitter.getX()),
                                emitter.getLight().getSY() / 2 - (emitter.getY()) + h - emitter.getLight().getSY(), shade.canBeLit() && emitter.getY() >= shade.getCentralY(), shadeColor);
                    }
                } else {
                    emitter.renderShadow(-emitter.getX() + emitter.getLight().getSX() / 2, -emitter.getY() + emitter.getLight().getSY() / 2 + h - emitter.getLight().getSY(), true, 1);
                }
            }
            glColor3f(1f, 1f, 1f);
            glBlendFunc(GL_DST_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            emitter.getLight().render(h - emitter.getLight().getSY());
            emitter.getLight().fbo.deactivate();
        }
    }

    private static void findShades(GameObject src, Place place) {
        // Powinno sortować według wysoskości - najpier te, które są najwyżej na planszy, a później coraz niższe,
        // obiekty tej samej wysokości powinny być renderowane w kolejności od najdalszych od źródła, do najbliższych.
        nrShades = 0;
        for (Area a : place.areas) {    //iteracja po Shades - tych co dają cień
            if (!a.isBorder()) {
                for (Figure f : a.getParts()) {
                    if ((Math.abs(f.getCentralY() - src.getY()) <= (src.getLight().getSY() >> 1) + (f.getHeight() >> 1))
                            && (Math.abs(f.getCentralX() - src.getX()) <= (src.getLight().getSX() >> 1) + (f.getWidth() >> 1))) {
                        shades[nrShades++] = f;
                        f.setDistFromLight(Math.abs(src.getX() - f.getCentralX()));
                    }
                }
            }
        }
        for (GameObject tile : place.foregroundTiles) {   // FGTiles muszą mieć Collision
            tmp = tile.getCollision();
            if (!((FGTile) tmp.getOwner()).isLightproof() && (Math.abs(tmp.getOwner().getY() - src.getY()) <= (src.getLight().getSY() >> 1) + (tmp.getOwner().getHeight() >> 1))
                    && (Math.abs(tmp.getOwner().getX() - src.getX()) <= (src.getLight().getSX() >> 1) + (tmp.getOwner().getWidth() >> 1))) {
                shades[nrShades++] = tmp;
                tmp.setDistFromLight(Math.abs(src.getX() - tmp.getCentralX()));
            }
        }
        for (GameObject go : place.depthObj) {   // FGTiles muszą mieć Collision
            tmp = go.getCollision();
            if ((Math.abs(tmp.getOwner().getY() - src.getY()) <= (src.getLight().getSY() >> 1) + (tmp.getOwner().getHeight() >> 1))
                    && (Math.abs(tmp.getOwner().getX() - src.getX()) <= (src.getLight().getSX() >> 1) + (tmp.getOwner().getWidth() >> 1))) {
                shades[nrShades++] = tmp;
                tmp.setDistFromLight(Math.abs(src.getX() - tmp.getCentralX()));
            }
        }
        Arrays.sort(shades, 0, nrShades);
//        System.out.println("Posortowana: ");
//        for (int i = 0; i < nrShades; i++) {
//            System.out.println("Y: " + shades[i].getCentralY() + " X: " + shades[i].getDistFromLight() + " - Klasa:" + shades[i].getOwner().getClass());
//        }
//        System.out.print("\n");
    }

    public static void preRenderShadowedLightsFBO(Camera cam) {
        fbFrame.activate();
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(1, 1, 1);
        glBlendFunc(GL_ONE, GL_ONE);
        for (int i = 0; i < cam.nrVLights; i++) {
            drawLight(cam.visibleLights[i].getLight().fbo.getTexture(), cam.visibleLights[i], cam);
        }
        fbFrame.deactivate();
    }

    private static void calculateShadow(GameObject src, Figure shade) {
        center.set(src.getX(), src.getY());
        nrPoints = shade.listPoints().length;
        System.arraycopy(shade.listPoints(), 0, tempPoints, 0, nrPoints);
        angle = 0;
        for (int p = 0; p < nrPoints; p++) {
            for (int s = p + 1; s < nrPoints; s++) {
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
        shDif = src.getLight().getSX() + src.getLight().getSY();
        if (points[0].getX() == center.getX()) {
            points[2].set(points[0].getX(), points[0].getY() + (points[0].getY() > center.getY() ? shDif : -shDif));
        } else if (points[0].getY() == center.getY()) {
            points[2].set(points[0].getX() + (points[0].getX() > center.getX() ? shDif : -shDif), points[0].getY());
        } else {
            al1 = (center.getY() - points[0].getY()) / (double) (center.getX() - points[0].getX());
            bl1 = points[0].getY() - al1 * points[0].getX();
            if (al1 > 0) {
                shX = points[0].getX() + (points[0].getY() > center.getY() ? shDif : -shDif);
                shY = (int) (al1 * shX + bl1);
            } else if (al1 < 0) {
                shX = points[0].getX() + (points[0].getY() > center.getY() ? -shDif : shDif);
                shY = (int) (al1 * shX + bl1);
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
            al2 = (center.getY() - points[1].getY()) / (double) (center.getX() - points[1].getX());
            bl2 = points[1].getY() - al2 * points[1].getX();
            if (al2 > 0) {
                shX = points[1].getX() + (points[1].getY() > center.getY() ? shDif : -shDif);
                shY = (int) (al2 * shX + bl2);
            } else if (al2 < 0) {
                shX = points[1].getX() + (points[1].getY() > center.getY() ? -shDif : shDif);
                shY = (int) (al2 * shX + bl2);
            } else {
                shX = points[1].getX();
                shY = points[1].getY() + (points[1].getY() > center.getY() ? shDif : -shDif);
            }
            points[3].set(shX, shY);
        }
    }

    private static void calculateWalls(Figure f, GameObject src) {
        left = right = null;
        for (int i = 0; i < nrShades; i++) {
            other = shades[i];
            if (f.getY() < src.getY() && other.canGiveShadow() && other != f && other.getY() < f.getY() && other.getEndY() < src.getY()) {
                XOL = ((other.getEndY() - bl1) / al1);
                XOL2 = ((other.getY() - bl1) / al1);
                XOR = ((other.getEndY() - bl2) / al2);
                XOR2 = ((other.getY() - bl2) / al2);
                if ((XOL >= other.getX() && XOL <= other.getEndX()) || (XOL2 >= other.getX() && XOL2 <= other.getEndX())) {
                    left = (left != null) ? (Math.abs(f.getY() - other.getY()) < Math.abs(f.getY() - left.getY())) ? other : left : other;
                }
                if ((XOR >= other.getX() && XOR <= other.getEndX()) || (XOR2 >= other.getX() && XOR2 <= other.getEndX())) {
                    right = (right != null) ? (Math.abs(f.getY() - other.getY()) < Math.abs(f.getY() - right.getY())) ? other : right : other;
                }
            }
        }
        if (left != null) {    //czy lewy koniec pada na ścianę?
            YL = left.getEndY();
            XL1 = (int) ((YL - bl1) / al1);
            if (Math.abs(al1) > 1 && XL1 > left.getX() && XL1 <= (left.getEndX())) { //dodaj światło
                XL2 = al1 > 0 ? left.getX() : left.getEndX();
                leftWallPoints[0].set(XL1, YL - left.getHeight());
                leftWallPoints[1].set(XL1, YL);
                leftWallPoints[2].set(XL2, YL);
                leftWallPoints[3].set(XL2, YL - left.getHeight());
                leftWallColor = isLeftWall = true;
//                System.out.println("L: Light");
            } else if (XL1 >= left.getX() && XL1 <= (left.getEndX())) { //dodaj cień
                XL2 = al1 > 0 ? left.getX() : left.getEndX();
                leftWallPoints[0].set(XL1, YL);
                leftWallPoints[1].set(XL1, YL - left.getHeight());
                leftWallPoints[2].set(XL2, YL - left.getHeight());
                leftWallPoints[3].set(XL2, YL);
                leftWallColor = false;
                isLeftWall = true;
//                System.out.println("L: Shadow");
            } else {
                left.getOwner().renderShadow((left.getX()) + src.getLight().getSX() / 2 - (src.getX()),
                        left.getY() + src.getLight().getSY() / 2 - (src.getY()) + h - src.getLight().getSY(), false, shadeColor);
//                System.out.println("L: Dark");
            }
        }
        if (right != null) {     //czy prawy koniec pada na ścianę?
            YR = right.getEndY();
            XR1 = (int) ((YR - bl2) / al2);
            if (Math.abs(al2) >= 1 && XR1 >= right.getX() && XR1 < (right.getEndX())) {     // dodaj światło
                XR2 = al2 > 0 ? right.getX() : right.getEndX();
                rightWallPoints[0].set(XR1, YR - right.getHeight());
                rightWallPoints[1].set(XR1, YR);
                rightWallPoints[2].set(XR2, YR);
                rightWallPoints[3].set(XR2, YR - right.getHeight());
                isRightWall = rightWallColor = true;
//                System.out.println("R: Light");
            } else if (XR1 >= right.getX() && XR1 <= (right.getEndX())) { //dodaj cień
                XR2 = al2 > 0 ? right.getX() : right.getEndX();
                rightWallPoints[0].set(XR1, YR);
                rightWallPoints[1].set(XR1, YR - right.getHeight());
                rightWallPoints[2].set(XR2, YR - right.getHeight());
                rightWallPoints[3].set(XR2, YR);
                rightWallColor = false;
                isRightWall = true;
//                System.out.println("R: Shadow");
            } else {
                right.getOwner().renderShadow((right.getX()) + src.getLight().getSX() / 2 - (src.getX()),
                        right.getY() + src.getLight().getSY() / 2 - (src.getY()) + h - src.getLight().getSY(), false, shadeColor);
//                System.out.println("R: Dark");
            }
        }
    }

    private static void drawWalls(GameObject emitter) {
        int lX = emitter.getLight().getSX();
        int lY = emitter.getLight().getSY();
        glDisable(GL_TEXTURE_2D);
        if (isRightWall) {
            if (rightWallColor) {
                glColor3f(1, 1, 1);
            } else {
                glColor3f(0, 0, 0);
            }
            glPushMatrix();
            glTranslatef(lX / 2 - emitter.getX(), lY / 2 - emitter.getY() + h - lY, 0);
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
            glTranslatef(lX / 2 - emitter.getX(), lY / 2 - emitter.getY() + h - lY, 0);
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
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0);
        glPushMatrix();
        glTranslatef(emitter.getLight().getSX() / 2 - emitter.getX(), lightY / 2 - emitter.getY() + h - lightY, 0);
        glBegin(GL_QUADS);
        glVertex2f(points[0].getX(), points[0].getY());
        glVertex2f(points[2].getX(), points[2].getY());
        glVertex2f(points[3].getX(), points[3].getY());
        glVertex2f(points[1].getX(), points[1].getY());
        glEnd();
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    public static void clearFBO(float color) {
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_ONE, GL_ZERO);
        glColor3f(color, color, color);
        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(0, h);
        glVertex2f(w, h);
        glVertex2f(w, 0);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }

    public static void drawLight(int textureHandle, GameObject emitter, Camera cam) {
        lightX = emitter.getLight().getSX();
        lightY = emitter.getLight().getSY();
        glPushMatrix();
        glTranslatef(emitter.getX() - emitter.getLight().getSX() / 2 + cam.getXOffEffect(), emitter.getY() - emitter.getLight().getSY() / 2 + cam.getYOffEffect(), 0);
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

//    private static void frameSave(int txtrHandle, float xStart, float yStart) {
//        glColor3f(1, 1, 1);
//        glReadBuffer(GL_BACK);
//        glBindTexture(GL_TEXTURE_2D, txtrHandle);
//        glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (int) (xStart * w), (int) (yStart * h), w, h);
//    }
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

    public static void initVariables(Place place) {
        for (int i = 0; i < 4; i++) {
            points[i] = new Point(0, 0);
            tempPoints[i] = new Point(0, 0);
            leftWallPoints[i] = new Point(0, 0);
            rightWallPoints[i] = new Point(0, 0);
        }
        fbFrame = new FBORendererRegular(w, h, place.settings);
    }

//    public static SimpleTexture generateWhiteTex(Sprite s, Settings settings) {
//        FBORendererRegular fbo = new FBORendererRegular(s.getWidth(), s.getHeight(), settings);
//        fbo.activate();
//        white.render();
//        Renderer.clearFBO(1, fbo);
//        glColor3f(1, 1, 1);
//        glEnable(GL_TEXTURE_2D);
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA); //rysowanie w kolorze czarnym
//        s.renderFull();
//        glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO); // odwraca kolor tĹ‚a jeĹ›li rysowane kolorem biaĹ‚ym
//        white.render();
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        fbo.deactivate();
//        return new SimpleTexture(fbo.getTexture(), s.getWidth(), s.getHeight());
//    }
    public static void border(int ssMode) {
        glViewport(0, 0, w, h);
        if (ssMode != 0) {
            glBlendFunc(GL_ZERO, GL_ZERO);
            glColor3f(0, 0, 0);
            if (ssMode == 1) {
                glBegin(GL_QUADS);
                glVertex2f(0, h / 4 - 1);
                glVertex2f(0, h / 4 + 1);
                glVertex2f(w, h / 4 + 1);
                glVertex2f(w, h / 4 - 1);
                glEnd();
            } else if (ssMode == 2) {
                glBegin(GL_QUADS);
                glVertex2f(w / 4 - 1, 0);
                glVertex2f(w / 4 - 1, h);
                glVertex2f(w / 4 + 1, h);
                glVertex2f(w / 4 + 1, 0);
                glEnd();
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
            }
        }
    }

    private Renderer() {
    }
}
