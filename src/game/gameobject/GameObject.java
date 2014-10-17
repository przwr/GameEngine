/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

/**
 *
 * @author przemek
 */
import game.place.Light;
import game.place.Place;
import sprites.Sprite;

public abstract class GameObject {

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int sX;
    protected int sY;
    protected boolean solid;
    protected boolean emitter;
    protected boolean emits;
    protected boolean top;
    protected Sprite spr;
    protected Light light;
    protected String name;
    protected Place place;

    public abstract void render(int xEffect, int yEffect);

    protected void init(String textureKey, String name, int x, int y, int sx, int sy, Place place) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.place = place;
        this.spr = place.getSprite(textureKey, sx, sy);
    }

    public void addX(int x) {
        this.x += x;
    }

    public void addY(int y) {
        this.y += y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getMidX() {
        return sX + x + width / 2;
    }

    public int getMidY() {
        return sY + y + height / 2;
    }

    public int getBegOfX() {
        return sX + x;
    }

    public int getBegOfY() {
        return sY + y;
    }

    public int getEndOfX() {
        return sX + x + width;
    }

    public int getEndOfY() {
        return sY + y + height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isOnTop() {
        return top;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isEmitter() {
        return emitter;
    }

    public boolean isEmits() {
        return emits;
    }

    public int getSX() {
        return sX;
    }

    public int getSY() {
        return sY;
    }

    public void renderLight(Place place, int x, int y) {
        if (light != null) {
            light.render(this, place, x, y);
        }
    }

    public void renderShadowedLight(Place place, int x, int y) {
        if (light != null) {
            light.renderShadowed(this, place, x, y);
        }
    }

    public String getName() {
        return name;
    }

    public void setEmits(boolean emits) {
        this.emits = emits;
    }

    public Light getLight() {
        return light;
    }
}
