/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.entities.Player;
import game.gameobject.stats.PlayerStats;
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
    private float alpha, lifeAlpha, energyAlpha;
    private int emptySlot;
    private FrameBufferObject frameBufferObject;
    private Color lifeColor = new Color(0f, 0f, 0f), energyColor = new Color(0f, 0f, 1f);
    private boolean lowHealth, riseLifeAlpha, on = true;

    public MyGUI(String name, Place place) {
        super(name, place);
        color = new Color(Color.white);
        alpha = 0f;
        emptySlot = 0;
        firstAttackType = emptySlot;
        secondAttackType = emptySlot;
        attackIcons = Settings.nativeScale == 1 ? place.getSpriteSheet("attackIcons", "") : place.getSpriteSheetSetScale("attackIcons", "");
    }

    @Override
    public void setPlayer(Player player) {
        super.setPlayer(player);
        setFrameBuffer();
    }

    private void setFrameBuffer() {
        frameBufferObject = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(player.getCollision().getWidth(),
                player.getCollision().getHeight()) : new RegularFrameBufferObject(player.getCollision().getWidth(), player.getCollision().getHeight());
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
        energyAlpha = 3f;
        if (!lowHealth) {
            lifeAlpha = 3f;
        }
    }

    public void deactivate() {
        alpha = 0f;
        energyAlpha = 0f;
        if (!lowHealth) {
            lifeAlpha = 0f;
        }
    }

    public void activateLifeIndicator() {
        if (!lowHealth) {
            lifeAlpha = 3f;
        }
    }

    public void activateEnergyIndicator() {
        energyAlpha = 3f;
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
            glPopMatrix();

            if (on) {
                renderRegularGUI();
            }
            Drawer.refreshColor();
        }
    }

    private void renderRegularGUI() {
        int size = (int) (Place.tileSize * Settings.nativeScale);
        int border = (int) (12 * Settings.nativeScale);
        int innerSize = 2 + 2 * border / 3;
        glPushMatrix();
//                Bez Translate Lewy Górny
//                Prawy Górny:
//                glTranslatef(Display.getWidth() - Math.round(Place.tileSize * Settings.nativeScale), 0, 0);
//                Lewy Dolny:
//                glTranslatef(Display.getWidth() - Math.round(Place.tileSize * Settings.nativeScale), Display.getHeight() - Math.round(2 * Place.tileSize *
//                        Settings.nativeScale), 0);
//                Prawy Dolny:
//                glTranslatef(0, Display.getHeight() - Math.round(2 * Place.tileSize * Settings.nativeScale), 0);

        glTranslatef(border / 2, border / 2, 0);
        Drawer.setCentralPoint();
        renderLife(size, border, innerSize);
        Drawer.returnToCentralPoint();
        renderEnergy(size, border, innerSize);
        Drawer.returnToCentralPoint();

        Drawer.setColorStatic(new Color(0, 0, 0));
        Drawer.drawRing(size + border, size + border, size + border, border / 3, size);
        Drawer.returnToCentralPoint();
        Drawer.drawRing(size + border, size + border, size, border / 3, size);
        Drawer.returnToCentralPoint();

        Drawer.setColorStatic(color);
        glTranslatef(size / 2 + border, border, 0);
        attackIcons.renderPiece(firstAttackType);
        glTranslatef(0, size, 0);
        attackIcons.renderPiece(secondAttackType);


        Drawer.returnToCentralPoint();
        renderPairArrow(size, border);

        glPopMatrix();
    }

    private void renderLife(int size, int border, int innerSize) {
        int halfLifeAngle = 180, startAngle, endAngle;
        int minimumLifePercentage = Methods.roundDouble(45f / (Place.tileSize * Settings.nativeScale / 2f));
        int lifePercentageAngle = Methods.roundDouble(player.getStats().getHealth() * halfLifeAngle / (float) player.getStats().getMaxHealth());
        if (lifePercentageAngle < minimumLifePercentage && player.getStats().getHealth() != 0) {
            lifePercentageAngle = minimumLifePercentage;
        }
        startAngle = 90;
        endAngle = lifePercentageAngle + 90;
        int precision = (size * lifePercentageAngle) / halfLifeAngle;
        if (precision == 0) {
            precision = 1;
        }
        Color c = new Color(0, 0, 0);
        Drawer.setPercentToRGBColor((halfLifeAngle - lifePercentageAngle) * 100 / halfLifeAngle, c);
        if (lowHealth) {
            c.a = lifeColor.a;
        }
        Drawer.setColorStatic(c);
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, precision);
    }

    private void renderEnergy(int size, int border, int innerSize) {
        int halfEnergyAngle = 180, startAngle, endAngle;
        int minimumEnergyPercentage = Methods.roundDouble(45f / (Place.tileSize * Settings.nativeScale * Place.getCurrentScale() / 2f));
        int energyPercentageAngle = Methods.roundDouble(((PlayerStats) player.getStats()).getEnergy()
                * halfEnergyAngle / ((PlayerStats) player.getStats()).getMaxEnergy());
        if (energyPercentageAngle < minimumEnergyPercentage && ((PlayerStats) player.getStats()).getEnergy() != 0) {
            energyPercentageAngle = minimumEnergyPercentage;
        }
        startAngle = 450 - energyPercentageAngle;
        endAngle = 450;
        int precision = (size * energyPercentageAngle) / halfEnergyAngle;
        if (precision == 0) {
            precision = 1;
        }
        Drawer.setColorStatic(new Color(0.25f, 0.25f, 1f));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, precision);
    }

    private void renderPairArrow(int size, int border) {
        Drawer.setColorStatic(Color.white);
        int pair = ((MyPlayer) player).getActiveActionPairID();
        int base = border / 2;
        glTranslatef(-base + size / 2, -base, 0);
        switch (pair) {
            case 0:
                Drawer.drawTriangle(base, 0, 2 * base, 2 * base, 0, 2 * base);
                break;
            case 1:
                Drawer.drawTriangle(0, 0, 2 * base, base, 0, 2 * base);
                break;
            case 2:
                Drawer.drawTriangle(0, 0, 2 * base, 0, base, 2 * base);
                break;
            case 3:
                Drawer.drawTriangle(2 * base, 0, 2 * base, 2 * base, 0, base);
                break;
            default:
        }
    }

    private void updateAlpha() {
//        color.a = alpha;
//        if (alpha > 0) {
//            alpha -= 0.02f;
//        } else {
//            alpha = 0;
//        }
        if (!lowHealth) {
            if (lifeAlpha > 0) {
                lifeAlpha -= 0.02f;
            } else {
                lifeAlpha = 0;
            }
        }
        if (energyAlpha > 0) {
            energyAlpha -= 0.02f;
        } else {
            energyAlpha = 0;
        }
    }

    private void renderGroundGUI() {
        glBlendFunc(GL_DST_ALPHA, GL_ONE_MINUS_SRC_COLOR);
        calculateLifeAlpha();
        if (lifeAlpha > 0) {
            Drawer.setColorStatic(lifeColor);
            frameBufferObject.renderPiece(0, 0, 0, 0, frameBufferObject.getWidth() / 2, frameBufferObject.getHeight());
        }
        if (energyAlpha > 0) {
            energyColor.a = energyAlpha;
            Drawer.setColorStatic(energyColor);
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
            riseLifeAlpha = true;
            lifeColor.a = lifeAlpha;
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
        return lifeAlpha > 0 || energyAlpha > 0 || lowHealth || on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public FrameBufferObject getFrameBufferObject() {
        return frameBufferObject;
    }
}
