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
import game.Settings;
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
    public short mobID;

    public abstract void update(Place place);

    public Mob(int x, int y, int startX, int startY, int width, int height, int speed, int range, String name, Place place, String spriteName, boolean solid) {
        this.width = Methods.roundHalfUp(Settings.scale * width);
        this.height = Methods.roundHalfUp(Settings.scale * height);
        this.solid = solid;
        this.xStart = Methods.roundHalfUp(Settings.scale * startX);
        this.yStart = Methods.roundHalfUp(Settings.scale * startY);
        this.range = Methods.roundHalfUp(Settings.scale * range);
        this.setMaxSpeed(speed);
        this.sprite = place.getSprite(spriteName);
        initialize(name, Methods.roundHalfUp(Settings.scale * x), Methods.roundHalfUp(Settings.scale * y), place);
        setCollision(Rectangle.create(this.width, this.height / 4, OpticProperties.NO_SHADOW, this));
    }

    public synchronized void look(GameObject[] players) {
        GameObject g;
        for (int i = 0; i < place.playersCount; i++) {
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
    protected boolean isColided(int xMagnitude, int yMagnitude) {
        return collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map) || collision.isCollidePlayer(getX() + xMagnitude, getY() + yMagnitude, place);
    }

    @Override
    public Player getCollided(int xMagnitude, int yMagnitude) {
        return collision.firstPlayerCollide(getX() + xMagnitude, getY() + yMagnitude, place);
    }

    @Override
    protected void move(int xPosition, int yPosition) {
        setX(x + xPosition);
        setY(y + yPosition);
    }

    @Override
    protected void setPosition(int xPosition, int yPosition) {
        setX(xPosition);
        setY(yPosition);
    }

    @Override
    public void renderName(Place place, Camera cam) {
        place.renderMessage(0, cam.getXOffset() + getX(), cam.getYOffset() + getY() + sprite.getSy() + collision.getHeight() / 2,
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
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure, int xStart, int xEnd) {
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
    public void updateRest(Update update) {
    }

    @Override
    public void updateOnline() {
    }
}
