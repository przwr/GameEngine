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
import game.place.Place;
import game.place.map.Map;
import game.place.map.WarpPoint;
import gamecontent.MyController;
import gamecontent.MyPlayer;
import sprites.Appearance;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {

    public final static byte RIGHT = 0, UP_RIGHT = 1, UP = 2, UP_LEFT = 3, LEFT = 4, DOWN_LEFT = 5, DOWN = 6, DOWN_RIGHT = 7;
    protected String name;
    protected double x, y;
    protected int direction;  //Obecny, bądź ostatni kierunek ruchu (stopnie)
    protected int direction8Way;  //Obecny, bądź ostatni kierunek ruchu (8 kierunków 0 - 7)
    protected int depth;
    protected boolean solid, emitter, emits, onTop, simpleLighting, visible, makeNoise;
    protected Appearance appearance;
    protected Figure collision;
    protected Stats stats;
    protected Place place;
    protected Map map;
    protected Map prevMap;
    protected int area = -1;
    protected int prevArea = -1;
    protected WarpPoint warp;
    protected boolean toUpdate;
    protected boolean canCover, canBeCovered = true;
    protected double jumpForce;
    protected double floatHeight;
    protected double gravity = 0.6;
    protected ArrayList<Light> lights;
    protected ArrayList<Interactive> interactiveObjects;

    public void update() {
    }

    public abstract void render();

    public abstract void renderShadowLit(Figure figure);

    public abstract void renderShadowLit(int xStart, int xEnd);

    public abstract void renderShadow(Figure figure);

    public abstract void renderShadow(int xStart, int xEnd);

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

    public void clearLights() {
        if (lights != null) {
            for (Light light : lights) {
                light.getFrameBufferObject().clear();
                light.getFrameBufferObject().delete();
            }
            lights.clear();
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

    public void reactToAttack(byte attackType, GameObject attacked, int hurt) {
        //<(^.^<) TIII DADADA NANA NANA KENTACZDIS (>^-')>
    }

    public boolean isPlayerTalkingToMe(MyPlayer player) {
        return player.getController().getAction(MyController.INPUT_ACTION).isKeyClicked()
                && !player.getTextController().isStarted() && Methods.pointDistanceSimple(getX(), getY(),
                player.getX(), player.getY()) <= Place.tileSize * 1.5 + Math.max(appearance.getActualWidth(), appearance.getActualHeight()) / 2
                && Math.abs(Methods.angleDifference(player.getDirection(), (int) Methods.pointAngleCounterClockwise(player.getX(), player.getY(), x, y))) <= 80;
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

    public int getXSpriteBegin(boolean... forCover) {
        if (appearance != null) {
            return (int) x + appearance.getXOffset() + appearance.getXStart();
        } else {
            return (int) x;
        }
    }

    public int getYSpriteBegin(boolean... forCover) {
        if (appearance != null) {
            return (int) y + appearance.getYOffset() + appearance.getYStart();
        } else {
            return (int) y;
        }
    }

    public int getXSpriteEnd(boolean... forCover) {
        if (appearance != null) {
            return (int) x + appearance.getXOffset() + appearance.getActualWidth();
        } else {
            return (int) x;
        }
    }

    public int getYSpriteEnd(boolean... forCover) {
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
            clearLights();
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

    public boolean canBeCovered() {
        return canBeCovered;
    }
}
