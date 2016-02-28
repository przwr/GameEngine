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
import game.place.cameras.Camera;
import game.place.fbo.FrameBufferObject;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class MyGUI extends GUIObject {

    private final Color color;
    private SpriteSheet attackIcons, itemIcons;
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
        attackIcons = place.getSpriteSheetSetScale("attackIcons", "");
        itemIcons = place.getSpriteSheetSetScale("itemIcons", "");
    }

    @Override
    public void setPlayer(Player player) {
        super.setPlayer(player);
//        setFrameBuffer();
    }

    private void setFrameBuffer() {
//        frameBufferObject = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(player.getCollision().getWidth(),
//                player.getCollision().getHeight()) : new RegularFrameBufferObject(player.getCollision().getWidth(), player.getCollision().getHeight());
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
//        activate();
    }

//    public void activate() {
//        alpha = 3f;
//        energyAlpha = 3f;
//        if (!lowHealth) {
//            lifeAlpha = 3f;
//        }
//    }

//    public void deactivate() {
//        alpha = 0f;
//        energyAlpha = 0f;
//        if (!lowHealth) {
//            lifeAlpha = 0f;
//        }
//    }

//    public void activateLifeIndicator() {
//        if (!lowHealth) {
//            lifeAlpha = 3f;
//        }
//    }

    public void activateEnergyIndicator() {
        energyAlpha = 3f;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (on) {
            lowHealth = player.getStats().getHealth() <= player.getStats().getMaxHealth() * 0.4f;
//            updateAlpha();
            calculateLifeAlpha();
//            glPushMatrix();
//            glTranslatef((int) ((player.getX()) * Place.getCurrentScale() - frameBufferObject.getWidth() / 2 + xEffect),
//                    (int) ((player.getY() - player.getFloatHeight()) * Place.getCurrentScale() + yEffect - frameBufferObject.getHeight() / 2), 0);
//            renderGroundGUI();
//            glPopMatrix();

//            if (on) {
            renderRegularGUI();
//            }
            Drawer.refreshColor();
        }
    }

    private void renderRegularGUI() {
        float change = (float) Settings.nativeScale;
        Camera cam = Place.currentCamera;
        if (change == 1 && (cam.getWidth() < Display.getWidth() || cam.getHeight() < Display.getHeight())) {
            change = 0.75f;
//            attackIcons = place.getSpriteSheetSetScale("attackIcons", "", 0.75);
        }
        int size = (int) (Place.tileSize * change);
        int border = (int) (12 * change);
        int innerSize = 2 + 2 * border / 3;
        glPushMatrix();
        if (player.isNotFirst()) {
            glTranslatef(0, cam.getHeight() - size * 2 - border * 3, 0);
        }

//        Bez Translate Lewy Górny
//        Prawy Górny:
//        glTranslatef(Display.getWidth() - size * 2 - border * 3, 0, 0);
//        Prawy Dolny:
//        glTranslatef(Display.getWidth() - size * 2 - border * 3, Display.getHeight() - size * 2 - border * 3, 0);
//        Lewy Dolny:

        glTranslatef(border / 2, border / 2, 0);
        Drawer.setCentralPoint();
        renderLife(size, border, innerSize);
        renderEnergy(size, border, innerSize);
        Drawer.translate(size / 2 - border - 1, size / 2 - border - 1);
        Drawer.setColorStatic(Color.white);
        itemIcons.renderPiece(1);
        Drawer.translate(size - border - 1, 0);
        itemIcons.renderPiece(0);
        Drawer.translate(0, size - border - 1);
        itemIcons.renderPiece(0);
        Drawer.translate(-size + border + 1, 0);
        itemIcons.renderPiece(0);
        Drawer.returnToCentralPoint();

        int r = size / 2 - border / 3 - 2;
        Drawer.setColorStatic(Color.black);
        Drawer.drawRing(size + border - r, size + border - r, r + 1, border / 3, size);
        Drawer.returnToCentralPoint();
        Drawer.drawRing(size + border + r, size + border - r, r + 1, border / 3, size);
        Drawer.returnToCentralPoint();
        Drawer.drawRing(size + border + r, size + border + r, r + 1, border / 3, size);
        Drawer.returnToCentralPoint();
        Drawer.drawRing(size + border - r, size + border + r, r + 1, border / 3, size);
        Drawer.returnToCentralPoint();

//
        Drawer.setColorStatic(Color.black);
        Drawer.drawRing(size + border, size + border, size + border, border / 3, size);
        Drawer.returnToCentralPoint();
        Drawer.drawRing(size + border, size + border, size, border / 3, size);
        Drawer.returnToCentralPoint();

//        Jeśli Prawy
//        glTranslatef(-size * 3 - border * 2, 0, 0);

//        Jeśli Lewy
        glTranslatef(size / 2, 0, 0);


        glTranslatef(2 * (size + border), border, 0);
        Drawer.setColorStatic(Color.white);
        if (change != Settings.nativeScale)
            glScalef(change, change, change);
        attackIcons.renderPiece(firstAttackType);
        if (change != Settings.nativeScale)
            glScalef(1 / change, 1 / change, 1 / change);
        Drawer.setColorStatic(Color.black);
        Drawer.drawRing(size / 2, size / 2, size / 2 - border / 3, border / 3, size);
        Drawer.returnToCentralPoint();
        glTranslatef(0, size, 0);

//        Jeśli Prawy
        glTranslatef(-size / 2, 0, 0);


        Drawer.setColorStatic(Color.white);
        if (change != Settings.nativeScale)
            glScalef(change, change, change);
        attackIcons.renderPiece(secondAttackType);
        if (change != Settings.nativeScale)
            glScalef(1 / change, 1 / change, 1 / change);
        Drawer.setColorStatic(Color.black);
        Drawer.drawRing(size / 2, size / 2, size / 2 - border / 3, border / 3, size);
        Drawer.returnToCentralPoint();


        renderPairArrow(size, border);
//        glTranslatef(size / 2 + border, border, 0);


        glPopMatrix();
    }

    private void renderAttacks(int size, int border, int innerSize) {

    }

    private void renderLife(int size, int border, int innerSize) {
        int halfLifeAngle = 180, startAngle, endAngle;
        int minimumLifePercentage = 1;
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
        c.a = 0.75f;
        float blink = (float) FastMath.sqrt(lifeAlpha);
        if (blink < 1) {
            c.r *= blink;
            c.g *= blink;
            c.b *= blink;
        }
        Drawer.setColorStatic(new Color(0.5f * blink, 0.1f * blink, 0.1f * blink, 0.75f));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, 90, 270, size);
        Drawer.returnToCentralPoint();
        Drawer.setColorStatic(c);
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, precision);
        Drawer.returnToCentralPoint();
    }

    private void renderEnergy(int size, int border, int innerSize) {
        int halfEnergyAngle = 180, startAngle, endAngle;
        int minimumEnergyPercentage = 1;
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
        Drawer.setColorStatic(new Color(0.3f, 0.6f, 1f, 0.75f));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, 270, 450, size);
        Drawer.returnToCentralPoint();
        Drawer.setColorStatic(new Color(0.1f, 0.2f, 0.8f, 0.75f));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, precision);
        Drawer.returnToCentralPoint();
    }

    private void renderPairArrow(int size, int border) {
        Drawer.setColorStatic(Color.white);
        int pair = ((MyPlayer) player).getActiveActionPairID();
        int base = 2 * border / 3;
        glTranslatef(-base + size / 2 + size / 4, -base, 0);
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
        Drawer.returnToCentralPoint();
    }

    private void updateAlpha() {
//        color.a = alpha;
//        if (alpha > 0) {
//            alpha -= 0.02f;
//        } else {
//            alpha = 0;
//        }
//        if (!lowHealth) {
//            if (lifeAlpha > 0) {
//                lifeAlpha -= 0.02f;
//            } else {
//                lifeAlpha = 0;
//            }
//    }
//        if (energyAlpha > 0) {
//            energyAlpha -= 0.02f;
//        } else {
//            energyAlpha = 0;
//        }
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
//                lifeColor.a = lifeAlpha;
        } else {
//                lifeColor.a = 1f;
            lifeAlpha = 1f;
        }
    }

//    public float getAlpha() {
//        return alpha;
//    }
//
//    public Color getLifeColor() {
//        return lifeColor;
//    }
//
//    public Color getEnergyColor() {
//        return energyColor;
//    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

//    public FrameBufferObject getFrameBufferObject() {
//        return frameBufferObject;
//    }
}
