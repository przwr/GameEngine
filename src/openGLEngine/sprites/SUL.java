/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openGLEngine.sprites;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

/**
 *
 * @author przemek
 */
public class SUL extends SpriteRenderer {

    @Override
    public void render(int sx, int sy, float bx, float ex, float by, float ey) {
        glBegin(GL_QUADS);
        glTexCoord2f(ex, ey);
        glVertex2f(0, 0);
        glTexCoord2f(bx, ey);
        glVertex2f(sx, 0);
        glTexCoord2f(bx, by);
        glVertex2f(sx, sy);
        glTexCoord2f(ex, by);
        glVertex2f(0, sy);
        glEnd();
    }
}
