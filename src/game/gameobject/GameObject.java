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
import engine.utilities.Methods;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.activator.InteractiveActivator;
import game.gameobject.stats.Stats;
import game.place.map.Map;
import game.place.map.WarpPoint;
import sprites.Appearance;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

    public final static byte RIGHT = 0, UP_RIGHT = 1, UP = 2, UP_LEFT = 3, LEFT = 4, DOWN_LEFT = 5, DOWN = 6, DOWN_RIGHT = 7;
    protected ArrayList<Light> lights;
    protected ArrayList<Interactive> interactiveObjects;
    protected double x, y;
    protected int depth;
    protected boolean solid;
    protected boolean emitter;
    protected boolean emits;
    protected boolean onTop;
    protected boolean simpleLighting;
    protected boolean visible;
    protected boolean makeNoise;
    protected double floatHeight;
    protected double jumpForce;
    protected Appearance appearance;
    protected Stats stats;
    protected String name;
    protected Map map;
    protected Map prevMap;
    protected int area = -1;
    protected Figure collision;
    protected WarpPoint warp;
    protected double gravity = 0.6;
    protected int direction;  //Obecny, bądź ostatni kierunek ruchu (stopnie)
    protected int direction8Way;  //Obecny, bądź ostatni kierunek ruchu (8 kierunków 0 - 7)
    protected int prevArea = -1;
    protected boolean toUpdate;
    protected boolean canCover;

    public void update() {
    }

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
        updateAreaPlacement();
    }


    public void changeMap(Map map, int x, int y) {
        if (this.map != map) {
            if (this.map != null) {
                this.map.deleteObject(this);
            }
            this.map = map;
            this.setPositionWithoutAreaUpdate(x, y);
            if (prevArea != -1 && prevMap != null) {
                updateAreaPlacement();
            }
            this.map.addObject(this);
        }
    }

    public void updateAreaPlacement() {
        if (map != null) {
            prevArea = area;
            area = map.getAreaIndex(getX(), getY());
            if (area != prevArea && map == prevMap && prevArea != -1) {
                map.changeArea(area, prevArea, this);
            }
            prevMap = map;
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

    public void getHurt(int knockBackPower, double jumpPower, GameObject attacker) {
        //<(^.^<) TIII DADADA NANA NANA KENTACZDIS (>^-')>
    }

    public void reactToAttack(byte attackType, GameObject attacked) {
        //<(^.^<) TIII DADADA NANA NANA KENTACZDIS (>^-')>
    }

    public void updateCausedDamage(GameObject hurted, int hurt) {
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
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
        return interactiveObjects != null && !interactiveObjects.isEmpty();
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
        return (int) x + collision.getWidthHalf();
    }

    public int getEndOfY() {
        return (int) y + collision.getHeightHalf();
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

    public int getXSpriteBegin(boolean... forCover) {  //TODO Źle działa dla animacji jeśli jest użyty FrameBufferedSpriteSheet :(
        if (appearance != null) {
            return (int) x + appearance.getXOffset() + appearance.getXStart();
        } else {
            return (int) x;
        }
    }

    public int getYSpriteBegin(boolean... forCover) {  //TODO Źle działa dla animacji jeśli jest użyty FrameBufferedSpriteSheet :(
        if (appearance != null) {
            return (int) y + appearance.getYOffset() + appearance.getYStart();
        } else {
            return (int) y;
        }
    }

    public int getXSpriteEnd(boolean... forCover) {    //TODO Źle działa dla animacji jeśli jest użyty FrameBufferedSpriteSheet :(
        if (appearance != null) {
            return (int) x + appearance.getXOffset() + appearance.getActualWidth();
        } else {
            return (int) x;
        }
    }

    public int getYSpriteEnd(boolean... forCover) {    //TODO Źle działa dla animacji jeśli jest użyty FrameBufferedSpriteSheet :(
        if (appearance != null) {
            return (int) y + appearance.getYOffset() + appearance.getActualHeight();
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
        if (prevArea == -1) {
            this.prevArea = area;
        }
    }

    public double getFloatHeight() {
        return floatHeight;
    }

    public void setFloatHeight(double floatHeight) {
        this.floatHeight = floatHeight;
    }

    public double getJumpForce() {
        return jumpForce;
    }

    public void setJumpForce(double jumpForce) {
        this.jumpForce = jumpForce;
    }

    protected void updateWithGravity() {
        if (floatHeight > 0 || jumpForce > 0) {
            floatHeight += jumpForce;
            jumpForce -= gravity;
        } else {
            floatHeight = 0;
        }
        if (floatHeight < 0) {
            floatHeight = 0;
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

    public Interactive getInteractive(byte attackType) {
        for (Interactive interactive : interactiveObjects) {
            if (interactive.getAttackType() == attackType) {
                return interactive;
            }
        }
        return null;
    }

    public InteractiveActivator getAttackActivator(byte attackType, Object modifier) {
        for (Interactive i : interactiveObjects) {
            if (i.getAttackType() == attackType) {
                i.setActionModifier(modifier);
                return i.getActivator();
            }
        }
        return null;
    }

    public InteractiveActivator getAttackActivator(Object modifier) {
        Interactive ret = interactiveObjects.get(0);
        ret.setActionModifier(modifier);
        return ret.getActivator();
    }

    public InteractiveActivator getAttackActivator(byte attackType) {
        for (Interactive i : interactiveObjects) {
            if (i.getAttackType() == attackType) {
                return i.getActivator();
            }
        }
        return null;
    }

    public InteractiveActivator getAttackActivator() {
        return interactiveObjects.get(0).getActivator();
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public int getActualHeight() {
        if (appearance != null) {
            return appearance.getActualHeight();
        }
        if (collision != null) {
            return Methods.roundDouble(collision.getHeight() * Methods.ONE_BY_SQRT_ROOT_OF_2);
        }
        return 0;
    }

    public boolean isInCollidingPosition() {
        return collision != null;
    }

    public void setPositionWithoutAreaUpdate(double x, double y) {
        setX(x);
        setY(y);
    }

    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
        updateAreaPlacement();
    }

    public void setMapNotChange(Map map) {
        this.map = map;
        if (prevMap == null) {
            prevMap = map;
        }
    }

    public Stats getStats() {
        return stats;
    }

    public void delete() {
        if (map != null) {
            map.deleteObject(this);
        }
    }

    public boolean isMakeNoise() {
        return makeNoise;
    }

    public void setMakeNoise(boolean makeNoise) {
        this.makeNoise = makeNoise;
    }

    public boolean isInBlock() {
        return false;
    }

    public boolean isToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(boolean toUpdate) {
        this.toUpdate = toUpdate;
    }

    public void setCanCover(boolean canCover) {
        this.canCover = canCover;
    }

    public boolean canCover() {
        return canCover;
    }

    public WarpPoint getWarp() {
        return warp;
    }

    public void setWarp(WarpPoint warp) {
        this.warp = warp;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof GameObject && x == ((GameObject) o).x && y == ((GameObject) o).y && appearance == ((GameObject) o).appearance;
    }

    @Override
    public int hashCode() {
        int hash = Methods.roundDouble(83 * x + 17 * y);
        if (appearance != null) {
            hash += 13 * appearance.hashCode();
        }
        return hash;
    }
}
