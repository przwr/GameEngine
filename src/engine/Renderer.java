/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.place.fbo.RegularFrameBufferObject;
import game.place.fbo.FrameBufferObject;
import game.Settings;
import game.gameobject.Player;
import game.place.Map;
import game.place.Place;
import game.place.cameras.Camera;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class Renderer {

    private static final int displayWidth = Display.getWidth(), displayHeight = Display.getHeight(), halfDisplayWidth = (displayWidth / 2), halfDisplayHeight = (displayHeight / 2);
    private static FrameBufferObject frame;
    private static final int[] xStart = new int[7], xEnd = new int[7], yStart = new int[7], yEnd = new int[7];
    private static boolean visible;
    private static int lightX, lightY;
    private static float lightColor, lightBrightness, lightStrength;
    private static Camera camera;
    private static Place place;
    private static final drawBorder[] borders = new drawBorder[5];
    private static final resetOrtho[] orthos = new resetOrtho[5];

    public static void findVisibleLights(Map map, int playersLength) {
        place = map.place;
        readyVarsToFindLights(map);
        map.getLightsFromAreasToUpdate().stream().forEach((tempLight) -> {
            for (int i = 0; i < playersLength; i++) {
                if (place.players[i].getMap() == map) {
                    if (place.singleCamera && playersLength > 1) {
                        if (tempLight.isEmits() && yStart[2 + playersLength] <= tempLight.getY() + tempLight.getYBottomEdge() && yEnd[2 + playersLength] >= tempLight.getY() - tempLight.getYTopEdge()
                                && xStart[2 + playersLength] <= tempLight.getX() + tempLight.getXRightEdge() && xEnd[2 + playersLength] >= tempLight.getX() - tempLight.getXLeftEdge()) {
                            visible = true;
                            if (!place.cameras[playersLength - 2].getVisibleLights().contains(tempLight)) {
                                place.cameras[playersLength - 2].addVisibleLight(tempLight);
                            }
                        }
                    } else {
                        for (int j = 0; j < playersLength; j++) {
                            if (place.players[j].getMap() == map && tempLight.isEmits() && yStart[j] <= tempLight.getY() + tempLight.getYBottomEdge() && yEnd[j] >= tempLight.getY() - (tempLight.getYTopEdge())
                                    && xStart[j] <= tempLight.getX() + (tempLight.getXRightEdge()) && xEnd[j] >= tempLight.getX() - (tempLight.getXLeftEdge())) {
                                visible = true;
                                if (!(((Player) place.players[j]).getCamera()).getVisibleLights().contains(tempLight)) {
                                    (((Player) place.players[j]).getCamera()).addVisibleLight(tempLight);
                                }
                            }
                        }
                    }
                    if (visible) {
                        if (!map.getVisibleLights().contains(tempLight)) {
                            map.addVisibleLight(tempLight);
                        }
                        visible = false;
                    }
                }
            }
        });
        // Docelowo iteracja po graczach nie będzie potrzebna - nie będą oni źródłem światła, a raczej jakieś obiekty.
        for (int i = 0; i < place.playersCount; i++) {
            if (place.players[i].getMap() == map) {
                for (Light light : place.players[i].getLights()) {
                    if (place.singleCamera && playersLength > 1) {
                        if (light.isEmits() && yStart[2 + playersLength] <= light.getY() + (light.getYBottomEdge()) && yEnd[2 + playersLength] >= light.getY() - (light.getYBottomEdge())
                                && xStart[2 + playersLength] <= light.getX() + (light.getXLeftEdge()) && xEnd[2 + playersLength] >= light.getX() - (light.getXLeftEdge())) {
                            visible = true;
                            if (!place.cameras[playersLength - 2].getVisibleLights().contains(light)) {
                                place.cameras[playersLength - 2].addVisibleLight(light);
                            }
                        }
                    } else {
                        for (int j = 0; j < playersLength; j++) {
                            if (place.players[j].getMap() == map && light.isEmits() && yStart[j] <= light.getY() + (light.getYBottomEdge()) && yEnd[j] >= light.getY() - (light.getYBottomEdge())
                                    && xStart[j] <= light.getX() + (light.getXLeftEdge()) && xEnd[j] >= light.getX() - (light.getXLeftEdge())) {
                                visible = true;
                                if (!(((Player) place.players[j]).getCamera()).getVisibleLights().contains(light)) {
                                    (((Player) place.players[j]).getCamera()).addVisibleLight(light);
                                }
                            }
                        }
                    }
                    if (visible) {
                        if (!map.getVisibleLights().contains(light)) {
                            map.addVisibleLight(light);
                        }
                        visible = false;
                    }
                }
            }
        }
    }

    private static void readyVarsToFindLights(Map map) {
        for (int p = 0; p < place.getPlayersCount(); p++) {
            if (map == place.players[p].getMap()) {
                camera = (((Player) place.players[p]).getCamera());
                camera.clearVisibleLights();
                xStart[p] = camera.getXStart();
                xEnd[p] = camera.getXEnd();
                yStart[p] = camera.getYStart();
                yEnd[p] = camera.getYEnd();
            }
        }
        for (int i = 0; i < 3; i++) {
            if (place.cameras[i] != null) {
                camera = place.cameras[i];
                camera.clearVisibleLights();
                xStart[4 + i] = camera.getXStart();            // 4 to maksymalna liczba graczy
                xEnd[4 + i] = camera.getXEnd();
                yStart[4 + i] = camera.getYStart();
                yEnd[4 + i] = camera.getYEnd();
            }
        }
        map.clearVisibleLights();
    }

    public static void preRenderLights(Map map) {
        if (!Settings.shadowOff) {
            map.getVisibleLights().stream().filter((light) -> (light.isGiveShadows())).forEach((light) -> {
                ShadowRenderer.prerenderLight(map, light);
            });
        }
    }

    public static void preRenderShadowedLights(Place place, Camera camera) {
        frame.activate();
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(1, 1, 1);
        glBlendFunc(GL_ONE, GL_ONE);
        camera.getVisibleLights().stream().forEach((light) -> {

            if (Settings.shadowOff || !light.isGiveShadows()) {
                light.render(camera.getXOffsetEffect(), camera.getYOffsetEffect(), camera);
            } else {
                drawLight(light, camera);
            }

        });
        frame.deactivate();
    }

    public static void drawLight(Light light, Camera camera) {
        glPushMatrix();
        glTranslated(camera.getXOffsetEffect(), camera.getYOffsetEffect(), 0);
        if (Settings.scaled) {
            glScaled(camera.getScale(), camera.getScale(), 1);
        }
        glTranslated(light.getX() - light.getXCenterShift(), light.getY() - light.getYCenterShift(), 0);
        renderLightPiece(light);
        glPopMatrix();
    }

    private static void renderLightPiece(Light light) {
        lightX = light.getWidth();
        lightY = light.getHeight();
        glBindTexture(GL_TEXTURE_2D, light.getFrameBufferObject().getTexture());
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
    }

    public static void renderLights(Color color, float xStart, float yStart, float xEnd, float yEnd, float xTStart, float yTStart, float xTEnd, float yTEnd) {
        lightBrightness = FastMath.max(color.r, FastMath.max(color.g, color.b));
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

    public static void border(int splitScreenMode) {
        glViewport(0, 0, displayWidth, displayHeight);
        if (splitScreenMode != 0) {
            glBlendFunc(GL_ZERO, GL_ZERO);
            glColor3f(0, 0, 0);
            borders[splitScreenMode - 1].draw();
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
