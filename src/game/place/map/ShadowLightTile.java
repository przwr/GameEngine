/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import static collision.OpticProperties.FULL_SHADOW;
import static collision.OpticProperties.IN_SHADE_NO_SHADOW;
import collision.Rectangle;
import engine.utilities.Drawer;
import game.place.Place;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class ShadowLightTile extends ForegroundTile {

    private final boolean isShadow;

    private final Color alteredColor = new Color(0, 0, 0);
    private Color tmpColor;

    ShadowLightTile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int type, int yStart, boolean round, boolean solid, boolean isShadow) {
        super(spriteSheet, size, xSheet, ySheet, type, yStart, round, solid);
        this.isShadow = isShadow;
    }

    public static ForegroundTile createOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart, boolean isShadow) {
        return new ShadowLightTile(spriteSheet, size, xSheet, ySheet, IN_SHADE_NO_SHADOW, yStart, false, false, isShadow);
    }

    public static ForegroundTile createOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean isShadow) {
        return new ShadowLightTile(spriteSheet, size, xSheet, ySheet, IN_SHADE_NO_SHADOW, 0, false, false, isShadow);
    }

    public static ForegroundTile createWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart, boolean isShadow) {
        return new ShadowLightTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, yStart, false, true, isShadow);
    }

    public static ForegroundTile createWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean isShadow) {
        return new ShadowLightTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, 0, false, true, isShadow);
    }

    public static ForegroundTile createRoundOrdinaryShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart, boolean isShadow) {
        return new ShadowLightTile(spriteSheet, size, xSheet, ySheet, IN_SHADE_NO_SHADOW, yStart, true, false, isShadow);
    }

    public static ForegroundTile createRoundOrdinary(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean isShadow) {
        return new ShadowLightTile(spriteSheet, size, xSheet, ySheet, IN_SHADE_NO_SHADOW, 0, true, false, isShadow);
    }

    public static ForegroundTile createRoundWallShadowHeight(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart, boolean isShadow) {
        return new ShadowLightTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, yStart, true, true, isShadow);
    }

    public static ForegroundTile createRoundWall(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, boolean isShadow) {
        return new ShadowLightTile(spriteSheet, size, xSheet, ySheet, FULL_SHADOW, 0, true, true, isShadow);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        tmpColor = Drawer.getCurrentColor();
        if (isShadow) {
            alteredColor.r = tmpColor.r;
            alteredColor.g = tmpColor.g;
            alteredColor.b = tmpColor.b;
            alteredColor.a = Place.getDayCycle().getDayShadowAlpha();
        } else {
            alteredColor.r = alteredColor.g = alteredColor.b = 0.5f;
            alteredColor.a = Place.getDayCycle().getNightLightAlpha();
        }
        Drawer.setColor(alteredColor);
        super.render(xEffect, yEffect);
        Drawer.refreshColor();
    }

    public boolean isShadow() {
        return isShadow;
    }

    @Override
    public String saveToString(SpriteSheet s, int xBegin, int yBegin, int tile) {
        return "s" + super.saveToString(s, xBegin, yBegin, tile) + ":" + (isShadow ? 1 : 0);
    }

    @Override
    public void setDepth(int depth) {
        this.depth = depth - 1;
    }

    @Override
    public int getPureDepth() {
        return depth + 1;
    }
}
