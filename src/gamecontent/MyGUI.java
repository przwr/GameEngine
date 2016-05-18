/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.view.SplitScreen;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.entities.Player;
import game.gameobject.stats.PlayerStats;
import game.place.Place;
import game.place.cameras.Camera;
import net.jodk.lang.FastMath;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import sounds.Sound;
import sprites.SpriteSheet;
import sprites.vbo.VertexBufferObject;

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
    private Sound error;

    public MyGUI(String name, Place place) {
        super(name, place);
        initializeSounds();
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

    public final void initializeSounds() {
        if (error == null) {
            error = Settings.sounds.getSoundEffect("error.ogg");
        }
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
        arrows = VertexBufferObject.create(arrowsVertices);

        int r = size / 2 - border / 3 - border / 6;
        float[] ringVs1 = Drawer.getRingVertices(size / 2, size / 2, r, border / 3, size);
        float[] ringVs2 = Drawer.getRingVertices(size + border, size + border, size + border, border / 3, size);
        float[] ringVs3 = Drawer.getRingVertices(size + border, size + border, size, border / 3, size);
        addSize(0, ringVs1.length);
        addSize(1, ringVs2.length);
        addSize(2, ringVs3.length);
        rings = VertexBufferObject.create(Methods.concatAll(ringVs1, ringVs2, ringVs3));
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
    public void render() {
        if (on) {
            calculateLifeAlpha();
            renderGUI();
            Drawer.refreshColor();
        }
    }

    private void renderGUI() {
        if (player.isInGame()) {
            Color light = player.getMap().getLightColor();
            Camera cam = Place.currentCamera;
            color.r = color.g = color.b = (1f - Math.min(Math.min(light.r, light.g), light.b)) * 0.5f;
            if (place.playersCount == 1 || !place.singleCamera) {
                corner = SplitScreen.corner;
            } else {
                int i;
                for (i = 0; i < place.playersCount; i++) {
                    if (place.players[i] == player) {
                        break;
                    }
                }
                corner = i;
//                System.out.println(player.getName() + ": " + corner);
            }
            switch (corner) {
                case LEFT_TOP:
                    Drawer.regularShader.translateDefault(2 * border / 3, 2 * border / 3);
                    renderAllElements();
                    Drawer.regularShader.translateDefault(-2 * border / 3, -2 * border / 3);
                    break;
                case RIGHT_TOP:
                    Drawer.regularShader.translateDefault(cam.getWidth() - size * 2 - 8 * border / 3, 2 * border / 3);
                    renderAllElements();
                    Drawer.regularShader.translateDefault(-cam.getWidth() + size * 2 + 8 * border / 3, -2 * border / 3);
                    break;
                case LEFT_BOTTOM:
                    Drawer.regularShader.translateDefault(2 * border / 3, cam.getHeight() - size * 2 - 8 * border / 3);
                    renderAllElements();
                    Drawer.regularShader.translateDefault(-2 * border / 3, -cam.getHeight() + size * 2 + 8 * border / 3);
                    break;
                case RIGHT_BOTTOM:
                    Drawer.regularShader.translateDefault(cam.getWidth() - size * 2 - 8 * border / 3, cam.getHeight() - size * 2 - 8 * border / 3);
                    renderAllElements();
                    Drawer.regularShader.translateDefault(-cam.getWidth() + size * 2 + 8 * border / 3, -cam.getHeight() + size * 2 + 8 * border / 3);
                    break;
            }
        }
    }

    private void renderAllElements() {
        renderLife();
        renderEnergy();
        renderPairArrow();
        renderIcons();
        renderHandyMenu();
    }

    private void renderHandyMenu() {
        if (player.usesHandyMenu()) {
            int menuHeight = Display.getHeight() / 2 - (2 * size + 4 * border) - 4;
            int menuWidth = Display.getWidth() / 2 - border - 4;
            int secondPartHeight = size + 3 * border;
            int secondPartWidth = 2 * size + 3 * border;
            switch (corner) {
                case LEFT_TOP:
                    Drawer.regularShader.translate(0, secondPartWidth);
                    break;
                case RIGHT_TOP:
                    Drawer.regularShader.translate(-menuWidth + secondPartWidth - border, secondPartWidth);
                    break;
                case LEFT_BOTTOM:
                    Drawer.regularShader.translate(0, -menuHeight - border);
                    break;
                case RIGHT_BOTTOM:
                    Drawer.regularShader.translate(-menuWidth + secondPartWidth - border, -menuHeight - border);
                    break;
            }
            Drawer.setColorStatic(0.5f, 0.4f, 0.3f, 0.8f);
            Drawer.drawRectangle(0, 0, menuWidth, menuHeight);
            switch (corner) {
                case LEFT_TOP:
                    Drawer.drawRectangle(secondPartWidth, -secondPartHeight, menuWidth - secondPartWidth, secondPartHeight);
                    break;
                case RIGHT_TOP:
                    Drawer.drawRectangle(0, -secondPartHeight, menuWidth - secondPartWidth, secondPartHeight);
                    break;
                case LEFT_BOTTOM:
                    Drawer.drawRectangle(secondPartWidth, menuHeight, menuWidth - secondPartWidth, secondPartHeight);
                    break;
                case RIGHT_BOTTOM:
                    Drawer.drawRectangle(0, menuHeight, menuWidth - secondPartWidth, secondPartHeight);
                    break;
            }
        }
    }

    private void renderIcons() {
        Drawer.regularShader.translate(size / 2 + border, 2 * border / 3);
        renderItemIcon(0);
        renderIconRing();

        Drawer.regularShader.translateNoReset(size / 2 + border / 3, size / 2 + border / 3);
        renderItemIcon(0);
        renderIconRing();

        Drawer.regularShader.translateNoReset(-size / 2 - border / 3, size / 2 + border / 3);
        renderItemIcon(1);
        renderIconRing();

        Drawer.regularShader.translateNoReset(-size / 2 - border / 3, -size / 2 - border / 3);
        renderItemIcon(0);
        renderIconRing();

        renderLifeEnergyRings();

        switch (corner) {
            case LEFT_TOP:
                Drawer.regularShader.translate(2 * (size + border), -2 * border / 3 + border / 6);
                break;
            case RIGHT_TOP:
                Drawer.regularShader.translate(-2 * (size) - border, -2 * border / 3 + border / 6);
                break;
            case LEFT_BOTTOM:
                Drawer.regularShader.translate(2 * (size + border), size + 8 * border / 3 - border / 6);
                break;
            case RIGHT_BOTTOM:
                Drawer.regularShader.translate(-2 * (size) - border, size + 8 * border / 3 - border / 6);
                break;
        }

        renderAttackIcon(firstAttackType);
        renderIconRing();

        Drawer.regularShader.translateNoReset(size + border, 0);
        renderAttackIcon(secondAttackType);
        renderIconRing();
    }

    private void renderItemIcon(int icon) {
        Drawer.setColorStatic(Color.white);
        if (scale != 1f) {
            Drawer.regularShader.scaleNoReset(scale, scale);
        }
        itemIcons.renderPiece(icon);
        if (scale != 1f) {
            Drawer.regularShader.scaleNoReset(1 / scale, 1 / scale);
        }
    }

    private void renderAttackIcon(int icon) {
        Drawer.setColorStatic(Color.white);
        if (scale != 1f) {
            Drawer.regularShader.scaleNoReset(scale, scale);
        }
        attackIcons.renderPiece(icon);
        if (scale != 1f) {
            Drawer.regularShader.scaleNoReset(1 / scale, 1 / scale);
        }
    }

    private void
    renderIconRing() {
        Drawer.setColorStatic(color);
        Drawer.regularShader.setUseTexture(false);
        rings.renderTriangleStrip(placement[0], placement[1]);
        Drawer.regularShader.setUseTexture(true);
    }

    private void renderLifeEnergyRings() {
        Drawer.regularShader.resetTransformationMatrix();
        Drawer.setColorStatic(color);
        Drawer.regularShader.setUseTexture(false);
        rings.renderTriangleStrip(placement[2], placement[3]);
        rings.renderTriangleStrip(placement[4], placement[5]);
        Drawer.regularShader.setUseTexture(true);
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
        Drawer.regularShader.setUseTexture(false);
        Drawer.setColorStatic(color);
        switch (corner) {
            case LEFT_TOP:
                Drawer.regularShader.translate(3 * size + 5 * border / 2 - base, base + size / 4 - 2 * border / 3 + border / 6);
                break;
            case RIGHT_TOP:
                Drawer.regularShader.translate(-size - base - border / 2, base + size / 4 - 2 * border / 3 + border / 6);
                break;
            case LEFT_BOTTOM:
                Drawer.regularShader.translate(3 * size + 5 * border / 2 - base, size * 2 + base + size / 4 - 8 * border / 3 - border / 6);
                break;
            case RIGHT_BOTTOM:
                Drawer.regularShader.translate(-size - base - border / 2, size * 2 + base + size / 4 - 8 * border / 3 - border / 6);
                break;
        }
        switch (((MyPlayer) player).getActiveActionPairID()) {
            case 0:
                arrows.renderTriangles(0, 3);
                break;
            case 1:
                arrows.renderTriangles(3, 3);
                break;
            case 2:
                arrows.renderTriangles(6, 3);
                break;
            case 3:
                arrows.renderTriangles(9, 3);
                break;
            default:
        }
        Drawer.regularShader.setUseTexture(true);
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
        error.play();
    }

}
