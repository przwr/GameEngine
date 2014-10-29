/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Rectangle;
import game.Methods;
import game.place.cameras.Camera;
import game.place.Place;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public abstract class Mob extends Entity {

    protected final int range;
    protected GameObject prey;

    public Mob(int x, int y, int startX, int startY, int width, int height, int sx, int sy, int speed, int range, String name, Place place, boolean solid, double SCALE) {
        this.width = (int) (SCALE * width);
        this.height = (int) (SCALE * height);
        this.solid = solid;
        this.sX = (int) (SCALE * startX);
        this.sY = (int) (SCALE * startY);
        this.top = true;
        this.setSpeed((int) (SCALE * speed));
        this.range = (int) (SCALE * range);
        init("rabbit", name, (int) (SCALE * x), (int) (SCALE * y), (int) (SCALE * sx), (int) (SCALE * sy), place);
        setCollision(new Rectangle(sX, sY, this.width, this.height, this));
    }

    public abstract void update(Place place);

    @Override
    protected boolean isColided(int magX, int magY) {
        return collision.ifCollideSolid(getX() + magX, getY() + magY, place) || collision.ifCollide(getX() + magX, getY() + magY, place.players);
        /*if ((getBegOfX() + magX) < 0 || (getEndOfX() + magX) > place.getWidth() || (getBegOfY() + magY) < 0 || (getEndOfY() + magY) > place.getHeight()) {
         return true;
         }
         return (place.isObjCTl(magX, magY, this) || place.isObjCObj(magX, magY, this));*/
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
        for (GameObject g : players) {
            if (Methods.PointDistance(g.getX(), g.getY(), getX(), getY()) < range) {
                prey = g;
            }
        }
    }

    public synchronized void chase(GameObject prey) {
        if (prey != null) {
            int xToGo = 0;
            int yToGo = 0;
            if (prey.getMidX() != getMidX()) {
                xToGo = prey.getMidX() > getMidX() ? 1 : -1;
            }
            if (prey.getMidY() != getMidY()) {
                yToGo = prey.getMidY() > getMidY() ? 1 : -1;
            }
            canMove(xToGo, yToGo);
        }
    }

    @Override
    public void renderName(Place place, Camera cam) {
        place.renderMessage(0, cam.getXOff() + getMidX(), cam.getYOff() + getBegOfY(), name, new Color(place.r, place.g, place.b));
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

    public void renderShadow(int xEffect, int yEffect, boolean isLit) {
        if (nLit != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            if (isLit) {
                lit.render();
            } else {
                nLit.render();
            }
            glPopMatrix();
        }
    }
}
