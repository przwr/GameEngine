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

    protected double x;
    protected double y;
    protected int width;
    protected int height;
    protected boolean solid;
    protected boolean emitter;
    protected boolean emits;
    protected boolean top;
    protected boolean stale;
    protected boolean animate;
    protected boolean simpleLighting;
    protected Sprite sprite;
    protected Light light;
    protected String name;
    protected Place place;
    protected Map map;
    protected Figure collision;
    protected int depth;

    protected int startX;   // *--<('~'<) BEGONE YOU EVIL SX AND SY!
    protected int startY;

    public abstract void render(int xEffect, int yEffect);

    public abstract void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f);

    public abstract void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f, int xs, int xe);

    protected void init(String name, int x, int y, Place place) {
        this.x = x;
        this.y = y;
        depth = 0;
        this.name = name;
        this.place = place;
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

    public int getDepth() {
        return (int) (depth + y);
    }

    public int getPureDepth() {
        return depth;
    }

    public void setDepth(int d) {
        depth = d;
    }

    public Figure getCollision() {
        return collision;
    }

    public void setCollision(Figure f) {
        collision = f;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map otherMap) {  //UWAGA! nie zmienia planszy! tylko ustawia
        map = otherMap;
    }

    public void changeMap(Map otherMap) {
        if (map != null) {
            map.deleteObj(this);
        }
        map = otherMap;
        map.addObj(this);
    }

    protected void init(String name, int x, int y, int sx, int sy, Place place) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.place = place;
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
        return collision != null ? collision.getWidth() : width;
    }

    public int getHeight() {
        return collision != null ? collision.getHeight() : height;
    }

    public int Width() {
        return width;
    }

    public int Height() {
        return height;
    }

    public boolean isOnTop() {
        return top;
    }

    public void setOnTop(boolean top) {
        this.top = top;
    }

    public boolean isStale() {
        return stale;
    }

    public void setStale(boolean st) {
        this.stale = st;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
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

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public void renderLight(Place place, int x, int y) {
        if (light != null) {
            light.render(this, place, x, y);
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

    public Sprite getSprite() {
        return sprite;
    }

    public boolean isAnimate() {
        return animate;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place p) {
        place = p;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
