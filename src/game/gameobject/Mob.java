/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.Rectangle;
import engine.Methods;
import game.place.cameras.Camera;
import game.place.Place;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL13.GL_COMBINE;
import static org.lwjgl.opengl.GL13.GL_COMBINE_RGB;
import static org.lwjgl.opengl.GL13.GL_OPERAND0_RGB;
import static org.lwjgl.opengl.GL13.GL_OPERAND1_RGB;
import static org.lwjgl.opengl.GL13.GL_PREVIOUS;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_SRC0_RGB;
import static org.lwjgl.opengl.GL15.GL_SRC1_RGB;
import org.newdawn.slick.Color;
import sprites.Sprite;

/**
 *
 * @author przemek
 */
public abstract class Mob extends Entity {

    protected final double range;
    protected GameObject prey;

    private static final Sprite whites = new Sprite("alpha", 1, 1, null);
    int white = glGenTextures();

    public Mob(int x, int y, int startX, int startY, int width, int height, int sx, int sy, int speed, int range, String name, Place place, boolean solid, double SCALE) {
        this.width = (int) (SCALE * width);
        this.height = (int) (SCALE * height);
        this.solid = solid;
        this.sX = (int) (SCALE * startX);
        this.sY = (int) (SCALE * startY);
        this.top = false;
        this.range = (int) (SCALE * range);
        scale = SCALE;
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
    public void renderShadow(int xEffect, int yEffect, boolean isLit, int white, float color) {
        if (nLit != null && lit != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            if (isLit) {
//                sprite.bindCheck();
//                glColor3f(1, 1, 1);
//                glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_ADD);
//                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//                sprite.renderNotBind();
//                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
//                glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glColor4f(color, color, color, 1.0f);
                glActiveTexture(white);
                
                sprite.bindCheck();

                glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_COMBINE);
                glTexEnvi(GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_REPLACE);
                glTexEnvi(GL_TEXTURE_ENV, GL_SRC0_RGB, GL_PREVIOUS);
                glTexEnvi(GL_TEXTURE_ENV, GL_SRC1_RGB, GL_TEXTURE);
                glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
                glTexEnvi(GL_TEXTURE_ENV, GL_OPERAND1_RGB, GL_SRC_COLOR);
                
                sprite.renderNotBind();
                
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
                glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
            } else {
                glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
                sprite.render();
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            }
            glPopMatrix();
        }
    }
}
