/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
/**
 *
 * @author przemek
 */
public class Resizer {

    private static final int frame = Renderer.makeTexture(null, 2048, 2048);

    public static void resize(float camXStart, float camYStart, float camXSize, float camYSize) {
        Renderer.frameSave(frame, camXStart, camYStart, camXSize, camYSize);
        Renderer.clearScreen(0);        
        glColor3f(1, 1, 1);
        drawFrame(frame, Display.getWidth(), Display.getHeight());
        glEnable(GL_BLEND);
    }

    private static void drawFrame(int textureHandle, float w, float h) {
        float hx = h / 2048.0f;
        float wx = w / 2048.0f;
        float scale = (w - 1024) / 4096;
        float scalarw = scale;//(int) (w / scale);
        float scalarh = scale * (h / w);//(int) (w / scale);
        glPushMatrix();
        glBindTexture(GL_TEXTURE_2D, textureHandle);
        glBegin(GL_QUADS);
        glTexCoord2f(scalarw, hx - scalarh);
        glVertex2f(0, 0);
        glTexCoord2f(wx - scalarw, hx - scalarh);
        glVertex2f(w, 0);
        glTexCoord2f(wx - scalarw, scalarh);
        glVertex2f(w, h);
        glTexCoord2f(scalarw, scalarh);
        glVertex2f(0, h);
        glEnd();
        glPopMatrix();
    }
}