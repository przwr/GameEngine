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
import game.place.Place;
import java.util.Objects;
import sprites.Sprite;

public abstract class GameObject {

    protected double x;
    protected double y;
    protected boolean solid;
    protected boolean emitter;
    protected boolean emits;
    protected boolean top;
    protected Sprite sprite;
    protected Sprite nLit;
    protected Sprite lit;
    protected Light light;
    protected String name;
    protected Place place;
    protected Figure collision;
    
    protected int sX;
    protected int sY;    
    protected int width;
    protected int height;

    public abstract void render(int xEffect, int yEffect);

    protected void init(String textureKey, String name, int x, int y, int sx, int sy, Place place) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.place = place;
        this.sprite = place.getSprite(textureKey, sx, sy);        
    }

    @Override
    public boolean equals(Object o) {
        try {
            GameObject go = (GameObject) o;
            if (go.getX() == getX() && go.getY() == getY() && go.getName().equals(getName()))
                return true;
        } catch(Exception e) {
            return false;
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
    
    public Figure getCollision(){
        return collision;
    }
    
    public void setCollision(Figure f){
        collision = f;
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

    public int getMidX() {
        return (int) (sX + x + width / 2);
    }

    public int getMidY() {
        return (int) (sY + y + height / 2);
    }

    public int getBegOfX() {
        return (int) (sX + x);
    }

    public int getBegOfY() {
        return (int) (sY + y);
    }

    public int getEndOfX() {
        return (int) (sX + x + width);
    }

    public int getEndOfY() {
        return (int) (sY + y + height);
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
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
