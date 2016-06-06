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
import game.gameobject.items.Item;
import game.gameobject.items.Weapon;
import game.gameobject.stats.PlayerStats;
import game.place.Place;
import game.place.cameras.Camera;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import net.jodk.lang.FastMath;
import org.newdawn.slick.Color;
import sounds.Sound;
import sprites.SpriteSheet;
import sprites.shaders.ShaderProgram;
import sprites.vbo.VertexBufferObject;

/**
 * @author Wojtek
 */
public class MyGUI extends GUIObject {


    public final static int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3, USE = 4;
    public static final int QUICK = 0, GEAR = 1, EQUIP = 2;
    private final static int LEFT_TOP = 0, RIGHT_TOP = 1, LEFT_BOTTOM = 2, RIGHT_BOTTOM = 3;
    private SpriteSheet attackIcons, itemIcons;
    private int firstAttackType, secondAttackType;
    private float lifeAlpha;
    private int emptySlot;
    private float lastLife, lastEnergy, energyNeeded;
    private boolean riseLifeAlpha, on = true;
    private Color color = new Color(0, 0, 0);
    private Color selected = new Color(0.2f, 0.7f, 0.3f);
    private Color active = new Color(0.3f, 0.3f, 0.2f);
    private Delay lifeDelay = Delay.createInSeconds(1),
            energyDelay = Delay.createInSeconds(1),
            energyLowDelay = Delay.createInMilliseconds(250),
            helpDelay = Delay.createInMilliseconds(750);
    private VertexBufferObject arrows, rings;
    private int[] placement = new int[2 * 3];
    private int corner = LEFT_TOP, activeWeapon = -1;
    private int size, base, border, innerSize;
    private float scale;
    private Sound error;
    private int navigation = 0, navX = 0, navY = 0;
    private int eqX = 3, eqY = 4;
    private TextPiece info = new TextPiece("", (int) (12 * (0.75 / Settings.nativeScale)), TextMaster.getFont("Lato-Regular"), 64, true);
    private TextPiece gearTitle = new TextPiece("", (int) (12 * (0.75 / Settings.nativeScale)), TextMaster.getFont("Lato-Regular"), 64, true);
    private TextPiece equipTitle = new TextPiece("", (int) (12 * (0.75 / Settings.nativeScale)), TextMaster.getFont("Lato-Regular"), 64, true);

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
        float[] ringVs3 = Drawer.getRingVertices(size + border, size + border, size - 3 * border, border / 3, size);
//        float[] ringVs3 = Drawer.getRingVertices(size + border, size + border, size, border / 3, size);
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
            updateHandyMenu();
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
            int menuHeight = 384 - (2 * size + 4 * border) - 4;
            int secondPartHeight = size + 3 * border;
            int secondPartWidth = 2 * size + 3 * border - 10;
            switch (corner) {
                case LEFT_TOP:
                    Drawer.regularShader.translate(0, menuHeight - 3 * border + 1);
                    break;
                case RIGHT_TOP:
                    Drawer.regularShader.translate(-2, menuHeight - 3 * border + 1);
                    break;
                case LEFT_BOTTOM:
                    Drawer.regularShader.translate(0, -menuHeight - border + 7);
                    break;
                case RIGHT_BOTTOM:
                    Drawer.regularShader.translate(-2, -menuHeight - border + 7);
                    break;
            }
            if (player.isGearOn()) {
                Drawer.setColorStatic(0.4f, 0.4f, 0.4f, 0.9f);
                Drawer.drawRectangle(0, -12, secondPartWidth, menuHeight + 12);
                Item[] gear = ((MyPlayer) player).getGear();
                if (gear[3] == player.getActiveWeapon()) {
                    activeWeapon = 3;
                } else if (gear[5] == player.getActiveWeapon()) {
                    activeWeapon = 5;
                }
                drawSlots(eqX * eqY, eqX, 50, gear, navigation == GEAR);
                equipTitle.setLineMaxSize(secondPartWidth);
                equipTitle.setText(Settings.language.gui.Outfit
                );
                TextMaster.renderOnce(equipTitle, (int) ShaderProgram.getTransformationMatrix().m30,
                        (int) ShaderProgram.getTransformationMatrix().m31 - 12);
            }
            activeWeapon = -1;
            if (player.isEquipmentOn()) {
                if (player.isLoot()) {
                    Drawer.setColorStatic(0.8f, 0.7f, 0.3f, 0.9f);
                } else {
                    Drawer.setColorStatic(0.5f, 0.4f, 0.3f, 0.9f);
                }
                int space = 50;
                int xSlots = player.getXBackpackSize();
                int ySlots = player.getYBackpackSize();
                int xBackpack = 6 + xSlots * space;
                int yBackpack = 4 + ySlots * space;
                switch (corner) {
                    case LEFT_TOP:
                        Drawer.regularShader.translateNoReset(secondPartWidth + 8, -secondPartHeight);
                        break;
                    case RIGHT_TOP:
                        Drawer.regularShader.translateNoReset(-xBackpack - 8, -secondPartHeight);
                        break;
                    case LEFT_BOTTOM:
                        Drawer.regularShader.translateNoReset(secondPartWidth + 8, menuHeight + secondPartHeight - yBackpack);
                        break;
                    case RIGHT_BOTTOM:
                        Drawer.regularShader.translateNoReset(-xBackpack - 8, menuHeight + secondPartHeight - yBackpack);
                        break;
                }
                Drawer.drawRectangle(0, -12, xBackpack, yBackpack + 12);
                drawSlots(xSlots * ySlots, xSlots, space, player.getItems(), navigation == EQUIP);
                equipTitle.setLineMaxSize(xBackpack);
                equipTitle.setText(player.isLoot() ? player.getLootName() : Settings.language.gui.Equipment);
                TextMaster.renderOnce(equipTitle, (int) ShaderProgram.getTransformationMatrix().m30,
                        (int) ShaderProgram.getTransformationMatrix().m31 - 12);
            }
        }
    }

    private void drawSlots(int all, int cols, int space, Item[] items, boolean highLighted) {
        if (items != null && all != items.length) {
            System.out.println("Wielkość plecaka/pojemnika nie zgadza się z ilością przedmiotów");
        }
        int sel = -1;
        Drawer.regularShader.translateNoReset(-4, -5);
        for (int i = 0; i < all; i++) {
            active = new Color(0.9f, 1f, 0.7f);
            renderItemIcon(0, (i == activeWeapon) ? active : Color.white);
            if (items != null && items[i] != Item.EMPTY) {
                Drawer.regularShader.translateNoReset(32, 32);
                items[i].renderIcon();
                Drawer.regularShader.translateNoReset(-32, -32);
            }
            if (highLighted && i == navX + navY * cols) {
                sel = i;
            } else {
                renderIconRing(color);
            }
            Drawer.regularShader.translateNoReset(space, 0);
            if ((i + 1) % cols == 0) {
                Drawer.regularShader.translateNoReset(-space * cols, space);
            }
        }
        Drawer.regularShader.translateNoReset(4, -(all / cols) * space + 5);
        if (sel != -1) {
            Drawer.regularShader.translateNoReset(-4 + (sel % cols) * space, -5 + (sel / cols) * space);
            renderIconRing(selected);
            renderHelp();
            Drawer.regularShader.translateNoReset(4 - (sel % cols) * space, 5 - (sel / cols) * space);
        }
    }

    private void renderHelp() {
        if (helpDelay.isOver()) {
            Item item;
            switch (navigation) {
                case QUICK:
                    if (navX == 1 && navY == 1) {
                        info.setText("Świeczka");
                    } else {
                        return;
                    }
                    break;
                case GEAR:
                    item = ((MyPlayer) player).getGear()[navX + navY * eqX];
                    if (item != Item.EMPTY) {
                        info.setText(item.getName());
                    } else {
                        return;
                    }
                    break;
                case EQUIP:
                    Item[] items = player.getItems();
                    if (items != null) {
                        item = items[navX + navY * player.getXBackpackSize()];
                        if (item != Item.EMPTY) {
                            info.setText(item.getName());
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                    break;
            }

            Drawer.setColorStatic(selected);
            int yStart = 4 + (5 - info.getNumberOfLines()) * 12;
            Drawer.drawRectangle(0, yStart, 64, info.getNumberOfLines() * 12);
            TextMaster.renderOnce(info, (int) ShaderProgram.getTransformationMatrix().m30,
                    (int) ShaderProgram.getTransformationMatrix().m31 + yStart);
        }
    }

    private void renderIcons() {
        renderLifeEnergyRings();
        int sel = 0;
        boolean highlighted = player.usesHandyMenu() && navigation == QUICK;

        Drawer.regularShader.translate(size / 2 + border, 2 * border / 3);
        renderItemIcon(0, Color.white);
        if (highlighted && navX == 0 && navY == 0) {
            sel = 0;
        } else {
            renderIconRing(color);
        }

        Drawer.regularShader.translateNoReset(size / 2 + border / 3, size / 2 + border / 3);
        renderItemIcon(0, Color.white);
        if (highlighted && navX == 1 && navY == 0) {
            sel = 1;
        } else {
            renderIconRing(color);
        }

        Drawer.regularShader.translateNoReset(-size / 2 - border / 3, size / 2 + border / 3);
        renderItemIcon(1, Color.white);
        if (highlighted && navX == 1 && navY == 1) {
            sel = 2;
        } else {
            renderIconRing(color);
        }

        Drawer.regularShader.translateNoReset(-size / 2 - border / 3, -size / 2 - border / 3);
        renderItemIcon(0, Color.white);
        if (highlighted && navX == 0 && navY == 1) {
            sel = 3;
        } else {
            renderIconRing(color);
        }

        if (highlighted) {
            switch (sel) {
                case UP:
                    Drawer.regularShader.translate(size / 2 + border, 2 * border / 3);
                    break;
                case DOWN:
                    Drawer.regularShader.translateNoReset(size + 2 * border / 3, 0);
                    break;
                case LEFT:
                    Drawer.regularShader.translateNoReset(size / 2 + border / 3, size / 2 + border / 3);
                    break;
            }
            renderIconRing(selected);
            renderHelp();
        }


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
        renderIconRing(color);

        Drawer.regularShader.translateNoReset(size + border, 0);
        renderAttackIcon(secondAttackType);
        renderIconRing(color);
    }

    private void renderItemIcon(int icon, Color color) {
        Drawer.setColorStatic(color);
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

    private void renderIconRing(Color color) {
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
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize + 3 * border, 90, 270, size / 2);
//        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, 90, 270, size / 2);
        if (!lifeDelay.isOver()) {
            float alpha = (lifeDelay.getDifference() / (float) lifeDelay.getLength());
            Drawer.setColorStatic(new Color(1f * blink, 0.4f * blink, 0.4f * blink, alpha));
            int last = Methods.roundDouble(lastLife * halfLifeAngle / ((PlayerStats) player.getStats()).getMaxEnergy()) + 90;
            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize + 3 * border, startAngle, last, size / 2);
//            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, last, size / 2);
        }
        Drawer.setColorStatic(new Color(0.8f * blink, 0.1f * blink, 0.1f * blink));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize + 3 * border, startAngle, endAngle, size / 2);
//        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, size / 2);
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
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize + 3 * border, 270, 450, size / 2);
//        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, 270, 450, size / 2);
        if (!energyLowDelay.isOver()) {
            Drawer.setColorStatic(new Color(0.45f, 0.4f, 0.1f));
            int last = 450 - Methods.roundDouble(energyNeeded * halfEnergyAngle / ((PlayerStats) player.getStats()).getMaxEnergy());
            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize + 3 * border, last, endAngle, size / 2);
