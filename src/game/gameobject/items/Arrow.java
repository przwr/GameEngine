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
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.PointedValue;
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
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class Arrow extends Entity {

    private final GameObject owner;
    private boolean stopped;
    private final Color color;
    private final int lenght;
    private final TailEffect tail;

    public Arrow(double speed, int direction, int height, GameObject owner) {
        this.floatHeight = height;
//        speed /= 10;
//        gravity /= 10;
        this.xSpeed = Methods.xRadius(direction, speed);
        this.ySpeed = -Methods.yRadius(direction, speed);
        this.owner = owner;
        setDirection(direction);
        setCollision(Rectangle.create(24, 24, OpticProperties.NO_SHADOW, this));
        visible = true;
        lenght = (int) (Place.tileSize * 1.2);
        this.color = new Color(108, 59, 44);
        tail = new TailEffect(6, (float) (lenght / 8));
        stats = new Stats(this);
        stats.setStrength(20);
        Interactive attack = Interactive.create(this, new UpdateBasedActivator(), new CircleInteractiveCollision(0, 64, -24, 64), BOW_HURT, BOW, (byte) 1, 2f);
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
    public void reactToAttack(byte attackType, GameObject attacked) {
        delete();
    }

    @Override
    protected Player getCollided(double xMagnitude, double yMagnitude) {
        return null;
    }

    @Override
    public void update() {
        int delta;
        if (!stopped) {
            moveIfPossibleWithoutSliding(xSpeed + xEnvironmentalSpeed, ySpeed + yEnvironmentalSpeed);
            tail.updatePoint((int) x, (int) y, (int) floatHeight - lenght / 15, getDirection());
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
        updateWithGravity();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        tail.render(xEffect, yEffect);
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        glTranslatef(getX(), getY(), 0);
        int ix = (int) (Methods.xRadius(getDirection(), lenght / 2));
        int iy = (int) (-Methods.yRadius(getDirection(), lenght / 2));
        Drawer.setColor(JUMP_SHADOW_COLOR);
        Drawer.setCentralPoint();
        Drawer.drawLineWidth(-ix, -iy, ix, iy, lenght / 15);
        Drawer.setColor(color);
        Drawer.returnToCentralPoint();
        Drawer.translate(0, (float) -floatHeight);
        Drawer.drawLineWidth(-ix, -iy, ix, iy, lenght / 15);
        Drawer.returnToCentralPoint();
        Drawer.refreshColor();
        glPopMatrix();
        if (Main.SHOW_INTERACTIVE_COLLISION) {
            interactiveObjects.stream().forEach((interactive) -> {
                interactive.render(xEffect, yEffect);
            });
        }
    }

    @Override
    public int getYSpriteBegin() {
        return super.getYSpriteBegin() - lenght - tail.getHeight();
    }

    @Override
    public int getYSpriteEnd() {
        return super.getYSpriteEnd() + lenght + tail.getHeight();
    }

    @Override
    public int getXSpriteBegin() {
        return super.getXSpriteBegin() - lenght - tail.getWidth();
    }

    @Override
    public int getXSpriteEnd() {
        return super.getXSpriteEnd() + lenght + tail.getWidth();
    }
    
    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInShade(appearance, 1);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapeInBlack(appearance);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInShade(appearance, 1, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect - (int) floatHeight, 0);
            Drawer.drawShapePartInBlack(appearance, xStart, xEnd);
            glPopMatrix();
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
