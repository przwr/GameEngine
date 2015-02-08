/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.place.fbo.RegularFrameBufferObject;
import game.place.fbo.FrameBufferObject;
import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.Player;
import game.place.cameras.Camera;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author przemek
 */
public class Renderer {

    private static final int displayWidth = Display.getWidth(), displayHeight = Display.getHeight(), halfDisplayWidth = (displayWidth / 2), halfDisplayHeight = (displayHeight / 2);
    private static FrameBufferObject frame;
    private static GameObject light;
    private static final int[] xStart = new int[7], xEnd = new int[7], yStart = new int[7], yEnd = new int[7];
    private static boolean visible;
    private static int lightX, lightY;
    private static float lightColor, lightBrightness, lightStrength;
    private static Camera camera;
    private static final drawBorder[] borders = new drawBorder[5];
    private static final resetOrtho[] orthos = new resetOrtho[5];

    public static void findVisibleLights(Map map, int playersLength) {
        Place place = map.place;
        readyVarsToFindLights(map);
        map.getEmitters().stream().forEach((tmpLight) -> {
            for (int p = 0; p < playersLength; p++) {
                if (place.players[p].getMap() == map) {
                    if (place.singleCamera && playersLength > 1) {
                        if (tmpLight.isEmits() && yStart[2 + playersLength] <= tmpLight.getY() + (tmpLight.getLight().getHeight() / 2) && yEnd[2 + playersLength] >= tmpLight.getY() - (tmpLight.getLight().getHeight() / 2)
                                && xStart[2 + playersLength] <= tmpLight.getX() + (tmpLight.getLight().getWidth() / 2) && xEnd[2 + playersLength] >= tmpLight.getX() - (tmpLight.getLight().getWidth() / 2)) {
                            visible = true;
                            place.cameras[playersLength - 2].visibleLights[place.cameras[playersLength - 2].visibleLightsCount++] = tmpLight;
                        }
                    } else {
                        for (int pi = 0; pi < playersLength; pi++) {
                            if (place.players[pi].getMap() == map && tmpLight.isEmits() && yStart[pi] <= tmpLight.getY() + (tmpLight.getLight().getHeight() / 2) && yEnd[pi] >= tmpLight.getY() - (tmpLight.getLight().getHeight() / 2)
                                    && xStart[pi] <= tmpLight.getX() + (tmpLight.getLight().getWidth() / 2) && xEnd[pi] >= tmpLight.getX() - (tmpLight.getLight().getWidth() / 2)) {
                                visible = true;
                                (((Player) place.players[pi]).getCamera()).visibleLights[(((Player) place.players[pi]).getCamera()).visibleLightsCount++] = tmpLight;
                            }
                        }
                    }
                    if (visible) {
                        map.visibleLights.add(tmpLight);
                        visible = false;
                    }
                }
            }
        });
        // Docelowo iteracja po graczach nie będzie potrzebna - nie będą oni źródłem światła, a raczej jakieś obiekty.
        for (int p = 0; p < place.playersCount; p++) {
            light = place.players[p];
            if (light.getMap() == map) {
                if (place.singleCamera && playersLength > 1) {
                    if (light.isEmits() && yStart[2 + playersLength] <= light.getY() + (light.getLight().getHeight() / 2) && yEnd[2 + playersLength] >= light.getY() - (light.getLight().getHeight() / 2)
                            && xStart[2 + playersLength] <= light.getX() + (light.getLight().getWidth() / 2) && xEnd[2 + playersLength] >= light.getX() - (light.getLight().getWidth() / 2)) {
                        visible = true;
                        place.cameras[playersLength - 2].visibleLights[place.cameras[playersLength - 2].visibleLightsCount++] = light;
                    }
                } else {
                    for (int pi = 0; pi < playersLength; pi++) {
                        if (place.players[pi].getMap() == map && light.isEmits() && yStart[pi] <= light.getY() + (light.getLight().getHeight() / 2) && yEnd[pi] >= light.getY() - (light.getLight().getHeight() / 2)
                                && xStart[pi] <= light.getX() + (light.getLight().getWidth() / 2) && xEnd[pi] >= light.getX() - (light.getLight().getWidth() / 2)) {
                            visible = true;
                            (((Player) place.players[pi]).getCamera()).visibleLights[(((Player) place.players[pi]).getCamera()).visibleLightsCount++] = light;
                        }
                    }
                }
                if (visible) {
                    map.visibleLights.add(light);
                    visible = false;
                }
            }
        }
    }

    private static void readyVarsToFindLights(Map map) {
        Place place = map.place ;
        for (int p = 0; p < place.getPlayersCount(); p++) {
            if (map == place.players[p].getMap()) {
                camera = (((Player) place.players[p]).getCamera());
                camera.visibleLightsCount = 0;
                xStart[p] = camera.getXStart();
                xEnd[p] = camera.getXEnd();
                yStart[p] = camera.getYStart();
                yEnd[p] = camera.getYEnd();
            }
        }
        for (int c = 0; c < 3; c++) {
            if (place.cameras[c] != null) {
                camera = place.cameras[c];
                camera.visibleLightsCount = 0;
                xStart[4 + c] = camera.getXStart();            // 4 to maksymalna liczba graczy
                xEnd[4 + c] = camera.getXEnd();
                yStart[4 + c] = camera.getYStart();
                yEnd[4 + c] = camera.getYEnd();
            }
        }
        map.visibleLights.clear();
    }

