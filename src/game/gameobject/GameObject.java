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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sprites.Animation;
import sprites.Sprite;

public abstract class GameObject {

    protected double x, y;
    protected int width, height, depth, xStart, yStart;
    protected boolean solid, emitter, emits, onTop, simpleLighting, visible;
    protected Sprite sprite;
    protected ArrayList<Light> lights = new ArrayList<>(1);
    protected String name;
    protected Map map;
    protected int area, prevArea = -1;
    protected Figure collision;
    protected Animation animation;

    public abstract void render(int xEffect, int yEffect);

    public abstract void renderShadowLit(int xEffect, int yEffect, Figure figure);

    public abstract void renderShadowLit(int xEffect, int yEffect, Figure figure, int xStart, int xEnd);

    public abstract void renderShadow(int xEffect, int yEffect, Figure figure);

    public abstract void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd);

    protected void initialize(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        depth = 0;
        visible = true;
    }

    public void changeMap(Map map) {
        if (this.map != null && this.map != map) {
            this.map.deleteObject(this);
        }
        this.map = map;
        this.map.addObject(this);
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isOnTop() {
        return onTop;
    }

    public boolean isEmitter() {
        return emitter;
    }

    public boolean isEmits() {
        return emits;
    }

    public boolean isSimpleLighting() {
        return simpleLighting;
    }

    public boolean isVisible() {
        return visible;
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

    public int getXObjectBegin() {
        return (int) x + sprite.getXStart() + xStart;
    }

    public int getYObjectBegin() {
        return (int) y + sprite.yStart() + yStart;
    }

    public int getXObjectEnd() {
        return (int) x + sprite.getXStart() + xStart + width;
    }

    public int getYObjectEnd() {
        return (int) y + sprite.yStart() + yStart + height;
    }

    public Figure getCollision() {
        return collision;
    }

    public Map getMap() {
        return map;
    }

    public int getArea() {
        return area;
    }

    public int getPrevArea() {
        return prevArea;
    }

    public int getCollisionWidth() {
        return collision != null ? collision.getWidth() : width;
    }

    public int getCollisionHeight() {
        return collision != null ? collision.getHeight() : height;
    }

    public int getStartX() {
        return xStart;
    }

    public int getStartY() {
        return yStart;
    }

    public String getName() {
        return name;
    }

    public List<Light> getLights() {
        return Collections.unmodifiableList(lights);
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public void setOnTop(boolean onTop) {
        this.onTop = onTop;
    }

    public void setEmits(boolean emits) {
        this.emits = emits;
    }

    public void setSimpleLighting(boolean simpleLighting) {
        this.simpleLighting = simpleLighting;
    }

    public void setVisible(boolean vis) {
        this.visible = vis;
    }

    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
        updateAreaPlacement();
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void updateAreaPlacement() {
        if (map != null) {
            prevArea = area;
            area = map.getAreaIndex(getX(), getY());
            map.changeAreaIfNeeded(area, prevArea, this);
        }
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCollision(Figure figure) {
        collision = figure;
    }

    public void setMapNotChange(Map map) {
        this.map = map;
    }
}
