/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.items;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.lights.Light;
import engine.utilities.Drawer;
import game.gameobject.GameObject;
import game.place.Place;
import org.newdawn.slick.Color;


/**
 * @author Przemek
 */
public class LightSource extends GameObject {

    public LightSource(int x, int y, int width, int height, String name, Place place, String appearanceName, boolean solid) {
        this.solid = solid;
        this.appearance = place.getSprite(appearanceName, "");
        emitter = true;
        emits = true;
        Color lightColor = new Color(0.85f, 0.85f, 0.85f);
        if (lights.isEmpty()) {
            addLight(Light.createNoShadows(place.getSpriteSheetSetScale("light", ""), lightColor, 768, 768, this, 0));
            addLight(Light.createNoShadows(place.getSpriteSheetSetScale("light", ""), lightColor, 768, 768, this, 1));
            addLight(Light.createNoShadows(place.getSpriteSheetSetScale("light", ""), lightColor, 768, 768, this, 2));
            addLight(Light.createNoShadows(place.getSpriteSheetSetScale("light", ""), lightColor, 768, 768, this, 3));
        }
        initialize(name, x, y);
        setCollision(Rectangle.create(width, height, OpticProperties.NO_SHADOW, this));
    }

    @Override
    public void render() {
        if (appearance != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            appearance.render();
        }
    }

    @Override
    public void renderShadowLit(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeShade(appearance, 1, getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadow(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeBlack(appearance, getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartShade(appearance, 1, getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartBlack(appearance, getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }
}