    public static void preRendLights(Map map) {
        if (!Settings.shadowOff) {
            map.visibleLights.stream().forEach((emitter) -> {
                ShadowRenderer.preRendLight(map, emitter);
            });
        }
    }

    public static void preRenderShadowedLights(Place place, Camera cam) {
        frame.activate();
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(1, 1, 1);
        glBlendFunc(GL_ONE, GL_ONE);
        for (int i = 0; i < cam.visibleLightsCount; i++) {
            if (!Settings.shadowOff) {
                drawLight(cam.visibleLights[i].getLight().frameBufferObject.getTexture(), cam.visibleLights[i], cam);
            } else {
                cam.visibleLights[i].getLight().render(cam.visibleLights[i], place, cam.getXOffsetEffect(), cam.getYOffsetEffect());
            }
        }
        frame.deactivate();
    }

    public static void drawLight(int textureHandle, GameObject emitter, Camera cam) {
        lightX = emitter.getLight().getWidth();
        lightY = emitter.getLight().getHeight();
        glPushMatrix();
        glTranslatef(emitter.getX() - lightX / 2 + cam.getXOffsetEffect(), emitter.getY() - lightY / 2 + cam.getYOffsetEffect(), 0);
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
            drawTexture(frame.getTexture(), displayWidth, displayHeight, xStart, yStart, xEnd, yEnd, xTStart, yTStart, xTEnd, yTEnd);
        }
    }

    private static void drawTexture(int textureHandle, float w, float h, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart, float xTEnd, float yTEnd) {
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

    public static void initializeVariables() {
        ShadowRenderer.initializeRenderer();
        frame = new RegularFrameBufferObject(displayWidth, displayHeight);
        borders[0] = () -> {
            glBegin(GL_QUADS);
            glVertex2f(0, halfDisplayHeight - 1);
            glVertex2f(0, halfDisplayHeight + 1);
            glVertex2f(displayWidth, halfDisplayHeight + 1);
            glVertex2f(displayWidth, halfDisplayHeight - 1);
            glEnd();
        };
        borders[1] = () -> {
            glBegin(GL_QUADS);
            glVertex2f(halfDisplayWidth - 1, 0);
            glVertex2f(halfDisplayWidth - 1, displayHeight);
            glVertex2f(halfDisplayWidth + 1, displayHeight);
            glVertex2f(halfDisplayWidth + 1, 0);
            glEnd();
        };
        initializeBorders();
    }

    private static void initializeBorders() {
        borders[2] = () -> {
            glBegin(GL_QUADS);
            glVertex2f(0, halfDisplayHeight - 1);
            glVertex2f(0, halfDisplayHeight + 1);
            glVertex2f(displayWidth, halfDisplayHeight + 1);
            glVertex2f(displayWidth, halfDisplayHeight - 1);
            glEnd();
            glBegin(GL_QUADS);
            glVertex2f(halfDisplayWidth - 1, halfDisplayHeight);
            glVertex2f(halfDisplayWidth - 1, displayHeight);
            glVertex2f(halfDisplayWidth + 1, displayHeight);
            glVertex2f(halfDisplayWidth + 1, halfDisplayHeight);
            glEnd();
        };
        borders[3] = () -> {
            glBegin(GL_QUADS);
            glVertex2f(halfDisplayWidth, halfDisplayHeight - 1);
            glVertex2f(halfDisplayWidth, halfDisplayHeight + 1);
            glVertex2f(displayWidth, halfDisplayHeight + 1);
            glVertex2f(displayWidth, halfDisplayHeight - 1);
            glEnd();
            glBegin(GL_QUADS);
            glVertex2f(halfDisplayWidth - 1, 0);
            glVertex2f(halfDisplayWidth - 1, displayHeight);
            glVertex2f(halfDisplayWidth + 1, displayHeight);
            glVertex2f(halfDisplayWidth + 1, 0);
            glEnd();
        };
        borders[4] = () -> {
            glBegin(GL_QUADS);
            glVertex2f(0, halfDisplayHeight - 1);
            glVertex2f(0, halfDisplayHeight + 1);
            glVertex2f(displayWidth, halfDisplayHeight + 1);
            glVertex2f(displayWidth, halfDisplayHeight - 1);
            glEnd();
            glBegin(GL_QUADS);
            glVertex2f(halfDisplayWidth - 1, 0);
            glVertex2f(halfDisplayWidth - 1, displayHeight);
            glVertex2f(halfDisplayWidth + 1, displayHeight);
            glVertex2f(halfDisplayWidth + 1, 0);
            glEnd();
        };

        orthos[0] = () -> {
            glOrtho(-1.0, 1.0, -2.0, 2.0, 1.0, -1.0);
        };
        orthos[1] = () -> {
            glOrtho(-2.0, 2.0, -1.0, 1.0, 1.0, -1.0);
        };
        orthos[2] = orthos[3] = orthos[4] = () -> {
            glOrtho(-2.0, 2.0, -2.0, 2.0, 1.0, -1.0);
        };
    }

    public static void border(int ssMode) {
        glViewport(0, 0, displayWidth, displayHeight);
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
