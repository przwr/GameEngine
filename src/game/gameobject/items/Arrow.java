/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.items;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import game.gameobject.GameObject;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.gameobject.interactive.CircleInteractiveCollision;
import game.gameobject.interactive.Interactive;
import game.gameobject.interactive.UpdateBasedActivator;
import game.gameobject.stats.MobStats;
import game.gameobject.stats.Stats;
import game.place.Place;
import net.packets.Update;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public class Arrow extends Entity {

    private boolean stopped;
    private final GameObject owner;

    public Arrow(double speed, int direction, int height, GameObject owner) {
        this.floatHeight = height;
        this.xSpeed = Methods.xRadius(direction, speed);
        this.ySpeed = -Methods.yRadius(direction, speed);
        this.owner = owner;
        setDirection(direction);
        setCollision(Rectangle.create(Place.tileHalf, Place.tileHalf, OpticProperties.NO_SHADOW, this));
        visible = true;
        stats = new Stats(this);
        stats.setStrength(20);
        Interactive attack = new Interactive(this,
                new UpdateBasedActivator(),
                new CircleInteractiveCollision(0, 64, -24, 64),
                Interactive.HURT, (byte) 0, 1f);
        attack.addException(owner);
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
    public void render(int xEffect, int yEffect) {
        int delta;
        if (!stopped) {
            moveIfPossibleWithoutSliding(xSpeed + xEnvironmentalSpeed, ySpeed + yEnvironmentalSpeed);
            updateWithGravity();
            if (floatHeight == 0) {
                stopped = true;
            }
        } else if ((delta = Methods.pointDifference(getX(), getY(), owner.getX(), owner.getY())) < Place.tileSize) {
            setPosition(x + (owner.getX() - x) / 5, y + (owner.getY() - y) / 5);
            if (delta < Place.tileHalf / 2) {
                delete();
            }
        }
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        glTranslatef(getX(), getY(), 0);
        Drawer.setColor(JUMP_SHADOW_COLOR);
        Drawer.drawEllipse(0, 0, Methods.roundDouble(collision.getWidth() * Place.getCurrentScale() / 2f), Methods.roundDouble(collision.getHeight()
                * Place.getCurrentScale() / 2f), 24);
        Drawer.setColor(Color.black);
        Drawer.drawEllipse(0, (int) -floatHeight, Methods.roundDouble(collision.getWidth() * Place.getCurrentScale() / 2f), Methods.roundDouble(collision.getHeight()
                * Place.getCurrentScale() / 2f), 24);
        Drawer.refreshColor();
        glPopMatrix();
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

}
