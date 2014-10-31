/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author przemek
 */
public class Sprite {

    protected Texture texture;

    protected SpriteBase base;
    protected int sx;
    protected int sy;
    protected String key;
    protected int id;

    public Sprite(String textureKey, int sx, int sy, SpriteBase base) {
        if (textureKey != null) {
            //Sprite.class.getResourceAsStream(textureKey);
            this.texture = loadTexture(textureKey);
        }
        this.sx = sx;
        this.sy = sy;
        this.key = textureKey;
        this.base = base;
    }

    public Sprite(Texture texture, int sx, int sy) {
        this.texture = texture;
        this.sx = sx;
        this.sy = sy;
    }

    public static Texture loadTexture(String key) {
        try {
            return TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream("/res/" + key + ".png"), GL_LINEAR);
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public BufferedImage getImg() {
        try {
            return ImageIO.read(Sprite.class.getResourceAsStream("/res/" + key + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void bindCheck() {
        if (base == null) {
            texture.bind();
        } else if (base.getLastTex() != id) {
            texture.bind();
            base.setLastTex(id);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public void render() {
        bindCheck();
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
        bindCheck();
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
        bindCheck();
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
        bindCheck();
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Texture getTex() {
        return texture;
    }
}
