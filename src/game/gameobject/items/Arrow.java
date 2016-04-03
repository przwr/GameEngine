/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.items;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Main;
import engine.lights.ShadowDrawer;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.activator.UpdateBasedActivator;
import game.gameobject.interactive.collision.CircleInteractiveCollision;
import game.gameobject.stats.Stats;
import game.place.Place;
import net.packets.Update;
import org.newdawn.slick.Color;

import static game.gameobject.interactive.Interactive.BOW_HURT;
import static game.gameobject.items.Weapon.BOW;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * @author Wojtek
 */
public class Arrow extends Entity {

    private final GameObject owner;
    private final Color color;
    private final int lenght;
    private final TailEffect tail;
    private boolean stopped;

    public Arrow(double speed, int direction, int height, GameObject owner) {
        this.floatHeight = height;
        this.xSpeed = Methods.xRadius(direction, speed);
        this.ySpeed = -Methods.yRadius(direction, speed);
        this.owner = owner;
        setDirection(direction);
        lenght = (int) (Place.tileSize * 1.2);
        setCollision(Rectangle.create(lenght / 10, lenght / 10, OpticProperties.NO_SHADOW, this));
        visible = true;
        this.color = new Color(108, 59, 44);
        tail = new ArrowTail(6, (float) (lenght / 8), this);
        stats = new Stats(this);
        stats.setStrength((int) (speed / 3));
        Interactive attack = Interactive.create(this, new UpdateBasedActivator(), new CircleInteractiveCollision(0, 64, -24, 64), BOW_HURT, BOW, (byte) 1,
                2f, 1f);
        attack.addException(owner);
        attack.setCollidesWithEnvironment(false);
        addInteractive(attack);
    }

    @Override
    public void updateOnline() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void updateRest(Update update) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean isCollided(double xMagnitude, double yMagnitude) {
        boolean ret = collision.isCollideSolid((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), map);
        if (ret) {
            stopped = true;
            getAttackActivator().setActivated(true);
        }
        return ret;
    }

    @Override
    public void reactToAttack(byte attackType, GameObject attacked, int hurt) {
        delete();
    }

    @Override
    protected Player getCollided(double xMagnitude, double yMagnitude) {
        return null;
    }

    @Override
    public void update() {
        int delta;
        updateWithGravity();
        if (!stopped) {
            moveIfPossibleWithoutSliding(xSpeed + xEnvironmentalSpeed, ySpeed + yEnvironmentalSpeed);
            tail.updatePoint((int) x, (int) y, (int) floatHeight, getDirection());
            if (floatHeight == 0) {
                stopped = true;
            }
        } else {
            tail.updateStatic();
            if ((delta = Methods.pointDifference(getX(), getY(), owner.getX(), owner.getY())) < Place.tileSize) {
                setPosition(x + (owner.getX() - x) / 5, y + (owner.getY() - y) / 5);
                if (delta < Place.tileHalf / 2) {
                    delete();
                }
            }
        }
    }

    @Override
    public void render() {
        tail.render();
        glTranslatef(getX(), getY(), 0);
        int ix = (int) (Methods.xRadius(getDirection(), lenght / 2));
        int iy = (int) (-Methods.yRadius(getDirection(), lenght / 2));
        Drawer.setColorStatic(JUMP_SHADOW_COLOR);
        Drawer.drawLineWidth(-ix, -iy, ix, iy, lenght / 15);
        Drawer.setColorBlended(color);
        if (tail.isActive()) {
            ix = (int) (Methods.xRadius(tail.getDirection(), lenght / 2));
            iy = (int) (-Methods.yRadius(tail.getDirection(), lenght / 2));
        }
        Drawer.drawLineWidth(-ix, -iy - (int) floatHeight, ix, iy, lenght / 15);
        Drawer.refreshColor();
        glTranslatef(-getX(), -getY(), 0);
        if (Main.SHOW_INTERACTIVE_COLLISION) {
            interactiveObjects.stream().forEach((interactive) -> {
                interactive.render();
            });
        }
    }

    @Override
    public int getYSpriteBegin(boolean... forCover) {
        return super.getYSpriteBegin() - lenght - tail.getHeight();
    }

    @Override
    public int getYSpriteEnd(boolean... forCover) {
        return super.getYSpriteEnd() + lenght + tail.getHeight();
    }

    @Override
    public int getXSpriteBegin(boolean... forCover) {
        return super.getXSpriteBegin() - lenght - tail.getWidth();
    }

    @Override
    public int getXSpriteEnd(boolean... forCover) {
        return super.getXSpriteEnd() + lenght + tail.getWidth();
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
            ShadowDrawer.renderCurrentVBO();
            Drawer.drawShapePartBlack(appearance, getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }

    @Override
    public int getActualHeight() {
        return Methods.roundDouble(collision.getHeight() * Methods.ONE_BY_SQRT_ROOT_OF_2);
    }

    public GameObject getOwner() {
        return owner;
    }
}
