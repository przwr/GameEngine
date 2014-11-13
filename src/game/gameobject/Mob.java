/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Rectangle;
import engine.Drawer;
import engine.Methods;
import game.place.cameras.Camera;
import game.place.Place;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;
import sprites.Sprite;

/**
 *
 * @author przemek
 */
public abstract class Mob extends Entity {

    protected final double range;
    protected GameObject prey;

    public Mob(int x, int y, int startX, int startY, int width, int height, int sx, int sy, int speed, int range, String name, Place place, boolean solid, double SCALE) {
        this.width = (int) (SCALE * width);
        this.height = (int) (SCALE * height);
        this.solid = solid;
        this.sX = (int) (SCALE * startX);
        this.sY = (int) (SCALE * startY);
        this.range = (int) (SCALE * range);
        scale = SCALE;
//        this.emitter = true;
//        setEmits(true);
//        this.light = new Light("light", 0.85f, 0.85f, 0.85f, (int) (SCALE * 1024), (int) (SCALE * 1024), place);
        init("rabbit", name, (int) (SCALE * x), (int) (SCALE * y), (int) (SCALE * sx), (int) (SCALE * sy), place);
        this.lit = new Sprite("rabbitw", (int) (SCALE * sx), (int) (SCALE * sy), null);
        this.nLit = new Sprite("rabbitb", (int) (SCALE * sx), (int) (SCALE * sy), null);
        setCollision(new Rectangle(this.width / 2, this.height / 3, true, false, this));
        this.setMaxSpeed(speed);
    }

    public abstract void update(Place place);

    @Override
    protected boolean isColided(int magX, int magY) {
        return collision.ifCollideSolid(getX() + magX, getY() + magY, place) || collision.ifCollide(getX() + magX, getY() + magY, place);
    }

    @Override
    protected void move(int xPos, int yPos) {
        x += xPos;
        y += yPos;
    }

    @Override
    protected void setPosition(int xPos, int yPos) {
        x = xPos;
        y = yPos;
    }

    public synchronized void look(GameObject[] players) {
        GameObject g;
        for (int i = 0; i < place.playersLength; i++) {
            g = players[i];
            if (Methods.PointDistance(g.getX(), g.getY(), getX(), getY()) < range) {
                prey = g;
            }
        }
    }

    public synchronized void chase(GameObject prey) {
        if (prey != null) {
            double angle = Methods.PointAngle360(getX(), getY(), prey.getX(), prey.getY());
            myHspeed = Methods.xRadius(angle, maxSpeed);
            myVspeed = Methods.yRadius(angle, maxSpeed);
        }
    }

    @Override
    public void renderName(Place place, Camera cam) {
        place.renderMessage(0, cam.getXOff() + getX(), cam.getYOff() + getY() - (sprite.getSy() - 15),
                name, new Color(place.r, place.g, place.b));
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            sprite.render();
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect, 0);
            if (isLit) {
                Drawer.drawShapeInColor(sprite, color, color, color, 1);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            } else {
                Drawer.drawShapeInBlack(sprite);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            }
            glPopMatrix();
        }
    }
}
