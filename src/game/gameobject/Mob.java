/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.place.Camera;
import game.place.Place;
import java.util.ArrayList;
import openGLEngine.Physics;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class Mob extends Entity {

    protected final Place place;
    protected final int range;
    protected GameObject prey;

    public Mob(int x, int y, int startX, int startY, int width, int height, int sx, int sy, int speed, int range, String name, Place place, boolean solid) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.solid = solid;
        this.sX = startX;
        this.sY = startY;
        this.place = place;
        this.top = true;
        this.speed = speed;
        this.range = range;
        init("rabbit", x, y, sx, sy);
    }

    @Override
    protected boolean isColided(int magX, int magY) {
        if ((x + magX) < 0 || (getEndOfX() + magX) > place.getWidth() || (y + magY) < 0 || (getEndOfY() + magY) > place.getHeight()) {
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
            if (prey.getMidX() != place.getXOff(((Player)prey).getCam()) + getMidX()) {
                xToGo = prey.getMidX() > place.getXOff(((Player)prey).getCam()) + getMidX() ? 1 : -1;
            }
            if (prey.getMidY() != place.getYOff(((Player)prey).getCam()) + getMidY()) {
                yToGo = prey.getMidY() > place.getYOff(((Player)prey).getCam()) + getMidY() ? 1 : -1;
            }
            canMove(xToGo, yToGo);
        }
    }

    @Override
    public void renderName(Place place, Camera cam) {
        place.renderMessage(0, place.getXOff(cam) + getMidX(), place.getYOff(cam) + getBegOfY(), name, new Color(place.r, place.g, place.b));
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (spr != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            spr.render();
            glPopMatrix();
        }
    }
}
