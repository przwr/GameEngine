/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.GUIObject;
import game.gameobject.entities.Player;
import game.gameobject.stats.PlayerStats;
import game.place.Place;
import game.place.cameras.Camera;
import net.jodk.lang.FastMath;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;
import sprites.vbo.VertexBufferObject;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class MyGUI extends GUIObject {

    private final static int LEFT_TOP = 0, RIGHT_TOP = 1, LEFT_BOTTOM = 2, RIGHT_BOTTOM = 3;
    private SpriteSheet attackIcons, itemIcons;
    private int firstAttackType, secondAttackType;
    private float lifeAlpha;
    private int emptySlot;
    private float lastLife, lastEnergy, energyNeeded;
    private boolean riseLifeAlpha, on = true;
    private Color color = new Color(0, 0, 0);
    private Delay lifeDelay = Delay.createInSeconds(1), energyDelay = Delay.createInSeconds(1), energyLowDelay = Delay.createInMilliseconds(250);
    private VertexBufferObject arrows, rings;
    private int[] placement = new int[2 * 3];
    private int corner = LEFT_TOP;
    private int size, base, border, innerSize;
    private float scale;

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
        scale = 1f;
//        if (change == 1 && (cam.getWidth() < Display.getWidth() || cam.getHeight() < Display.getHeight())) {
//            change = 0.75f;
//        }
        initializeBuffers();
    }

    private void initializeBuffers() {
        size = (int) (Place.tileSize * scale);
        border = (int) (12 * scale);
        innerSize = 2 + 2 * border / 3;
        base = (int) (8 * scale);
        float[] arrowsVertices = {0, 2 * base, 2 * base, 2 * base, base, 0,
                0, 2 * base, 2 * base, base, 0, 0,
                base, 2 * base, 2 * base, 0, 0, 0,
                0, base, 2 * base, 2 * base, 2 * base, 0};
        int r = size / 2 - border / 3 - border / 6;
        float[] ringVs1 = Drawer.getRingVertices(size / 2, size / 2, r, border / 3, size);
        float[] ringVs2 = Drawer.getRingVertices(size + border, size + border, size + border, border / 3, size);
        float[] ringVs3 = Drawer.getRingVertices(size + border, size + border, size, border / 3, size);

        addSize(0, ringVs1.length);
        addSize(1, ringVs2.length);
        addSize(2, ringVs3.length);

        arrows = new VertexBufferObject(arrowsVertices);
        rings = new VertexBufferObject(Methods.concatAll(ringVs1, ringVs2, ringVs3));
    }

    private void addSize(int i, int count) {
        placement[i * 2] = i == 0 ? 0 : placement[i * 2 - 2] + placement[i * 2 - 1];
        placement[i * 2 + 1] = count / 2;
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
            renderGUI();
            Drawer.refreshColor();
        }
    }

    private void renderGUI() {
        Color light = player.getMap().getLightColor();
        Camera cam = Place.currentCamera;
        color.r = color.g = color.b = (1f - Math.min(Math.min(light.r, light.g), light.b)) * 0.5f;
        glPushMatrix();
        if (player.isNotFirst()) {
            corner = LEFT_BOTTOM;
        }
        switch (corner) {
            case LEFT_TOP:
                glTranslatef(2 * border / 3, 2 * border / 3, 0);
                break;
            case RIGHT_TOP:
                glTranslatef(cam.getWidth() - size * 2 - 8 * border / 3, 2 * border / 3, 0);
                break;
            case LEFT_BOTTOM:
                glTranslatef(2 * border / 3, cam.getHeight() - size * 2 - 8 * border / 3, 0);
                break;
            case RIGHT_BOTTOM:
                glTranslatef(cam.getWidth() - size * 2 - 8 * border / 3, cam.getHeight() - size * 2 - 8 * border / 3, 0);
                break;
        }
        Drawer.setCentralPoint();
        renderLife();
        renderEnergy();
        renderPairArrow();
        renderIcons();
        glPopMatrix();
    }

    private void renderIcons() {
        Drawer.translate(size / 2 + border, 2 * border / 3);
        renderItemIcon(0);
        renderIconRing();

        Drawer.translate(size / 2 + border / 3, size / 2 + border / 3);
        renderItemIcon(0);
        renderIconRing();

        Drawer.translate(-size / 2 - border / 3, size / 2 + border / 3);
        renderItemIcon(1);
        renderIconRing();

        Drawer.translate(-size / 2 - border / 3, -size / 2 - border / 3);
        renderItemIcon(0);
        renderIconRing();

        Drawer.returnToCentralPoint();

        renderLifeEnergyRings();

        switch (corner) {
            case LEFT_TOP:
                Drawer.translate(2 * (size + border), -2 * border / 3 + border / 6);
                break;
            case RIGHT_TOP:
                Drawer.translate(-2 * (size) - border, -2 * border / 3 + border / 6);
                break;
            case LEFT_BOTTOM:
                Drawer.translate(2 * (size + border), size + 8 * border / 3 - border / 6);
                break;
            case RIGHT_BOTTOM:
                Drawer.translate(-2 * (size) - border, size + 8 * border / 3 - border / 6);
                break;
        }

        renderAttackIcon(firstAttackType);
        renderIconRing();

        Drawer.translate(size + border, 0);
        renderAttackIcon(secondAttackType);
        renderIconRing();

        Drawer.returnToCentralPoint();
    }

    private void renderItemIcon(int icon) {
        Drawer.setColorStatic(Color.white);
        if (scale != 1f) {
            glScalef(scale, scale, scale);
        }
        itemIcons.renderPiece(icon);
        if (scale != 1f) {
            glScalef(1 / scale, 1 / scale, 1 / scale);
        }
    }

    private void renderAttackIcon(int icon) {
        Drawer.setColorStatic(Color.white);
        if (scale != 1f) {
            glScalef(scale, scale, scale);
        }
        attackIcons.renderPiece(icon);
        if (scale != 1f) {
            glScalef(1 / scale, 1 / scale, 1 / scale);
        }
    }

    private void renderIconRing() {
        Drawer.setColorStatic(color);
        glDisable(GL_TEXTURE_2D);
        rings.renderTriangleStrip(placement[0], placement[1]);
        glEnable(GL_TEXTURE_2D);
    }

    private void renderLifeEnergyRings() {
        Drawer.setColorStatic(color);
        glDisable(GL_TEXTURE_2D);
        rings.renderTriangleStrip(placement[2], placement[3]);
        rings.renderTriangleStrip(placement[4], placement[5]);
        glEnable(GL_TEXTURE_2D);
    }

    private void renderLife() {
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
        if (!lifeDelay.isOver()) {
            float alpha = (lifeDelay.getDifference() / (float) lifeDelay.getLength());
            Drawer.setColorStatic(new Color(1f * blink, 0.4f * blink, 0.4f * blink, alpha));
            int last = Methods.roundDouble(lastLife * halfLifeAngle / ((PlayerStats) player.getStats()).getMaxEnergy()) + 90;
            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, last, size / 2);

        }
        Drawer.setColorStatic(new Color(0.8f * blink, 0.1f * blink, 0.1f * blink));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, size / 2);
    }

    private void renderEnergy() {
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
        if (!energyLowDelay.isOver()) {
            Drawer.setColorStatic(new Color(0.45f, 0.4f, 0.1f));
            int last = 450 - Methods.roundDouble(energyNeeded * halfEnergyAngle / ((PlayerStats) player.getStats()).getMaxEnergy());
            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, last, endAngle, size / 2);
        }
        if (energyLowDelay.isOver() && !energyDelay.isOver()) {
            float alpha = (energyDelay.getDifference() / (float) energyDelay.getLength());
            Drawer.setColorStatic(new Color(0.4f, 0.4f, 1f, alpha));
            int last = 450 - Methods.roundDouble(lastEnergy * halfEnergyAngle / ((PlayerStats) player.getStats()).getMaxEnergy());
            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, last, endAngle, size / 2);
        }
        Drawer.setColorStatic(new Color(0.1f, 0.2f, 0.8f));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, size / 2);
    }

    private void renderPairArrow() {
        glDisable(GL_TEXTURE_2D);
        Drawer.setColorStatic(color);
        switch (corner) {
            case LEFT_TOP:
                Drawer.translate(3 * size + 5 * border / 2 - base, base + size / 4 - 2 * border / 3 + border / 6);
                break;
            case RIGHT_TOP:
                Drawer.translate(-size - base - border / 2, base + size / 4 - 2 * border / 3 + border / 6);
                break;
            case LEFT_BOTTOM:
                Drawer.translate(3 * size + 5 * border / 2 - base, size * 2 + base + size / 4 - 8 * border / 3 - border / 6);
                break;
            case RIGHT_BOTTOM:
                Drawer.translate(-size - base - border / 2, size * 2 + base + size / 4 - 8 * border / 3 - border / 6);
                break;
        }
        switch (((MyPlayer) player).getActiveActionPairID()) {
            case 0:
                arrows.renderTriangles(0, 1, 1);
                break;
            case 1:
                arrows.renderTriangles(1, 1, 1);
                break;
            case 2:
                arrows.renderTriangles(2, 1, 1);
                break;
            case 3:
                arrows.renderTriangles(3, 1, 1);
                break;
            default:
        }
        Drawer.returnToCentralPoint();
        glEnable(GL_TEXTURE_2D);
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
