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
    int xBladesCount, yBladesCount, bladeWidth, bladeHeight, xRadius, yRadius, ySpacing, xCount, yCount, xCentering;

    boolean updateGrass, prerendered;

    FrameBufferObject fbo;

    public GrassClump(int x, int y, int xCount, int yCount) {
        initialize("GrassClump", x, y);
        toUpdate = true;
        this.xCount = xCount;
        this.yCount = yCount;
        xBladesCount = 7;
        yBladesCount = 2;
        bladeWidth = 8;
        bladeHeight = 32;
        xRadius = 20 * xCount;
        yRadius = 4 * yCount;
        grasses = new Grass[xCount * yCount];
        ySpacing = 8;
        xCentering = Math.round((xBladesCount / 2f) * bladeWidth * 0.75f);
        depth = yRadius;
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                grasses[i * xCount + j] = new Grass(x + xCentering + j * 40, y + (i + 1) * ySpacing, xBladesCount, yBladesCount,
                        bladeWidth, bladeHeight);
            }
        }
        fbo = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(xCount * 64, yCount * 16) :
                new RegularFrameBufferObject(xCount * 64, yCount * 16);
    }

    @Override
    public void update() {
        updateGrass = false;
        Area area = map.getArea(getX(), getY());
        for (int i = 0; i < this.map.place.getPlayersCount(); i++) {
            GameObject player = map.place.players[i];
            if (player.getFloatHeight() < bladeHeight) {
                if (Math.abs(getX() + xRadius - player.getX()) < xRadius + player.getCollision().getWidth() / 2) {
                    if (Math.abs(getY() + yRadius - player.getY()) < yRadius + player.getCollision().getHeight() / 2) {
                        updateGrass = true;
                        break;
                    }
                }
            }
        }
        if (!updateGrass) {
            for (Mob mob : area.getNearSolidMobs()) {
                if (mob.getFloatHeight() < bladeHeight) {
                    if (Math.abs(getX() + xRadius - mob.getX()) < xRadius + mob.getCollision().getWidth() / 2) {
                        if (Math.abs(getY() + yRadius - mob.getY()) < yRadius + mob.getCollision().getHeight() / 2) {
                            updateGrass = true;
                            break;
                        }
                    }
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
                grasses[i].resetFactor();
            }
        }
        if (!prerendered) {
            fbo.activate();
            glPushMatrix();
            glClearColor(0, 0.7f * Place.getDayCycle().getShade().g, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT);
            glTranslatef(xRadius, -2 * yRadius + ySpacing + Display.getHeight(), 0);
            for (int i = 0; i < yCount; i++) {
                for (int j = 0; j < xCount; j++) {
                    grasses[i * xCount + j].renderStill();
                    glTranslatef(40, 0, 0);
                }
                glTranslatef(-40 * (xCount), ySpacing, 0);
            }
            glPopMatrix();
            fbo.deactivate();
            prerendered = true;
            for (int i = 0; i < grasses.length; i++) {
                map.addObject(grasses[i]);
            }
        }
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
