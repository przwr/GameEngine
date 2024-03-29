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
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.entities.Entity;
import game.gameobject.items.Item;
import game.place.Place;
import game.place.map.Map;
import game.place.map.WarpPoint;
import sprites.Appearance;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public abstract class GameObject {

    private final static int SOLID = 0, EMITTER = 1, EMITS = 2, ON_TOP = 3, SIMPLE_LIGHTING = 4, VISIBLE = 5, MAKE_NOISE = 6, HAS_STATIC_SHADOW = 7,
            TO_UPDATE = 8, CAN_COVER = 9, CAN_BE_COVERED = 10, CAN_INTERACT = 11;
    protected String name;
    protected double x, y;
    protected int depth;
    protected BitSet flags = new BitSet();
    protected Appearance appearance;
    protected Figure collision;
    protected Place place;
    protected Map map;
    protected Map prevMap;
    protected int area = -1;
    protected int prevArea = -1;
    protected WarpPoint warp;
    protected double upForce;
    protected double floatHeight;
    protected double gravity = 0.6;
    protected ArrayList<Light> lights;
    protected Item[] items;
    protected int xBackpackSize = 2;
    protected int yBackpackSize = 3;
    protected int xEffect, yEffect;

    {
        flags.set(CAN_BE_COVERED);
    }

    public void update() {
    }

    public void render() {
        if (appearance != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            appearance.render();
        }
    }

    public void interact(Entity entity) {
        //PANIE KIEROWNIKU!!! WSPOMÓŻ PIĄTAKIEM!
    }

    public void renderShadowLit(Figure figure) {
    }

    public void renderShadowLit(int xStart, int xEnd) {
    }

    public void renderShadow(Figure figure) {
    }

    public void renderShadow(int xStart, int xEnd) {
    }

    public void renderStaticShadow() {
        appearance.renderStaticShadow(this);
    }

    protected void initialize(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        depth = 0;
        setVisible(true);
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

    public void getHurt(int knockBackPower, double jumpPower, GameObject attacker) {
        //<(^.^<) TIII DADADA NANA NANA KENTACZDIS (>^-')>
    }

    public void reactToAttack(byte attackType, GameObject attacked, int hurt) {
        //<(^.^<) TIII DADADA NANA NANA KENTACZDIS (>^-')>
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
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

    public double getUpForce() {
        return upForce;
    }

    public void setUpForce(double upForce) {
        this.upForce = upForce;
    }

    protected void updateWithGravity() {
        if (floatHeight > 0 || upForce > 0) {
            floatHeight += upForce;
            upForce -= gravity;
            if (floatHeight < 0) {
                floatHeight = 0;
            }
        } else {
            floatHeight = 0;
        }

    }

    public boolean isInteractive() {
        return false;
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

    public int getActualWidth() {
        if (appearance != null) {
            return appearance.getActualWidth();
        }
        if (collision != null) {
            return collision.getWidth();
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
        if (map != null) {
            this.place = map.place;
        }
        if (prevMap == null) {
            prevMap = map;
        }
    }

    public void delete() {
        if (map != null) {
            clearLights();
            map.deleteObject(this);
        }
    }

    public boolean isInBlock() {
        return false;
    }

    public boolean canCover() {
        return isCanCover();
    }

    public WarpPoint getWarp() {
        return warp;
    }

    public void setWarp(WarpPoint warp) {
        this.warp = warp;
    }

    public boolean canBeCovered() {
        return isCanBeCovered();
    }

    public int getXEffect() {
        return xEffect;
    }

    public int getYEffect() {
        return yEffect;
    }

    public boolean isToUpdate() {
        return flags.get(TO_UPDATE);
    }

    public void setToUpdate(boolean toUpdate) {
        flags.set(TO_UPDATE, toUpdate);
    }

    public boolean isCanCover() {
        return flags.get(CAN_COVER);
    }

    public void setCanCover(boolean canCover) {
        flags.set(CAN_COVER, canCover);
    }

    public boolean isMakeNoise() {
        return flags.get(MAKE_NOISE);
    }

    public void setMakeNoise(boolean makeNoise) {
        flags.set(MAKE_NOISE, makeNoise);
    }

    public boolean hasStaticShadow() {
        return flags.get(HAS_STATIC_SHADOW);
    }

    public void setHasStaticShadow(boolean hasStaticShadow) {
        flags.set(HAS_STATIC_SHADOW, hasStaticShadow);
    }

    public boolean isSolid() {
        return flags.get(SOLID);
    }

    public void setSolid(boolean solid) {
        flags.set(SOLID, solid);
    }

    public boolean isOnTop() {
        return flags.get(ON_TOP);

    }

    public void setOnTop(boolean onTop) {
        flags.set(ON_TOP, onTop);
    }

    public boolean isEmitter() {
        return flags.get(EMITTER);
    }

    public void setEmitter(boolean emitter) {
        flags.set(EMITTER, emitter);
    }

    public boolean isEmits() {
        return flags.get(EMITS);
    }

    public void setEmits(boolean emits) {
        flags.set(EMITS, emits);
    }

    public boolean isSimpleLighting() {
        return flags.get(SIMPLE_LIGHTING);
    }

    public void setSimpleLighting(boolean simpleLighting) {
        flags.set(SIMPLE_LIGHTING, simpleLighting);
    }

    public boolean isVisible() {
        return flags.get(VISIBLE);
    }

    public void setVisible(boolean visible) {
        flags.set(VISIBLE, visible);
    }

    public boolean isCanBeCovered() {
        return flags.get(CAN_BE_COVERED);
    }

    public void setCanBeCovered(boolean canBeCovered) {
        flags.set(CAN_BE_COVERED, canBeCovered);
    }

    public boolean canInteract() {
        return flags.get(CAN_INTERACT);
    }

    public void setCanInteract(boolean canInteract) {
        flags.set(CAN_INTERACT, canInteract);
    }

    public boolean addItem(Item item) {
        if (items == null) {
            items = new Item[xBackpackSize * yBackpackSize];
            for (int i = 0; i < items.length; i++) {
                items[i] = Item.EMPTY;
            }
        }
        for (int i = 0; i < items.length; i++) {
            if (items[i] == Item.EMPTY) {
                items[i] = item;
                return true;
            }
        }
        System.out.println("Brak miejsca, by to wziąć!");
        return false;
    }

    public void removeItem(Item item) {
        if (items != null) {
            for (int i = 0; i < items.length; i++) {
                if (items[i] == item) {
                    items[i] = Item.EMPTY;
                }
            }
        }
    }


    public ArrayList<Item> setBackpackSize(int xSize, int ySize) {
        xBackpackSize = xSize;
        yBackpackSize = ySize;
        Item[] temp = new Item[xSize * ySize];
        if (xSize * ySize != items.length) {
            int j = 0;
            for (int i = 0; i < temp.length; i++) {
                while (j < items.length) {
                    if (items[j] != Item.EMPTY) {
                        temp[i] = items[j];
                        j++;
                        break;
                    }
                    j++;
                }
                if (j == items.length) {
                    temp[i] = Item.EMPTY;
                }
            }
            if (j < items.length) {
                ArrayList<Item> left = new ArrayList<>(items.length - j);
                for (; j < items.length; j++) {
                    if (items[j] != Item.EMPTY) {
                        left.add(items[j]);
                    }
                }
                if (!left.isEmpty()) {
                    return left;
                }
            }
        }
        items = temp;
        return null;
    }

    public Item[] getItems() {
        return items;
    }

    public int getXBackpackSize() {
        return xBackpackSize;
    }

    public int getYBackpackSize() {
        return yBackpackSize;
    }
}