//            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, last, endAngle, size / 2);
        }
        if (energyLowDelay.isOver() && !energyDelay.isOver()) {
            float alpha = (energyDelay.getDifference() / (float) energyDelay.getLength());
            Drawer.setColorStatic(new Color(0.4f, 0.4f, 1f, alpha));
            int last = 450 - Methods.roundDouble(lastEnergy * halfEnergyAngle / ((PlayerStats) player.getStats()).getMaxEnergy());
            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize + 3 * border, last, endAngle, size / 2);
//            Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, last, endAngle, size / 2);
        }
        Drawer.setColorStatic(new Color(0.1f, 0.2f, 0.8f));
        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize + 3 * border, startAngle, endAngle, size / 2);
//        Drawer.drawBow(size + border, size + border, size + innerSize - 1, innerSize, startAngle, endAngle, size / 2);
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

    private void updateHandyMenu() {
        switch (navigation) {
            case QUICK:
                moveFromQuick();
                break;
            case GEAR:
                moveFromGear();
                break;
            case EQUIP:
                moveFromEquip();
                break;
        }
        if (player.getMenuKey() == USE) {
            helpDelay.terminate();
        } else if (player.getMenuKey() != -1) {
            helpDelay.start();
        }
        player.setMenuKey(-1);
    }

    private void moveFromGear() {
        switch (player.getMenuKey()) {
            case UP:
                if (navY == 0) {
                    navigation = QUICK;
                    navY = navX = 1;
                } else {
                    navY--;
                }
                break;
            case DOWN:
                if (navY == eqY - 1) {
                    navigation = QUICK;
                    navY = navX = 0;
                } else {
                    navY++;
                }
                break;
            case LEFT:
                if (navX == 0) {
                    if (player.isEquipmentOn()) {
                        if (corner == LEFT_TOP || corner == RIGHT_TOP) {
                            if (navY < player.getYBackpackSize() - 2) {
                                navigation = EQUIP;
                                navY += 2;
                                navX = player.getXBackpackSize() - 1;
                            } else {
                                navX = eqX - 1;
                            }
                        } else {
                            if (navY > 5 - player.getYBackpackSize()) {
                                navigation = EQUIP;
                                navY = player.getYBackpackSize() - (6 - navY);
                                navX = player.getXBackpackSize() - 1;
                            } else {
                                navX = eqX - 1;
                            }
                        }
                    } else {
                        navX = eqX - 1;
                    }
                } else {
                    navX--;
                }
                break;
            case RIGHT:
                if (navX == eqX - 1) {
                    if (player.isEquipmentOn()) {
                        if (corner == LEFT_TOP || corner == RIGHT_TOP) {
                            if (navY < player.getYBackpackSize() - 2) {
                                navigation = EQUIP;
                                navY += 2;
                                navX = 0;
                            } else {
                                navX = 0;
                            }
                        } else {
                            if (navY > 5 - player.getYBackpackSize()) {
                                navigation = EQUIP;
                                navY = player.getYBackpackSize() - (6 - navY);
                                navX = 0;
                            } else {
                                navX = 0;
                            }
                        }
                    } else {
                        navX = 0;
                    }
                } else {
                    navX++;
                }
                break;
            case USE:
                int i = navX + navY * eqX;
                Item[] items = ((MyPlayer) player).getGear();
                Item item = items[i];
                if (item != Item.EMPTY) {
                    if (item instanceof Weapon) {
                        player.addItem(item);
                        ((MyPlayer) player).putBackWeapon(item);
                    } else {
                        System.out.println("To jest " + item.getName());
                    }
                }
                break;
        }
    }

    private void moveFromEquip() {
        switch (player.getMenuKey()) {
            case UP:
                if (navY == 0) {
                    navY = player.getYBackpackSize() - 1;
                } else {
                    navY--;
                }
                break;
            case DOWN:
                if (navY == player.getYBackpackSize() - 1) {
                    navY = 0;
                } else {
                    navY++;
                }
                break;
            case LEFT:
                if (navX == 0) {
                    if (player.isEquipmentOn()) {
                        if (corner == LEFT_TOP || corner == RIGHT_TOP) {
                            if (navY < 2) {
                                navigation = QUICK;
                                navX = 1;
                                navY = 0;
                            } else if (navY >= 2 && navY <= eqY + 1) {
                                navigation = GEAR;
                                navY -= 2;
                                navX = eqX - 1;
                            } else {
                                navX = player.getXBackpackSize() - 1;
                            }
                        } else {
                            if (navY > player.getYBackpackSize() - 3) {
                                navigation = QUICK;
                                navX = 1;
                                navY = 0;
                            } else if (navY >= 0) {
                                navigation = GEAR;
                                navY = eqY - player.getYBackpackSize() + 2 - navY;
                                navX = eqX - 1;
                            } else {
                                navX = player.getXBackpackSize() - 1;
                            }
                        }
                    } else {
                        navX = player.getXBackpackSize() - 1;
                    }
                } else {
                    navX--;
                }
                break;
            case RIGHT:
                if (navX == player.getXBackpackSize() - 1) {
                    if (player.isEquipmentOn()) {
                        if (corner == LEFT_TOP || corner == RIGHT_TOP) {
                            if (navY < 2) {
                                navigation = QUICK;
                                navX = 0;
                                navY = 1;
                            } else if (navY >= 2 && navY <= eqY + 1) {
                                navigation = GEAR;
                                navY -= 2;
                                navX = 0;
                            } else {
                                navX = player.getXBackpackSize() - 1;
                            }
                        } else {
                            if (navY > player.getYBackpackSize() - 3) {
                                navigation = QUICK;
                                navX = 0;
                                navY = 1;
                            } else if (navY >= 0) {
                                navigation = GEAR;
                                navY = eqY - player.getYBackpackSize() + 2 - navY;
                                navX = 0;
                            } else {
                                navX = player.getXBackpackSize() - 1;
                            }
                        }
                    } else {
                        navX = 0;
                    }
                } else {
                    navX++;
                }
                break;
            case USE:
                int i = navX + navY * player.getXBackpackSize();
                Item[] items = player.getItems();
                if (items != null) {
                    Item item = items[i];
                    if (item != Item.EMPTY) {
                        if (player.isLoot()) {
                            if (player.addItem(item)) {
                                player.getItems()[i] = Item.EMPTY;
                            }
                        } else {
                            if (item instanceof Weapon) {
                                player.removeItem(item);
                                ((MyPlayer) player).addWeapon((Weapon) item);
                            } else {
                                System.out.println("To jest " + item.getName());
                            }
                        }
                    }
                }
                break;
        }
    }

    private void moveFromQuick() {
        switch (player.getMenuKey()) {
            case UP:
                if (navX == 0 && navY == 0) {
                    if (player.isGearOn()) {
                        navigation = GEAR;
                        navX = 1;
                        navY = eqY - 1;
                    } else {
                        navX = 1;
                        navY = 1;
                    }
                } else {
                    navX = 0;
                    navY = 0;
                }
                break;
            case DOWN:
                if (navX == 1 && navY == 1) {
                    if (player.isGearOn()) {
                        navigation = GEAR;
                        navX = 1;
                        navY = 0;
                    } else {
                        navX = 0;
                        navY = 0;
                    }
                } else {
                    navX = 1;
                    navY = 1;
                }
                break;
            case LEFT:
                if (navX == 0 && navY == 1) {
                    if (player.isEquipmentOn()) {
                        navigation = EQUIP;
                        navX = player.getXBackpackSize() - 1;
                        if (corner == LEFT_TOP || corner == RIGHT_TOP) {
                            navY = 0;
                        } else {
                            navY = player.getYBackpackSize() - 1;
                        }
                    } else {
                        navX = 1;
                        navY = 0;
                    }
                } else {
                    navX = 0;
                    navY = 1;
                }
                break;
            case RIGHT:
                if (navX == 1 && navY == 0) {
                    if (player.isEquipmentOn()) {
                        navigation = EQUIP;
                        navX = 0;
                        if (corner == LEFT_TOP || corner == RIGHT_TOP) {
                            navY = 0;
                        } else {
                            navY = player.getYBackpackSize() - 1;
                        }
                    } else {
                        navX = 0;
                        navY = 1;
                    }
                } else {
                    navX = 1;
                    navY = 0;
                }
                break;
            case USE:
                System.out.println("Use QUICK");
                break;
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

    public int getNavigation() {
        return navigation;
    }

    public void setNavigation(int navigation) {
        this.navigation = navigation;
    }

    public int getNavX() {
        return navX;
    }

    public void setNavX(int navX) {
        this.navX = navX;
    }

    public int getNavY() {
        return navY;
    }

    public void setNavY(int navY) {
        this.navY = navY;
    }

    public int getEqX() {
        return eqX;
    }

    public int getEqY() {
        return eqY;
    }

    public void resetHelpDelay() {
        helpDelay.start();
    }
}
