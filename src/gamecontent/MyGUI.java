/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Drawer;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.entities.Player;
import game.place.Place;
import game.place.fbo.FrameBufferObject;
import game.place.fbo.MultiSampleFrameBufferObject;
import game.place.fbo.RegularFrameBufferObject;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class MyGUI extends GUIObject {

    private final SpriteSheet attackIcons;
    private final Color color;
    private int firstAttackType, secondAttackType;
    private float alpha, lifeAlpha;
    private int emptySlot;
    private FrameBufferObject frameBufferObject;
    private Color lifeColor = new Color(0f, 0f, 0f), energyColor = new Color(0f, 0f, 1f);
    private boolean lowLife, riseLifeAlpha;

    public MyGUI(String name, Place place) {
        super(name, place);
        color = new Color(Color.white);
        alpha = 0f;
        emptySlot = 5;
        firstAttackType = emptySlot;
        secondAttackType = emptySlot;
        attackIcons = place.getSpriteSheet("attackIcons", "");
    }


    @Override
    public void setPlayer(Player player) {
        super.setPlayer(player);
        setFrameBuffer();
    }

    private void setFrameBuffer() {
        if (!Settings.shadowOff) {
            frameBufferObject = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(player.getCollision().getWidth(),
                    player.getCollision().getHeight()) :
                    new RegularFrameBufferObject(player.
                            getCollision().getWidth(),
                            player.getCollision().getHeight());
        }
    }

    public void changeAttackIcon(int first, int second) {
        if (first < 0) {
            first = emptySlot;
        }
        if (second < 0) {
            second = emptySlot;
        }
        firstAttackType = first;
        secondAttackType = second;
        alpha = 3f;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        boolean lowHealth = player.getStats().getHealth() <= player.getStats().getMaxHealth() * 0.4f;
        if (alpha > 0 || lowHealth) {
            color.a = alpha;
            alpha -= 0.02f;
            glPushMatrix();
            glTranslatef((int) ((player.getX()) * Place.getCurrentScale() - frameBufferObject.getWidth() / 2 + xEffect),
                    (int) ((player.getY() - player.getFloatHeight()) * Place.getCurrentScale() + yEffect - frameBufferObject.getHeight() / 2), 0);

            renderGroundGUI(lowHealth);

            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);

            Drawer.setColor(color);
            glTranslatef(-player.getCollision().getWidth() * 1.5f, -player.getCollision().getHeight() / 2, 0);
            attackIcons.renderPiece(firstAttackType);
            glTranslatef(0, -Place.tileSize, 0);
            attackIcons.renderPiece(secondAttackType);

            Drawer.refreshColor();
            glPopMatrix();
        }
    }

    private void renderGroundGUI(boolean lowHealth) {
        glBlendFunc(GL_SRC_COLOR, GL_DST_ALPHA);
        if (lowHealth) {
            if (player.getStats().getHealth() <= player.getStats().getMaxHealth() * 0.2f) {
                if (riseLifeAlpha) {
                    lifeAlpha += 0.015f;
                    if (lifeAlpha > 1f) {
                        riseLifeAlpha = false;
                    }
                } else {
                    lifeAlpha -= 0.015f;
                    if (lifeAlpha < 0.3f) {
                        riseLifeAlpha = true;
                    }
                }
                lifeColor.a = lifeAlpha;
            } else {
                lifeColor.a = 1f;
                lifeAlpha = 1f;
            }
        } else {
            lifeAlpha = 1f;
            riseLifeAlpha = true;
            lifeColor.a = alpha;
        }
        energyColor.a = alpha;
        Drawer.setColor(lifeColor);
        frameBufferObject.renderPiece(0, 0, 0, 0, frameBufferObject.getWidth() / 2, frameBufferObject.getHeight());
        if (alpha > 0) {
            Drawer.setColor(energyColor);
            frameBufferObject.renderPiece(0, 0, frameBufferObject.getWidth() / 2, 0, frameBufferObject.getWidth(), frameBufferObject.getHeight());
        }
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public float getAlpha() {
        return alpha;
    }

    public Color getLifeColor() {
        return lifeColor;
    }

    public Color getEnergyColor() {
        return energyColor;
    }


    public FrameBufferObject getFrameBufferObject() {
        return frameBufferObject;
    }
}
