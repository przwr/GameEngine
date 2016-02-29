/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.effects;

import collision.Figure;
import engine.utilities.Delay;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.RandomGenerator;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.place.Place;
import net.packets.Update;
import org.newdawn.slick.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Wojtek
 */
public class DamageNumber extends Entity {

    private final Color color;
    private final int damage;
    private final Delay time;

    public DamageNumber(int damage, int x, int y, int height, Place place) {
        initialize("damage", x, y);
        this.place = place;
        this.floatHeight = height;
        this.damage = damage;
        RandomGenerator rand = RandomGenerator.create();
        int direction = rand.random(360);
        int speed = Math.min(rand.randomInRange(damage / 20, damage / 10) + 1, 5);
        setJumpForce(Math.min(rand.randomInRange(damage / 20, damage / 10) + 1, 2));
        setGravity(0.1);
        this.xSpeed = Methods.xRadius(direction, speed);
        this.ySpeed = -Methods.yRadius(direction, speed);
        time = Delay.createInMilliseconds((rand.random(5) + 5) * 100);
        time.start();
        onTop = true;
        setDirection(direction);
        if (damage <= 5) {
            color = Color.lightGray;
        } else if (damage <= 10) {
            color = Color.white;
        } else if (damage <= 25) {
            color = Color.yellow;
        } else if (damage <= 55) {
            color = Color.orange;
        } else if (damage <= 85) {
            color = Color.red;
        } else {
            color = Color.black;
        }
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
        return false;
    }

    @Override
    protected Player getCollided(double xMagnitude, double yMagnitude) {
        return null;
    }

    @Override
    public void update() {
        moveIfPossibleWithoutSliding(xSpeed + xEnvironmentalSpeed, ySpeed + yEnvironmentalSpeed);
        xSpeed /= 1.1;
        ySpeed /= 1.1;
        if (time.isOver()) {
            delete();
        }
        updateWithGravity();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef((int) (getX() * Place.getCurrentScale() + xEffect), (int) ((getY() - floatHeight) * Place.getCurrentScale() + yEffect), 0);
        Drawer.renderStringCentered("" + damage, 0, 0, place.standardFont, color);
        glPopMatrix();
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, int xStart, int xEnd) {
    }

}
