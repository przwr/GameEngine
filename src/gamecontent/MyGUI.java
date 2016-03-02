/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.entities.Player;
import game.gameobject.stats.PlayerStats;
import game.place.Place;
import game.place.cameras.Camera;
import net.jodk.lang.FastMath;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class MyGUI extends GUIObject {

    private SpriteSheet attackIcons, itemIcons;
    private int firstAttackType, secondAttackType;
    private float lifeAlpha;
    private int emptySlot;
    private float lastLife, lastEnergy, energyNeeded;
    private boolean riseLifeAlpha, on = true;
    private Color color = new Color(0, 0, 0);
    private Delay lifeDelay = Delay.createInSeconds(1), energyDelay = Delay.createInSeconds(1), energyLowDelay = Delay.createInMilliseconds(250);

    public MyGUI(String name, Place place) {
        super(name, place);
        emptySlot = 0;
        firstAttackType = emptySlot;
        secondAttackType = emptySlot;
        attackIcons = place.getSpriteSheet("attackIcons", "");
        itemIcons = place.getSpriteSheet("itemIcons", "");
        lifeDelay.terminate();
        energyDelay.terminate();
        energyLowDelay.terminate();
    }

    @Override
    public void setPlayer(Player player) {
        super.setPlayer(player);
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
    }


    @Override
    public void render(int xEffect, int yEffect) {
        if (on) {
            calculateLifeAlpha();
            renderRegularGUI();
            Drawer.refreshColor();
        }
    }

    private void renderRegularGUI() {
        Color light = player.getMap().getLightColor();
        float value = 1f - Math.max(Math.max(light.r, light.g), light.b);
        color.r = color.g = color.b = value * 1.5f;
        float change = 1f;
        Camera cam = Place.currentCamera;
//        if (change == 1 && (cam.getWidth() < Display.getWidth() || cam.getHeight() < Display.getHeight())) {
//            change = 0.75f;
//        }
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

        glTranslatef(2 * border / 3, 2 * border / 3, 0);
        Drawer.setCentralPoint();
        renderLife(size, border, innerSize);
        renderEnergy(size, border, innerSize);
        renderIcons(size, border, change);
        renderPairArrow(size, border);
        glPopMatrix();
    }

    private void renderIcons(int size, int border, float change) {
        Drawer.setColorStatic(Color.white);
        Drawer.translate(size / 2 + border, 2 * border / 3);
        if (change != Settings.nativeScale)
            glScalef(change, change, change);
        itemIcons.renderPiece(0);
        if (change != Settings.nativeScale)
            glScalef(1 / change, 1 / change, 1 / change);
        Drawer.translate(size / 2 + border / 3, size / 2 + border / 3);
        if (change != Settings.nativeScale)
            glScalef(change, change, change);
        itemIcons.renderPiece(0);
        if (change != Settings.nativeScale)
            glScalef(1 / change, 1 / change, 1 / change);
        Drawer.translate(-size / 2 - border / 3, size / 2 + border / 3);
        if (change != Settings.nativeScale)
            glScalef(change, change, change);
        itemIcons.renderPiece(1);
        if (change != Settings.nativeScale)
            glScalef(1 / change, 1 / change, 1 / change);
        Drawer.translate(-size / 2 - border / 3, -size / 2 - border / 3);
        if (change != Settings.nativeScale)
            glScalef(change, change, change);
        itemIcons.renderPiece(0);
        if (change != Settings.nativeScale)
            glScalef(1 / change, 1 / change, 1 / change);
        Drawer.returnToCentralPoint();

        int r = size / 2 - border / 3 - border / 6;
        Drawer.setColorStatic(color);
        Drawer.drawRing(size + border, size / 2 + border - border / 3, r, border / 3, size);
        Drawer.returnToCentralPoint();
        Drawer.drawRing(size + border, size + size / 2 + border + border / 3, r, border / 3, size);
        Drawer.returnToCentralPoint();
        Drawer.drawRing(size / 2 + border - border / 3, size + border, r, border / 3, size);
        Drawer.returnToCentralPoint();
        Drawer.drawRing(size + size / 2 + border + border / 3, size + border, r, border / 3, size);
        Drawer.returnToCentralPoint();

        Drawer.setColorStatic(color);
        Drawer.drawRing(size + border, size + border, size + border, border / 3, size);
        Drawer.returnToCentralPoint();
        Drawer.drawRing(size + border, size + border, size, border / 3, size);
        Drawer.returnToCentralPoint();

//        Jeśli Prawy
//        glTranslatef(-size * 3 - border * 2, 0, 0);
//        Jeśli Lewy

        Drawer.translate(2 * (size + border), border);
        Drawer.setColorStatic(Color.white);
        if (change != Settings.nativeScale)
            glScalef(change, change, change);
        attackIcons.renderPiece(firstAttackType);
        if (change != Settings.nativeScale)
            glScalef(1 / change, 1 / change, 1 / change);
        Drawer.setColorStatic(color);
        Drawer.drawRing(size / 2, size / 2, size / 2 - border / 3, border / 3, size);
        Drawer.returnToCentralPoint();

        Drawer.translate(3 * size + 3 * border, border);
        Drawer.setColorStatic(Color.white);
        if (change != Settings.nativeScale)
            glScalef(change, change, change);
        attackIcons.renderPiece(secondAttackType);
        if (change != Settings.nativeScale)
            glScalef(1 / change, 1 / change, 1 / change);
        Drawer.setColorStatic(color);
        Drawer.drawRing(size / 2, size / 2, size / 2 - border / 3, border / 3, size);
        Drawer.returnToCentralPoint();
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
        Color c = new Color(0, 0, 0);
        c.a = 1f;
        float blink = (float) FastMath.sqrt(lifeAlpha);
        Drawer.setColorStatic(new Color(1f * blink, 0.9f * blink, 0.9f * blink, 0.75f));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, 90, 270, size / 2);
        Drawer.returnToCentralPoint();
        if (!lifeDelay.isOver()) {
            float alpha = (lifeDelay.getDifference() / (float) lifeDelay.getLength());
            Drawer.setColorStatic(new Color(1f * blink, 0.4f * blink, 0.4f * blink, alpha));
            int last = Methods.roundDouble(lastLife * halfLifeAngle / ((PlayerStats) player.getStats()).getMaxEnergy()) + 90;
            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, last, size / 2);
            Drawer.returnToCentralPoint();
        }
        Drawer.setColorStatic(new Color(0.8f * blink, 0.1f * blink, 0.1f * blink));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, size / 2);
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
        Drawer.setColorStatic(new Color(0.8f, 0.8f, 1f, 0.75f));
        if (!energyLowDelay.isOver()) {
            Drawer.setColorStatic(new Color(0.9f, 0.8f, 0.2f));
        }
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, 270, 450, size / 2);
        Drawer.returnToCentralPoint();
        if (!energyLowDelay.isOver()) {
            Drawer.setColorStatic(new Color(0.45f, 0.4f, 0.1f));
            int last = 450 - Methods.roundDouble(energyNeeded * halfEnergyAngle / ((PlayerStats) player.getStats()).getMaxEnergy());
            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, last, endAngle, size / 2);
            Drawer.returnToCentralPoint();
        }
        if (energyLowDelay.isOver() && !energyDelay.isOver()) {
            float alpha = (energyDelay.getDifference() / (float) energyDelay.getLength());
            Drawer.setColorStatic(new Color(0.4f, 0.4f, 1f, alpha));
            int last = 450 - Methods.roundDouble(lastEnergy * halfEnergyAngle / ((PlayerStats) player.getStats()).getMaxEnergy());
            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, last, endAngle, size / 2);
            Drawer.returnToCentralPoint();
        }
        Drawer.setColorStatic(new Color(0.1f, 0.2f, 0.8f));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, size / 2);
        Drawer.returnToCentralPoint();
    }

    private void renderPairArrow(int size, int border) {
        Drawer.setColorStatic(color);
        int pair = ((MyPlayer) player).getActiveActionPairID();
        int base = 2 * border / 3;
        Drawer.translate(3 * size + 2 * border - base + border / 2, border + base + size / 4);
//        glTranslatef(size / 2 + size / 4, 2 * (size + border), 0);
//        glTranslatef(-base + size / 2 + size / 4, -base, 0);
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
//        Drawer.setColorStatic(Color.white);
//        base = border / 3;
//        Drawer.translate(3 * size + 2 * border - base + border / 2, 2 * border + size / 4);
//        switch (pair) {
//            case 0:
//                Drawer.drawTriangle(base, 0, 2 * base, 2 * base, 0, 2 * base);
//                break;
//            case 1:
//                Drawer.drawTriangle(0, 0, 2 * base, base, 0, 2 * base);
//                break;
//            case 2:
//                Drawer.drawTriangle(0, 0, 2 * base, 0, base, 2 * base);
//                break;
//            case 3:
//                Drawer.drawTriangle(2 * base, 0, 2 * base, 2 * base, 0, base);
//                break;
//            default:
//        }
//        Drawer.returnToCentralPoint();
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
        } else {
            lifeAlpha = 1f;
        }
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public void activateEnergyHistory(float last) {
        lastEnergy = last;
        energyDelay.start();
    }

    public void activateLifeHistory(float last) {
        lastLife = last;
        lifeDelay.start();
    }

    public void activateLowEnergy(float need) {
        energyNeeded = need;
        energyLowDelay.start();
    }

}
