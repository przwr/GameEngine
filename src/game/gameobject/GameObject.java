/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

/**
 * @author przemek
 */

import collision.Figure;
import engine.lights.Light;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.InteractiveActivator;
import game.gameobject.stats.Stats;
import game.place.map.Map;
import game.place.map.WarpPoint;
import sprites.Appearance;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

    public final static byte RIGHT = 0, UP_RIGHT = 1, UP = 2, UP_LEFT = 3, LEFT = 4, DOWN_LEFT = 5, DOWN = 6, DOWN_RIGHT = 7;
    protected final ArrayList<Light> lights = new ArrayList<>(1);
    protected final ArrayList<Interactive> interactiveObjects = new ArrayList<>(1);
    protected double x, y;
    protected int depth;
    protected boolean solid;
    protected boolean emitter;
    protected boolean emits;
    protected boolean onTop;
    protected boolean simpleLighting;
    protected boolean visible;
    protected boolean makeNoise;
    protected double jumpHeight;
    protected double jumpForce;
    private final double gravity = 0.6f;
    protected Appearance appearance;
    protected Stats stats;
    protected String name;
    protected Map map;
    protected Map prevMap;
    protected int area = -1;
    protected Figure collision;
    protected WarpPoint warp;
    private int direction;  //Obecny, bądź ostatni kierunek ruchu (stopnie)
    private int direction8Way;  //Obecny, bądź ostatni kierunek ruchu (8 kierunków 0 - 7)
    private int prevArea = -1;

    public abstract void render(int xEffect, int yEffect);

    public abstract void renderShadowLit(int xEffect, int yEffect, Figure figure);

    public abstract void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd);

    public abstract void renderShadow(int xEffect, int yEffect, Figure figure);

    public abstract void renderShadow(int xEffect, int yEffect, int xStart, int xEnd);

    protected void initialize(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        depth = 0;
        visible = true;
    }

    public void changeMap(Map map, int x, int y) {
        if (this.map != map) {
            if (this.map != null) {
                this.map.deleteObject(this);
            }
            this.map = map;
            this.setPositionAreaUpdate(x, y);
            this.map.addObject(this);
        }
    }

    public void updateAreaPlacement() {
        if (map != null) {
            if (area != -1 && prevArea != -1) {
                prevArea = area;
                area = map.getAreaIndex(getX(), getY());
                if (area != prevArea && map == prevMap) {
                    map.changeArea(area, prevArea, this);
                }
                prevMap = map;
            } else {
                area = map.getAreaIndex(getX(), getY());
                prevArea = area;
            }
        }
    }

    protected void addLight(Light light) {
        lights.add(light);
    }

    protected void addInteractive(Interactive interactive) {
        interactiveObjects.add(interactive);
    }

    protected void removeInteractive(Interactive interactive) {
        interactiveObjects.remove(interactive);
    }

    public void getHurt(int knockbackPower, double jumpPower, GameObject attacker) {
        //<(^.^<) TIII DADADA NANA NANA KENTACZDIS (>^-')>
    }
    
    public void reactToAttack(byte attackType, GameObject attacked) {
        //<(^.^<) TIII DADADA NANA NANA KENTACZDIS (>^-')>
    }
    
    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public boolean isOnTop() {
        return onTop;
    }

    public void setOnTop(boolean onTop) {
        this.onTop = onTop;
    }

    public boolean isEmitter() {
        return emitter;
    }

    public boolean isEmits() {
        return emits;
    }

    public void setEmits(boolean emits) {
        this.emits = emits;
    }

    public boolean isSimpleLighting() {
        return simpleLighting;
    }

    public void setSimpleLighting(boolean simpleLighting) {
        this.simpleLighting = simpleLighting;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean vis) {
        this.visible = vis;
    }

    public boolean isInteractive() {
        return !interactiveObjects.isEmpty();
    }

    public int getX() {
        return (int) x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public int getY() {
        return (int) y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getXInDouble() {
        return x;
    }

    public double getYInDouble() {
        return y;
    }

    public int getDepth() {
        return (int) (depth + y);
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getPureDepth() {
        return depth;
    }

    public int getXEnd() {
        return (int) x + collision.getWidth();
    }

    public int getYEnd() {
        return (int) y + collision.getHeight();
    }

    public int getEndOfX() {
        return (int) x + collision.getWidth() / 2;
    }

    public int getEndOfY() {
        return (int) y + collision.getHeight() / 2;
    }

    public int getXSpriteTextureCorner() {
        if (appearance != null) {
            return (int) x + appearance.getXOffset();
        } else {
            return (int) x;
        }
    }

    public int getYSpriteTextureCorner() {
        if (appearance != null) {
            return (int) y + appearance.getYOffset();
        } else {
            return (int) y;
        }
    }

    public int getXSpriteBegin() {  //TODO Źle działa dla animacji jeśli jest użyty FrameBufferedSpriteSheet :(
        if (appearance != null) {
            return (int) x + appearance.getXOffset() + appearance.getXStart();
        } else {
            return (int) x;
        }
    }

    public int getYSpriteBegin() {  //TODO Źle działa dla animacji jeśli jest użyty FrameBufferedSpriteSheet :(
        if (appearance != null) {
            return (int) y + appearance.getYOffset() + appearance.getYStart();
        } else {
            return (int) y;
        }
    }

    public int getXSpriteEnd() {    //TODO Źle działa dla animacji jeśli jest użyty FrameBufferedSpriteSheet :(
        if (appearance != null) {
            return (int) x + appearance.getXOffset() + appearance.getXStart() + appearance.getActualWidth();
        } else {
            return (int) x;
        }
    }

    public int getYSpriteEnd() {    //TODO Źle działa dla animacji jeśli jest użyty FrameBufferedSpriteSheet :(
        if (appearance != null) {
            return (int) y + appearance.getYOffset() + appearance.getYStart() + appearance.getActualHeight();
        } else {
            return (int) y;
        }
    }

    public int getXSpriteOffset() {
        return appearance.getXOffset();
    }

    public int getYSpriteOffset() {
        return appearance.getYOffset();
    }

    public int getXSpriteOffsetWidth() {
        return appearance.getXOffset() + appearance.getWidth();
    }

    public Figure getCollision() {
        return collision;
    }

    public void setCollision(Figure figure) {
        collision = figure;
    }

    public Map getMap() {
        return map;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public double getJumpHeight() {
        return jumpHeight;
    }

    public void setJumpHeight(double jumpHeight) {
        this.jumpHeight = jumpHeight;
    }
    
    public double getJumpForce() {
        return jumpForce;
    }

    public void setJumpForce(double jumpForce) {
        this.jumpForce = jumpForce;
    }

    protected void updateWithGravity() {
        if (jumpHeight > 0 || jumpForce > 0) {
            jumpHeight += jumpForce;
            jumpForce -= gravity;
        } else {
            jumpHeight = 0;
        }
    }
    
    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction % 360;
        direction8Way = (int) (((float) direction / 45 + 0.5f) % 8);
    }

    public int getDirection8Way() {
        return direction8Way;
    }
    
    public void setDirection8way(int direction8Way) {
        this.direction8Way = direction8Way % 8;
        direction = direction8Way * 45;
    }

    public int getCollisionWidth() {
        return collision != null ? collision.getWidth() : appearance.getActualWidth();
    }

    public int getCollisionHeight() {
        return collision != null ? collision.getHeight() : appearance.getActualHeight();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Light> getLights() {
        return lights;
    }

    public List<Interactive> getInteractiveObjects() {
        return interactiveObjects;
    }

    public InteractiveActivator getActivator(int i) {
        return interactiveObjects.get(i).getActivator();
    }
    
    public Appearance getAppearance() {
        return appearance;
    }

    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
    }

    public void setPositionAreaUpdate(double x, double y) {
        setX(x);
        setY(y);
        updateAreaPlacement();
    }

    public void setMapNotChange(Map map) {
        this.map = map;
    }

    public Stats getStats() {
        return stats;
    }

    public void delete() {
        map.deleteObject(this);
    }

    public void setWarp(WarpPoint warp) {
        this.warp = warp;
    }

    public boolean isMakeNoise() {
        return makeNoise;
    }

    public void setMakeNoise(boolean makeNoise) {
        this.makeNoise = makeNoise;
    }
}
