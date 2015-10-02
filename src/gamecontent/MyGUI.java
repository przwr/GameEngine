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
    private boolean lowHealth, riseLifeAlpha, on;

    public MyGUI(String name, Place place) {
        super(name, place);
        color = new Color(Color.white);
        alpha = 0f;
        emptySlot = 0;
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
                    player.getCollision().getHeight())
                    : new RegularFrameBufferObject(player.
                            getCollision().getWidth(),
                            player.getCollision().getHeight());
        }
    }

    public void changeAttackIcon(int first, int second) {
        if (first < 0) {
            firstAttackType = emptySlot;
        } else {
            firstAttackType = first + 1;
        }
        if (second < 0) {
            secondAttackType = emptySlot;
        } else {
            secondAttackType = second + 1;
        }
        activate();
    }

    public void activate() {
        alpha = 3f;
        on = true;
    }

    public void deactivate() {
        alpha = 0f;
        on = false;
    }

    public void activateLifeIndicator() {
        alpha = 3f;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (isOn()) {
            lowHealth = player.getStats().getHealth() <= player.getStats().getMaxHealth() * 0.4f;
            updateAlpha();
            glPushMatrix();
            glTranslatef((int) ((player.getX()) * Place.getCurrentScale() - frameBufferObject.getWidth() / 2 + xEffect),
                    (int) ((player.getY() - player.getFloatHeight()) * Place.getCurrentScale() + yEffect - frameBufferObject.getHeight() / 2), 0);

            renderGroundGUI();

            if (on) {
                glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
                Drawer.setColor(color);
                glTranslatef(-player.getCollision().getWidth() * 1.5f, -player.getCollision().getHeight() / 2, 0);
                attackIcons.renderPiece(firstAttackType);
                glTranslatef(0, -Place.tileSize, 0);
                attackIcons.renderPiece(secondAttackType);
            }
            Drawer.refreshColor();
            glPopMatrix();
        }
    }

    private void updateAlpha() {
        color.a = alpha;
        if (alpha > 0) {
            alpha -= 0.02f;
        } else {
            on = false;
            alpha = 0;
        }
    }

    private void renderGroundGUI() {
        glBlendFunc(GL_DST_ALPHA, GL_ONE_MINUS_SRC_COLOR);
        calculateLifeAlpha();
        Drawer.setColor(lifeColor);
        frameBufferObject.renderPiece(0, 0, 0, 0, frameBufferObject.getWidth() / 2, frameBufferObject.getHeight());
        if (on) {
            energyColor.a = alpha;
            Drawer.setColor(energyColor);
            frameBufferObject.renderPiece(0, 0, frameBufferObject.getWidth() / 2, 0, frameBufferObject.getWidth(), frameBufferObject.getHeight());
        }
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void calculateLifeAlpha() {
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

    public boolean isOn() {
        return alpha > 0 || lowHealth || on;
    }

    public FrameBufferObject getFrameBufferObject() {
        return frameBufferObject;
    }
}
