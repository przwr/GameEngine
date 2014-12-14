/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import game.gameobject.GameObject;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.Sprite;

/**
 *
 * @author przemek
 */
public class Light {

    protected Sprite light;
    protected float r;
    protected float g;
    protected float b;

    protected FBORenderer fbo;

    public Light(String lightName, float r, float g, float b, int sx, int sy, AbstractPlace place) {
        this.r = r;
        this.g = g;
        this.b = b;
        if (!place.settings.shadowOff) {
            fbo = (place.settings.nrSamples > 0) ? new FBORendererMS(sx, sy, place.settings) : new FBORendererRegular(sx, sy, place.settings);
        }
        this.light = new Sprite(lightName, sx, sy, null);
    }
    
    public int getTexture(){
        return light.getId();
    }

    public void setSize(int sx, int sy) {
        light.setWidth(sx);
        light.setHeight(sy);
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void render(GameObject emitter, AbstractPlace place, int x, int y) {
        if (light != null) {
            glColor3f(r, g, b);
            glPushMatrix();
            glTranslatef(emitter.getX() - light.getWidth() / 2 + x, emitter.getY() - light.getHeight() / 2 + y, 0);
            light.render();
            glPopMatrix();
        }
    }

    public void render(int h) {
        if (light != null) {
            glColor3f(r, g, b);
            glPushMatrix();
            glTranslatef(0, h, 0);
            light.render();
            glPopMatrix();
        }
    }

    public void render() {
        if (light != null) {
            glColor3f(r, g, b);
            glPushMatrix();
            light.render();
            glPopMatrix();
        }
    }

    public int getSX() {
        return light.getWidth();
    }

    public int getSY() {
        return light.getHeight();
    }

    public FBORenderer getFBO() {
        return fbo;
    }

}
