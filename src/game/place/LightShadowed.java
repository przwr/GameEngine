/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Sprite;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author przemek
 */
public class LightShadowed {

    protected Sprite light;
    protected float r;
    protected float g;
    protected float b;
    protected int strength;
    protected int midX;
    protected int midY;

    public LightShadowed(Light light, int midX, int midY) {
        this.r = light.r;
        this.g = light.g;
        this.b = light.b;
        this.strength = light.strength;
        this.light = light.light;
        this.midX = midX;
        this.midY = midY;
    }

    public void changeTex(Sprite light) {
        this.light = light;
    }

    public void render(Place place, int x, int y) {
        if (light != null) {
            glColor3f(r, g, b);
            glPushMatrix();
            glTranslatef(midX - light.getSx() / 2 + x, midY - light.getSy() / 2 + y, 0);
            for (int i = 0; i < strength; i++) {
                light.render();
            }
            glPopMatrix();
        }
    }

}
