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
    int xBladesCount, yBladesCount, bladeWidth, bladeHeight, xRadius, yRadius, ySpacing, xCount, yCount, xCentering, grassWidth;
    boolean updateGrass, prerendered;
    FrameBufferObject fbo;


    private GrassClump(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
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
        xCentering = Math.round(xBladesCount * bladeWidth / 2f) - 4;
        depth = 1;
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                grasses[i * xCount + j] = new Grass(x + xCentering + j * grassWidth, y + (i + 1) * ySpacing, xBladesCount, yBladesCount,
                        bladeWidth, bladeHeight);
            }
        }
        int fboWidth = (xCentering + xRadius) * 2;
        int fboHeight = yRadius * 2 + bladeHeight + 8;
        fbo = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(fboWidth, fboHeight) :
                new RegularFrameBufferObject(fboWidth, fboHeight);
    }

    public static GrassClump createRectangle(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        return new GrassClump(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight);
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
        glTranslatef(xRadius, -2 * yRadius + ySpacing + Display.getHeight(), 0);
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                grasses[i * xCount + j].renderStill();
                glTranslatef(grassWidth, 0, 0);
            }
            glTranslatef(-grassWidth * (xCount), ySpacing, 0);
        }
        glPopMatrix();
        fbo.deactivate();
        for (int i = 0; i < grasses.length; i++) {
            map.addObject(grasses[i]);
        }
        prerendered = true;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (!updateGrass) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX() + xCentering - xRadius, (int) (getY() - fbo.getHeight() + 2 * yRadius - floatHeight), 0);
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
