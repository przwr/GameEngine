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
import collision.Figure;
import game.place.Light;
import game.place.Map;
import game.place.Place;
import java.util.Objects;
import sprites.Sprite;

public abstract class GameObject {

    protected double x, y;
    protected int width, height, depth, startX, startY;
    protected boolean solid, emitter, emits, top, simpleLighting;
    protected Sprite sprite;
    protected Light light;
    protected String name;
    protected Place place;
    protected Map map;
    protected Figure collision;

    public abstract void render(int xEffect, int yEffect);

    public abstract void renderShadowLit(int xEffect, int yEffect, float color, Figure f);

    public abstract void renderShadowLit(int xEffect, int yEffect, float color, Figure f, int xs, int xe);

    public abstract void renderShadow(int xEffect, int yEffect, Figure f);

    public abstract void renderShadow(int xEffect, int yEffect, Figure f, int xs, int xe);

    protected void init(String name, int x, int y, Place place) {
        this.x = x;
        this.y = y;
        depth = 0;
        this.name = name;
        this.place = place;
    }

    public void changeMap(Map otherMap) {
        if (map != null) {
            map.deleteObj(this);
        }
        map = otherMap;
        map.addObj(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GameObject) {
            GameObject go = (GameObject) o;
            if (go.getX() == getX() && go.getY() == getY() && go.getName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 83 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 83 * hash + (this.solid ? 1 : 0);
        hash = 83 * hash + Objects.hashCode(this.sprite);
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isOnTop() {
        return top;
    }

    public boolean isEmitter() {
        return emitter;
    }

    public boolean isEmits() {
        return emits;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public double getXInDouble() {
        return x;
    }

    public double getYInDouble() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return (int) (depth + y);
    }

    public int getPureDepth() {
        return depth;
    }

    public int getEndOfX() {
        return (int) x + collision.getWidth() / 2;
    }

    public int getEndOfY() {
        return (int) y + collision.getHeight() / 2;
    }

    public int getObjBegOfX() {
        return (int) x + sprite.getSx() + startX;
    }

    public int getObjBegOfY() {
        return (int) y + sprite.getSy() + startY;
    }

    public int getObjEndOfX() {
        return (int) x + sprite.getSx() + startX + width;
    }

    public int getObjEndOfY() {
        return (int) y + sprite.getSy() + startY + height;
    }

    public int getObjectBegY() {
        return (int) y + collision.getHeight() / 2 - height;
    }

    public int getObjectEndY() {
        return (int) y + collision.getHeight() / 2;
    }

    public Figure getCollision() {
        return collision;
    }

    public Map getMap() {
        return map;
    }

    public int getCollisionWidth() {
        return collision != null ? collision.getWidth() : width;
    }

    public int getCollisionHeight() {
        return collision != null ? collision.getHeight() : height;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public String getName() {
        return name;
    }

    public Light getLight() {
        return light;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Place getPlace() {
        return place;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public void setOnTop(boolean top) {
        this.top = top;
    }

    public void setEmits(boolean emits) {
        this.emits = emits;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDepth(int d) {
        depth = d;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setCollision(Figure f) {
        collision = f;
    }

    public void setMapNotChange(Map otherMap) {
        map = otherMap;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
