/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Methods;
import game.Settings;
import game.place.Light;
import game.place.Place;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

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
        this.light = new Light("light", 0.85f, 0.85f, 0.85f, Methods.roundHalfUp(Settings.scale * 1024), Methods.roundHalfUp(Settings.scale * 1024), place);
        initialize(name, x, y);
        setCollision(Rectangle.create(this.width, this.height, OpticProperties.NO_SHADOW, this));
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
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure, int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
    }

}
