/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import engine.utilities.Drawer;
import game.place.Place;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

import static collision.OpticProperties.TRANSPARENT;

/**
 * @author Wojtek
 */
public class ShadowLightTile extends ForegroundTile {

    private final boolean isShadow;

    private final Color alteredColor = new Color(0, 0, 0);
    private int width;
    private int height;

    public ShadowLightTile(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int type, int yStart, boolean round, boolean solid, boolean isShadow) {
        super(spriteSheet, size, xSheet, ySheet, type, yStart, round, solid);
        this.isShadow = isShadow;
        hasStaticShadow = isShadow;
        setVisible(!isShadow);
    }

    public static ForegroundTile create(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart, boolean isShadow) {
        return new ShadowLightTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, yStart, false, false, isShadow);
    }

    public static ForegroundTile createSimple(SpriteSheet spriteSheet, int size, int xSheet, int ySheet, int yStart, boolean isShadow, int width, int height) {
        ShadowLightTile st = new ShadowLightTile(spriteSheet, size, xSheet, ySheet, TRANSPARENT, yStart, false, false, isShadow);
        st.width = width;
        st.height = height;
        st.simpleLighting = true;
        return st;
    }

    @Override
    public void render() {
        if (!isShadow) {
            alteredColor.r = alteredColor.g = alteredColor.b = 0.5f;
            alteredColor.a = Place.getDayCycle().getNightLightAlpha();
        }
        Drawer.setColorStatic(alteredColor);
        super.render();
        Drawer.refreshColor();
    }


    @Override
    public void renderStaticShadow() {
        if (simpleLighting) {
            Drawer.drawRectangle(0, 0, width, height);
        } else {
            appearance.render();
        }
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

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }
}
