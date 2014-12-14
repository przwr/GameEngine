/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.gameobject.GameObject;
import game.gameobject.AbstractPlayer;
import game.place.cameras.Camera;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glOrtho;
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

    private static final int w = Display.getWidth(), h = Display.getHeight(), w1o2 = (w >> 1), h1o2 = (h >> 1);
    private static FBORenderer fbFrame;
    private static GameObject light;
    private static final int[] SX = new int[7], EX = new int[7], SY = new int[7], EY = new int[7];
    private static boolean isVisible;
    private static int lightX, lightY;
    private static float lightColor, lightBrightness, lightStrength;
    private static Camera cam;
    private static final drawBorder[] borders = new drawBorder[5];
    private static final resetOrtho[] orthos = new resetOrtho[5];

    public static void findVisibleLights(AbstractPlace place, int playersLength) {
        readyVarsToFindLights(place);
        for (GameObject light : place.emitters) {
            for (int p = 0; p < playersLength; p++) {
                if (place.singleCam && playersLength > 1) {
                    if (light.isEmits() && SY[2 + playersLength] <= light.getY() + (light.getLight().getSY() >> 1) && EY[2 + playersLength] >= light.getY() - (light.getLight().getSY() >> 1)
                            && SX[2 + playersLength] <= light.getX() + (light.getLight().getSX() >> 1) && EX[2 + playersLength] >= light.getX() - (light.getLight().getSX() >> 1)) {
                        isVisible = true;
                        place.cams[playersLength - 2].visibleLights[place.cams[playersLength - 2].nrVLights++] = light;
                    }
                } else {
                    for (int pi = 0; pi < playersLength; pi++) {
                        if (light.isEmits() && SY[pi] <= light.getY() + (light.getLight().getSY() >> 1) && EY[pi] >= light.getY() - (light.getLight().getSY() >> 1)
                                && SX[pi] <= light.getX() + (light.getLight().getSX() >> 1) && EX[pi] >= light.getX() - (light.getLight().getSX() >> 1)) {
                            isVisible = true;
                            (((AbstractPlayer) place.players[pi]).getCam()).visibleLights[(((AbstractPlayer) place.players[pi]).getCam()).nrVLights++] = light;
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
            if (place.singleCam && playersLength > 1) {
                if (light.isEmits() && SY[2 + playersLength] <= light.getY() + (light.getLight().getSY() >> 1) && EY[2 + playersLength] >= light.getY() - (light.getLight().getSY() >> 1)
                        && SX[2 + playersLength] <= light.getX() + (light.getLight().getSX() >> 1) && EX[2 + playersLength] >= light.getX() - (light.getLight().getSX() >> 1)) {
                    isVisible = true;
                    place.cams[playersLength - 2].visibleLights[place.cams[playersLength - 2].nrVLights++] = light;
                }
            } else {
                for (int pi = 0; pi < playersLength; pi++) {
                    if (light.isEmits() && SY[pi] <= light.getY() + (light.getLight().getSY() >> 1) && EY[pi] >= light.getY() - (light.getLight().getSY() >> 1)
                            && SX[pi] <= light.getX() + (light.getLight().getSX() >> 1) && EX[pi] >= light.getX() - (light.getLight().getSX() >> 1)) {
                        isVisible = true;
                        (((AbstractPlayer) place.players[pi]).getCam()).visibleLights[(((AbstractPlayer) place.players[pi]).getCam()).nrVLights++] = light;
                    }
                }
            }
            if (isVisible) {
                place.visibleLights[place.nrVLights++] = light;
                isVisible = false;
            }
        }
    }

    private static void readyVarsToFindLights(AbstractPlace place) {
        for (int p = 0; p < place.getPlayersLenght(); p++) {
            cam = (((AbstractPlayer) place.players[p]).getCam());
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
    }

    public static void preRendLights(AbstractPlace place) {
        if (!place.settings.shadowOff) {
            for (int l = 0; l < place.nrVLights; l++) {
                ShadowRenderer.preRendLight(place, l);
            }
        }
    }

    public static void preRenderShadowedLights(AbstractPlace place, Camera cam) {
        fbFrame.activate();
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(1, 1, 1);
        glBlendFunc(GL_ONE, GL_ONE);
        for (int i = 0; i < cam.nrVLights; i++) {
            if (!place.settings.shadowOff) {
                drawLight(cam.visibleLights[i].getLight().fbo.getTexture(), cam.visibleLights[i], cam);
            } else {
                cam.visibleLights[i].getLight().render(cam.visibleLights[i], place, cam.getXOffEffect(), cam.getYOffEffect());
            }
        }
        fbFrame.deactivate();
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
        lightBrightness = FastMath.max(b, FastMath.max(r, g));
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

    public static void initVariables(AbstractPlace place) {
        ShadowRenderer.initVariables(place);
        fbFrame = new FBORendererRegular(w, h, place.settings);
        borders[0] = new drawBorder() {
            @Override
            public void draw() {
                glBegin(GL_QUADS);
                glVertex2f(0, h1o2 - 1);
                glVertex2f(0, h1o2 + 1);
                glVertex2f(w, h1o2 + 1);
                glVertex2f(w, h1o2 - 1);
                glEnd();
            }
        };
        borders[1] = new drawBorder() {
            @Override
            public void draw() {
                glBegin(GL_QUADS);
                glVertex2f(w1o2 - 1, 0);
                glVertex2f(w1o2 - 1, h);
                glVertex2f(w1o2 + 1, h);
                glVertex2f(w1o2 + 1, 0);
                glEnd();
            }
        };
        initBorders();
    }

    private static void initBorders() {
        borders[2] = new drawBorder() {
            @Override
            public void draw() {
                glBegin(GL_QUADS);
                glVertex2f(0, h1o2 - 1);
                glVertex2f(0, h1o2 + 1);
                glVertex2f(w, h1o2 + 1);
                glVertex2f(w, h1o2 - 1);
                glEnd();
                glBegin(GL_QUADS);
                glVertex2f(w1o2 - 1, h1o2);
                glVertex2f(w1o2 - 1, h);
                glVertex2f(w1o2 + 1, h);
                glVertex2f(w1o2 + 1, h1o2);
                glEnd();
            }
        };
        borders[3] = new drawBorder() {
            @Override
            public void draw() {
                glBegin(GL_QUADS);
                glVertex2f(w1o2, h1o2 - 1);
                glVertex2f(w1o2, h1o2 + 1);
                glVertex2f(w, h1o2 + 1);
                glVertex2f(w, h1o2 - 1);
                glEnd();
                glBegin(GL_QUADS);
                glVertex2f(w1o2 - 1, 0);
                glVertex2f(w1o2 - 1, h);
                glVertex2f(w1o2 + 1, h);
                glVertex2f(w1o2 + 1, 0);
                glEnd();
            }
        };
        borders[4] = new drawBorder() {
            @Override
            public void draw() {
                glBegin(GL_QUADS);
                glVertex2f(0, h1o2 - 1);
                glVertex2f(0, h1o2 + 1);
                glVertex2f(w, h1o2 + 1);
                glVertex2f(w, h1o2 - 1);
                glEnd();
                glBegin(GL_QUADS);
                glVertex2f(w1o2 - 1, 0);
                glVertex2f(w1o2 - 1, h);
                glVertex2f(w1o2 + 1, h);
                glVertex2f(w1o2 + 1, 0);
                glEnd();
            }
        };

        orthos[0] = new resetOrtho() {
            @Override
            public void reset() {
                glOrtho(-1.0, 1.0, -2.0, 2.0, 1.0, -1.0);
            }
        };
        orthos[1] = new resetOrtho() {
            @Override
            public void reset() {
                glOrtho(-2.0, 2.0, -1.0, 1.0, 1.0, -1.0);
            }
        };
        orthos[2] = orthos[3] = orthos[4] = new resetOrtho() {
            @Override
            public void reset() {
                glOrtho(-2.0, 2.0, -2.0, 2.0, 1.0, -1.0);
            }
        };
    }

    public static void border(int ssMode) {
        glViewport(0, 0, w, h);
        if (ssMode != 0) {
            glBlendFunc(GL_ZERO, GL_ZERO);
            glColor3f(0, 0, 0);
            borders[ssMode - 1].draw();
        }
    }

    public static void resetOrtho(int ssMode) {
        if (ssMode != 0) {
            orthos[ssMode - 1].reset();
        }
    }

    private interface drawBorder {

        void draw();
    }

    private Renderer() {
    }

    private interface resetOrtho {

        void reset();
    }
}
