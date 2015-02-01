/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import engine.Drawer;
import engine.Methods;
import game.place.cameras.Camera;
import game.place.Place;
import net.packets.Update;
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
        scale = place.settings.SCALE;
        this.width = Methods.roundHalfUp(scale * width);
        this.height = Methods.roundHalfUp(scale * height);
        this.solid = solid;
        this.xStart = Methods.roundHalfUp(scale * startX);
        this.yStart = Methods.roundHalfUp(scale * startY);
        this.range = Methods.roundHalfUp(scale * range);
        init(name, Methods.roundHalfUp(scale * x), Methods.roundHalfUp(scale * y), place);
        this.sprite = place.getSprite("rabbit");
        setCollision(Rectangle.create(this.width, this.height / 4, OpticProperties.NO_SHADOW, this));
        this.setMaxSpeed(speed);
    }

    public abstract void update(Place place);

    @Override
    protected boolean isColided(int magX, int magY) {
        return collision.isCollideSolid(getX() + magX, getY() + magY, map) || collision.isCollidePlayer(getX() + magX, getY() + magY, place);
    }

    @Override
    public Player getCollided(int magX, int magY) {
        return collision.firstPlayerCollide(getX() + magX, getY() + magY, place);
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
            if (g.getMap() == map && Methods.pointDistance(g.getX(), g.getY(), getX(), getY()) < range) {
                prey = g;
                break;
            }
        }
    }

    public synchronized void chase(GameObject prey) {
        if (prey != null) {
            double angle = Methods.pointAngle360(getX(), getY(), prey.getX(), prey.getY());
            xSpeed = Methods.xRadius(angle, maxSpeed);
            ySpeed = Methods.yRadius(angle, maxSpeed);
        }
    }

    @Override
    public void renderName(Place place, Camera cam) {
        place.renderMessage(0, cam.getXOff() + getX(), cam.getYOff() + getY() + sprite.getSy() + collision.getHeight() / 2,
                name, new Color(place.red, place.green, place.blue));
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
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect, 0);
            Drawer.drawShapeInShade(sprite, color);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect, 0);
            Drawer.drawShapeInBlack(sprite);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect, 0);
            Drawer.drawShapePartInShade(sprite, color, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect, 0);
            Drawer.drawShapePartInBlack(sprite, xStart, xEnd);
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
