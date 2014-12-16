/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Figure;
import collision.Rectangle;
import engine.Drawer;
import engine.Methods;
import game.place.cameras.Camera;
import game.place.Place;
import net.packets.Update;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public abstract class Mob extends Entity {

    protected final double range;
    protected GameObject prey;
    public short id;

    public Mob(int x, int y, int startX, int startY, int width, int height, int speed, int range, String name, Place place, boolean solid) {
        float SCALE = place.settings.SCALE;
        this.width = Methods.RoundHU(SCALE * width);
        this.height = Methods.RoundHU(SCALE * height);
        this.solid = solid;
        this.startX = Methods.RoundHU(SCALE * startX);
        this.startY = Methods.RoundHU(SCALE * startY);
        this.range = Methods.RoundHU(SCALE * range);
        scale = SCALE;
        init(name, Methods.RoundHU(SCALE * x), Methods.RoundHU(SCALE * y), place);
        this.sprite = place.getSprite("rabbit");
        setCollision(new Rectangle(this.width, this.height / 4, true, false, 0, this));
        this.setMaxSpeed(speed);
    }

    public abstract void update(Place place);

    @Override
    protected boolean isColided(int magX, int magY) {
        return collision.ifCollideSolid(getX() + magX, getY() + magY, place) || collision.ifCollide(getX() + magX, getY() + magY, place);
    }

    @Override
    public Player getCollided(int magX, int magY) {
        return collision.getCollided(getX() + magX, getY() + magY, place);
    }

    @Override
    protected void move(int xPos, int yPos) {
        setX(x + xPos);
        setY(y + yPos);
    }

    @Override
    protected void setPosition(int xPos, int yPos) {
        setX(xPos);
        setY(yPos);
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
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect, 0);
            if (isLit) {
                Drawer.drawShapeInColor(sprite, color, color, color);
            } else {
                Drawer.drawShapeInBlack(sprite);
            }
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f, int xs, int xe) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect, 0);
            if (isLit) {
                Drawer.drawShapeInColor(sprite, color, color, color, xs, xe);
            } else {
                Drawer.drawShapeInBlack(sprite, xs, xe);
            }
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            glPopMatrix();
        }
    }

    @Override
    public void updateRest(Update up) {
    }

    @Override
    public void updateOnline() {
    }
}
