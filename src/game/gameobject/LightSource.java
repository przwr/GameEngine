/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Drawer;
import game.Settings;
import game.place.Light;
import game.place.Place;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author Domi
 */
public class LightSource extends GameObject {

    public LightSource(int x, int y, int xStart, int yStart, int width, int height, String name, Place place, String spriteName, boolean solid) {
        this.width = width;
        this.height = height;
        this.solid = solid;
        this.xStart = xStart;
        this.yStart = yStart;
        this.sprite = place.getSprite(spriteName);
        emitter = true;
        emits = true;
        Color lightColor = new Color(0.85f, 0.85f, 0.85f);
        addLight(Light.create(place.getSpriteSheetSetScale("light"), lightColor, 768, 768, this, 0));
        addLight(Light.create(place.getSpriteSheetSetScale("light"), lightColor, 768, 768, this, 1));
        addLight(Light.create(place.getSpriteSheetSetScale("light"), lightColor, 768, 768, this, 2));
        addLight(Light.create(place.getSpriteSheetSetScale("light"), lightColor, 768, 768, this, 3));
        initialize(name, x, y);
        setCollision(Rectangle.create(this.width / 2, this.height / 3, OpticProperties.NO_SHADOW, this));
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(Settings.scale, Settings.scale, 1);
            }
            glTranslatef(getX(), getY(), 0);
            sprite.render();
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect,  Figure figure) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapeInShade(sprite, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapeInBlack(sprite);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect,  Figure figure, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapePartInShade(sprite, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.drawShapePartInBlack(sprite, xStart, xEnd);
            glPopMatrix();
        }
    }
}
