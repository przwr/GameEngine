/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openGLEngine.sprites;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 *
 * @author przemek
 */
public class Sprite {

    private Texture texture;

    private int sx;
    private int sy;

    public Sprite(String textureKey, int sx, int sy) {
        if (textureKey != null) {
            Sprite.class.getResourceAsStream(textureKey);
            this.texture = loadTexture(textureKey);
        }        
        this.sx = sx;
        this.sy = sy;
    }

    public static Texture loadTexture(String key) {
        try {
            return TextureLoader.getTexture("png", Sprite.class.getResourceAsStream("/res/" + key + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void render() {
        texture.bind();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(0, 0);
        glTexCoord2f(0, 1);
        glVertex2f(0, sy);
        glTexCoord2f(1, 1);
        glVertex2f(sx, sy);
        glTexCoord2f(1, 0);
        glVertex2f(sx, 0);
        glEnd();
    }

    public void render(boolean flip) {
        texture.bind();
        glBegin(GL_QUADS);
        if (flip) {
            glTexCoord2f(0.5f, 0);
            glVertex2f(0, 0);
            glTexCoord2f(0, 0);
            glVertex2f(sx, 0);
            glTexCoord2f(0, 1);
            glVertex2f(sx, sy);
            glTexCoord2f(0.5f, 1);
            glVertex2f(0, sy);
        } else {
            glTexCoord2f(0, 0);
            glVertex2f(0, 0);
            glTexCoord2f(0, 1);
            glVertex2f(0, sy);
            glTexCoord2f(0.5f, 1);
            glVertex2f(sx, sy);
            glTexCoord2f(0.5f, 0);
            glVertex2f(sx, 0);
        }
        glEnd();
    }

    public void render(int flip) {
        texture.bind();
        glBegin(GL_QUADS);
        if (flip == 0) {
            glTexCoord2f(1, 0);
            glVertex2f(0, 0);
            glTexCoord2f(0, 0);
            glVertex2f(sx, 0);
            glTexCoord2f(0, 1);
            glVertex2f(sx, sy);
            glTexCoord2f(1, 1);
            glVertex2f(0, sy);
        } else if (flip == 1) {
            glTexCoord2f(0, 0);
            glVertex2f(0, 0);
            glTexCoord2f(0, 1);
            glVertex2f(0, sy);
            glTexCoord2f(1, 1);
            glVertex2f(sx, sy);
            glTexCoord2f(1, 0);
            glVertex2f(sx, 0);
        } else if (flip == 2) {
            glTexCoord2f(0, 1);
            glVertex2f(0, 0);
            glTexCoord2f(1, 1);
            glVertex2f(sx, 0);
            glTexCoord2f(1, 0);
            glVertex2f(sx, sy);
            glTexCoord2f(0, 0);
            glVertex2f(0, sy);
        } else {
            glTexCoord2f(1, 1);
            glVertex2f(0, 0);
            glTexCoord2f(0, 1);
            glVertex2f(sx, 0);
            glTexCoord2f(0, 0);
            glVertex2f(sx, sy);
            glTexCoord2f(1, 0);
            glVertex2f(0, sy);
        }
        glEnd();
    }

    public void render(int flip, float bx, float ex, float by, float ey) {
        texture.bind();
        glBegin(GL_QUADS);
        if (flip == 0) {
            glTexCoord2f(ex, by);
            glVertex2f(0, 0);
            glTexCoord2f(bx, by);
            glVertex2f(sx, 0);
            glTexCoord2f(bx, ey);
            glVertex2f(sx, sy);
            glTexCoord2f(ex, ey);
            glVertex2f(0, sy);
        } else if (flip == 1) {
            glTexCoord2f(bx, by);
            glVertex2f(0, 0);
            glTexCoord2f(bx, ey);
            glVertex2f(0, sy);
            glTexCoord2f(ex, ey);
            glVertex2f(sx, sy);
            glTexCoord2f(ex, by);
            glVertex2f(sx, 0);
        } else if (flip == 2) {
            glTexCoord2f(bx, ey);
            glVertex2f(0, 0);
            glTexCoord2f(ex, ey);
            glVertex2f(sx, 0);
            glTexCoord2f(ex, by);
            glVertex2f(sx, sy);
            glTexCoord2f(bx, by);
            glVertex2f(0, sy);
        } else {
            glTexCoord2f(ex, ey);
            glVertex2f(0, 0);
            glTexCoord2f(bx, ey);
            glVertex2f(sx, 0);
            glTexCoord2f(bx, by);
            glVertex2f(sx, sy);
            glTexCoord2f(ex, by);
            glVertex2f(0, sy);
        }
        glEnd();
    }

    public int getSx() {
        return sx;
    }

    public int getSy() {
        return sy;
    }

    public void setSx(int sx) {
        this.sx = sx;
    }

    public void setSy(int sy) {
        this.sy = sy;
    }

    public void bind() {
        texture.bind();
    }
}
