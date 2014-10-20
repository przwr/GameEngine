/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.place.cameras.Camera;
import game.place.Place;
import java.util.ArrayList;
import engine.Physics;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class Mob extends Entity {

    protected final int range;
    protected GameObject prey;

    public Mob(int x, int y, int startX, int startY, int width, int height, int sx, int sy, int speed, int range, String name, Place place, boolean solid) {
        this.width = width;
        this.height = height;
        this.solid = solid;
        this.sX = startX;
        this.sY = startY;
        this.top = true;
        this.setSpeed(speed);
        this.range = range;
        init("rabbit", name, x, y, sx, sy, place);
    }

    @Override
    protected boolean isColided(int magX, int magY) {
        if ((getBegOfX() + magX) < 0 || (getEndOfX() + magX) > place.getWidth() || (getBegOfY() + magY) < 0 || (getEndOfY() + magY) > place.getHeight()) {
            return true;
        }
        return (place.isObjCTl(magX, magY, this) || place.isObjCObj(magX, magY, this));
    }

    @Override
    protected void move(int xPos, int yPos) {
        x += xPos;
        y += yPos;
    }

    public void update(ArrayList<GameObject> players) {
        if (prey != null) {
            chase(prey);
        } else {
            look(players);
        }
    }

    public synchronized void look(ArrayList<GameObject> players) {
        prey = Physics.sphereCollideWPl(getMidX(), getMidY(), range, players, place);
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

    @Override
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
