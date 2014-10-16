/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author przemek
 */
public class Shadow {

    protected  int shadowedTex;

    public Shadow(int shadowedTex) {
        this.shadowedTex = shadowedTex;
    }

    public void render(float w, float h) {
        glPushMatrix();
        glBindTexture(GL_TEXTURE_2D, shadowedTex);
        glBegin(GL_QUADS);
        glTexCoord2f(0, h / 2048.0f);
        glVertex2f(0, 0);
        glTexCoord2f(w / 2048.0f, h / 2048.0f);
        glVertex2f(w, 0);
        glTexCoord2f(w / 2048.0f, 0);
        glVertex2f(w, h);
        glTexCoord2f(0, 0);
        glVertex2f(0, h);
        glEnd();
        glPopMatrix();
    }

}
