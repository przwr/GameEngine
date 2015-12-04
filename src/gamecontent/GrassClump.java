package gamecontent;

import collision.Figure;
import engine.utilities.Drawer;
import game.Settings;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.place.Place;
import game.place.fbo.FrameBufferObject;
import game.place.fbo.MultiSampleFrameBufferObject;
import game.place.fbo.RegularFrameBufferObject;
import game.place.map.Area;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 30.11.15.
 */
public class GrassClump extends GameObject {

    Grass[] grasses;
    int xBladesCount, yBladesCount, bladeWidth, bladeHeight, xRadius, yRadius, ySpacing, xCount, yCount, xCentering, grassWidth, curve;
    boolean updateGrass, prerendered;
    FrameBufferObject fbo;


    private GrassClump(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        setUp(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight);
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                grasses[i * xCount + j] = Grass.create(x + xCentering + j * grassWidth, y + (i + 1) * ySpacing, xBladesCount, yBladesCount,
                        bladeWidth, bladeHeight);
            }
        }
    }


    private GrassClump(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight, int curve) {
        this.curve = curve;
        if (xBladesCount * 2 < yCount / curve) {
            xBladesCount = yCount / curve / 2;
        }
        setUp(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight);
        int middle1 = yCount / 2, middle2 = yCount / 2, modXBladesCount, xCenter;
        if (yCount % 2 == 0) {
            middle1 -= 1;
        }
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                modXBladesCount = xBladesCount;
                xCenter = xCentering;
                if (xCount > 1) {
                    if (j == 0) {
                        modXBladesCount -= (i <= middle1 ? (middle1 - i) : (i - middle2)) / curve;
                        xCenter = xCentering * 2 - modXBladesCount * bladeWidth / 2;
                    } else if (j == xCount - 1) {
                        modXBladesCount -= (i <= middle1 ? (middle1 - i) : (i - middle2)) / curve;
                        xCenter = modXBladesCount * bladeWidth / 2;
                    }
                }
                grasses[i * xCount + j] = Grass.create(x + xCenter + j * grassWidth, y + (i + 1) * ySpacing, modXBladesCount, yBladesCount, bladeWidth,
                        bladeHeight);
            }
        }
    }

    public static GrassClump createRectangle(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        return new GrassClump(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight);
    }

    public static GrassClump createRound(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        return new GrassClump(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight, 1);
    }

    private void setUp(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        initialize("GrassClump", x, y);
        toUpdate = true;
        this.xCount = xCount;
        this.yCount = yCount;
        this.xBladesCount = xBladesCount;
        this.yBladesCount = yBladesCount;
        this.bladeWidth = bladeWidth;
        this.bladeHeight = bladeHeight;
        grassWidth = (bladeWidth - 2) * xBladesCount;
        xRadius = grassWidth / 2 * xCount;
        yRadius = 4 * yCount;
        grasses = new Grass[xCount * yCount];
        ySpacing = 8;
        depth = 1;
        xCentering = Math.round(xBladesCount * bladeWidth / 2f);
        int fboWidth = xRadius * 2 + 4 + (xCount - 1) * xCentering;
        int fboHeight = yRadius * 2 + bladeHeight + 8;
        fbo = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(fboWidth, fboHeight) :
                new RegularFrameBufferObject(fboWidth, fboHeight);
    }

    @Override
    public void update() {
        updateGrass = false;
        Area area = map.getArea(this.area);
        for (int i = 0; i < this.map.place.getPlayersCount(); i++) {
            GameObject player = map.place.players[i];
            if (player.getFloatHeight() < bladeHeight && Math.abs(getX() + xRadius - player.getX()) < xRadius + player.getCollision().getWidth() / 2
                    && Math.abs(getY() + yRadius - player.getY()) < yRadius + player.getCollision().getHeight() / 2) {
                updateGrass = true;
                break;
            }
        }
        if (!updateGrass) {
            for (Mob mob : area.getNearSolidMobs()) {
                if (mob.getFloatHeight() < bladeHeight && Math.abs(getX() + xRadius - mob.getX()) < xRadius + mob.getCollision().getWidth() / 2
                        && Math.abs(getY() + yRadius - mob.getY()) < yRadius + mob.getCollision().getHeight() / 2) {
                    updateGrass = true;
                    break;
                }
            }
        }
        if (updateGrass) {
            for (int i = 0; i < grasses.length; i++) {
                grasses[i].update(map);
                grasses[i].setVisible(true);
            }
        } else {
            for (int i = 0; i < grasses.length; i++) {
                grasses[i].setVisible(false);
                grasses[i].reset();
            }
        }
        if (!prerendered) {
            preRender();
        }
    }

    private void preRender() {
        fbo.activate();
        glPushMatrix();
        glClearColor(0, 0.7f * Place.getDayCycle().getShade().g, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        glTranslatef(xRadius + 2, -2 * yRadius + ySpacing + Display.getHeight(), 0);
        if (curve > 0) {
            preRenderRound();
        } else {
            preRenderRectangle();
        }
        glPopMatrix();
        fbo.deactivate();
        for (int i = 0; i < grasses.length; i++) {
            map.addObject(grasses[i]);
        }
        prerendered = true;
    }

    private void preRenderRound() {
        int middle1 = yCount / 2, middle2 = yCount / 2, moduleWidth, xCenter;
        if (yCount % 2 == 0) {
            middle1 -= 1;
        }
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                xCenter = 0;
                if (curve > 0 && xCount > 1) {
                    if (j == 0) {
                        xCenter = xCentering - (xBladesCount - (i <= middle1 ? (middle1 - i) : (i - middle2)) / curve) * bladeWidth / 2;
                        glTranslatef(xCenter, 0, 0);
                    } else if (j == xCount - 1) {
                        xCenter = (xBladesCount - (i <= middle1 ? (middle1 - i) : (i - middle2)) / curve) * bladeWidth / 2 - xCentering;
                        glTranslatef(xCenter, 0, 0);
                    }
                }
                grasses[i * xCount + j].renderStill();
                glTranslatef(grassWidth - xCenter, 0, 0);
            }
            glTranslatef(-grassWidth * (xCount), ySpacing, 0);
        }
    }

    private void preRenderRectangle() {
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                grasses[i * xCount + j].renderStill();
                glTranslatef(grassWidth, 0, 0);
            }
            glTranslatef(-grassWidth * (xCount), ySpacing, 0);
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (!updateGrass) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX() + xCentering - xRadius - 2, (int) (getY() - fbo.getHeight() + 2 * yRadius - floatHeight), 0);
            fbo.render();
            glPopMatrix();
        }
    }


    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInShade(appearance, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInBlack(appearance);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInShade(appearance, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInBlack(appearance, xStart, xEnd);
            glPopMatrix();
        }
    }

    public int getXSpriteBegin() {
        return getX();
    }

    public int getYSpriteBegin() {
        return getY();
    }

    public int getXSpriteEnd() {
        return getX() + xRadius * 2;
    }

    public int getYSpriteEnd() {
        return getY() + yRadius * 2;
    }

}