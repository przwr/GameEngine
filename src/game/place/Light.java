/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.place.cameras.Camera;
import game.gameobject.GameObject;
import game.gameobject.Player;
import openGLEngine.sprites.Sprite;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author przemek
 */
public class Light {

    protected Sprite light;
    protected float r;
    protected float g;
    protected float b;

    public Light(String lightName, float r, float g, float b, int sx, int sy) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.light = new Sprite(lightName, sx, sy);
    }

    public void render(GameObject emitter, Place place, Camera cam) {
        glColor3f(r - place.r, g - place.g, b - place.b);
        //float val = 0.8f - place.r;
        //glColor3f(val, val, val);
        //glColor3f(r - 2f * place.r, g - 2f * place.g, b - 2f * place.b);
        //glColor3f(r - 1.5f * place.r, g - 1.5f * place.g, b - 1.5f * place.b);
        //glColor3f(r, g, b);
        glPushMatrix();
        if (emitter.getClass() == Player.class) {
            glTranslatef(emitter.getMidX() - light.getSx() / 2 - place.getXOff(((Player) emitter).getCam()) + place.getXOff(cam), emitter.getMidY() - light.getSy() / 2 - place.getYOff(((Player) emitter).getCam()) + place.getYOff(cam), 0);
        } else {
            glTranslatef(emitter.getMidX() - light.getSx() / 2, emitter.getMidY() - light.getSy() / 2, 0);
        }
        light.render();
        glPopMatrix();
    }

    public void setSize(int sx, int sy) {
        light.setSx(sx);
        light.setSy(sy);
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void render(GameObject emitter, Place place, int x, int y) {
        glColor3f(r - place.r, g - place.g, b - place.b);
        //glColor3f(r - 1.5f * place.r, g - 1.5f * place.g, b - 1.5f * place.b);
        //glColor3f(r, g, b);
        glPushMatrix();
        glTranslatef(emitter.getMidX() - light.getSx() / 2 + x, emitter.getMidY() - light.getSy() / 2 + y, 0);
        light.render();
        glPopMatrix();
    }
}
