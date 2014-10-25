/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.gameobject.GameObject;
import sprites.Sprite;
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
    protected int strength;

    public Light(String lightName, float r, float g, float b, int strength, int sx, int sy, Place place) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.strength = strength;
        this.light = new Sprite(lightName, sx, sy, null);
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

    public void setStrength(int strenght) {
        this.strength = strenght;
    }

    public void render(GameObject emitter, Place place, int x, int y) {
        if (light != null) {
            glColor3f(r, g, b);
            glPushMatrix();
            glTranslatef(emitter.getMidX() - light.getSx() / 2 + x, emitter.getMidY() - light.getSy() / 2 + y, 0);
            for (int i = 0; i < strength; i++) {
                light.render();
            }
            glPopMatrix();
        }
    }

    public void render() {
        if (light != null) {
            glColor3f(r, g, b);
            glPushMatrix();
            //glTranslatef(light.getSx() / 2, light.getSy()/2, 0);
            light.render();
            glPopMatrix();
        }
    }

    public int getSX() {
        return light.getSx();
    }

    public int getSY() {
        return light.getSy();
    }

}
